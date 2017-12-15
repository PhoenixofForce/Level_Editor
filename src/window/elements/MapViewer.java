package window.elements;

import data.Layer;
import data.Location;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MapViewer extends JPanel {

	private ImageList imageList;
	private LayerPane layerPane;
	private int width, height, pixel_per_tile;

	private float dx, dy, zoom;

	private int last_x, last_y;

	public MapViewer(ImageList imageList, LayerPane layerPane, int width, int height, int pixel_per_tile) {
		this.layerPane = layerPane;
		this.imageList = imageList;
		this.width = width;
		this.height = height;
		this.pixel_per_tile = pixel_per_tile;

		centerCamera();

		this.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoom *= Math.pow(1.2, -e.getPreciseWheelRotation());
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
				if (SwingUtilities.isMiddleMouseButton(e)) {
					dx += (e.getX() - last_x) / zoom;
					dy += (e.getY() - last_y) / zoom;
				} else if (SwingUtilities.isLeftMouseButton(e)) {
					drag(last_x, last_y, e.getX(), e.getY());
				} else if (SwingUtilities.isRightMouseButton(e)) {
					set(e.getX(), e.getY());
				}

				last_x = e.getX();
				last_y = e.getY();
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) select(e.getX(), e.getY());
				else if (SwingUtilities.isRightMouseButton(e)) set(e.getX(), e.getY());
				else if (e.getButton() == 4) centerCamera();
			}
		});
	}

	private void centerCamera() {
		this.dx = -width*pixel_per_tile/2;
		this.dy = -height*pixel_per_tile/2;
		this.zoom = 0.5f;
	}

	private void set(int x, int y) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		String selectedTexture = imageList.getSelectedImageName();
		if(selectedLayer == null || selectedTexture == null) return;

		Location pos = getBlockLocation(x, y);
		selectedLayer.set(selectedTexture, pos.x, pos.y);
	}

	private void select(int x, int y) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		String selectedTexture = imageList.getSelectedImageName();
		if(selectedLayer == null || selectedTexture == null) return;

		Location pos = getBlockLocation(x, y);
		selectedLayer.select(pos.x, pos.y);
	}

	private void drag(int x, int y, int targetX, int targetY) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		String selectedTexture = imageList.getSelectedImageName();
		if(selectedLayer == null || selectedTexture == null) return;

		Location pos1 = getBlockLocation(x, y);
		Location pos2 = getBlockLocation(targetX, targetY);
		selectedLayer.drag(pos1.x, pos1.y, pos2.x, pos2.y);
	}

	private Location getBlockLocation(int xPos, int yPos) {
		float x = xPos, y = yPos;
		x -= this.getWidth()/2.0;
		y -= this.getHeight()/2.0;
		x /= zoom;
		y /= zoom;
		x -= dx;
		y -= dy;
		x = (float) Math.floor(x);
		y = (float) Math.floor(y);
		x /= pixel_per_tile;
		y /= pixel_per_tile;

		return new Location(x, y);
	}

	@Override
	protected void paintComponent(Graphics g) {
		BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();

		g2.setColor(new Color(240,248,255));
		g2.fillRect(0,0, img.getWidth(), img.getHeight());

		g2.translate(this.getWidth()/2.0, this.getHeight()/2.0);
		g2.scale(zoom, zoom);
		g2.translate(dx, dy);

		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, width*pixel_per_tile, height*pixel_per_tile);

		layerPane.getLayers().values().stream()
				.sorted((o1, o2) -> Float.compare(o2.depth(), o1.depth()))
				.forEach(l -> l.draw(g2));

		g.drawImage(img, 0, 0, null);

	}

}
