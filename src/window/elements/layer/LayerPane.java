package window.elements.layer;

import data.*;
import data.layer.*;
import window.Window;
import window.commands.LayerAddCommand;
import window.commands.LayerRemoveCommand;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LayerPane extends JPanel {
	
	private final Window window;
	private final JList<String> layerList;
	private final LayerControl layerControl;
	private DefaultListModel<String> layerListData;

	private GameMap map;
	private Map<Layer, Boolean> hidden;

	public LayerPane(Window window, GameMap newMap) {
		this.map = newMap;
		this.window = window;

		this.setLayout(new BorderLayout());

		hidden = new HashMap<>();

		layerListData = new DefaultListModel<>();
		layerList = new JList<>(layerListData);
		layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layerList.setLayoutOrientation(JList.VERTICAL);

		//TODO: Use Spinner?
		//change layer names depending on the layer being hidden
		layerList.setCellRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				String layerName = (String) value;
				boolean hidden = isHidden(map.getLayer(layerName));

				if (hidden) setForeground(Color.RED);

				setText((hidden ? "✕ " : "✓ ") + getText());
				setText(getText() + " " + "(" + map.getLayer(layerName).depth() + ")");

				return c;
			}
		});

		layerControl = new LayerControl(window, this);

		this.add(layerList, BorderLayout.PAGE_START);
		this.add(layerControl, BorderLayout.PAGE_END);

		updateGameMap(newMap, true);
	}

	public Layer getSelectedLayer() {
		if (layerList.getSelectedIndex() < 0) return null;
		return map.getLayer(layerListData.get(layerList.getSelectedIndex()));
	}

	public int getSelectedLayerIndex() {
		return layerList.getSelectedIndex();
	}

	public Layer getLayer(int i) {
		return map.getLayer(layerListData.get(i));
	}

	public void createLayer(String name, int type, float depth) {
		//renames layer if name already exist
		int i = 1;
		while (map.getLayers().containsKey(name)) {
			String newName = String.format("%s(%d)", name, i);
			if (map.getLayers().containsKey(newName)) {
				i++;
			} else {
				name = newName;
			}
		}

		Layer layer;
		switch (type) {
			case 2:
				layer = new FreeLayer(depth, map.getWidth(), map.getHeight(), map.getTileSize());
				break;
			case 1:
				layer = new AreaLayer(depth, map.getWidth(), map.getHeight(), map.getTileSize());
				break;
			case 0:
				layer = new TileLayer(window, depth, map.getWidth(), map.getHeight(), map.getTileSize());
				break;
			default:
				return;
		}

		new LayerAddCommand(this, map, layer, name).execute(window.getMapViewer().getCommandHistory());
	}

	public void addLayer(String name, Layer layer) {
		int index = 0;
		while (index < layerListData.size() && map.getLayer(layerListData.get(index)).depth() > layer.depth()) index++;
		layerListData.add(index, name);
		layerList.setSelectedIndex(index);
	}

	public void removeLayer() {
		if (layerList.getSelectedIndex() < 0) return;
		int sel = layerList.getSelectedIndex();
		String name = layerListData.get(sel);

		new LayerRemoveCommand(this, map, name).execute(window.getMapViewer().getCommandHistory());
	}

	public void removeLayer(String name) {
		for(int i = 0; i < layerListData.getSize(); i++) {
			if(layerListData.get(i).equals(name)) {
				layerListData.remove(i);
				if (i == 0) layerList.setSelectedIndex(0);
				else layerList.setSelectedIndex(i - 1);

				return;
			}
		}
	}

	public void updateGameMap(GameMap map, boolean isNewMap) {
		Map<Layer, Boolean> newHidden = new HashMap<>();
		for(String s: map.getLayers().keySet()) {
			Layer l = this.map.getLayer(s);
			newHidden.put(map.getLayer(s), hidden.getOrDefault(l, false));
		}

		this.map = map;

		String selected = layerList.getSelectedIndex() >= 0? layerListData.get(layerList.getSelectedIndex()): null;

		//resets the listModel and list
		layerListData = new DefaultListModel<>();
		for (String name : map.getLayers().keySet()) {
			Layer layer = map.getLayer(name);

			int index = 0;
			while (index < layerListData.size() && map.getLayer(layerListData.get(index)).depth() > layer.depth()) index++;
			layerListData.add(index, name);
		}

		layerList.setModel(layerListData);
		layerList.setSelectedIndex(0);
		if(!isNewMap) {
			for(int i = 0; i < layerListData.size(); i++) {
				if(layerListData.get(i).equals(selected)) {
					layerList.setSelectedIndex(i);
					break;
				}
			}
		}

		hidden = newHidden;
	}

	public void reSize(int width, int height) {
		height -= layerControl.getHeight();
		Dimension d = new Dimension(width / 6, height);
		layerList.setPreferredSize(d);
		layerList.setSize(d);
	}

	public boolean isHidden(Layer layer) {
		if (hidden.containsKey(layer)) return hidden.get(layer);
		return false;
	}

	public void toggleHidden() {
		if (map == null || layerList.getSelectedIndex() < 0) return;

		Layer layer = map.getLayer(layerListData.get(layerList.getSelectedIndex()));

		hidden.put(layer, !isHidden(layer));

		layerList.updateUI();
	}
}
