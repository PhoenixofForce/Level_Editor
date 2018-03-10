package window.elements;

import data.*;
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

	private JButton importRessource, editMapTags, toggleAutoTile;

	private List<File> imports;
	protected File lastImport;

	public MainToolBar(Window w, ImageList imageList) {
		this.setFloatable(false);
		this.setRollover(true);

		imports = new ArrayList<>();

		importRessource = new JButton("Add Res");
		importRessource.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();

			if(lastImport != null) chooser.setCurrentDirectory(lastImport);
			//chooser.setOpaque(true);
			chooser.setMultiSelectionEnabled(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".text Files", "text"));

			int returnVal = chooser.showDialog(new JFrame(), "Load Texture");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				for(File text: chooser.getSelectedFiles()) {
					File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4) + "png");

					imports.add(text);

					if (text.exists() && image.exists()) {
						TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length() - 4), text.getAbsolutePath());
						imageList.update();
					} else {
						JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
					}

					lastImport = text.getParentFile();
				}
			}
		});
		this.add(importRessource);

		editMapTags = new JButton("Edit Map Tags");
		editMapTags.addActionListener(e -> {
			imageList.getModifier().setTagObject(w.getMap());
		});
		this.add(editMapTags);

		this.addSeparator();
		toggleAutoTile = new JButton("Disable AutoTile");
		toggleAutoTile.addActionListener(e -> {
			w.getMap().setAutoTile(!w.getMap().getAutoTile());
			if(w.getMap().getAutoTile()) toggleAutoTile.setText("Disable AutoTile");
			else toggleAutoTile.setText("Enable AutoTile");
		});
		this.add(toggleAutoTile);
	}

	public List<File> getImports() {
		return imports;
	}

	public void addImport(File f) {
		imports.add(f);
	}

	public void reset() {
		lastImport = null;
	}
}
