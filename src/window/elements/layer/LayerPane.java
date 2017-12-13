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
	private DefaultListModel listModel;
	private LayerControll lc;

	private Map<String, Layer> layers;

	public LayerPane(Window w) {
		layers = new HashMap<>();

		this.setLayout(new BorderLayout());

		listModel = new DefaultListModel();
		jList = new JList<>(listModel);

		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);

		add("Background", true, 1.0f);
		add("Tile", false, 0.5f);
		add("Object", true, 0.0f);

		jList.setSelectedIndex(0);

		this.add(jList, BorderLayout.PAGE_START);

		lc = new LayerControll(w, this ) {
			@Override
			public void onRemove() {
				if(jList.getSelectedIndex() < 0) return;
				int sel = jList.getSelectedIndex();
				String name = (String)listModel.get(sel);
				layers.remove(name);
				listModel.remove(sel);
				if(sel == 0) jList.setSelectedIndex(0);
				else jList.setSelectedIndex(sel-1);
			}
		};
		this.add(lc, BorderLayout.PAGE_END);
	}

	public Layer selectedLayer() {
		return layers.get(listModel.get(jList.getSelectedIndex()));
	}

	public void add(String name, boolean type, float depth) {
		if(layers.keySet().contains(name)) {
			add(name + "(" + 1 + ")", type, 1);
			return;
		}
		layers.put(name, type? new FreeLayer(depth): new TileLayer(100, 100, depth));
		listModel.addElement(name);
		jList.setSelectedIndex(listModel.indexOf(name));
	}

	private void add(String name, boolean type, int rek) {
		if(layers.keySet().contains(name)) {
			add(name.split("\\(")[0] + "(" + rek + ")", type, rek+1);
			return;
		}
		layers.put(name, type? new FreeLayer(0): new TileLayer(100, 100, 0));
		listModel.addElement(name);
		jList.setSelectedIndex(listModel.indexOf(name));
	}

	public Map<String, Layer> getLayers() {
		return layers;
	}

	public void reSize(int width, int height) {
		height -= lc.getHeight();
		Dimension d = new Dimension(width/6, height);
		jList.setPreferredSize(d);
		jList.setSize(d);
	}

}
