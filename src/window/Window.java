package window;

import data.*;
import window.elements.ImageList;
import window.elements.MainToolBar;
import window.elements.MapViewer;
import window.elements.layer.LayerPane;

import java.util.List;

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
		mapViewer = new MapViewer(images, layers, 800, 800){

			//TODO: draw by depth
			@Override
			public void draw(Graphics g, int width, int height) {
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics g2 = img.getGraphics();

				g2.setColor(Color.WHITE);
				g2.fillRect(0, 0, 800, 800);

				for(String s: layers.getLayers().keySet()) {
					Layer l = layers.getLayers().get(s);

					if(l instanceof TileLayer) {
						TileLayer t = (TileLayer) l;
						String[][] names = t.getTileNames();
						for(int x = 0; x < names[0].length; x++) {
							for(int y = 0; y < names.length; y++) {
								if(names[y][x] == null) continue;
								g2.drawImage(TextureHandler.getImagePng(names[y][x]), x * 8, y * 8, null);
							}
						}
					} else if(l instanceof FreeLayer) {
						FreeLayer f = (FreeLayer) l;
						List<GO> gos = f.getImages();
						for(int i = 0; i < gos.size(); i++) {
							Loc loc = gos.get(i).loc;
							String name = gos.get(i).name;
							g2.drawImage(TextureHandler.getImagePng(name), (int)loc.x*8, (int)loc.y*8, null);
						}
					}
				}
				g.drawImage(img, 0, 0, null);
			}
		};
		this.add(mapViewer, BorderLayout.CENTER);

		this.pack();

		new Thread(()->{
			long lastTime;
			while (true) {
				lastTime = System.currentTimeMillis();
				mapViewer.repaint();
				try {
					Thread.sleep(1000/60 - (System.currentTimeMillis()-lastTime));
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

}
