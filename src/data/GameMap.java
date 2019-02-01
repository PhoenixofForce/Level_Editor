package data;

import data.layer.*;
import data.layer.layerobjects.GO;
import data.layer.layerobjects.Tag;
import data.layer.layerobjects.TagObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the map contains all layers
 */
public class GameMap extends TagObject {
	private int width, height, tileSize;		//width, height and tileSize of the map
	private Map<String, Layer> layers;			//Layers saved to their name

	private int autoTile = 4;

	public GameMap(int width, int height, int tileSize) {
		this(width, height, tileSize, true);
	}

	/**
	 *
	 * @param width width of the map
	 * @param height height of the map
	 * @param tileSize tilesize of the map
	 * @param b true if the default layers should be added
	 */
	public GameMap(int width, int height, int tileSize, boolean b) {
		layers = new HashMap<>();
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;

		TextureHandler.createError(tileSize);

		if(b) {
			addLayer("Background", new FreeLayer(1.0f, width, height, tileSize));
			addLayer("Tile", new TileLayer(this, 0.5f, width, height, tileSize));
			addLayer("Object", new FreeLayer(0.0f, width, height, tileSize));
			addLayer("Camera", new AreaLayer(-1.0f, width, height, tileSize));
		}
	}

	/**
	 * @param name of the layer to get
	 * @return the layer whith that name
	 */
	public Layer getLayer(String name) {
		return layers.get(name);
	}

	/**
	 * adds a layer to the map
	 * @param name name of the layer to add
	 * @param layer the layer to add
	 */
	public void addLayer(String name, Layer layer) {
		layers.put(name, layer);
	}

	/**
	 * removes a layer
	 * @param name name of the layer to remove
	 */
	public void removeLayer(String name) {
		layers.remove(name);
	}

	/**
	 * @return the map containing all layers
	 */
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

	/**
	 * combines the map formats of all layers
	 * @param expo if its used for an export
	 * @return the map format of all layer
	 */
	public String toMapFormat(boolean expo) {
		String out = "";

		List<TileLayer> tiles = new ArrayList<>();
		List<FreeLayer> frees = new ArrayList<>();
		List<AreaLayer> areas = new ArrayList<>();

		List<String> names = new ArrayList<>();

		//Collecting all used textures
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

		//finding biggest and smalles coordinates
		float sx = -1, sy = -1, bx = -1, by = -1;
		if(expo) {
			//sx = Integer.MAX_VALUE;
			//sy = Integer.MAX_VALUE;
			//bx = Integer.MIN_VALUE;
			//by = Integer.MIN_VALUE;

			int[] bounds = getBounds();
			sx = bounds[0];
			sy = bounds[1];
			bx = bounds[2];
			by = bounds[3];
			/*for(TileLayer l: tiles) {
				float csx = l.smallestX(), csy = l.smallestY(), cbx = l.biggestX(), cby = l.biggestY();
				if(csx < sx) sx = csx;
				if(csy < sy) sy = csy;
				if(cbx > bx) bx = cbx;
				if(cby > by) by = cby;
			}

			//didnt work, could work when rounding up/down later
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

		//Adding the map formats from every layer to the output string
		for(TileLayer l: tiles) out += l.toMapFormat(names,  sx,  sy,  bx,  by);
		for(FreeLayer l: frees) out += l.toMapFormat(names,  sx,  sy,  bx,  by);
		for(AreaLayer a: areas) out += a.toMapFormat(names,  sx,  sy,  bx,  by);

		//Adding the map tags
		String tags = "";
		for(int i = 0; i < this.getTags().size(); i++) {
			Tag t = this.getTags().get(i);
			tags += t.toMapFormat() + (i < this.getTags().size()-1? "; ": "");
		}

		//replacing the texture names with numbers
		String repl = tileSize + (tags.length() > 0 ? ";" : "" ) + tags + "\n";
		for(int i = 0; i < names.size(); i++) {
			out = out.replaceAll(names.get(i), (i+1) + "");
			repl += "#" + (i+1) + " - " + names.get(i) + "\n";
		}


		return repl + out;
	}

	public int[] getBounds() {
		int sx = Integer.MAX_VALUE,
		sy = Integer.MAX_VALUE,
		bx = Integer.MIN_VALUE,
		by = Integer.MIN_VALUE;

		for(String s: layers.keySet()) {
			Layer l = layers.get(s);
			float csx = l.smallestX(), csy = l.smallestY(), cbx = l.biggestX(), cby = l.biggestY();
			if(csx < sx && csx != -1) sx = (int)Math.floor(csx);
			if(csy < sy && csy != -1) sy = (int)Math.floor(csy);
			if(cbx > bx) bx = (int)Math.ceil(cbx);
			if(cby > by) by = (int)Math.ceil(cby);

		}

		return new int[]{sx, sy, bx, by};
	}

	@Override
	public String getText() {
		return "MAP";
	}

	public void setAutoTile(int at) {
		this.autoTile = at;
	}

	@Override
	public GameMap clone() {
		GameMap out = new GameMap(width, height, tileSize);
		out.setAutoTile(autoTile);
		ArrayList<java.util.Map.Entry<String, Layer>> listOfEntry = new ArrayList<>(layers.entrySet());
		for(java.util.Map.Entry<String, Layer> e: listOfEntry) {
			Layer l = e.getValue();
			if(l instanceof TileLayer) {
				TileLayer tl = (TileLayer) l;
				tl.setMap(out);
				out.addLayer(e.getKey(), tl.clone());
				continue;
			}
			if(l instanceof FreeLayer) {
				FreeLayer tl = (FreeLayer) l;
				out.addLayer(e.getKey(), tl.clone());
				continue;
			}
			if(l instanceof AreaLayer) {
				AreaLayer tl = (AreaLayer) l;
				out.addLayer(e.getKey(), tl.clone());
				continue;
			}
		}
		return out;
	}

	public void updateMap() {
		ArrayList<java.util.Map.Entry<String, Layer>> listOfEntry = new ArrayList<>(layers.entrySet());
		for(java.util.Map.Entry<String, Layer> e: listOfEntry) {
			Layer l = e.getValue();
			if (l instanceof TileLayer) {
				TileLayer tl = (TileLayer) l;
				tl.setMap(this);
			}
		}
	}

	public int getAutoTile() {
		return autoTile;
	}
}
