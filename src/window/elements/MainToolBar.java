package window.elements;

import data.*;
import window.Tools;
import window.Window;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * used so the user can open/save/.... maps
 */
public class MainToolBar extends JToolBar {

	private JButton editMapTags, toggleAutoTile, chooseBrush, chooseEraser, chooseBucket, chooseSelect;

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

		chooseBrush = new JButton("Brush");
		chooseBrush.addActionListener(e -> w.getMapViewer().setTool(Tools.BRUSH));
		this.add(chooseBrush);

		chooseEraser = new JButton("Eraser");
		chooseEraser.addActionListener(e -> w.getMapViewer().setTool(Tools.ERASER));
		this.add(chooseEraser);

		chooseBucket = new JButton("Fill");
		chooseBucket.addActionListener(e -> w.getMapViewer().setTool(Tools.BUCKET));
		this.add(chooseBucket);

		chooseSelect = new JButton("Select");
		chooseSelect.addActionListener(e -> w.getMapViewer().setTool(Tools.SELECT));
		this.add(chooseSelect);
	}

	protected void update(Tools t) {
		chooseBucket.setEnabled(t != Tools.BUCKET);
		chooseEraser.setEnabled(t != Tools.ERASER);
		chooseBrush.setEnabled(t != Tools.BRUSH);
		chooseSelect.setEnabled(t != Tools.SELECT);
	}
}
