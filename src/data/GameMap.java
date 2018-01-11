package data;

import data.layer.*;
import data.layer.layerobjects.GO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
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

	public String toMapFormat() {
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

		for(TileLayer l: tiles) out += l.toMapFormat(names);
		for(FreeLayer l: frees) out += l.toMapFormat(names);
		for(AreaLayer a: areas) out += a.toMapFormat(names);

		String repl = tileSize + "\n";
		for(int i = 0; i < names.size(); i++) {
			out = out.replaceAll(names.get(i), (i+1) + "");
			repl += "#" + (i+1) + " - " + names.get(i) + "\n";
		}


		return repl + out;
	}
}
