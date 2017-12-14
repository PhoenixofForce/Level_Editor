package window.elements;

import data.Layer;
import data.Loc;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class MapViewer extends JScrollPane{

	private ImageList imageList;
	private LayerPane layerPane;

	private JPanel drawable;

	public MapViewer(ImageList imageList, LayerPane layerPane, int width, int height) {
		this.layerPane = layerPane;
		this.imageList = imageList;

		drawable = new JPanel(){
			@Override
			public void paintComponent(Graphics g) {
				draw(g, drawable.getWidth(), drawable.getHeight());
			}
		};
		drawable.setPreferredSize(new Dimension(width, height));
		this.setViewportView(drawable);

		this.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println(e.getPreciseWheelRotation());
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				event(e.getX(), e.getY());
			}
		});

		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				event(e.getX(), e.getY());
			}
		});
	}

	/**
	 * Is called on MouseDrag and MouseClick
	 * @param x
	 * @param y
	 */
	private void event(int x, int y) {
		Layer selectedLayer = layerPane.getSelectedLayer();
		String selectedTexture = imageList.getSelectedImageName();
		if(selectedLayer == null || selectedTexture == null) return;

		Loc pos = getBlockLocation(x, y);
		selectedLayer.event(selectedTexture, pos.x, pos.y);
	}

	protected Loc getBlockLocation(int x, int y) {
		float res_x = x + this.getHorizontalScrollBar().getValue();
		float res_y = y + this.getVerticalScrollBar().getValue();

		return new Loc(res_x/8.0f, res_y/8.0f);
	}

	/**
	 * Used to draw on the jpanel
	 * @param g graphics from the panel
	 * @param width width from the panel
	 * @param heigth height from the panel
	 */
	public abstract void draw(Graphics g, int width, int heigth);

}
