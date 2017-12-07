package window;

import data.*;
import window.elements.ImageList;
import window.elements.MainToolBar;
import window.elements.MapViewer;
import window.elements.layer.LayerPane;

import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class Window extends JFrame{

	private LayerPane layers;
	private MainToolBar buttons;
	private MapViewer mapViewer;
	private ImageList images;

	public Window() {
		this.setTitle("POF - Level Editor");
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);

		layers = new LayerPane();
		this.add(layers, BorderLayout.LINE_START);
		images = new ImageList();
		this.add(images, BorderLayout.LINE_END);
		buttons = new MainToolBar(images);
		this.add(buttons, BorderLayout.PAGE_START);
		images.reSize(getContentPane().getWidth(), getContentPane().getHeight());
		layers.reSize(getContentPane().getWidth(), getContentPane().getHeight());
		mapViewer = new MapViewer(images, layers, 800, 800);
		this.add(mapViewer, BorderLayout.CENTER);

		this.pack();

		new Thread(()->{
			while (true) {
				draw();
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				images.reSize(getContentPane().getWidth(), getContentPane().getHeight() - buttons.getHeight());
				layers.reSize(getContentPane().getWidth(), getContentPane().getHeight() - buttons.getHeight());
			}
		});
	}

	private void draw() {
		JPanel canvas = mapViewer.getDrawable();
		Graphics g2 = canvas.getGraphics();
		BufferedImage img = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();

		for(String s: layers.getLayers().keySet()) {
			Layer l = layers.getLayers().get(s);

			if(l instanceof TileLayer) {
				TileLayer t = (TileLayer) l;
				String[][] names = t.getTileNames();
				for(int x = 0; x < names[0].length; x++) {
					for(int y = 0; y < names.length; y++) {
						if(names[y][x] == null) continue;
						g.drawImage(TextureHandler.getImagePng(names[y][x]), x * 8, y * 8, null);
					}
				}
			} else if(l instanceof FreeLayer) {
				FreeLayer f = (FreeLayer) l;
				Map<Loc, String> names = f.getImages();
				for(Loc loc: names.keySet()) {
					String name = names.get(loc);
					g.drawImage(TextureHandler.getImagePng(name), (int)loc.x*8, (int)loc.y*8, null);
				}
			}

		}
		g2.drawImage(img, 0, 0, null);
	}

}
