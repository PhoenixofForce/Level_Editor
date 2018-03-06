package window;

import data.GameMap;
import window.elements.ImageList;
import window.elements.MainToolBar;
import window.elements.MapViewer;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * combining all elements and displaying them in a window
 */
public class Window extends JFrame {

	private LayerPane layerPane;			//bar on the right side to controll layers
	private MainToolBar buttons;			//toolbar to save/ open/ export map and import ressources
	private MapViewer mapViewer;			//displays the current map
	private ImageList images;				//image selector and filter
	private GameMap map;					//the map that is currently edited

	public Window() {
		//setting window attributes
		this.setTitle("Level Editor");
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);

		//creating new standart map
		setMap(new GameMap(100,100,8));

		//creating objects and adding them to the window
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

		//starting repaint thread
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

		//resizing components on window resize
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (images != null)
					images.reSize(getContentPane().getWidth(), getContentPane().getHeight() - buttons.getHeight());
				if (images != null && buttons != null)
					layerPane.reSize(getContentPane().getWidth(), getContentPane().getHeight() - buttons.getHeight());
			}
		});
		//setting window size to trigger resize
		this.setSize(this.getWidth() + 1, this.getHeight());
	}

	/** Setting new map and resetting controll objects
	*/
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

	public void open(File f) {
		buttons.open(f);
	}
}
