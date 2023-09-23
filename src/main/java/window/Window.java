package window;

import data.GameMap;
import data.layer.Layer;
import lombok.Getter;
import window.elements.*;
import window.elements.MenuBar;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

@Getter
public class Window extends JFrame {

	public static Window INSTANCE;

	/*
		Every component that needs to be exposed to multiple other components should be declared here in the window class.
		LayerControl as an example only interacts with LayerPane, so LayerPane has the control over LayerControl
	 */
	private final ErrorPanel errorPanel;
	private final LayerPane layerPane;
	private final MainToolBar toolbar;
	private final MapViewer mapViewer;
	private final ImageList imageDisplay;
	private final Modifier tagModifier;
	private final MenuBar menu;

	private GameMap map;

	public Window() {
		INSTANCE = this;

		//setting window attributes
		this.setTitle("Level Editor - Untitled");
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setMap(new GameMap(this, 100,100,16), true);

		tagModifier = new Modifier(this);

		imageDisplay = new ImageList(tagModifier);
		this.add(imageDisplay, BorderLayout.LINE_END);
		imageDisplay.reSize(getContentPane().getWidth(), getContentPane().getHeight());
		imageDisplay.setFocusable(false);

		toolbar = new MainToolBar(this);
		this.add(toolbar, BorderLayout.PAGE_START);

		this.menu = new MenuBar(this, imageDisplay);
		this.setJMenuBar(menu);

		layerPane = new LayerPane(this, map);
		this.add(layerPane, BorderLayout.LINE_START);
		layerPane.reSize(getContentPane().getWidth(), getContentPane().getHeight());

		JPanel centerPane = new JPanel();
		centerPane.setLayout(new BorderLayout());

		mapViewer = new MapViewer(this, map);
		centerPane.add(mapViewer, BorderLayout.CENTER);
		mapViewer.setFocusable(true);

		errorPanel = new ErrorPanel();
		centerPane.add(errorPanel, BorderLayout.NORTH);

		this.add(centerPane, BorderLayout.CENTER);

		//starting repaint thread
		new Thread(() -> {
			long lastTime = 0;
			while (true) {
				errorPanel.update(System.currentTimeMillis() - lastTime);
				mapViewer.repaint();
				lastTime = System.currentTimeMillis();

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
				int width = getContentPane().getWidth();
				int height = getContentPane().getHeight();
				imageDisplay.reSize(width, height - toolbar.getHeight());
				layerPane.reSize(width, height - toolbar.getHeight());
			}
		});

		this.setVisible(true);
		//setting window size to trigger resize
		this.setSize(this.getWidth() + 1, this.getHeight());
	}

	public void setMap(GameMap map, boolean isNewMap) {
		this.map = map;
		if(toolbar != null) toolbar.mapUpdate(this, isNewMap);
		if(menu != null && isNewMap) menu.reset();
		if (mapViewer != null) mapViewer.setGameMap(map, isNewMap);
		if (imageDisplay != null && isNewMap) tagModifier.setTagObject(null);
		if (layerPane != null) layerPane.updateGameMap(map, isNewMap);
	}

	// Forwarding methods

	public void open(File f) {
		menu.open(f, true);
	}

	public void save() {
		menu.save();
	}

	public int getAutoTile() {
		return toolbar.getAutoTile();
	}

	public int getTileSize() {
		return map.getTileSize();
	}

	public boolean isLayerHidden(Layer layer) {
		return layerPane.isHidden(layer);
	}

	public Layer getSelectedLayer() {
		return layerPane.getSelectedLayer();
	}

	public String getSelectedTexture() {
		return imageDisplay.getSelectedImageName();
	}

	public void setError(String error, long displayTime) {
		errorPanel.setError(error, displayTime);
	}

}
