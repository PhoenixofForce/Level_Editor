package window.elements;

import data.*;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MapViewer extends JPanel {
	private static final boolean TILE_HIGHLIGHT = true;

	private ImageList imageList;
	private LayerPane layerPane;

	private Camera camera;

	private int last_x, last_y, midX, midY;

	private boolean mouseEntered, drawMode;

	private GameMap map;

	public MapViewer(ImageList imageList, LayerPane layerPane, GameMap map) {
		this.layerPane = layerPane;
		this.imageList = imageList;
		this.map = map;

		drawMode = true;
		mouseEntered = false;

		camera = new Camera();
		centerCamera();

		this.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				camera.setZoom(camera.zoom * (float) Math.pow(1.2, -e.getPreciseWheelRotation()));
			}
		});

		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				last_x = e.getX();
				last_y = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isMiddleMouseButton(e)) camera.move((e.getX() - last_x) / camera.zoom,(e.getY() - last_y) / camera.zoom);
				else if (SwingUtilities.isRightMouseButton(e)) drag(last_x, last_y, e.getX(), e.getY());
				else if (SwingUtilities.isLeftMouseButton(e) && drawMode) set(e.getX(), e.getY(), true);
				else if (SwingUtilities.isLeftMouseButton(e) && !drawMode) remove(e.getX(), e.getY());

				last_x = e.getX();
				last_y = e.getY();
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				mouseEntered = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseEntered = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) select(e.getX(), e.getY());
				else if (SwingUtilities.isLeftMouseButton(e) && drawMode) set(e.getX(), e.getY(), false);
				else if (SwingUtilities.isLeftMouseButton(e) && !drawMode) remove(e.getX(), e.getY());
				else if (SwingUtilities.isMiddleMouseButton(e)) {
					midX = e.getX();
					midY = e.getY();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isMiddleMouseButton(e) && Math.abs(midX - e.getX()) <= 10 && Math.abs(midY - e.getY()) <= 10) drawMode = !drawMode;
			}
		});
	}

	public void setGameMap(GameMap map) {
		this.map = map;
		centerCamera();
	}

	private void centerCamera() {
		camera.setPosition(-map.getWidth() * map.getTileSize() / 2, -map.getHeight() * map.getTileSize() / 2);
		camera.setZoom(0.5f);
	}

	private void set(int x, int y, boolean drag) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		String selectedTexture = imageList.getSelectedImageName();
		if (selectedLayer == null || (selectedTexture == null && !(selectedLayer instanceof AreaLayer)) || layerPane.isHidden(selectedLayer)) {
			sendErrorMessage();
			return;
		}

		Location pos = getBlockLocation(x, y);
		selectedLayer.set(selectedTexture, pos.x, pos.y, drag);
	}

	private void remove(int x, int y) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		if (selectedLayer == null  || layerPane.isHidden(selectedLayer)) {
			sendErrorMessage();
			return;
		}

		Location pos = getBlockLocation(x, y);
		selectedLayer.remove(pos.x, pos.y);
	}

	private void select(int x, int y) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		String selectedTexture = imageList.getSelectedImageName();
		if (selectedLayer == null || (selectedTexture == null && !(selectedLayer instanceof AreaLayer)) || layerPane.isHidden(selectedLayer)) {
			sendErrorMessage();
			return;
		}

		Location pos = getBlockLocation(x, y);
		TagObject obj = selectedLayer.select(pos.x, pos.y);

		imageList.getModifier().setTagObject(obj);
	}

	private void drag(int x, int y, int targetX, int targetY) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		String selectedTexture = imageList.getSelectedImageName();
		if (selectedLayer == null || (selectedTexture == null && !(selectedLayer instanceof AreaLayer)) || layerPane.isHidden(selectedLayer)) {
			sendErrorMessage();
			return;
		}

		Location pos1 = getBlockLocation(x, y);
		Location pos2 = getBlockLocation(targetX, targetY);
		selectedLayer.drag(pos1.x, pos1.y, pos2.x, pos2.y);
	}

	private Location getBlockLocation(int xPos, int yPos) {
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

		BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();

		g2.setColor(new Color(240, 248, 255));
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());

		g2.translate(this.getWidth() / 2.0, this.getHeight() / 2.0);
		g2.scale(camera.zoom, camera.zoom);
		g2.translate(camera.x, camera.y);

		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, map.getWidth() * map.getTileSize(), map.getHeight() * map.getTileSize());

		g2.setColor(Color.LIGHT_GRAY.darker());
		g2.setStroke(new BasicStroke(1 / camera.zoom));

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				g2.drawLine(x * map.getTileSize(), y * map.getTileSize(), map.getWidth() * map.getTileSize(), y * map.getTileSize());
				g2.drawLine(x * map.getTileSize(), y * map.getTileSize(), x * map.getTileSize(), map.getHeight() * map.getTileSize());
			}
		}

		map.getLayers().values().stream()
				.filter(l -> !layerPane.isHidden(l))
				.sorted((o1, o2) -> Float.compare(o2.depth(), o1.depth()))
				.forEach(l -> l.draw(g2));

		if (mouseEntered && TILE_HIGHLIGHT) {
			Location l = getBlockLocation(last_x, last_y);
			g2.setColor(drawMode? Color.GREEN: Color.RED);
			boolean isAreaLayer = layerPane.getSelectedLayer() instanceof AreaLayer;
			if (imageList.getSelectedImageName() != null || isAreaLayer) {
				BufferedImage tex = imageList.getSelectedImageName() == null? null: TextureHandler.getImagePng(imageList.getSelectedImageName());
				if (!(layerPane.getSelectedLayer() instanceof TileLayer))
					g2.drawRect((int) (l.x * map.getTileSize()), (int) (l.y * map.getTileSize()), isAreaLayer? 1: tex.getWidth(), isAreaLayer? 1: tex.getHeight());
				else
					g2.drawRect((int) (l.x) * map.getTileSize(), (int) (l.y) * map.getTileSize(), tex.getWidth(), tex.getHeight());
			}
		}

		g.drawImage(img, 0, 0, null);
	}

	private void sendErrorMessage() {
		camera.addScreenshake(10f);
	}
}