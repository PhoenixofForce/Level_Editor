package window.elements;

import data.GameMap;
import data.TextureHandler;
import data.exporter.MapExporter;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.GO;
import data.layer.layerobjects.Tag;
import window.UserInputs;
import window.Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MenuBar extends JMenuBar {

	private JMenu file;
	private JMenuItem newFile, openFile, saveFile, saveFileAs, exportFile;

	private JMenuItem res;
	private JMenuItem importRes, updateRes;

	private List<File> imports;
	private File lastExport, lastSave, lastOpen, lastImport;
	private Window w;
	private ImageList list;

	public MenuBar(Window w, ImageList list) {

		imports = new ArrayList<>();

		file = new JMenu("File");
		newFile = new JMenuItem("New...");
		openFile = new JMenuItem("Open");
		saveFile = new JMenuItem("Save");
		saveFileAs = new JMenuItem("Save As");
		exportFile = new JMenuItem("Export");

		file.add(newFile);
		file.add(openFile);
		file.addSeparator();
		file.add(saveFile);
		file.add(saveFileAs);
		file.add(exportFile);
		this.add(file);

		res = new JMenu("Ressources");
		importRes = new JMenuItem("Import Resource");
		updateRes = new JMenuItem("Update Ressources");

		res.add(importRes);
		res.add(updateRes);
		this.add(res);

		this.w = w;
		this.list = list;
		addActions();
	}

	private void addActions() {
		newFile.addActionListener(e -> {
			UserInputs.newMap(w);
			lastSave = null;
		});

		openFile.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			if(lastOpen != null) chooser.setCurrentDirectory(lastOpen);
			//chooser.setOpaque(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".umap Files", "umap"));

			int returnVal = chooser.showDialog(new JFrame(), "Open Map");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				open(f, true);
			}
		});

		saveFile.addActionListener(e -> {
			save();
		});

		saveFileAs.addActionListener(e -> {
			saveAs(w);
		});

		exportFile.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser(){
				public void approveSelection() {
					File f = getSelectedFile();
					if(!f.getName().endsWith(".map") && !f.getName().endsWith(".png")) setSelectedFile( new File(f.getAbsolutePath() + getFileFilter().getDescription()));
					f = getSelectedFile();

					if(f.exists()) {
						int n = JOptionPane.showOptionDialog(this, "The file already exists, should it be replaced?", "File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
						if(n == 0) super.approveSelection();
					} else super.approveSelection();
				}
			};
			if(lastExport != null) chooser.setSelectedFile(lastExport);
			else if(lastSave != null) chooser.setSelectedFile(new File(lastSave.getAbsolutePath().substring(0, lastSave.getAbsolutePath().length()-4) + "map"));

			chooser.setOpaque(true);

			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".map", "map"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(".png", "png"));

			int returnVal = chooser.showDialog(new JButton(""), "Export File");
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File f = chooser.getSelectedFile();
				lastExport = f;

				if(f.getName().endsWith(".map")) {
					try {
						PrintWriter wr = new PrintWriter(f);
						wr.write(MapExporter.getInstance().export(w.getMap()));
						wr.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				} else if(f.getName().endsWith(".png")) {
					int[] bounds = w.getMap().getBounds();
					BufferedImage img = new BufferedImage(w.getMap().getWidth()*w.getMap().getTileSize(), w.getMap().getHeight()*w.getMap().getTileSize(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = (Graphics2D) img.getGraphics();

					//draws layer by their drawing depth
					w.getMap().getLayers().values().stream()
							.sorted((o1, o2) -> Float.compare(o2.depth(), o1.depth()))
							.forEach(l -> l.draw(g2, null, null));
					try {
						ImageIO.write(img.getSubimage(bounds[0]*w.getMap().getTileSize(), bounds[1]*w.getMap().getTileSize(), (1+bounds[2]-bounds[0])*w.getMap().getTileSize(), (1+bounds[3]-bounds[1])*w.getMap().getTileSize()), "PNG", f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		});

		importRes.addActionListener(e -> {
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
						list.update();
					} else {
						JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
					}

					lastImport = text.getParentFile();
				}
			}
		});

		updateRes.addActionListener(e -> reimport());
	}

	public void open(File f, boolean isNewMap) {
		if(isNewMap) {
			lastSave = f;
			lastOpen = f;
		}
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));

			GameMap map = null;
			int width = -1, height = -1, tileSize = -1;
			HashMap<String, String> mapTags = new HashMap<>();
			String line = r.readLine();
			while(line != null) {

				if(line.startsWith("i: ")) {
					File text = new File(line.substring("i: ".length()));
					File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4) + "png");

					imports.add(text);

					if (text.exists() && image.exists()) {
						TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length() - 4), text.getAbsolutePath());
						if(isNewMap) list.update();
					} else {
						JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
					}

					lastImport = text.getParentFile();
				}

				else if(line.startsWith("h: ")) height = Integer.parseInt(line.split(" ")[1]);
				else if(line.startsWith("w: ")) width = Integer.parseInt(line.split(" ")[1]);
				else if(line.startsWith("t: ")) tileSize = Integer.parseInt(line.split(" ")[1]);
				else if (line.startsWith("tags: ")) {
					String[] tagString = line.substring("tags: ".length()).replaceAll(" ", "").replaceAll("\\[", "").replaceAll("]", "").split(";");

					int i = 0;
					while (i < tagString.length) {
						if (tagString[i].equals("tag")) {
							mapTags.put(tagString[i + 1], tagString[i + 2].replaceAll("δ", ";"));
							i++;
							i++;
						}

						i++;
					}
				}
				else if(line.startsWith("t_")) {
					float depth = Float.parseFloat(line.split(" ")[1]);
					String[][] names = new String[height][width];
					String name = line.split(" ")[0].substring(2).trim();

					String data = line.split("\\[")[1];
					for(int y = 4; y < data.split(";").length; y++) {
						String row = data.split(";")[y];
						if(y == data.split(";").length-1) row = row.substring(0, row.length()-1);

						for(int x = 0; x < row.split(",").length; x++) {
							String tname = row.split(",")[x].trim();
							names[x][y-4] = tname.startsWith("null")? null: tname;
						}
					}

					TileLayer l = new TileLayer(w, depth, names, tileSize);
					if(map != null) map.addLayer(name, l);
				}

				else if(line.startsWith("f_")) {
					float depth = Float.parseFloat(line.split(" ")[1]);
					String name = line.split(" ")[0].substring(2).trim();

					FreeLayer l = new FreeLayer(depth, width, height, tileSize);

					for(int i = 1; i < line.split("\\[put; ").length; i++) {
						String s = line.split("\\[put; ")[i];
						s = s.substring(0, s.length()-1);

						String gName = s.split(";")[1].trim().startsWith("null")? null: s.split(";")[1].trim();

						float gX = Float.parseFloat(s.split(";")[2]);
						float gY = Float.parseFloat(s.split(";")[3]);

						l.set(gName, gX, gY, false);
						GO go = l.select(gX, gY);

						int last = 0;
						for(int j = 1; j < s.split("; \\[tag").length; j++) {

							String data = s.substring(s.indexOf("; [tag", last));

							data = data.substring(3, data.length()-1);
							last = s.indexOf("; [tag", last)+1;

							if(data.length() == 0) continue;
							go.addTag(new Tag(data.split(";")[1].trim(), data.substring(data.indexOf(";",data.indexOf(";")+1), data.indexOf("; [tag") == -1? data.length(): data.indexOf("; [tag")-1).trim().substring(2)));
						}
					}

					if(map != null) map.addLayer(name, l);
				}

				else if(line.startsWith("a_")) {
					float depth = Float.parseFloat(line.split(" ")[1]);
					String name = line.split(" ")[0].substring(2).trim();

					AreaLayer l = new AreaLayer(depth, width, height, tileSize);

					for(int i = 1; i < line.split("\\[area; ").length; i++) {
						String s = line.split("\\[area; ")[i];
						s = s.substring(0, s.length()-1);

						float x1 = Float.parseFloat(s.split(";")[0]);
						float y1 = Float.parseFloat(s.split(";")[1]);
						float x2 = Float.parseFloat(s.split(";")[2]);
						float y2 = Float.parseFloat(s.split(";")[3]);

						l.set("", x1, y1, false);
						Area a = l.select(x1, y1);
						a.setX1(x1);
						a.setX2(x2 - 1.0f/(float)tileSize);
						a.setY1(y1);
						a.setY2(y2 - 1.0f/(float)tileSize);

						int last = 0;
						for(int j = 1; j < s.split("; \\[tag").length; j++) {

							String data = s.substring(s.indexOf("; [tag", last));
							data = data.substring(3, data.length()-1);
							last = s.indexOf("; [tag", last)+1;

							if(data.length() == 0) continue;
							a.addTag(new Tag(data.split(";")[1].trim(), data.substring(data.indexOf(";",data.indexOf(";")+1), data.indexOf("; [tag") == -1? data.length(): data.indexOf("; [tag")).trim().substring(2)));
						}
					}

					if(map != null) map.addLayer(name, l);
				}

				if(width > 0 && height > 0 && tileSize > 0 && map == null) {
					map = new GameMap(w, width, height, tileSize, false);
				}

				line = r.readLine();
			}

			for (String tag: mapTags.keySet()) {
				map.addTag(new Tag(tag, mapTags.get(tag)));
			}

			w.setMap(map, isNewMap);
			r.close();
		} catch (Exception e1) {
			lastSave = null;
			e1.printStackTrace();
		}
	}

	public void save() {
		if(lastSave == null) {
			if(!saveAs(w)) return;
		} else {
			writeToFile(w, lastSave);
		}
		w.getMapViewer().saveAction();
	}

	private boolean saveAs(Window w) {
		JFileChooser chooser = new JFileChooser(){
			public void approveSelection() {
				File f = getSelectedFile();
				if(!f.getName().endsWith(".umap")) setSelectedFile( new File(f.getAbsolutePath() + ".umap"));
				f = getSelectedFile();
				lastSave = f;

				if(f.exists()) {
					int n = JOptionPane.showOptionDialog(this, "The file already exists, should it be replaced?", "File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
					if(n == 0) super.approveSelection();
				} else super.approveSelection();
			}
		};
		if(lastSave != null) chooser.setSelectedFile(lastSave);

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
			lastSave = f;
			writeToFile(w, f);
			return true;
		}
		return false;
	}

	private void writeToFile(Window w, File f) {
		writeToFile(w.getMap(), f);
	}

	private void writeToFile(GameMap map, File f) {
		try {
			PrintWriter wr = new PrintWriter(f);

			for(File i: imports) {
				wr.write("i: " + i.getAbsolutePath() + "\n");
			}

			wr.write("w: " + map.getWidth() + "\n");
			wr.write("h: " + map.getHeight() + "\n");
			wr.write("t: " + map.getTileSize() + "\n");

			String tags = "";
			for(int i = 0; i < map.getTags().size(); i++) {
				Tag t = map.getTags().get(i);
				tags += t.accept(MapExporter.getInstance(), null) + (i < map.getTags().size()-1? "; ": "");
			}
			wr.write("tags: " + tags + "\n");

			for(String s: map.getLayers().keySet()) {
				Layer l = map.getLayer(s);
				wr.write((l instanceof FreeLayer? "f_": l instanceof TileLayer? "t_": "a_") + s + " " + l.depth() + " " + l.accept(MapExporter.getInstance(), new Object[]{null, new float[]{-1, -1, -1, -1}, map.getTileSize()}).replaceAll("\n", "") + "\n");
			}

			wr.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public String getFileName() {
		if(lastSave == null) return "Untitled";
		return lastSave.getName().substring(0, lastSave.getName().length()-5);
	}

	public void reimport() {
		for(File f: imports) {
			TextureHandler.loadImagePngSpriteSheet(f.getName().substring(0, f.getName().length() - 5), f.getAbsolutePath());
		}
		list.update();
	}

	public void reset() {
		lastExport = null;
		lastImport = null;
	}
}
