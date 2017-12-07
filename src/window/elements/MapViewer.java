package window.elements;

import data.Layer;
import data.TileLayer;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapViewer extends JScrollPane{

	private ImageList il;
	private LayerPane lp;

	public MapViewer(ImageList il, LayerPane lp) {
		this.lp = lp;
		this.il = il;

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

	private void event(int x, int y) {
		Layer selectedLayer = lp.selectedLayer();
		String selectedTexture = il.getSelectedImageName();
		if(selectedLayer == null || selectedLayer == null) return;

		if(selectedLayer instanceof TileLayer) {
			TileLayer l = (TileLayer) selectedLayer;
			l.set(selectedTexture, (int)Math.floor(x/8.0f), (int)Math.floor(y/8.0f));
		} else selectedLayer.set(selectedTexture, x/8.0f, y/8.0f);
	}

}
