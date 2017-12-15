package data;


import java.util.HashMap;
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
}
