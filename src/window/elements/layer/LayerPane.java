package window.elements.layer;

import data.FreeLayer;
import data.GameMap;
import data.Layer;
import data.TileLayer;
import window.Window;

import javax.swing.*;
import java.awt.*;

public class LayerPane extends JPanel {
	private JList<String> jList;
	private DefaultListModel<String> listModel;
	private LayerControl layerControl;

	private GameMap map;

	public LayerPane(Window window, GameMap map) {
		this.map = map;

		this.setLayout(new BorderLayout());

		listModel = new DefaultListModel<>();
		jList = new JList<>(listModel);
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);

		layerControl = new LayerControl(window, this);

		this.add(jList, BorderLayout.PAGE_START);
		this.add(layerControl, BorderLayout.PAGE_END);

		updateGameMap(map);
	}

	public Layer getSelectedLayer() {
		if (jList.getSelectedIndex() < 0) return null;
		return map.getLayer(listModel.get(jList.getSelectedIndex()));
	}

	public void addLayer(String name, boolean type, float depth) {
		int i = 1;
		while (map.getLayers().keySet().contains(name)) {
			String newName = String.format("%s(%d)", name, i);
			if (map.getLayers().keySet().contains(newName)) {
				i++;
			} else {
				name = newName;
			}
		}
		Layer layer = type ? new FreeLayer(depth, map.getWidth(), map.getHeight(), map.getTileSize()) : new TileLayer(depth, map.getWidth(), map.getHeight(), map.getTileSize());
		map.addLayer(name, layer);
		int index = 0;
		while (index < listModel.size() && map.getLayer(listModel.get(index)).depth() > layer.depth()) index++;
		listModel.add(index, name);
		jList.setSelectedIndex(index);
	}

	public void removeLayer() {
		if (jList.getSelectedIndex() < 0) return;
		int sel = jList.getSelectedIndex();
		String name = listModel.get(sel);
		map.removeLayer(name);
		listModel.remove(sel);
		if (sel == 0) jList.setSelectedIndex(0);
		else jList.setSelectedIndex(sel - 1);
	}

	public void updateGameMap(GameMap map) {
		this.map = map;

		listModel.clear();
		for (String name : map.getLayers().keySet()) {
			Layer layer = map.getLayer(name);

			int index = 0;
			while (index < listModel.size() && map.getLayer(listModel.get(index)).depth() > layer.depth()) index++;
			listModel.add(index, name);
		}

		jList.setSelectedIndex(0);
	}

	public void reSize(int width, int height) {
		height -= layerControl.getHeight();
		Dimension d = new Dimension(width / 6, height);
		jList.setPreferredSize(d);
		jList.setSize(d);
	}
}
