package data.io.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.maps.GameMap;
import data.TextureHandler;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.Tag;
import data.maps.SquareGameMap;
import window.Window;

public class UmapImporter implements Importer {

	private static final UmapImporter INSTANCE = new UmapImporter();
	
	private final FileNameExtensionFilter fileFilter;
	private int width,
				height,
				tileSize;

	private boolean isNewMap;

	private UmapImporter() {
		this.fileFilter = new FileNameExtensionFilter(".umap Files", "umap");
	}
	
	@Override
	public GameMap importMap(Window w, File input, boolean isNewMap) {
		this.isNewMap = isNewMap;
		this.width = -1;
		this.height = -1;
		this.tileSize = -1;

		try {
			BufferedReader r = new BufferedReader(new FileReader(input));
			GameMap map = null;

			List<Tag> mapTags = new ArrayList<>();

			String line = r.readLine();
			while(line != null) {
				if(line.startsWith("i: ")) {
					handleImports(line);
				}
				else if(line.startsWith("h: ")) {
					height = Integer.parseInt(line.split(" ")[1]);
				}
				else if(line.startsWith("w: ")) {
					width = Integer.parseInt(line.split(" ")[1]);
				}
				else if(line.startsWith("t: ")) {
					tileSize = Integer.parseInt(line.split(" ")[1]);
				}
				else if (line.startsWith("tags: ")) {
					mapTags = handleTags(line.substring(6));
				}
				else if(line.startsWith("t_")) {
					String name = line.split(" ")[0].substring(2).trim();
					TileLayer l = importTileLayer(line);
					if(map != null) map.addLayer(name, l);
				}

				else if(line.startsWith("f_")) {	
					String name = line.split(" ")[0].substring(2).trim();
					FreeLayer l = importFreeLayer(line);
					if(map != null) map.addLayer(name, l);
				}

				else if(line.startsWith("a_")) {
					String name = line.split(" ")[0].substring(2).trim();
					AreaLayer l = importAreaLayer(line);
					if(map != null) map.addLayer(name, l);
				}

				if(width > 0 && height > 0 && tileSize > 0 && map == null) {
					map = new SquareGameMap(width, height, tileSize);
				}

				line = r.readLine();
			}

			for (Tag tag: mapTags) {
				if(map != null)
					map.addTag(tag);
			}

			r.close();
			return map;
		} catch(Exception e) {
			return null;
		}
	}

	public TileLayer importTileLayer(String line) {
		float depth = Float.parseFloat(line.split(" ")[1]);
		String[][] names = new String[height][width];

		String data = line.split("\\[")[1];
		String[] splitData = data.split(";");

		for(int y = 4; y < splitData.length; y++) {
			String row = splitData[y];
			if(y == splitData.length - 1) row = row.substring(0, row.length()-1);
			String[] splitRow = row.split(",");

			for(int x = 0; x < splitRow.length; x++) {
				String tileName = splitRow[x].trim();
				names[x][y-4] = tileName.startsWith("null")? null: tileName;
			}
		}

		return new TileLayer(depth, names, tileSize);
	}

	public AreaLayer importAreaLayer(String line) {
		float depth = Float.parseFloat(line.split(" ")[1]);

		AreaLayer layer = new AreaLayer(depth, width, height, tileSize);
		for(int i = 1; i < line.split("\\[area; ").length; i++) {
			String s = line.split("\\[area; ")[i];
			s = s.substring(0, s.length()-1);

			float x1 = Float.parseFloat(s.split(";")[0]);
			float y1 = Float.parseFloat(s.split(";")[1]);
			float x2 = Float.parseFloat(s.split(";")[2]);
			float y2 = Float.parseFloat(s.split(";")[3]);

			layer.set("", x1, y1, false);
			Area currentArea = layer.select(x1, y1);
			currentArea.setX1(x1);
			currentArea.setX2(x2 - 1.0f/(float)tileSize);
			currentArea.setY1(y1);
			currentArea.setY2(y2 - 1.0f/(float)tileSize);
			
			int tagStart = s.indexOf("[tag;");
			if(tagStart >= 0) {
				List<Tag> objectTags = handleTags(s.substring(tagStart));
				for(Tag t: objectTags) currentArea.addTag(t);
			}
		}

		return layer;
	}

	public FreeLayer importFreeLayer(String line) {
		float depth = Float.parseFloat(line.split(" ")[1]);

		FreeLayer layer = new FreeLayer(depth, width, height, tileSize);
		for(int i = 1; i < line.split("\\[put; ").length; i++) {
			String s = line.split("\\[put; ")[i];
			s = s.substring(0, s.length()-1);

			String textureName = s.split(";")[1].trim().startsWith("null")? null: s.split(";")[1].trim();

			float gX = Float.parseFloat(s.split(";")[2]);
			float gY = Float.parseFloat(s.split(";")[3]);

			layer.set(textureName, gX, gY, false);
			GameObject gameObject = layer.select(gX, gY);
			
			int tagStart = s.indexOf("[tag;");
			if(tagStart >= 0) {
				List<Tag> objectTags = handleTags(s.substring(tagStart));
				for(Tag t: objectTags) gameObject.addTag(t);
			}
		}
		
		return layer;
	}

	public void handleImports(String line) {
		Window window = Window.INSTANCE;

		File text = new File(line.substring("i: ".length()));
		File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4) + "png");

		window.getMyMenuBar().addImport(text);

		if (text.exists() && image.exists()) {
			TextureHandler.loadImagePngSpriteSheet(image.getName().substring(0, image.getName().length() - 4), text.getAbsolutePath());
			if(isNewMap) window.getImageList().update();
		} else {
			JOptionPane.showMessageDialog(new JFrame(), "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public List<Tag> handleTags(String line) {
		//Input tag(; tag)*
		List<Tag> out = new ArrayList<>();
		
		int startIndex = line.indexOf("[tag;");
		while(startIndex >= 0 && startIndex < line.length()) {
			int endIndex = line.indexOf("[tag;", startIndex+1);
			if(endIndex < 0) endIndex = line.length(); 
			
			String tagLine = line.substring(startIndex, endIndex);
			
			String tagName = tagLine.split(";")[1];
			String tagContent = tagLine.substring(tagLine.indexOf(tagName)+2+tagName.length(), tagLine.lastIndexOf("]"));
						
			out.add(new Tag(tagName, tagContent));
			startIndex = endIndex;
		}
		
		return out;
	}

	public FileNameExtensionFilter getFileFilter() {
		return fileFilter;
	}
	
	public static UmapImporter getInstance() {
		return INSTANCE;
	}
}
