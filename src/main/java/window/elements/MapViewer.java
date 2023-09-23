package window.elements;

import data.GameMap;
import data.Location;
import data.TextureHandler;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import lombok.Getter;
import lombok.Setter;
import window.EditorError;
import window.Selection;
import window.Tools;
import window.Window;
import window.commands.Command;
import window.commands.CommandHistory;
import window.commands.MergeCopyLayerCommand;
import window.keyCombinations.*;
import window.tools.SelectTool;
import window.tools.ToolImplementation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class MapViewer extends JPanel {

	private static final boolean TILE_HIGHLIGHT = true;		//true if tiles should be highlighted

	private final Window window;
	private final Camera camera;
	private GameMap map;

	private Tools selectedTool;

	private Selection selection;
	private FreeLayer copyLayer;

	private Command bulkCommand;
	private CommandHistory commandHistory;

	private BufferedImage staticTileGrid;
	private final List<KeyCombination> keyCombinations;

	private final MapViewerMouseAdapter mouseAdapter;

	public MapViewer(Window window, GameMap inputMap) {
		requestFocus();
		grabFocus();

		this.window = window;
		camera = new Camera();
		setGameMap(inputMap, true);

		setSelectedTool(Tools.BRUSH);

		this.keyCombinations = List.of(
				new ZoomCombination(camera),
				new UndoRedoCombination(commandHistory),
				new LambdaCombination('s', window::save),
				new CopyPasteCombination(commandHistory),
				new SelectAllCombination(commandHistory)
		);

		//change zoom when using mouse-wheel
		this.addMouseWheelListener(e -> {
			camera.setZoom(camera.zoom * (float) Math.pow(1.2, -e.getPreciseWheelRotation()));
		});

		mouseAdapter = new MapViewerMouseAdapter(this);

		this.addMouseMotionListener(mouseAdapter);
		this.addMouseListener(mouseAdapter);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				for(KeyCombination keyCombination: keyCombinations) {
					keyCombination.update(e);
				}

				if (e.getKeyCode() >= 48 && e.getKeyCode() <= 57) {
					int toolIndex = e.getKeyCode() - 48;
					setSelectedTool(Tools.get(toolIndex - 1));
				}
			}
		});
	}

	public void setGameMap(GameMap map, boolean isNewMap) {
		this.map = map;
		this.staticTileGrid = generateStaticTileGrid();
		if(isNewMap) {
			centerCamera();
			commandHistory = new CommandHistory(this);
		}
	}

	private void centerCamera() {
		camera.setPosition(-map.getWidth() * map.getTileSize() / 2.0f, -map.getHeight() * map.getTileSize() / 2.0f);
		camera.setZoom(0.5f);
	}

	boolean executeToolAction(int button, int x, int y, boolean isDragged, boolean shiftDown, boolean controlDown) {
		return executeToolAction(selectedTool.getImplementation(), button, x, y, isDragged, shiftDown, controlDown);
	}

	 boolean executeToolAction(ToolImplementation implementation, int button, int x, int y, boolean isDragged, boolean shiftDown, boolean controlDown) {
		Layer selectedLayer = window.getSelectedLayer();
		String selectedTexture = window.getSelectedTexture();

		if (window.isLayerHidden(selectedLayer)) {
			sendErrorMessage(new EditorError("You cannot modify a hidden layer!", true, true));
			return false;
		}

		Location pos = windowToMapPosition(x, y);

		Optional<EditorError> actionThrewError;
		if(isDragged) actionThrewError = implementation.onMouseDrag(commandHistory, button, selectedLayer, selectedTexture, pos, selection, shiftDown, controlDown);
		else actionThrewError = implementation.onMouseClick(commandHistory, button, selectedLayer, selectedTexture, pos, selection, shiftDown, controlDown);

		actionThrewError.ifPresent(this::sendErrorMessage);

		return actionThrewError.isEmpty();
	}

	/**
	 * converts window position to map position
	 * @param xPos x coordinate the user clicked
	 * @param yPos y coordinate the user clicked
	 * @return the position on the whole map
	 */
	public Location windowToMapPosition(int xPos, int yPos) {
		float x = xPos, y = yPos;
		x -= this.getWidth() / 2.0f;
		y -= this.getHeight() / 2.0f;
		x /= camera.zoom;
		y /= camera.zoom;
		x -= camera.x;
		y -= camera.y;
		x = (float) Math.floor(x);
		y = (float) Math.floor(y);
		x /= map.getTileSize();
		y /= map.getTileSize();

		return new Location(x, y);
	}

	@Override
	protected void paintComponent(Graphics g) {
		camera.update();

		//Creates new BufferedImage for double buffering => no flickers
		BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();

		//fills this panel with background color
		g2.setColor(new Color(240, 248, 255));
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());

		//Applies camera settings to graphics object
		g2.translate(this.getWidth() / 2.0, this.getHeight() / 2.0);
		g2.scale(camera.zoom, camera.zoom);
		g2.translate(camera.x, camera.y);

		//draws map canvas
		g2.drawImage(staticTileGrid, 0, 0, null);
		g2.setStroke(new BasicStroke(2 / camera.zoom));

		//draws layer by their drawing depth
		map.getLayers().values().stream()
				.filter(l -> !window.isLayerHidden(l))
				.sorted((o1, o2) -> Float.compare(o2.depth(), o1.depth()))
				.forEach(l -> l.draw(g2, windowToMapPosition(0, 0), windowToMapPosition(this.getWidth(), this.getHeight())));

		//Draws a highlighter (in size of tile => tilelayer, of selected texture => freelayer, of area corner => arealayer) in green(drawing) or red(erasing)
		if (mouseAdapter.isMouseEntered() && TILE_HIGHLIGHT) {
			Location l = windowToMapPosition(mouseAdapter.getLastMousePosX(), mouseAdapter.getLastMousePosY());
			g2.setColor(selectedTool.getColor());

			String selectedTexture = window.getSelectedTexture();
			Layer selectedLayer = window.getSelectedLayer();

			boolean isAreaLayer = selectedLayer instanceof AreaLayer;
			if (selectedTexture != null || isAreaLayer) {
				BufferedImage tex = selectedTexture == null? null: TextureHandler.getImagePng(selectedTexture);
				if (!(selectedLayer instanceof TileLayer))
					g2.drawRect((int) (l.x * map.getTileSize()), (int) (l.y * map.getTileSize()), isAreaLayer? 1: tex.getWidth(), isAreaLayer? 1: tex.getHeight());
				else
					g2.drawRect((int) (l.x) * map.getTileSize(), (int) (l.y) * map.getTileSize(), tex.getWidth(), tex.getHeight());
			}
		}

		if(copyLayer != null) {
			copyLayer.draw(g2, null, null);
		}

		//Draws selection
		g2.setStroke(new BasicStroke(2 / camera.zoom));
		if(selection != null) {
			Color c = Tools.SELECT.getColor();
			g2.setColor(c);
			g2.draw(selection.getArea());
			g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
			g2.fill(selection.getArea());
		}

		Location startClick = ((SelectTool) Tools.SELECT.getImplementation()).getStartClick();
		if(startClick != null) {
			g2.setColor(Tools.SELECT.getColor().brighter());
			Location last = windowToMapPosition(mouseAdapter.getLastMousePosX(), mouseAdapter.getLastMousePosY());
			int x = (int)(Math.min(last.x, startClick.x));
			int y = (int)(Math.min(last.y, startClick.y));
			int w = (int)(Math.max(last.x, startClick.x)) -x+1;
			int h = (int)(Math.max(last.y, startClick.y)) -y+1;
			Rectangle r = new Rectangle(x * map.getTileSize(), y * map.getTileSize(), w * map.getTileSize(),h * map.getTileSize());
			g2.draw(new Area(r));
		}

		g.drawImage(img, 0, 0, null);
	}

	private BufferedImage generateStaticTileGrid() {
		/*
			Since the tile grid does not change, its better to draw this once, when the map changes
		 */

		BufferedImage out = new BufferedImage( map.getWidth() * map.getTileSize(), map.getHeight() * map.getTileSize(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) out.getGraphics();

		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, map.getWidth() * map.getTileSize(), map.getHeight() * map.getTileSize());

		//prepares graphics object to draw tile separators
		g2.setColor(Color.LIGHT_GRAY.darker());
		g2.setStroke(new BasicStroke(1));

		//draw tile separators
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				g2.drawLine(x * map.getTileSize(), y * map.getTileSize(), map.getWidth() * map.getTileSize(), y * map.getTileSize());
				g2.drawLine(x * map.getTileSize(), y * map.getTileSize(), x * map.getTileSize(), map.getHeight() * map.getTileSize());
			}
		}

		return out;
	}

	public void setSelectedTool(Tools t) {
		if(copyLayer != null && window.getSelectedLayer() instanceof TileLayer) new MergeCopyLayerCommand(this, (TileLayer) window.getSelectedLayer(), copyLayer).execute(commandHistory);

		this.selectedTool = t;
		window.getToolbar().update(selectedTool);
	}

	public void updateTitle() {
		String title = "LevelEditor - " + window.getMenu().getFileName() + (commandHistory.isCurrentlySaved() ? "" : " (*)");
		window.setTitle(title);
	}

	public void saveAction() {
		commandHistory.save();
		updateTitle();
	}

	public void sendErrorMessage(EditorError error) {
		if(error.throwScreenShake()) camera.addScreenshake(10f);
		window.setError(error.errorMessage(), 2000);
	}

	public Location getLastMousePosInMapPosition() {
		return this.windowToMapPosition(mouseAdapter.getLastMousePosX(), mouseAdapter.getLastMousePosY());
	}
}