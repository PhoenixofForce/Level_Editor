package window.elements;

import window.Tools;
import window.Window;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainToolBar extends JToolBar {

	private JButton editMapTags, toggleAutoTile;
	private List<JButton> chooser;

	public MainToolBar(Window w, ImageList imageList) {
		this.setFloatable(false);
		this.setRollover(true);

		editMapTags = new JButton("Edit Map Tags");
		editMapTags.addActionListener(e -> {
			imageList.getModifier().setTagObject(w.getMap());
		});
		this.add(editMapTags);

		toggleAutoTile = new JButton("Disable AutoTile");
		toggleAutoTile.addActionListener(e -> {
			w.getMap().setAutoTile(!w.getMap().getAutoTile());
			if(w.getMap().getAutoTile()) toggleAutoTile.setText("Disable AutoTile");
			else toggleAutoTile.setText("Enable AutoTile");
		});
		this.add(toggleAutoTile);

		this.addSeparator();

		chooser = new ArrayList<>();
		for(Tools t: Tools.values()) {
			JButton button = new JButton(t.toString().substring(0, 1) + t.toString().substring(1).toLowerCase());
			button.addActionListener(e -> w.getMapViewer().setTool(t));
			this.add(button);
			chooser.add(button);
		}
	}

	protected void update(Tools t) {
		for(int i = 0; i < chooser.size(); i++) {
			chooser.get(i).setEnabled(i != t.getIndex());
		}
	}
}
