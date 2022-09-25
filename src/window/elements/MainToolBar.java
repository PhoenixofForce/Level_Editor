package window.elements;

import window.Tools;
import window.Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainToolBar extends JToolBar {

	private final JComboBox<String> toggleAutoTile;
	private final List<JButton> chooser;

	public MainToolBar(Window w, ImageList imageList) {
		this.setFloatable(false);
		this.setRollover(true);

		JButton editMapTags = new JButton("Edit Map Tags");
		editMapTags.addActionListener(e -> imageList.getModifier().setTagObject(w.getMap()));
		this.add(editMapTags);

		toggleAutoTile = new JComboBox<>();
		toggleAutoTile.setPreferredSize(new Dimension(100, -1));
		toggleAutoTile.setMaximumSize(new Dimension(100, 100));

		toggleAutoTile.addItem("No Autotile");
		toggleAutoTile.addItem("4Bit Autotile");
		toggleAutoTile.addItem("8Bit Autotile");

		toggleAutoTile.setSelectedIndex(1);
		this.add(toggleAutoTile);

		this.addSeparator();

		chooser = new ArrayList<>();
		for(Tools t: Tools.values()) {
			JButton button = new JButton(t.toString().charAt(0) + t.toString().substring(1).toLowerCase());
			button.addActionListener(e -> w.getMapViewer().setTool(t));
			this.add(button);
			chooser.add(button);
		}
	}

	public void mapUpdate(Window w, boolean newMap) {
		toggleAutoTile.setSelectedIndex(1);
	}

	protected void update(Tools t) {
		for(int i = 0; i < chooser.size(); i++) {
			chooser.get(i).setEnabled(i != t.getIndex());
		}
	}

	public int getAutoTile() {
		return toggleAutoTile.getSelectedIndex();
	}
}