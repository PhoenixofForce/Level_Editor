package window;

import data.GameMap;
import window.elements.ImageList;
import window.elements.MainToolBar;
import window.elements.MapViewer;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Window extends JFrame {

	private LayerPane layerPane;
	private MainToolBar buttons;
	private MapViewer mapViewer;
	private ImageList images;
	private GameMap map;

	public Window() {
		this.setTitle("POF - Level Editor");
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);

		setMap(new GameMap(100,100,8));

		images = new ImageList(this);
		this.add(images, BorderLayout.LINE_END);
		images.reSize(getContentPane().getWidth(), getContentPane().getHeight());

		buttons = new MainToolBar(this, images);
		this.add(buttons, BorderLayout.PAGE_START);

		layerPane = new LayerPane(this, map);
		this.add(layerPane, BorderLayout.LINE_START);
		layerPane.reSize(getContentPane().getWidth(), getContentPane().getHeight());

		mapViewer = new MapViewer(images, layerPane, map);
		this.add(mapViewer, BorderLayout.CENTER);

		new Thread(() -> {
			long lastTime;
			while (true) {
				lastTime = System.currentTimeMillis();
				if (mapViewer != null) mapViewer.repaint();
				try {
					Thread.sleep(Math.max(0, 1000 / 60 - (System.currentTimeMillis() - lastTime)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		this.addWindowStateListener(e -> {
				layerPane.updateUI();
		});

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (images != null)
					images.reSize(getContentPane().getWidth(), getContentPane().getHeight() - buttons.getHeight());
				if (images != null && buttons != null)
					layerPane.reSize(getContentPane().getWidth(), getContentPane().getHeight() - buttons.getHeight());
			}
		});
		this.setSize(this.getWidth() + 1, this.getHeight());
	}

	public void setMap(GameMap map) {
		this.map = map;
		if(buttons != null) buttons.reset();

		if (mapViewer != null) mapViewer.setGameMap(map);
		if (images != null)    images.getModifier().setTagObject(null);
		if (layerPane != null) layerPane.updateGameMap(map);
	}

	public GameMap getMap() {
		return map;
	}

}
