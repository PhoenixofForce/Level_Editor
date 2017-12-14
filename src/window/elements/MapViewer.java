package window.elements;

import data.Layer;
import data.Location;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

public class MapViewer extends JPanel {

	private ImageList imageList;
	private LayerPane layerPane;
	private int width;
	private int height;

	private float dx, dy, zoom;

	private int last_x, last_y;

	public MapViewer(ImageList imageList, LayerPane layerPane, int width, int height) {
		this.layerPane = layerPane;
		this.imageList = imageList;
		this.width = width;
		this.height = height;

		this.dx = -width/2;
		this.dy = -height/2;
		this.zoom = 1f;

		this.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoom *= Math.pow(1.2, -e.getPreciseWheelRotation());
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) event(e.getX(), e.getY());
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
				if (SwingUtilities.isRightMouseButton(e)) {
					dx += (e.getX() - last_x) / zoom;
					dy += (e.getY() - last_y) / zoom;
				} else if (SwingUtilities.isLeftMouseButton(e)) {
					event(e.getX(), e.getY());
				}

				last_x = e.getX();
				last_y = e.getY();
			}
		});
	}

	private void event(int x, int y) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		String selectedTexture = imageList.getSelectedImageName();
		if(selectedLayer == null || selectedTexture == null) return;

		Location pos = getBlockLocation(x, y);
		pos.x = (float) Math.floor(pos.x * 8)/8;
		pos.y = (float) Math.floor(pos.y * 8)/8;
		selectedLayer.event(selectedTexture, pos.x, pos.y);
	}

	protected Location getBlockLocation(int xPos, int yPos) {
		float x = xPos, y = yPos;
		x -= this.getWidth()/2.0;
		y -= this.getHeight()/2.0;
		x /= zoom;
		y /= zoom;
		x -= dx;
		y -= dy;
		x /= 8.0f;
		y /= 8.0f;

		return new Location(x, y);
	}

	@Override
	protected void paintComponent(Graphics g) {
		BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();

		g2.setColor(Color.WHITE);
		g2.fillRect(0,0, img.getWidth(), img.getHeight());

		g2.translate(this.getWidth()/2.0, this.getHeight()/2.0);

		g2.scale(zoom, zoom);

		g2.translate(dx, dy);

		g2.setColor(Color.GRAY);
		g2.fillRect(0, 0, width, height);

		layerPane.getLayers().values().stream()
				.sorted((o1, o2) -> Float.compare(o2.depth(), o1.depth()))
				.forEach(l -> l.draw(g2));

		g.drawImage(img, 0, 0, null);
	}

}
