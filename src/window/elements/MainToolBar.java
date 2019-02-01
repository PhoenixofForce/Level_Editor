package window.elements;

import window.Tools;
import window.Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainToolBar extends JToolBar {

	private JButton editMapTags;
	private JComboBox<String> toggleAutoTile;
	private List<JButton> chooser;

	public MainToolBar(Window w, ImageList imageList) {
		this.setFloatable(false);
		this.setRollover(true);

		editMapTags = new JButton("Edit Map Tags");
		editMapTags.addActionListener(e -> {
			imageList.getModifier().setTagObject(w.getMap());
		});
		this.add(editMapTags);

		toggleAutoTile = new JComboBox();
		toggleAutoTile.setPreferredSize(new Dimension(100, -1));
		toggleAutoTile.setMaximumSize(new Dimension(100, 100));

		toggleAutoTile.addItem("No Autotile");
		toggleAutoTile.addItem("4Bit Autotile");
		toggleAutoTile.addItem("8Bit Autotile");
		toggleAutoTile.addActionListener(e -> {
			w.getMap().setAutoTile((int)Math.pow(2, 1+toggleAutoTile.getSelectedIndex()));

		});
		toggleAutoTile.setSelectedIndex(binlog(w.getMap().getAutoTile())-1);

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

	public void mapUpdate(Window w) {
		toggleAutoTile.setSelectedIndex(binlog(w.getMap().getAutoTile())-1);
	}

	protected void update(Tools t) {
		for(int i = 0; i < chooser.size(); i++) {
			chooser.get(i).setEnabled(i != t.getIndex());
		}
	}

	private static int binlog( int bits ) {
		int log = 0;
		if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
		if( bits >= 256 ) { bits >>>= 8; log += 8; }
		if( bits >= 16  ) { bits >>>= 4; log += 4; }
		if( bits >= 4   ) { bits >>>= 2; log += 2; }
		return log + ( bits >>> 1 );
	}
}