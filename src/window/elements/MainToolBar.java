package window.elements;

import data.*;
import window.UserInputs;
import window.Window;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainToolBar extends JToolBar {

	private JButton newMap, open, saveMap, saveAsMap, export, importRessource;
	private File lastExport, lastImport, lastSave, lastOpen;

	private List<File> inports;

	// TODO: Many Errors can occure when TagActions uses []

	public MainToolBar(Window w, ImageList imageList) {
		this.setFloatable(false);
		this.setRollover(true);

		inports = new ArrayList<>();

		newMap = new JButton("New");
		this.add(newMap);
		newMap.addActionListener(e -> {
			UserInputs.newMap(w);
			lastSave = null;
		});

		open = new JButton("Open");
		this.add(open);
		open.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();

			if(lastOpen != null) chooser.setCurrentDirectory(lastOpen);
			//chooser.setOpaque(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".umap Files", "umap"));

			int returnVal = chooser.showDialog(new JFrame(), "Open Map");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				lastSave = f;
				lastOpen = f;

				try {
					BufferedReader r = new BufferedReader(new FileReader(f));

					GameMap map = null;
					int width = -1, height = -1, tileSize = -1;

					String line = r.readLine();
					while(line != null) {

						if(line.startsWith("i: ")) {
							File text = new File(line.split(" ")[1]);
							File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4) + "png");

							inports.add(text);

							if (text.exists() && image.exists()) {
								TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length() - 5), text.getAbsolutePath());
								imageList.update();
							} else {
								JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
							}

							lastImport = text.getParentFile();
						}

						else if(line.startsWith("h: ")) height = Integer.parseInt(line.split(" ")[1]);
						else if(line.startsWith("w: ")) width = Integer.parseInt(line.split(" ")[1]);
						else if(line.startsWith("t: ")) tileSize = Integer.parseInt(line.split(" ")[1]);

						else if(line.startsWith("t_")) {
							float depth = Float.parseFloat(line.split(" ")[1]);
							String[][] names = new String[height][width];
							String name = line.split(" ")[0].split("_")[1].trim();

							String data = line.split("\\[")[1];
							for(int y = 4; y < data.split(";").length; y++) {
								String row = data.split(";")[y];

								for(int x = 0; x < row.split(",").length; x++) {
									names[x][y-4] = row.split(",")[x].trim().startsWith("null")? null: row.split(",")[x].trim();
								}
							}

							TileLayer l = new TileLayer(depth, names, tileSize);
							if(map != null) map.addLayer(name, l);
						}

						else if(line.startsWith("f_")) {
							float depth = Float.parseFloat(line.split(" ")[1]);
							String name = line.split(" ")[0].split("_")[1].trim();

							FreeLayer l = new FreeLayer(depth, width, height, tileSize);

							for(int i = 1; i < line.split("\\[put; ").length; i++) {
								String s = line.split("\\[put; ")[i];
								s = s.substring(0, s.length()-1);

								String gName = s.split(";")[0].trim().startsWith("null")? null: s.split(";")[0].trim();

								float gX = Float.parseFloat(s.split(";")[1]);
								float gY = Float.parseFloat(s.split(";")[2]);

								l.set(gName, gX, gY);
								GO go = l.select(gX, gY);

								int last = 0;
								for(int j = 1; j < s.split("; \\[tag").length; j++) {

									String data = s.substring(s.indexOf("; [tag", last));
									data = data.substring(3, data.length()-1);
									last = s.indexOf("; [tag", last)+1;

									if(data.length() == 0) continue;
									go.addTag(new Tag(data.split(";")[1].trim(), data.substring(data.indexOf(";")+5, data.indexOf("; [tag") == -1? data.length(): data.indexOf("; [tag")).trim()));	//TODO:
								}
							}

							if(map != null) map.addLayer(name, l);
						}

						if(width > 0 && height > 0 && tileSize > 0 && map == null) {
							map = new GameMap(width, height, tileSize, false);
						}

						line = r.readLine();
					}

					w.setMap(map);
					r.close();
				} catch (Exception e1) {
					lastSave = null;
					e1.printStackTrace();
				}
			}
		});

		this.addSeparator();

		saveMap = new JButton("Save");
		this.add(saveMap);
		saveMap.addActionListener(e -> {

			if(lastSave == null) {
				saveAs(w);
			} else {
				writeToFile(w, lastSave);
			}
		});

		saveAsMap = new JButton("Save As");
		this.add(saveAsMap);
		saveAsMap.addActionListener(e -> {
			saveAs(w);
		});

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

			int returnVal = chooser.showDialog(new JButton(""), "Export File");
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

			if(lastImport != null) chooser.setCurrentDirectory(lastImport);
			//chooser.setOpaque(true);
			chooser.setMultiSelectionEnabled(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".text Files", "text"));

			int returnVal = chooser.showDialog(new JFrame(), "Load Texture");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				for(File text: chooser.getSelectedFiles()) {
					File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4) + "png");

					inports.add(text);

					if (text.exists() && image.exists()) {
						TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length() - 5), text.getAbsolutePath());
						imageList.update();
					} else {
						JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
					}

					lastImport = text.getParentFile();
				}
			}
		});
		this.add(importRessource);
	}

	private void saveAs(Window w) {
		JFileChooser chooser = new JFileChooser(){
			public void approveSelection() {
				File f = getSelectedFile();
				if(!f.getName().endsWith(".map")) setSelectedFile( new File(f.getAbsolutePath() + ".umap"));
				f = getSelectedFile();
				lastSave = f;

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
				return ".umap files";
			}
		});

		int returnVal = chooser.showDialog(new JButton(""), "Save File");
		if(returnVal == JFileChooser.APPROVE_OPTION){
			File f = chooser.getSelectedFile();
			lastExport = f;
			writeToFile(w, f);
		}
	}

	private void writeToFile(Window w, File f) {
		try {
			PrintWriter wr = new PrintWriter(f);

			for(File i: inports) {
				wr.write("i: " + i.getAbsolutePath() + "\n");
			}

			GameMap map = w.getMap();
			wr.write("w: " + map.getWidth() + "\n");
			wr.write("h: " + map.getHeight() + "\n");
			wr.write("t: " + map.getTileSize() + "\n");

			for(String s: map.getLayers().keySet()) {
				Layer l = map.getLayer(s);
				wr.write((l instanceof FreeLayer? "f_": "t_") + s + " " + l.depth() + " " + l.toMapFormat(null).replaceAll("\n", "") + "\n");
			}

			wr.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public void reset() {
		lastExport = null;
		lastImport = null;
	}

}
