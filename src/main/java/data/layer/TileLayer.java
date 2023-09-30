package data.layer;

import data.AutoTiling;
import data.io.exporter.Exporter;
import data.Location;
import data.io.exporter.ExporterData;
import lombok.Getter;
import util.Util;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.TagObject;
import data.TextureHandler;
import window.Window;

import javax.swing.text.html.Option;
import java.awt.Graphics;

import java.awt.geom.Area;
import java.util.*;

/**
 * a layer where the user can place textures on a grid
 * this whole class could benefit from a rewrite
 */
public class TileLayer implements Layer {

	private final float depth;

	@Getter
	private final String[][] tileNames;

	private final int width;
	private final int height;
	private final int tileSize;
	private final Random r;

	private final Window window;
	public TileLayer(Window window, float depth, String[][] tiles, int tileSize) {
		this.depth = depth;
		this.tileNames = tiles;
		this.tileSize = tileSize;
		this.width = tiles[0].length;
		this.height = tiles.length;

		r = new Random();
		this.window = window;
	}

	public TileLayer(Window window, float depth, int width, int height, int tileSize) {
		this(window, depth, new String[height][width], tileSize);
	}

	@Override
	public void set(String name, float x2, float y2, boolean drag) {
		int x = (int) x2;
		int y = (int) y2;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			tileNames[y][x] = name;
			update(x, y, true);
		}
	}

	@Override
	public Optional<String> textureAt(float x2, float y2) {
		int x = (int) x2;
		int y = (int) y2;

		return Optional.ofNullable(tileNames[y][x]);
	}

	private void update(int x, int y, boolean center) {
		if(window.getAutoTile() == 0) return;

		if (x >= 0 && y >= 0 && x < width && y < height && tileNames[y][x] != null) {
			String name = tileNames[y][x];

			//if new tile is a block
			if (name.split("_")[1].equalsIgnoreCase("block")) {
				String tileNameStart = name.substring(0, name.lastIndexOf("_"));

				int tileID = AutoTiling.calcAutoTileIndex(tileNames, x, y, name, width, height);
				name = tileNameStart + "_" + tileID;

				int count = TextureHandler.getBlockCount(name + "_");
				if(count > 0) {
					int random = r.nextInt(count);
					name += "_" + random;
				}

				String tile = TextureHandler.existsImagePng(name)? name: tileNameStart + "_0";
				if(!TextureHandler.existsImagePng(tile)) tile = "error_" + tileSize;

				if(!tile.equals(name)) System.err.println("MSSING TEXTURE: " + name);
				tileNames[y][x] = tile;
			}
		}

		if(center) {
			update(x+1, y, false);
			update(x-1, y, false);
			update(x, y-1, false);
			update(x, y+1, false);
			if(window.getAutoTile() == 2) {
				update(x + 1, y - 1, false);
				update(x - 1, y + 1, false);
				update(x - 1, y - 1, false);
				update(x + 1, y + 1, false);
			}
		}
	}

	@Override
	public Optional<TagObject> remove(float x2, float y2) {
		int x = (int) x2;
		int y = (int) y2;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			String oldName = tileNames[y][x];
			tileNames[y][x] = null;

			update(x, y, true);
			if(oldName != null) {
				return Optional.of(new GameObject(oldName, x, y, 1, 1));
			}
		}

		return Optional.empty();
	}

	public List<Location> fill(Area sel, String name, float x2, float y2) {
		int x = (int) x2;
		int y = (int) y2;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			String oldName = tileNames[y][x];
			System.out.println(Util.textureEquals(window.getAutoTile(), oldName, name));
			if(Util.textureEquals(window.getAutoTile(), oldName, name)) return null;

			List<Location> out = new ArrayList<>();
			Set<Location> alreadyFilled = new HashSet<>();

			Stack<Location> stack = new Stack<>();
			stack.push(new Location(x, y));

			while (!stack.isEmpty()) {
				Location i = stack.pop();

				if(alreadyFilled.contains(i)) continue;
				alreadyFilled.add(i);

				String newName = tileNames[(int) i.y][(int) i.x];
				boolean selectionContainsPoint = sel == null || sel.contains(i.x * tileSize, i.y * tileSize);
				if (Util.textureEquals(window.getAutoTile(), oldName, newName) && selectionContainsPoint) {
					set(name, i.x, i.y, false);
					out.add(i);

					if (i.x > 0) stack.push(new Location(i.x - 1, i.y));
					if (i.y > 0) stack.push(new Location(i.x, i.y - 1));
					if (i.x < width - 1) stack.push(new Location(i.x + 1, i.y));
					if (i.y < height - 1) stack.push(new Location(i.x, i.y + 1));
				}
			}

			return out;
		}

		return null;
	}

	@Override
	public float depth() {
		return depth;
	}

	@Override
	public void draw(Graphics g, Location topLeft, Location downRight) {
		int startX = (int) Math.max(0,  topLeft == null? 0: Math.floor(topLeft.x));
		int endX = (int) Math.min(tileNames[0].length,  downRight == null? tileNames[0].length: Math.ceil(downRight.x));
		int startY = (int) Math.max(0,  topLeft == null? 0: Math.floor(topLeft.y));
		int endY = (int) Math.min(tileNames.length,  downRight == null? tileNames.length: Math.ceil(downRight.y));

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				if (tileNames[y][x] == null) continue;
				g.drawImage(TextureHandler.getImagePng(tileNames[y][x]), x * tileSize, y * tileSize, null);
			}
		}
	}

	@Override
	public Location smallestPoint() {
		int smallestX = Integer.MAX_VALUE;
		for (int x = 0; x < tileNames[0].length; x++) {
			for (String[] tileName : tileNames) {
				if (tileName[x] == null) continue;
				if (x < smallestX) {
					smallestX = x;
					break;
				}
			}
		}
		smallestX = smallestX == Integer.MAX_VALUE? -1: smallestX;

		int smallestY = Integer.MAX_VALUE;
		for (int x = 0; x < tileNames[0].length; x++) {
			for (int y = 0; y < tileNames.length; y++) {
				if (tileNames[y][x] == null) continue;
				if(y < smallestY) smallestY = y;
			}
		}
		smallestY =  smallestY == Integer.MAX_VALUE? -1: smallestY;

		return new Location(smallestX, smallestY);
	}


	@Override
	public Location biggestPoint() {
		int biggestX = Integer.MIN_VALUE;
		for (int x = 0; x < tileNames[0].length; x++) {
			for (String[] tileName : tileNames) {
				if (tileName[x] == null) continue;
				if (x > biggestX) {
					biggestX = x;
					break;
				}
			}
		}
		biggestX = biggestX == Integer.MIN_VALUE? -1: biggestX;

		int biggestY = Integer.MIN_VALUE;
		for (int x = 0; x < tileNames[0].length; x++) {
			for (int y = 0; y < tileNames.length; y++) {
				if (tileNames[y][x] == null) continue;
				if(y > biggestY) biggestY = y;
			}
		}
		biggestY = biggestY == Integer.MIN_VALUE? -1: biggestY;

		return new Location(biggestX, biggestY);
	}


	@Override
	public TileLayer clone() {
		TileLayer out = new TileLayer(window, depth, width, height, tileSize);
		for(int y = 0; y < height; y++) {
			if (width >= 0) System.arraycopy(tileNames[y], 0, out.tileNames[y], 0, width);
		}
		return out;
	}

	@Override
	public void add(TagObject to) {
		if(to instanceof GameObject gameObject) {
			this.set(gameObject.name, gameObject.x, gameObject.y, false);
		}
	}

	@Override
	public Object accept(Exporter exporter, ExporterData data) {
		return exporter.export(this, data);
	}
}
