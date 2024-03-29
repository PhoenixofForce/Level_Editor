package window.elements;

import data.GameMap;
import data.TextureHandler;
import data.io.exporter.*;
import data.io.importer.Importer;
import data.io.importer.UmapImporter;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import data.layer.layerobjects.Tag;
import window.modals.UserInputs;
import window.Window;
import window.modals.ExportWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MenuBar extends JMenuBar {

	public static final Exporter[] exporter = new Exporter[] { MapExporter.getInstance(), PngExporter.getInstance() };
	private static final Importer[] importer = new Importer[] { UmapImporter.getInstance() };

	private final JMenuItem newFile;
	private final JMenuItem openFile;
	private final JMenuItem saveFile;
	private final JMenuItem saveFileAs;
	private final JMenuItem exportFile;

	private final JMenuItem importRes;
	private final JMenuItem updateRes;

	private final ExportWindow exporterWindow;

	private final List<File> imports;
	public static File lastExport, lastSave, lastOpen, lastImport;

	private final Window window;
	private final ImageList list;

	public MenuBar(Window window, ImageList list) {

		imports = new ArrayList<>();

		JMenu file = new JMenu("File");
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

		JMenuItem res = new JMenu("Resources");
		importRes = new JMenuItem("Import Resource");
		updateRes = new JMenuItem("Update Resources");

		res.add(importRes);
		res.add(updateRes);
		this.add(res);

		exporterWindow = new ExportWindow(window);

		this.window = window;
		this.list = list;
		addActions();
	}

	private void addActions() {
		newFile.addActionListener(e -> {
			UserInputs.newMap(window);
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

		saveFile.addActionListener(e -> save());

		saveFileAs.addActionListener(e -> saveAs(window));

		exportFile.addActionListener(e -> exporterWindow.setVisible(true));

		importRes.addActionListener(e -> {
			importResAction();
		});

		updateRes.addActionListener(e -> reimport());
	}

	private void importResAction() {
		JFileChooser chooser = new JFileChooser();

		if(lastImport != null) chooser.setCurrentDirectory(lastImport);
		//chooser.setOpaque(true);
		chooser.setMultiSelectionEnabled(true);

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(".text Files", "text"));

		int returnVal = chooser.showDialog(new JFrame(), "Load Texture");
		if (returnVal != JFileChooser.APPROVE_OPTION) return;

		for(File text: chooser.getSelectedFiles()) {
			String fileName = text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4);
			File image = new File(fileName + "png");

			imports.add(text);

			if (text.exists() && image.exists()) {
				TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length() - 4), text.getAbsolutePath());
				list.update();
			} else {
				String error = "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.";
				JOptionPane.showMessageDialog(new JFrame(), error, "File not found", JOptionPane.ERROR_MESSAGE);
			}

			lastImport = text.getParentFile();
		}
	}

	public void open(File f, boolean isNewMap) {
		if(isNewMap) {
			lastSave = f;
			lastOpen = f;
		}

		GameMap map = null;
		for(Importer imp: importer) {
			if(imp.getFileFilter().accept(f)) {
				map = imp.importMap(window, f, isNewMap);
				break;
			}
		}
		if(map != null) window.setMap(map, isNewMap);
		else lastSave = null;
	}

	public void save() {
		if(lastSave == null) {
			if(!saveAs(window)) return;
		} else {
			writeToFile(window, lastSave);
		}
		window.getMapViewer().saveAction();
	}

	private boolean saveAs(Window w) {
		JFileChooser chooser = new JFileChooser(){
			public void approveSelection() {
				File f = getSelectedFile();
				if(!f.getName().endsWith(".umap")) setSelectedFile( new File(f.getAbsolutePath() + ".umap"));
				f = getSelectedFile();
				lastSave = f;

				if(f.exists()) {
					String error = "The file already exists, should it be replaced?";
					int n = JOptionPane.showOptionDialog(this, error, "File exists",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
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
				tags += t.accept(MapExporter.getInstance(), new ExporterData() {}) + (i < map.getTags().size()-1? "; ": "");
			}
			wr.write("tags: " + tags + "\n");

			for(String s: map.getLayers().keySet()) {
				Layer l = map.getLayer(s);
				String prefix = (l instanceof FreeLayer? "f_": l instanceof TileLayer? "t_": "a_");

				MapExporterData data = new MapExporterData(null, new float[]{-1, -1, -1, -1}, map.getTileSize());
				String layerAsString = ((String) l.accept(MapExporter.getInstance(), data))
												.replaceAll("\n", "") + "\n";

				wr.write(prefix + s + " " + l.depth() + " " + layerAsString);
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
