package data.exporter;

import data.GameMap;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.GO;
import data.layer.layerobjects.Tag;

import java.util.ArrayList;
import java.util.List;

public class MapExporter implements Exporter {

	private static final MapExporter INSTANCE = new MapExporter();

	private MapExporter() {}

	@Override
	public String export(GameMap map) {
		String out = "";

		List<TileLayer> tiles = new ArrayList<>();
		List<FreeLayer> frees = new ArrayList<>();
		List<AreaLayer> areas = new ArrayList<>();

		List<String> names = new ArrayList<>();

		//Collecting all used textures
		for(String s: map.getLayers().keySet()) {
			Layer l = map.getLayers().get(s);
			if(l instanceof TileLayer) {
				TileLayer t = (TileLayer) l;
				tiles.add(t);

				String[][] layerNames = t.getTileNames();
				for(String[] sa: layerNames) {
					for(String st: sa) if(st != null && !names.contains(st)) names.add(st);
				}
			}
			else if(l instanceof FreeLayer) {
				FreeLayer f = (FreeLayer) l;
				frees.add(f);

				for(GO go: f.getImages()) {
					String st = go.name;
					if(st != null && !names.contains(st)) names.add(st);
				}
			}
			else if(l instanceof AreaLayer) {
				AreaLayer a = (AreaLayer) l;
				areas.add(a);

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
		Object[] args = new Object[]{names,  new float[]{sx,  sy,  bx,  by}, map.getTileSize()};

		for(TileLayer l: tiles) out += l.accept(this, args);
		for(FreeLayer f: frees) out += f.accept(this, args);
		for(AreaLayer a: areas) out += a.accept(this, args);

		//Adding the map tags
		String tags = "";
		for(int i = 0; i < map.getTags().size(); i++) {
			Tag t = map.getTags().get(i);
			tags += t.accept(this, null) + (i < map.getTags().size()-1? "; ": "");
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
	public String export(TileLayer tileLayer, Object o2) {
		float[] sxsybxby = (float[]) ((Object[])o2)[1];
		List<String> names = (List<String>) ((Object[])o2)[0];

		String[][]tileNames = tileLayer.getTileNames();

		int startX = Math.max(0, (int) Math.floor(sxsybxby[0]));
		int startY = Math.max(0, (int) Math.floor(sxsybxby[1]));
		int endX = sxsybxby[2] == -1? tileNames[0].length: Math.min(tileNames[0].length, (int) Math.ceil(sxsybxby[2]))+1;
		int endY = sxsybxby[3] == -1? tileNames.length: Math.min(tileNames.length, (int) Math.ceil(sxsybxby[3])+1);

		int width = endX - startX;
		int height = endY - startY;

		String out = "[layer; " + tileLayer.depth() + "; " + width + "; " + height + "; ";

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				out += (names != null? names.indexOf(tileNames[y][x])+1: tileNames[y][x]) + (y < tileNames.length-1? ", ": "");
			}

			if(x < tileNames[0].length-1) out += "; ";
		}

		return out + "]\n";
	}

	@Override
	public String export(AreaLayer areaLayer, Object o2) {
		return areaLayer.accept(this, o2);
	}

	@Override
	public String export(FreeLayer freeLayer, Object o2) {
		return freeLayer.accept(this, o2);
	}

	@Override
	public String export(Tag tag, Object o2) {
		return String.format("[tag; %s; %s]", tag.getName(), tag.getAction().replaceAll(";", "δ").replaceAll("\n", ""));
	}

	@Override
	public String export(Area area, Object o2) {
		float[] sxsybxby = (float[]) ((Object[])o2)[1];
		int tileSize = (int) ((Object[])o2)[2];

		String out = "";

		String tags = "";
		for(int i = 0; i < area.getTags().size(); i++) {
			Tag t = area.getTags().get(i);
			tags += t.accept(this, null) + (i < area.getTags().size()-1? "; ": "");
		}
		out += "[area; " + (area.getSmallerX() - (sxsybxby[0]==-1? 0: sxsybxby[0])) + "; " + (area.getSmallerY() - (sxsybxby[1]==-1? 0: sxsybxby[1])) + "; " + ((area.getBiggerX() + 1.0f/tileSize) - (sxsybxby[0]==-1? 0: sxsybxby[0])) + "; " + ((area.getBiggerY() + 1.0f/tileSize) - (sxsybxby[1]==-1? 0: sxsybxby[1])) + (area.getTags().size() > 0? "; " + tags: "") + "]\n";


		return out;
	}

	@Override
	public String export(GO go, Object o2) {
		List<String> names = (List<String>) ((Object[])o2)[0];
		float[] sxsybxby = (float[]) ((Object[])o2)[1];
		float depth = (float) ((Object[])o2)[3];

		String tags = "";
		for(int i = 0; i < go.getTags().size(); i++) {
			Tag t = go.getTags().get(i);
			tags += t.accept(this, null) + (i < go.getTags().size()-1? "; ": "");
		}
		return "[put; " + depth + "; " + (names != null? names.indexOf(go.name)+1: go.name) + "; " + (go.x-(sxsybxby[0]==-1? 0: sxsybxby[1])) + "; " + (go.y-(sxsybxby[1]==-1? 0: sxsybxby[1])) + (go.getTags().size() > 0? "; " + tags: "") + "]\n";
	}

	public static MapExporter getInstance() {
		return INSTANCE;
	}
}
