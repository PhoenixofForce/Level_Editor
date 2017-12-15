package window.elements.layer;

import data.FreeLayer;
import data.Layer;
import data.TileLayer;
import window.Window;

import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;

public class LayerPane extends JPanel {
	private JList<String> jList;
	private DefaultListModel<String> listModel;
	private LayerControl layerControl;

	private Map<String, Layer> layers;

	public LayerPane(Window window) {
		layers = new HashMap<>();

		this.setLayout(new BorderLayout());

		listModel = new DefaultListModel<>();
		jList = new JList<>(listModel);
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);

		addLayer("Background", true, 1.0f);
		addLayer("Tile", false, 0.5f);
		addLayer("Object", true, 0.0f);
		jList.setSelectedIndex(0);

		layerControl = new LayerControl(window, this );

		this.add(jList, BorderLayout.PAGE_START);
		this.add(layerControl, BorderLayout.PAGE_END);
	}

	public Layer getSelectedLayer() {
		if (jList.getSelectedIndex() < 0) return null;
		return layers.get(listModel.get(jList.getSelectedIndex()));
	}

	public void addLayer(String name, boolean type, float depth) {
		int i = 1;
		while(layers.keySet().contains(name)) {
			String newName = String.format("%s(%d)", name, i);
			if (layers.keySet().contains(newName)) {
				i++;
			} else {
				name = newName;
			}
		}
		Layer layer = type? new FreeLayer(depth): new TileLayer(Window.MAP_SIZE, Window.MAP_SIZE, depth);
		layers.put(name, layer);
		int index = 0;
		while (index < listModel.size() && layers.get(listModel.get(index)).depth() > layer.depth()) index++;
		listModel.add(index, name);
		jList.setSelectedIndex(index);
	}

	public void removeLayer() {
		if(jList.getSelectedIndex() < 0) return;
		int sel = jList.getSelectedIndex();
		String name = listModel.get(sel);
		layers.remove(name);
		listModel.remove(sel);
		if(sel == 0) jList.setSelectedIndex(0);
		else jList.setSelectedIndex(sel-1);
	}

	public Map<String, Layer> getLayers() {
		return layers;
	}

	public void reSize(int width, int height) {
		height -= layerControl.getHeight();
		Dimension d = new Dimension(width/6, height);
		jList.setPreferredSize(d);
		jList.setSize(d);
	}

	@Override
	public void disable() {
		layerControl.disable();
		jList.setEnabled(false);
	}

	@Override
	public void enable() {
		layerControl.enable();
		jList.setEnabled(true);
	}
}
