package window.elements;

import javax.swing.*;

public class MainToolBar extends JToolBar{

	private JButton newMap, saveMap, importRessource;

	public MainToolBar() {
		this.setFloatable(false);
		this.setRollover(true);

		newMap = new JButton("New");
		this.add(newMap);

		saveMap = new JButton("Save");
		this.add(saveMap);

		this.addSeparator();

		importRessource = new JButton("Add Res");
		this.add(importRessource);
	}

}
