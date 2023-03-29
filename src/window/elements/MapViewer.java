package window.elements;

import data.*;
import data.layer.*;
import data.maps.GameMap;
import window.EditorError;
import window.Selection;
import window.keyCombinations.*;
import window.Tools;
import window.Window;
import window.commands.*;
import window.tools.DragTool;
import window.tools.SelectTool;
import window.tools.TagSelectTool;
import window.tools.ToolImplementation;

import java.util.List;
import java.util.Optional;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class MapViewer extends JPanel {

	private static final boolean TILE_HIGHLIGHT = true;		//true if tiles should be highlighted

	private final Window window;

	private final Camera camera;									//Camera to set viewpoint

	private int lastMousePosX, lastMousePosY, lastMiddleClickX, lastMiddleClickY;

	private Selection selection;
	private FreeLayer copyLayer;

	private boolean mouseEntered;							//booleans if the mouse is in the window and the user has drawing mode (true) or erase mode (false)
	private Tools selectedTool;

	private GameMap map;									//the game map

	private Command bulkCommand;
	private CommandHistory commandHistory;
	private final ToolImplementation tagSelectTool, dragTool;

	private final List<KeyCombination> keyCombinations;
	private BufferedImage staticTileGrid;

	public MapViewer(Window window, GameMap inputMap) {
		requestFocus();
		grabFocus();

		this.window = window;
		camera = new Camera();
		setGameMap(inputMap, true);

		tagSelectTool = new TagSelectTool();
		dragTool = new DragTool();

		setSelectedTool(Tools.BRUSH);
		mouseEntered = false;

		this.keyCombinations = List.of(
				new ZoomCombination(camera),
				new UndoRedoCombination(commandHistory),
				new LambdaCombination('s', window::save),
				new CopyPasteCombination(commandHistory),
				new SelectAllCombination(commandHistory)
		);

		//change zoom when using mouse-wheel
		this.addMouseWheelListener(e -> camera.setZoom(camera.zoom * (float) Math.pow(1.2, -e.getPreciseWheelRotation())));

		MouseAdapter mouseMotionAdapter = new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseEntered = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseEntered = false;
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				lastMousePosX = e.getX();
				lastMousePosY = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int button = 0;
				if(SwingUtilities.isMiddleMouseButton(e)) button = 1;
				else if(SwingUtilities.isRightMouseButton(e)) button = 2;

				boolean toolExecutionRan = executeToolAction(button, e.getX(), e.getY(), true, e.isShiftDown(), e.isControlDown());

				if(!toolExecutionRan) {
					if (button == 1) {
						camera.move((e.getX() - lastMousePosX) / camera.zoom,(e.getY() - lastMousePosY) / camera.zoom);
					} else if (button == 2) {
						executeToolAction(dragTool, button, e.getX(), e.getY(), true, false, false);
					}
				}

				lastMousePosX = e.getX();
				lastMousePosY = e.getY();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
				grabFocus();

				boolean executedToolAction =  false;
				if(selectedTool != Tools.MOVE) executedToolAction = executeToolAction(e.getButton() - 1, e.getX(), e.getY(), false, e.isShiftDown(), e.isControlDown());
				if(executedToolAction) return;

				if (e.getButton() == 3) executeToolAction(tagSelectTool, 2, e.getX(), e.getY(), false, false, false);
				else if (e.getButton() == 2) {
					//Save clicked position
					lastMiddleClickX = e.getX();
					lastMiddleClickY = e.getY();
				} else if (e.getButton() == 4) {
					window.getModifier().setTagObject(map);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//when the difference on middle mouse click and middle mouse release is smaller than => swap between tools
				if (e.getButton() == 2 && Math.abs(lastMiddleClickX - e.getX()) <= 10 && Math.abs(lastMiddleClickY - e.getY()) <= 10) {
					if(!e.isShiftDown()) {
						setSelectedTool(selectedTool.next());
						if(selectedTool == Tools.MOVE && selection == null) setSelectedTool(selectedTool.next());
					}
					else {
						setSelectedTool(selectedTool.pre());
						if(selectedTool == Tools.MOVE && selection == null) setSelectedTool(selectedTool.pre());
					}
					window.getToolbar().update(selectedTool);

					((SelectTool) Tools.SELECT.getImplementation()).eraseStartClick();
					if(copyLayer != null && window.getSelectedLayer() instanceof TileLayer) new MergeCopyLayerCommand(window.getMapViewer(), (TileLayer) window.getSelectedLayer(), copyLayer).execute(commandHistory);
				}

				executeToolAction(e.getButton() - 1, e.getX(), e.getY(), false, e.isShiftDown(), e.isControlDown());

				if(bulkCommand != null) {
					commandHistory.addCommand(bulkCommand);
					bulkCommand = null;
				}
			}
		};

		this.addMouseMotionListener(mouseMotionAdapter);
		this.addMouseListener(mouseMotionAdapter);

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
		if(isNewMap) {
			centerCamera();
			this.staticTileGrid = generateStaticTileGrid();
			commandHistory = new CommandHistory(this);
		}
	}

	private void centerCamera() {
		camera.setPosition(-map.getWidth() * map.getTileSize() / 2.0f, -map.getHeight() * map.getTileSize() / 2.0f);
		camera.setZoom(0.5f);
	}

	private boolean executeToolAction(int button, int x, int y, boolean isDragged, boolean shiftDown, boolean controlDown) {
		return executeToolAction(selectedTool.getImplementation(), button, x, y, isDragged, shiftDown, controlDown);
	}

	private boolean executeToolAction(ToolImplementation implementation, int button, int x, int y, boolean isDragged, boolean shiftDown, boolean controlDown) {
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
		x -= this.getWidth() / 2.0;
		y -= this.getHeight() / 2.0;
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
		if (mouseEntered && TILE_HIGHLIGHT) {
			Location l = windowToMapPosition(lastMousePosX, lastMousePosY);
			g2.setColor(selectedTool.getColor());

			String selectedTexture = window.getSelectedTexture();
			Layer selectedLayer = window.getSelectedLayer();

			int width = 0;
			int height = 0;

			if(selectedLayer instanceof TileLayer) {
				l.x = (int) l.x;
				l.y = (int) l.y;
			}

			if (selectedLayer instanceof AreaLayer){
				width = 1;
				height = 1;
			}
			else if (selectedTexture != null) {
				BufferedImage tex = TextureHandler.getImagePng(selectedTexture);
				width = tex.getWidth();
				height = tex.getHeight();
			}

			if(height > 0 && width > 0) g2.drawRect((int) (l.x * map.getTileSize()), (int) (l.y * map.getTileSize()), width, height);
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
			Location last = windowToMapPosition(lastMousePosX, lastMousePosY);
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

		return map.generateStaticTileGrid();
	}

	public void setSelectedTool(Tools t) {
		if(copyLayer != null && window.getSelectedLayer() instanceof TileLayer) new MergeCopyLayerCommand(this, (TileLayer) window.getSelectedLayer(), copyLayer).execute(commandHistory);

		this.selectedTool = t;
		window.getToolbar().update(selectedTool);
	}

	public void updateTitle() {
		String title = "LevelEditor - " + window.getMyMenuBar().getFileName() + (commandHistory.isCurrentlySaved() ? "" : " (*)");
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

	public CommandHistory getCommandHistory() {
		return commandHistory;
	}

	public Command getBulkCommand() {
		return bulkCommand;
	}

	public void setBulkCommand(Command command) {
		this.bulkCommand = command;
	}

	public Location getLastMousePosInMapPosition() {
		return windowToMapPosition(lastMousePosX, lastMousePosY);
	}

	public FreeLayer getCopyLayer() {
		return copyLayer;
	}

	public void setCopyLayer(FreeLayer fl) {
		this.copyLayer = fl;
	}

	public void setSelection(Selection toSet) {
		this.selection = toSet;
	}

	public Selection getSelection() {
		return selection;
	}
}