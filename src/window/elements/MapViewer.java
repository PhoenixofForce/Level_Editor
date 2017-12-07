package window.elements;

import data.FreeLayer;
import data.Layer;
import data.TileLayer;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class MapViewer extends JScrollPane{

	private ImageList il;
	private LayerPane lp;

	private JPanel drawable;

	public MapViewer(ImageList il, LayerPane lp, int width, int height) {
		this.lp = lp;
		this.il = il;

		drawable = new JPanel(){
			@Override
			public void paintComponent(Graphics g) {
				draw(g, drawable.getWidth(), drawable.getHeight());
			}
		};
		drawable.setPreferredSize(new Dimension(width, height));
		this.setViewportView(drawable);
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
		x += this.getHorizontalScrollBar().getValue();
		y += this.getVerticalScrollBar().getValue();

		Layer selectedLayer = lp.selectedLayer();
		String selectedTexture = il.getSelectedImageName();
		if(selectedLayer == null || selectedTexture == null) return;

		if(selectedLayer instanceof TileLayer) {
			TileLayer l = (TileLayer) selectedLayer;
			l.set(selectedTexture, (int)Math.floor(x/8.0f), (int)Math.floor(y/8.0f));
		}

		else if(selectedLayer instanceof  FreeLayer) {
			FreeLayer f = (FreeLayer) selectedLayer;
			f.set(selectedTexture, x/8.0f, y/8.0f);
		}
	}

	/**
	 * Used to draw on the jpanel
	 * @param g graphics from the panel
	 * @param width width from the panel
	 * @param heigth height from the panel
	 */
	public abstract void draw(Graphics g, int width, int heigth);

}
