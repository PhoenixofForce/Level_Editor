package data.maps;

import data.Location;
import data.TextureHandler;
import data.io.exporter.Exporter;
import data.layer.*;
import data.layer.layerobjects.TagObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * the map contains all layers
 */
public abstract class GameMap extends TagObject {

	protected final int width,
						height;
	protected final int	tileWidth,
						tileHeight;
	protected final Map<String, Layer> layers;			//Layers saved to their name

	public GameMap(int width, int height, int tileSize) {
		this(width, height, tileSize, tileSize);
	}

	public GameMap(int width, int height, int tileWidth, int tileHeight) {
		layers = new HashMap<>();
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		TextureHandler.createError(tileWidth);
	}

	public Optional<Polygon> getCustomTileHighlight() {
		return Optional.empty();
	}

	public abstract Location getDrawingOffset();

	public abstract Location worldToMapSpace(Location screenLocation);
	public abstract Location mapToWorldSpace(Location mapLocation);
	public abstract BufferedImage generateStaticTileGrid();

	public Layer getLayer(String name) {
		return layers.get(name);
	}

	public GameMap addLayer(String name, Layer layer) {
		layers.put(name, layer);
		return this;
	}

	public GameMap addTileLayer(String name, float depth) {
		return addLayer(name, new TileLayer(depth, width, height, tileWidth, tileHeight));
	}

	public GameMap addFreeLayer(String name, float depth) {
		return addLayer(name, new FreeLayer(depth, width, height, tileWidth, tileHeight));
	}

	public GameMap addAreaLayer(String name, float depth) {
		return addLayer(name, new AreaLayer(depth, width, height, tileWidth, tileHeight));
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

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
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

		return new int[]{ sx, sy, bx, by };
	}

	@Override
	public String getText() {
		return "Map";
	}

	protected void cloneLayersTo(GameMap other) {
		List<Map.Entry<String, Layer>> namedLayers = new ArrayList<>(layers.entrySet());

		for(Map.Entry<String, Layer> e: namedLayers) {
			Layer l = e.getValue();
			if(l instanceof TileLayer tl) {
				other.addLayer(e.getKey(), tl.clone());
				continue;
			}
			if(l instanceof FreeLayer tl) {
				other.addLayer(e.getKey(), tl.clone());
				continue;
			}
			if(l instanceof AreaLayer tl) {
				other.addLayer(e.getKey(), tl.clone());
			}
		}
	}

	@Override
	public Object accept(Exporter exporter, Object... o2) {
		return exporter.export(this);
	}
}
