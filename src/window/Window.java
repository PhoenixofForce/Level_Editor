package window;

import window.elements.ImageList;
import window.elements.MainToolBar;
import window.elements.MapViewer;
import window.elements.layer.LayerPane;

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

		layers = new LayerPane(this);
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
			long lastTime;
			while (true) {
				lastTime = System.currentTimeMillis();
				mapViewer.repaint();
				try {
					Thread.sleep(Math.max(0,1000/60 - (System.currentTimeMillis()-lastTime)));
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
		this.setSize(this.getWidth()+1, this.getHeight());
	}

}
