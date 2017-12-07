package window.elements;

import data.FreeLayer;
import data.Layer;
import data.TileLayer;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapViewer extends JScrollPane{

	private ImageList il;
	private LayerPane lp;

	private JPanel drawable;

	public MapViewer(ImageList il, LayerPane lp, int width, int height) {
		this.lp = lp;
		this.il = il;

		drawable = new JPanel();
		drawable.setSize(new Dimension(width, height));
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

	public JPanel getDrawable() {
		return drawable;
	}

	private void event(int x, int y) {
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

}
