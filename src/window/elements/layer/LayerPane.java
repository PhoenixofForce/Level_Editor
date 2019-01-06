package window.elements.layer;

import data.*;
import data.layer.*;
import window.Window;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * jcomponent to select layer
 * contains also layerControl
 */
public class LayerPane extends JPanel {
	private JList<String> jList;					//List of the layer names
	private DefaultListModel<String> listModel;		//listModel used for setting/getting selected layers
	private LayerControl layerControl;				//layer controll

	private GameMap map;							//the game map
	private Map<Layer, Boolean> hidden;				//map stores the data about the hidden state of each layer
	private Window window;

	public LayerPane(Window window, GameMap newMap) {
		this.map = newMap;
		this.window = window;

		this.setLayout(new BorderLayout());

		hidden = new HashMap<>();

		listModel = new DefaultListModel<>();
		jList = new JList<>(listModel);
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);

		//change layer names depending on the layer beeing hidden
		jList.setCellRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				String layerName = (String) value;
				boolean hidden = isHidden(map.getLayer(layerName));

				if (hidden) setForeground(Color.RED);

				setText((hidden ? "✕" : "✓") + getText());
				setText(getText() + " " + "(" + map.getLayer(layerName).depth() + ")");

				return c;
			}
		});

		layerControl = new LayerControl(window, this);

		this.add(jList, BorderLayout.PAGE_START);
		this.add(layerControl, BorderLayout.PAGE_END);

		updateGameMap(newMap, true);
	}

	/**
	 * @return the selected layer
	 */
	public Layer getSelectedLayer() {
		if (jList.getSelectedIndex() < 0) return null;
		return map.getLayer(listModel.get(jList.getSelectedIndex()));
	}

	/**
	 * adds a layer to the map
	 * @param name name of the layer
	 * @param type type of the layer 0-TileLayer, 1 - AreaLayer, 2-FreeLayer
	 * @param depth drawing depth
	 */
	public void addLayer(String name, int type, float depth) {
		//renames layer if name already exist
		int i = 1;
		while (map.getLayers().keySet().contains(name)) {
			String newName = String.format("%s(%d)", name, i);
			if (map.getLayers().keySet().contains(newName)) {
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
				layer = new TileLayer(window.getMap(), depth, map.getWidth(), map.getHeight(), map.getTileSize());
				break;
			default:
				return;
		}

		//Adds layer to map and to listModel at the position fitting to its depth
		map.addLayer(name, layer);
		int index = 0;
		while (index < listModel.size() && map.getLayer(listModel.get(index)).depth() > layer.depth()) index++;
		listModel.add(index, name);
		jList.setSelectedIndex(index);
	}

	/**
	 * removes a layer
	 */
	public void removeLayer() {
		if (jList.getSelectedIndex() < 0) return;
		int sel = jList.getSelectedIndex();
		String name = listModel.get(sel);
		map.removeLayer(name);
		listModel.remove(sel);
		if (sel == 0) jList.setSelectedIndex(0);
		else jList.setSelectedIndex(sel - 1);
	}

	/**
	 * sets new map
	 * @param map map to be updated
	 */
	public void updateGameMap(GameMap map, boolean isNewMap) {
		this.map = map;

		int selectedIndex = jList.getSelectedIndex();
		//resets the listModel and list
		listModel = new DefaultListModel<>();
		for (String name : map.getLayers().keySet()) {
			Layer layer = map.getLayer(name);

			int index = 0;
			while (index < listModel.size() && map.getLayer(listModel.get(index)).depth() > layer.depth()) index++;
			listModel.add(index, name);
		}

		jList.setModel(listModel);
		if(isNewMap) jList.setSelectedIndex(0);
		else jList.setSelectedIndex(selectedIndex);
	}

	/**
	 * resizes this component depending on window width and height
	 * @param width
	 * @param height
	 */
	public void reSize(int width, int height) {
		height -= layerControl.getHeight();
		Dimension d = new Dimension(width / 6, height);
		jList.setPreferredSize(d);
		jList.setSize(d);
	}

	/**
	 * @param layer which should be checked
	 * @return true if a layer is hidden
	 */
	public boolean isHidden(Layer layer) {
		if (hidden.containsKey(layer)) return hidden.get(layer);
		return false;
	}

	/**
	 * reverses the hidden value of the selected layer
	 */
	public void toggleHidden() {
		if (map == null || jList.getSelectedIndex() < 0) return;

		Layer layer = map.getLayer(listModel.get(jList.getSelectedIndex()));

		hidden.put(layer, !isHidden(layer));

		jList.updateUI();
	}
}
