package window.elements;

import data.GameMap;
import data.TextureHandler;
import data.exporter.Exporter;
import data.exporter.MapExporter;
import data.exporter.PngExporter;
import data.importer.Importer;
import data.importer.UmapImporter;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import data.layer.layerobjects.Tag;
import window.UserInputs;
import window.Window;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MenuBar extends JMenuBar {

	private Exporter[] exporter = new Exporter[] {MapExporter.getInstance(), PngExporter.getInstance()};
	private Importer[] importer = new Importer[] {UmapImporter.getInstance()};

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
			for(Importer imp: importer) chooser.addChoosableFileFilter(imp.getFileFilter());

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

					boolean endsWithFileType = false;
					for(Exporter exp: exporter) {
						if(exp.getFileFilter().accept(f)) {
							endsWithFileType = true;
							break;
						}
					}

					if(!endsWithFileType) setSelectedFile( new File(f.getAbsolutePath() + getFileFilter().getDescription()));
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
			for(Exporter exp: exporter) chooser.addChoosableFileFilter(exp.getFileFilter());

			int returnVal = chooser.showDialog(new JButton(""), "Export File");
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File f = chooser.getSelectedFile();
				lastExport = f;

				boolean exported = false;
				for(Exporter exp: exporter) {
					if(exp.getFileFilter().accept(f)) {
						exp.exportToFile(w.getMap(), f);
						exported = true;
						break;
					}
				}
				if(!exported) MapExporter.getInstance().exportToFile(w.getMap(), f);

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

		GameMap map = null;
		for(Importer imp: importer) {
			if(imp.getFileFilter().accept(f)) {
				map = imp.importMap(w, f, isNewMap);
				break;
			}
		}
		if(map != null) w.setMap(map, isNewMap);
		else lastSave = null;
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
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("umap", "umap"));

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
				tags += t.accept(MapExporter.getInstance()) + (i < map.getTags().size()-1? "; ": "");
			}
			wr.write("tags: " + tags + "\n");

			for(String s: map.getLayers().keySet()) {
				Layer l = map.getLayer(s);
				wr.write((l instanceof FreeLayer? "f_": l instanceof TileLayer? "t_": "a_") + s + " " + l.depth() + " " + ((String) l.accept(MapExporter.getInstance(), null, new float[]{-1, -1, -1, -1}, map.getTileSize())).replaceAll("\n", "") + "\n");
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

	public void addImport(File f) {
		imports.add(f);
		lastImport = f.getParentFile();
	}

	public void reset() {
		lastExport = null;
		lastImport = null;
	}
}
