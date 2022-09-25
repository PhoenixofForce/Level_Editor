package window;

import data.GameMap;
import window.elements.ImageList;
import window.elements.MainToolBar;
import window.elements.MapViewer;
import window.elements.MenuBar;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * combining all elements and displaying them in a window
 */
public class Window extends JFrame {

	public static Window INSTANCE;

	private final LayerPane layerPane;			//bar on the left side to control layers
	private final MainToolBar buttons;			//toolbar to save/ open/ export map and import resources
	private final MapViewer mapViewer;			//displays the current map
	private final ImageList images;				//image selector and filter
	private GameMap map;					//the map that is currently edited
	private final MenuBar menu;

	public Window() {
		INSTANCE = this;

		//setting window attributes
		this.setTitle("Level Editor - Untitled");
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		//creating new default map
		setMap(new GameMap(this, 100,100,16), true);

		//creating objects and adding them to the window
		images = new ImageList(this);
		this.add(images, BorderLayout.LINE_END);
		images.reSize(getContentPane().getWidth(), getContentPane().getHeight());
		images.setFocusable(false);

		buttons = new MainToolBar(this, images);
		this.add(buttons, BorderLayout.PAGE_START);

		this.menu = new MenuBar(this, images);
		this.setJMenuBar(menu);

		layerPane = new LayerPane(this, map);
		this.add(layerPane, BorderLayout.LINE_START);
		layerPane.reSize(getContentPane().getWidth(), getContentPane().getHeight());

		mapViewer = new MapViewer(this, menu, buttons, images, layerPane, map);
		this.add(mapViewer, BorderLayout.CENTER);
		mapViewer.setFocusable(true);

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

		this.addWindowStateListener(e -> layerPane.updateUI());

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

		this.setVisible(true);
		//setting window size to trigger resize
		this.setSize(this.getWidth() + 1, this.getHeight());
	}

	/** Setting new map and resetting control objects
	 */
	public void setMap(GameMap map, boolean isNewMap) {
		this.map = map;
		if(buttons != null) buttons.mapUpdate(this, isNewMap);
		if(menu != null && isNewMap) menu.reset();
		if (mapViewer != null) mapViewer.setGameMap(map, isNewMap);
		if (images != null && isNewMap) images.getModifier().setTagObject(null);
		if (layerPane != null) layerPane.updateGameMap(map, isNewMap);
	}

	public GameMap getMap() {
		return map;
	}

	public void open(File f) {
		menu.open(f, true);
	}

	public MapViewer getMapViewer() {
		return mapViewer;
	}

	public int getAutoTile() {
		return buttons.getAutoTile();
	}

	public MenuBar getMyMenuBar() {
		return menu;
	}

	public ImageList getImageList() {
		return images;
	}
}
