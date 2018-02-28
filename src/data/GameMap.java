package data;

import data.layer.*;
import data.layer.layerobjects.GO;
import data.layer.layerobjects.Tag;
import data.layer.layerobjects.TagObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap extends TagObject {
	private int width, height, tileSize;
	private Map<String, Layer> layers;

	public GameMap(int width, int height, int tileSize) {
		layers = new HashMap<>();
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;

		addLayer("Background", new FreeLayer(1.0f, width, height, tileSize));
		addLayer("Tile", new TileLayer(0.5f, width, height, tileSize));
		addLayer("Object", new FreeLayer(0.0f, width, height, tileSize));
		addLayer("Camera", new AreaLayer(-1.0f, width, height, tileSize));
	}

	public GameMap(int width, int height, int tileSize, boolean b) {
		layers = new HashMap<>();
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;

		if(b) {
			addLayer("Background", new FreeLayer(1.0f, width, height, tileSize));
			addLayer("Tile", new TileLayer(0.5f, width, height, tileSize));
			addLayer("Object", new FreeLayer(0.0f, width, height, tileSize));
			addLayer("Camera", new AreaLayer(-1.0f, width, height, tileSize));
		}
	}

	public Layer getLayer(String name) {
		return layers.get(name);
	}

	public void addLayer(String name, Layer layer) {
		layers.put(name, layer);
	}

	public void removeLayer(String name) {
		layers.remove(name);
	}

	public Map<String, Layer> getLayers() {
		return layers;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTileSize() {
		return tileSize;
	}

	public String toMapFormat(boolean expo) {
		String out = "";

		List<TileLayer> tiles = new ArrayList<>();
		List<FreeLayer> frees = new ArrayList<>();
		List<AreaLayer> areas = new ArrayList<>();

		List<String> names = new ArrayList<>();


		for(String s: layers.keySet()) {
			Layer l = layers.get(s);
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

		float sx = -1, sy = -1, bx = -1, by = -1;
		if(expo) {
			sx = Integer.MAX_VALUE;
			sy = Integer.MAX_VALUE;
			bx = Integer.MIN_VALUE;
			by = Integer.MIN_VALUE;

			for(TileLayer l: tiles) {
				float csx = l.smallestX(), csy = l.smallestY(), cbx = l.biggestX(), cby = l.biggestY();
				if(csx < sx) sx = csx;
				if(csy < sy) sy = csy;
				if(cbx > bx) bx = cbx;
				if(cby > by) by = cby;
			}
			/*for(FreeLayer l: frees) {
				float csx = l.smallestX(), csy = l.smallestY(), cbx = l.biggestX(), cby = l.biggestY();
				if(csx < sx) sx = csx;
				if(csy < sy) sy = csy;
				if(cbx > bx) bx = cbx;
				if(cby > by) by = cby;
			}
			for(AreaLayer l: areas) {
				float csx = l.smallestX(), csy = l.smallestY(), cbx = l.biggestX(), cby = l.biggestY();
				if(csx < sx) sx = csx;
				if(csy < sy) sy = csy;
				if(cbx > bx) bx = cbx;
				if(cby > by) by = cby;
			}*/
		}

		for(TileLayer l: tiles) out += l.toMapFormat(names,  sx,  sy,  bx,  by);
		for(FreeLayer l: frees) out += l.toMapFormat(names,  sx,  sy,  bx,  by);
		for(AreaLayer a: areas) out += a.toMapFormat(names,  sx,  sy,  bx,  by);

		String tags = "";
		for(int i = 0; i < this.getTags().size(); i++) {
			Tag t = this.getTags().get(i);
			tags += t.toMapFormat() + (i < this.getTags().size()-1? "; ": "");
		}

		String repl = tileSize + (tags.length() > 0 ? ";" : "" ) + tags + "\n";
		for(int i = 0; i < names.size(); i++) {
			out = out.replaceAll(names.get(i), (i+1) + "");
			repl += "#" + (i+1) + " - " + names.get(i) + "\n";
		}


		return repl + out;
	}

	@Override
	public String getText() {
		return "MAP";
	}
}
