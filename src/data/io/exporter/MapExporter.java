package data.io.exporter;

import data.GameMap;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.Tag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileNameExtensionFilter;

public class MapExporter implements Exporter<String> {

	private static final MapExporter INSTANCE = new MapExporter();

	private FileNameExtensionFilter fileFilter;

	private boolean tilesWithName = false,
					freeWithName = false,
					areaWithName = false;

	private MapExporter() {
		fileFilter = new FileNameExtensionFilter(".map", "map");
	}

	private String lastLayerName = "";

	@Override
	public boolean exportToFile(GameMap map, File file) {
		try {
			PrintWriter wr = new PrintWriter(file);
			wr.write(export(map));
			wr.close();

			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public String export(GameMap map) {
		String out = "";

		List<Map.Entry<String, TileLayer>> tiles = new ArrayList<>();
		List<Map.Entry<String, FreeLayer>> frees = new ArrayList<>();
		List<Map.Entry<String, AreaLayer>> areas = new ArrayList<>();

		List<String> names = new ArrayList<>();

		//Collecting all used textures
		for(String s: map.getLayers().keySet()) {
			Layer l = map.getLayers().get(s);
			if(l instanceof TileLayer) {
				TileLayer t = (TileLayer) l;
				tiles.add(new AbstractMap.SimpleEntry<>(s, t));

				String[][] layerNames = t.getTileNames();
				for(String[] sa: layerNames) {
					for(String st: sa) if(st != null && !names.contains(st)) names.add(st);
				}
			}
			else if(l instanceof FreeLayer) {
				FreeLayer f = (FreeLayer) l;
				frees.add(new AbstractMap.SimpleEntry<>(s, f));

				for(GameObject gameObject : f.getImages()) {
					String st = gameObject.name;
					if(st != null && !names.contains(st)) names.add(st);
				}
			}
			else if(l instanceof AreaLayer) {
				AreaLayer a = (AreaLayer) l;
				areas.add(new AbstractMap.SimpleEntry<>(s, a));
			}
		}

		//finding biggest and smalles coordinates
		float sx = -1, sy = -1, bx = -1, by = -1;
		int[] bounds = map.getBounds();
		sx = bounds[0];
		sy = bounds[1];
		bx = bounds[2];
		by = bounds[3];

		//Adding the map formats from every layer to the output string
		for(Map.Entry<String, TileLayer> l: tiles) {
			lastLayerName = l.getKey();
			out += l.getValue().accept(this, names, new float[]{ sx,  sy,  bx,  by }, map.getTileSize());
		}

		for(Map.Entry<String, FreeLayer> f: frees) {
			lastLayerName = f.getKey();
			out += f.getValue().accept(this, names,  new float[]{ sx,  sy,  bx,  by }, map.getTileSize());
		}
		for(Map.Entry<String, AreaLayer> a: areas) {
			lastLayerName = a.getKey();
			out += a.getValue().accept(this, names,  new float[]{ sx,  sy,  bx,  by }, map.getTileSize());
		}

		//Adding the map tags
		String tags = "";
		for(int i = 0; i < map.getTags().size(); i++) {
			Tag t = map.getTags().get(i);
			tags += t.accept(this) + (i < map.getTags().size()-1? "; ": "");
		}

		//replacing the texture names with numbers
		String repl = map.getTileSize() + (tags.length() > 0 ? ";" : "" ) + tags + "\n";
		for(int i = 0; i < names.size(); i++) {
			out = out.replaceAll(names.get(i), (i+1) + "");
			repl += "#" + (i+1) + " - " + names.get(i) + "\n";
		}

		return repl + out;
	}

	@Override
	public String export(TileLayer tileLayer, Object... o2) {
		float[] bounds = (float[]) o2[1];
		List<String> names = (List<String>) o2[0];

		String[][]tileNames = tileLayer.getTileNames();

		int startX = Math.max(0, (int) Math.floor(bounds[0]));
		int startY = Math.max(0, (int) Math.floor(bounds[1]));
		int endX = bounds[2] == -1? tileNames[0].length: Math.min(tileNames[0].length, (int) Math.ceil(bounds[2]))+1;
		int endY = bounds[3] == -1? tileNames.length: Math.min(tileNames.length, (int) Math.ceil(bounds[3])+1);

		int width = endX - startX;
		int height = endY - startY;

		String out = "[layer; " +
				(tilesWithName? lastLayerName + "; ": "") +
				tileLayer.depth() + "; " +
				width + "; " + height + "; ";

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				out += (names != null? names.indexOf(tileNames[y][x])+1: tileNames[y][x]) + (y < tileNames.length-1? ", ": "");
			}

			if(x < tileNames[0].length-1) out += "; ";
		}

		return out + "]\n";
	}

	@Override
	public String export(AreaLayer areaLayer, Object... o2) {
		return "";
	}

	@Override
	public String export(FreeLayer freeLayer, Object... o2) {
		return "";
	}

	@Override
	public String export(Tag tag, Object... o2) {
		return String.format("[tag; %s; %s]", tag.getName(), tag.getAction().replaceAll("\n", ""));
	}

	@Override
	public String export(Area area, Object... o2) {
		float[] bounds = (float[]) o2[1];
		int tileSize = (int) o2[2];

		String out = "";

		String tags = "";
		for(int i = 0; i < area.getTags().size(); i++) {
			Tag t = area.getTags().get(i);
			tags += t.accept(this) + (i < area.getTags().size()-1? "; ": "");
		}

		out += "[area; " +
				(areaWithName? lastLayerName + "; ": "") +
				(area.getSmallerX() - (bounds[0]==-1? 0: bounds[0])) + "; " +
				(area.getSmallerY() - (bounds[1]==-1? 0: bounds[1])) + "; " +
				((area.getBiggerX() + 1.0f/tileSize) - (bounds[0]==-1? 0: bounds[0])) + "; "
				+ ((area.getBiggerY() + 1.0f/tileSize) - (bounds[1]==-1? 0: bounds[1])) +
				(area.getTags().size() > 0? "; " + tags: "") +
				"]\n";

		return out;
	}

	@Override
	public String export(GameObject gameObject, Object... o2) {
		List<String> names = (List<String>) o2[0];
		float[] bounds = (float[]) o2[1];
		float depth = (float) o2[3];

		String tags = "";
		for(int i = 0; i < gameObject.getTags().size(); i++) {
			Tag t = gameObject.getTags().get(i);
			tags += t.accept(this) + (i < gameObject.getTags().size()-1? "; ": "");
		}
		return "[put; " +
				(freeWithName? lastLayerName + "; ": "") +
				depth + "; " +
				(names != null? names.indexOf(gameObject.name)+1: gameObject.name) + "; " +
				(gameObject.x-(bounds[0]==-1? 0: bounds[0])) + "; " +
				(gameObject.y-(bounds[1]==-1? 0: bounds[1])) +
				(gameObject.getTags().size() > 0? "; " + tags: "") +
				"]\n";
	}

	@Override
	public String append(String o1, String o2) {
		return o1 + o2;
	}

	@Override
	public FileNameExtensionFilter getFileFilter() {
		return fileFilter;
	}

	@Override
	public void setOptions(boolean tileWithName, boolean freeWithName, boolean areaWithName) {
		this.tilesWithName = tileWithName;
		this.freeWithName = freeWithName;
		this.areaWithName = areaWithName;
	}

	public static MapExporter getInstance() {
		return INSTANCE;
	}
}
