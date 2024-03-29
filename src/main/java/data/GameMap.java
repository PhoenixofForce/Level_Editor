package data;

import data.io.exporter.Exporter;
import data.io.exporter.ExporterData;
import data.layer.*;
import data.layer.layerobjects.TagObject;
import lombok.Getter;
import window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameMap extends TagObject {
	private final Window window;

	private final int width;
	private final int height;
	private final int tileSize;
	private final Map<String, Layer> layers;

	public GameMap(Window window, int width, int height, int tileSize) {
		this(window, width, height, tileSize, true);
	}

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

	public int[] getBounds() {
		int sx = Integer.MAX_VALUE,
				sy = Integer.MAX_VALUE,
				bx = Integer.MIN_VALUE,
				by = Integer.MIN_VALUE;

		for(String s: layers.keySet()) {
			Layer l = layers.get(s);

			Location smallestPoint = l.smallestPoint();
			Location biggestPoint = l.biggestPoint();

			if(smallestPoint.x < sx && smallestPoint.x != -1) sx = (int) Math.floor(smallestPoint.x);
			if(smallestPoint.y < sy && smallestPoint.y != -1) sy = (int) Math.floor(smallestPoint.y);
			if(biggestPoint.x > bx) bx = (int) Math.ceil(biggestPoint.x);
			if(biggestPoint.y > by) by = (int) Math.ceil(biggestPoint.y);

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
	public Object accept(Exporter exporter, ExporterData data) {
		return exporter.export(this);
	}
}
