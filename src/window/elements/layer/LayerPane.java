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

/**
 * jcomponent to select layer
 * contains also layerControl
 */
public class LayerPane extends JPanel {
	private final JList<String> jList;					//List of the layer names
	private DefaultListModel<String> listModel;		//listModel used for setting/getting selected layers
	private final LayerControl layerControl;				//layer control

	private GameMap map;							//the game map
	private Map<Layer, Boolean> hidden;				//map stores the data about the hidden state of each layer
	private final Window window;

	public LayerPane(Window window, GameMap newMap) {
		this.map = newMap;
		this.window = window;

		this.setLayout(new BorderLayout());

		hidden = new HashMap<>();

		listModel = new DefaultListModel<>();
		jList = new JList<>(listModel);
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);

		//change layer names depending on the layer being hidden
		//TODO: Use Spinner?
		jList.setCellRenderer(new DefaultListCellRenderer() {

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

		this.add(jList, BorderLayout.PAGE_START);
		this.add(layerControl, BorderLayout.PAGE_END);

		updateGameMap(newMap, true);
	}

	public Layer getSelectedLayer() {
		if (jList.getSelectedIndex() < 0) return null;
		return map.getLayer(listModel.get(jList.getSelectedIndex()));
	}

	public int getSelectedLayerIndex() {
		return jList.getSelectedIndex();
	}

	public Layer getLayer(int i) {
		return map.getLayer(listModel.get(i));
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

		//Adds layer to map and to listModel at the position fitting to its depth
		new LayerAddCommand(this, map, layer, name).execute(window.getMapViewer().getCommandHistory());
	}

	public void addLayer(String name, Layer layer) {
		int index = 0;
		while (index < listModel.size() && map.getLayer(listModel.get(index)).depth() > layer.depth()) index++;
		listModel.add(index, name);
		jList.setSelectedIndex(index);
	}

	/**
	 * removes the selected layer
	 */
	public void removeLayer() {
		if (jList.getSelectedIndex() < 0) return;
		int sel = jList.getSelectedIndex();
		String name = listModel.get(sel);

		new LayerRemoveCommand(this, map, name).execute(window.getMapViewer().getCommandHistory());
	}

	public void removeLayer(String name) {
		for(int i = 0; i < listModel.getSize(); i++) {
			if(listModel.get(i).equals(name)) {
				listModel.remove(i);
				if (i == 0) jList.setSelectedIndex(0);
				else jList.setSelectedIndex(i - 1);

				return;
			}
		}
	}

	/**
	 * sets new map
	 * @param map map to be updated
	 */
	public void updateGameMap(GameMap map, boolean isNewMap) {
		Map<Layer, Boolean> newHidden = new HashMap<>();
		for(String s: map.getLayers().keySet()) {
			Layer l = this.map.getLayer(s);
			newHidden.put(map.getLayer(s), hidden.getOrDefault(l, false));
		}

		this.map = map;

		String selected = jList.getSelectedIndex() >= 0? listModel.get(jList.getSelectedIndex()): null;
		//resets the listModel and list
		listModel = new DefaultListModel<>();
		for (String name : map.getLayers().keySet()) {
			Layer layer = map.getLayer(name);

			int index = 0;
			while (index < listModel.size() && map.getLayer(listModel.get(index)).depth() > layer.depth()) index++;
			listModel.add(index, name);
		}

		jList.setModel(listModel);
		jList.setSelectedIndex(0);
		if(!isNewMap) {
			for(int i = 0; i < listModel.size(); i++) {
				if(listModel.get(i).equals(selected)) {
					jList.setSelectedIndex(i);
					break;
				}
			}
		}

		hidden = newHidden;
	}

	public void reSize(int width, int height) {
		height -= layerControl.getHeight();
		Dimension d = new Dimension(width / 6, height);
		jList.setPreferredSize(d);
		jList.setSize(d);
	}

	public boolean isHidden(Layer layer) {
		if (hidden.containsKey(layer)) return hidden.get(layer);
		return false;
	}

	public void toggleHidden() {
		if (map == null || jList.getSelectedIndex() < 0) return;

		Layer layer = map.getLayer(listModel.get(jList.getSelectedIndex()));

		hidden.put(layer, !isHidden(layer));

		jList.updateUI();
	}
}
