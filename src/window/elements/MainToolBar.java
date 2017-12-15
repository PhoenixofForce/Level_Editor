package window.elements;

import data.TextureHandler;
import window.UserInputs;
import window.Window;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MainToolBar extends JToolBar {

	private JButton newMap, open, saveMap, export, importRessource;
	private File lastExport;

	public MainToolBar(Window w, ImageList imageList) {
		this.setFloatable(false);
		this.setRollover(true);

		//TODO: New, Open, Save
		newMap = new JButton("New");
		this.add(newMap);
		newMap.addActionListener(e -> UserInputs.newMap(w));

		open = new JButton("Open");
		this.add(open);

		this.addSeparator();

		saveMap = new JButton("Save");
		this.add(saveMap);

		export = new JButton("Export");
		this.add(export);
		export.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser(){
				public void approveSelection() {
					File f = getSelectedFile();
					if(!f.getName().endsWith(".map")) setSelectedFile( new File(f.getAbsolutePath() + ".map"));
					f = getSelectedFile();

					if(f.exists()) {
						int n = JOptionPane.showOptionDialog(this, "The file already exists, should it be replaced?", "File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
						if(n == 0) super.approveSelection();
					} else super.approveSelection();
				}
			};
			if(lastExport != null) chooser.setSelectedFile(lastExport);

			chooser.setOpaque(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory();
				}

				@Override
				public String getDescription() {
					return ".map files";
				}
			});

			int returnVal = chooser.showDialog(new JButton("Ch"), "Save File");
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File f = chooser.getSelectedFile();
				lastExport = f;
				try {
					PrintWriter wr = new PrintWriter(f);
					wr.write(w.getMap().toMapFormat());
					wr.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

			}
		});

		this.addSeparator();

		importRessource = new JButton("Add Res");
		importRessource.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();

			//chooser.setOpaque(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".text Files", "text"));

			int returnVal = chooser.showDialog(new JFrame(), "Load Texture");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File text = chooser.getSelectedFile();
				File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4) + "png");

				if (text.exists() && image.exists()) {
					TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length() - 5), text.getAbsolutePath());
					imageList.update();
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		this.add(importRessource);
	}

	public void reset() {
		lastExport = null;
	}

}
