package data.io.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.GameMap;
import data.TextureHandler;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.Tag;
import window.Window;

public class UmapImporter implements Importer {

	private static final UmapImporter INSTANCE = new UmapImporter();
	
	private final FileNameExtensionFilter fileFilter;
	private UmapImporter() {
		this.fileFilter = new FileNameExtensionFilter(".umap Files", "umap");
	}
	
	@Override
	public GameMap importMap(Window w, File input, boolean isNewMap) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(input));
			GameMap map = null;

			int width = -1, height = -1, tileSize = -1;
			List<Tag> mapTags = new ArrayList<>();

			String line = r.readLine();
			while(line != null) {

				if(line.startsWith("i: ")) {
					handleImports(line, w, isNewMap);
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
					TileLayer l = importTileLayer(line, w, width, height, tileSize);
					if(map != null) map.addLayer(name, l);
				}

				else if(line.startsWith("f_")) {	
					String name = line.split(" ")[0].substring(2).trim();
					FreeLayer l = importFreeLayer(line, w, width, height, tileSize);
					if(map != null) map.addLayer(name, l);
				}

				else if(line.startsWith("a_")) {
					String name = line.split(" ")[0].substring(2).trim();
					AreaLayer l = importAreaLayer(line, w, width, height, tileSize);
					if(map != null) map.addLayer(name, l);
				}

				if(width > 0 && height > 0 && tileSize > 0 && map == null) {
					map = new GameMap(w, width, height, tileSize, false);
				}

				line = r.readLine();
			}

			for (Tag tag: mapTags) {
				map.addTag(tag);
			}

			r.close();
			return map;
		} catch(Exception e) {
			return null;
		}
	}

	@Override
	public TileLayer importTileLayer(String line, Object... o2) {
		Window w = (Window) o2[0];
		int width = (int) o2[1];
		int height = (int) o2[2];
		int tileSize = (int) o2[3];
		
		float depth = Float.parseFloat(line.split(" ")[1]);
		String[][] names = new String[height][width];

		String data = line.split("\\[")[1];
		for(int y = 4; y < data.split(";").length; y++) {
			String row = data.split(";")[y];
			if(y == data.split(";").length-1) row = row.substring(0, row.length()-1);

			for(int x = 0; x < row.split(",").length; x++) {
				String tname = row.split(",")[x].trim();
				names[x][y-4] = tname.startsWith("null")? null: tname;
			}
		}

		return new TileLayer(w, depth, names, tileSize);
	}

	@Override
	public AreaLayer importAreaLayer(String line, Object... o2) {
		int width = (int) o2[1];
		int height = (int) o2[2];
		int tileSize = (int) o2[3];
		float depth = Float.parseFloat(line.split(" ")[1]);

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
			
			int tagStart = s.indexOf("[tag;");
			if(tagStart >= 0) {
				List<Tag> objectTags = handleTags(s.substring(tagStart));
				for(Tag t: objectTags) a.addTag(t);
			}
		}

		return l;
	}

	@Override
	public FreeLayer importFreeLayer(String line, Object... o2) {
		int width = (int) o2[1];
		int height = (int) o2[2];
		int tileSize = (int) o2[3];
		
		float depth = Float.parseFloat(line.split(" ")[1]);

		FreeLayer l = new FreeLayer(depth, width, height, tileSize);
		for(int i = 1; i < line.split("\\[put; ").length; i++) {
			String s = line.split("\\[put; ")[i];
			s = s.substring(0, s.length()-1);

			String gName = s.split(";")[1].trim().startsWith("null")? null: s.split(";")[1].trim();

			float gX = Float.parseFloat(s.split(";")[2]);
			float gY = Float.parseFloat(s.split(";")[3]);

			l.set(gName, gX, gY, false);
			GameObject gameObject = l.select(gX, gY);
			
			int tagStart = s.indexOf("[tag;");
			if(tagStart >= 0) {
				List<Tag> objectTags = handleTags(s.substring(tagStart));
				for(Tag t: objectTags) gameObject.addTag(t);
			}
		}
		
		return l;
	}

	@Override
	public void handleImports(String line, Object... o2) {
		Window w = (Window) o2[0];
		boolean isNewMap = (boolean)o2[1];
		
		File text = new File(line.substring("i: ".length()));
		File image = new File(text.getAbsolutePath().substring(0, text.getAbsolutePath().length() - 4) + "png");

		w.getMenu().addImport(text);

		if (text.exists() && image.exists()) {
			String sheetName = image.getName().substring(0, image.getName().length() - 4);
			TextureHandler.loadImagePngSpriteSheet(sheetName, text.getAbsolutePath());
			if(isNewMap) w.getImageDisplay().update();
		} else {
			String error = "Either " + text.getAbsolutePath() + " or " + image.getAbsolutePath() + " does not exist.";
			JOptionPane.showMessageDialog(new JFrame(), error, "File not found", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public List<Tag> handleTags(String line) {
		//Input tag(; tag)*
		List<Tag> out = new ArrayList<>();
		
		int startIndex = line.indexOf("[tag;");
		while(startIndex >= 0 && startIndex < line.length()) {
			int endIndex = line.indexOf("[tag;", startIndex+1);
			if(endIndex < 0) endIndex = line.length(); 
			
			String tagLine = line.substring(startIndex, endIndex);
			
			String tagName = tagLine.split(";")[1];
			int tagContentStartIndex = tagLine.indexOf(tagName)+2+tagName.length();
			String tagContent = tagLine.substring(tagContentStartIndex, tagLine.lastIndexOf("]"));
						
			out.add(new Tag(tagName, tagContent));
			startIndex = endIndex;
		}
		
		return out;
	}
	
	@Override
	public FileNameExtensionFilter getFileFilter() {
		return fileFilter;
	}
	
	public static UmapImporter getInstance() {
		return INSTANCE;
	}
}
