package data;

import data.exporter.Exporter;
import data.layer.*;
import data.layer.layerobjects.GO;
import data.layer.layerobjects.Tag;
import data.layer.layerobjects.TagObject;
import window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the map contains all layers
 */
public class GameMap extends TagObject {
	private Window w;

	private int width, height, tileSize;		//width, height and tileSize of the map
	private Map<String, Layer> layers;			//Layers saved to their name

	public GameMap(Window w, int width, int height, int tileSize) {
		this(w, width, height, tileSize, true);
	}

	/**
	 *
	 * @param width width of the map
	 * @param height height of the map
	 * @param tileSize tilesize of the map
	 * @param b true if the default layers should be added
	 */
	public GameMap(Window w, int width, int height, int tileSize, boolean b) {
		this.w = w;

		layers = new HashMap<>();
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;

		TextureHandler.createError(tileSize);

		if(b) {
			addLayer("Background", new FreeLayer(1.0f, width, height, tileSize));
			addLayer("Tile", new TileLayer(w, 0.5f, width, height, tileSize));
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
	public Layer removeLayer(String name) {
		Layer out = layers.get(name);
		layers.remove(name);
		return out;
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

	@Override
	public GameMap clone() {
		GameMap out = new GameMap(w, width, height, tileSize);
		ArrayList<java.util.Map.Entry<String, Layer>> listOfEntry = new ArrayList<>(layers.entrySet());
		for(java.util.Map.Entry<String, Layer> e: listOfEntry) {
			Layer l = e.getValue();
			if(l instanceof TileLayer) {
				TileLayer tl = (TileLayer) l;
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

	@Override
	public String accept(Exporter exporter, Object o2) {
		return exporter.export(this);
	}
}
