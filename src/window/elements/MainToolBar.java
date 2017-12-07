package window.elements;

import data.TextureHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class MainToolBar extends JToolBar{

	private JButton newMap, open, saveMap, importRessource;

	public MainToolBar(ImageList il) {
		this.setFloatable(false);
		this.setRollover(true);

		//TODO: New, Open, Save
		newMap = new JButton("New");
		this.add(newMap);

		open = new JButton("Open");
		this.add(open);

		saveMap = new JButton("Save");
		this.add(saveMap);

		this.addSeparator();

		importRessource = new JButton("Add Res");
		importRessource.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();

			//chooser.setOpaque(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".text Files", "text"));

			int returnVal = chooser.showDialog(new JFrame(), "Load Save");
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File text = chooser.getSelectedFile();
				File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length()-4) + "png");

				if(text.exists() && image.exists()) {
					TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length()-5), text.getAbsolutePath());
					il.update();
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
				}
 			}
		});
		this.add(importRessource);

	}

}
