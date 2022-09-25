package data;

import data.io.exporter.Exporter;
import data.layer.*;
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
	private final Window window;

	private final int width;
	private final int height;
	private final int tileSize;		//width, height and tileSize of the map
	private final Map<String, Layer> layers;			//Layers saved to their name

	public GameMap(Window window, int width, int height, int tileSize) {
		this(window, width, height, tileSize, true);
	}

	/**
	 *
	 * @param width width of the map
	 * @param height height of the map
	 * @param tileSize tilesize of the map
	 * @param addDefaultLayer true if the default layers should be added
	 */
	public GameMap(Window window, int width, int height, int tileSize, boolean addDefaultLayer) {
		this.window = window;

		layers = new HashMap<>();
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;

		TextureHandler.createError(tileSize);

		if(addDefaultLayer) {
			addLayer("Background", new FreeLayer(1.0f, width, height, tileSize));
			addLayer("Tile", new TileLayer(window, 0.5f, width, height, tileSize));
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

	public Layer removeLayer(String name) {
		Layer out = layers.get(name);
		layers.remove(name);
		return out;
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

	public int[] getBounds() {
		int sx = Integer.MAX_VALUE,
				sy = Integer.MAX_VALUE,
				bx = Integer.MIN_VALUE,
				by = Integer.MIN_VALUE;

		for(String s: layers.keySet()) {
			Layer l = layers.get(s);

			float csx = l.smallestX(),
					csy = l.smallestY(),
					cbx = l.biggestX(),
					cby = l.biggestY();

			if(csx < sx && csx != -1) sx = (int) Math.floor(csx);
			if(csy < sy && csy != -1) sy = (int) Math.floor(csy);
			if(cbx > bx) bx = (int) Math.ceil(cbx);
			if(cby > by) by = (int) Math.ceil(cby);

		}

		return new int[]{sx, sy, bx, by};
	}

	@Override
	public String getText() {
		return "Map";
	}

	@Override
	public GameMap clone() {
		GameMap out = new GameMap(window, width, height, tileSize);
		List<Map.Entry<String, Layer>> listOfEntry = new ArrayList<>(layers.entrySet());

		for(Map.Entry<String, Layer> e: listOfEntry) {
			Layer l = e.getValue();
			if(l instanceof TileLayer tl) {
				out.addLayer(e.getKey(), tl.clone());
				continue;
			}
			if(l instanceof FreeLayer tl) {
				out.addLayer(e.getKey(), tl.clone());
				continue;
			}
			if(l instanceof AreaLayer tl) {
				out.addLayer(e.getKey(), tl.clone());
			}
		}
		return out;
	}

	@Override
	public Object accept(Exporter exporter, Object... o2) {
		return exporter.export(this);
	}
}
