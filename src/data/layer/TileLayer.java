package data.layer;

import data.exporter.Exporter;
import data.Location;
import data.Util;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.TagObject;
import data.TextureHandler;
import window.Window;

import java.awt.Graphics;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * a layer where the user can place textures on a grid
 * this whole class could benefit from a rewrite
 */
public class TileLayer implements Layer {

	private static final int[] MAPPING = new int[]{   0,  16,   24,   8,
													 64, 208,  248, 104,
													 66, 214,  255, 107,
													  2,  22,   31,  11,
			   										 75,  80,   88,  72,
													106,  82,   90,  74,
													 86,  18,   26,  10,
													210, 218,  250, 122,
													219, 222,   -1, 123,
													126,  94,   95,  91,
													120, 216,  127, 223,
													 27,  30,  251, 254};

	private float depth;					//the drawing depth
	private String[][] tileNames;			//the texture grid

	private int width, height, tileSize;	//width, height and tilesize of the map
	private Random r;

	private Window window;

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

	//this needs a rewrite
	private void update(int x, int y, boolean center) {
		if(window.getAutoTile() == 0) return;

		if (x >= 0 && y >= 0 && x < width && y < height) {
			String name = tileNames[y][x];
			if (name == null) {
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
				return;
			}

			if (name.split("_")[1].equalsIgnoreCase("block")) {
				String[] parts = name.split("_");
				String spriteSheet = parts[0];
				String blockName = parts[2];
				String blockPart = parts[3];

				int out = 0;
				if(window.getAutoTile() == 1) {
					if (y != 0 && tileNames[y - 1][x] != null && Util.textureEquals(window.getAutoTile(), tileNames[y - 1][x], name))
						out ^= 12;
					if (y != height - 1 && tileNames[y + 1][x] != null && Util.textureEquals(window.getAutoTile(), tileNames[y + 1][x], name))
						out ^= 4;
					if (x != 0 && tileNames[y][x - 1] != null && Util.textureEquals(window.getAutoTile(), tileNames[y][x - 1], name))
						out ^= 3;
					if (x != width - 1 && tileNames[y][x + 1] != null && Util.textureEquals(window.getAutoTile(), tileNames[y][x + 1], name))
						out ^= 1;
				} else if(window.getAutoTile() == 2) {

					if (y != 0 && tileNames[y - 1][x] != null && Util.textureEquals(window.getAutoTile(), tileNames[y - 1][x], name))
						out ^= 2;
					if (y != height - 1 && tileNames[y + 1][x] != null && Util.textureEquals(window.getAutoTile(), tileNames[y + 1][x], name))
						out ^= 64;
					if (x != 0 && tileNames[y][x - 1] != null && Util.textureEquals(window.getAutoTile(), tileNames[y][x - 1], name))
						out ^= 8;
					if (x != width - 1 && tileNames[y][x + 1] != null && Util.textureEquals(window.getAutoTile(), tileNames[y][x + 1], name))
						out ^= 16;

					boolean w = (out&8)!=0, n = (out&2)!=0, e = (out&16)!=0, s = (out&64)!=0;
					if (x!= 0 && y != 0 && tileNames[y - 1][x-1] != null && Util.textureEquals(this.window.getAutoTile(), tileNames[y - 1][x - 1], name))
						out ^= w && n? 1: 0;
					if (x!= width-1 && y != 0 && tileNames[y - 1][x+1] != null && Util.textureEquals(this.window.getAutoTile(), tileNames[y - 1][x + 1], name))
						out ^= e && n? 4: 0;

					if (x!= 0 && y != height-1 && tileNames[y + 1][x-1] != null && Util.textureEquals(this.window.getAutoTile(), tileNames[y + 1][x - 1], name))
						out ^= w && s? 32: 0;
					if (x!= width-1 && y != height-1 && tileNames[y + 1][x+1] != null && Util.textureEquals(this.window.getAutoTile(), tileNames[y + 1][x + 1], name))
						out ^= e && s? 128: 0;

					out = find(MAPPING, out);
				}

				name = spriteSheet + "_block_" + blockName + "_" + out;

				if(blockPart.equalsIgnoreCase(name.split("_")[3])) {
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
					return;
				}

				int count = TextureHandler.getBlockCount(name);
				if(count > 0) {
					int random = r.nextInt(count);
					name += "_"+random;
				}
				String tile = TextureHandler.existsImagePng(name)? name: "error_"+tileSize;
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
	public boolean drag(float x, float y, float targetX, float targetY) {
		return false;
	}

	@Override
	public TagObject remove(float x2, float y2) {
		int x = (int) x2;
		int y = (int) y2;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			String oldName = tileNames[y][x];
			tileNames[y][x] = null;

			//update(x, y, true) ???
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
			return oldName == null? null: new GameObject(oldName, x, y, 1, 1);
		}
		return null;
	}

	public List<Location> fill(Area sel, String name, float x2, float y2) {
		int x = (int) x2;
		int y = (int) y2;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			String oldName = tileNames[y][x];
			boolean bool1 = check(name, x, y);
			boolean bool2 = (oldName != null && name != null && name.contains("block") && oldName.contains("block") && name.split("_")[2].equalsIgnoreCase(oldName.split("_")[2]));
			if(bool1 || bool2) return null;

			List<Location> out = new ArrayList<>();
			Stack<Location> stack = new Stack<>();
			stack.push(new Location(x, y));

			while (!stack.isEmpty()) {
				Location i = stack.pop();

				if (check(oldName, i.x, i.y) && (sel == null || (sel != null && sel.contains(i.x* window.getMap().getTileSize(), i.y* window.getMap().getTileSize())))) {
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

	public void fill(Area sel, String name) {
		int x = sel.getBounds().x+1;
		int y = sel.getBounds().y+1;
		if (x >= 0 && y >= 0 && x < width && y < height) {

			Stack<Location> stack = new Stack<>();
			stack.push(new Location(x, y));

			while (!stack.isEmpty()) {
				Location i = stack.pop();

				if ((sel == null || (sel != null && sel.contains(i.x* window.getMap().getTileSize(), i.y* window.getMap().getTileSize())))) {
					set(name, i.x, i.y, false);

					if (i.x > 0) stack.push(new Location(i.x - 1, i.y));
					if (i.y > 0) stack.push(new Location(i.x, i.y - 1));
					if (i.x < width - 1) stack.push(new Location(i.x + 1, i.y));
					if (i.y < height - 1) stack.push(new Location(i.x, i.y + 1));
				}
			}
		}
	}

	//TODO: test if equal to Util.onlyMethod()
	private boolean check(String oldName, float y, float x) {
		String name = tileNames[(int)x][(int)y];
		boolean bool1 = (oldName == null && name == null);
		boolean bool2 = ((name != null && oldName != null) && name.equals(oldName));
		boolean bool3 = (window.getAutoTile() > 0 && Util.textureEquals(window.getAutoTile(), oldName, name));
		return bool1 || bool2 || bool3;
	}

	@Override
	public GameObject select(float x, float y) {
		return null;
	}

	/**
	 * @return the tile grid
	 */
	public String[][] getTileNames() {
		return tileNames;
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
	public float smallestX() {
		int smallestX = Integer.MAX_VALUE;
		for (int x = 0; x < tileNames[0].length; x++) {
			for (int y = 0; y < tileNames.length; y++) {
				if (tileNames[y][x] == null) continue;
				if(x < smallestX) smallestX = x;
			}
		}
		return smallestX == Integer.MAX_VALUE? -1: smallestX;
	}

	@Override
	public float smallestY() {
		int smallestY = Integer.MAX_VALUE;
		for (int x = 0; x < tileNames[0].length; x++) {
			for (int y = 0; y < tileNames.length; y++) {
				if (tileNames[y][x] == null) continue;
				if(y < smallestY) smallestY = y;
			}
		}
		return smallestY == Integer.MAX_VALUE? -1: smallestY;
	}

	@Override
	public float biggestX() {
		int smallestX = Integer.MIN_VALUE;
		for (int x = 0; x < tileNames[0].length; x++) {
			for (int y = 0; y < tileNames.length; y++) {
				if (tileNames[y][x] == null) continue;
				if(x > smallestX) smallestX = x;
			}
		}
		return smallestX == Integer.MIN_VALUE? -1: smallestX;
	}

	@Override
	public float biggestY() {
		int smallestY = Integer.MIN_VALUE;
		for (int x = 0; x < tileNames[0].length; x++) {
			for (int y = 0; y < tileNames.length; y++) {
				if (tileNames[y][x] == null) continue;
				if(y > smallestY) smallestY = y;
			}
		}
		return smallestY == Integer.MIN_VALUE? -1: smallestY;
	}

	@Override
	public TileLayer clone() {
		TileLayer out = new TileLayer(window, depth, width, height, tileSize);
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				out.tileNames[y][x] = tileNames[y][x];
			}
		}
		return out;
	}

	private static int find(int[] a, int f) {
		for(int i = 0; i < a.length; i++) {
			if(a[i]==f) return i;
		}
		return -1;
	}

	@Override
	public void add(TagObject to) {
		if(to instanceof GameObject) {
			GameObject gameObject = (GameObject) to;
			this.set(gameObject.name, gameObject.x, gameObject.y, false);
		}
	}

	@Override
	public Object accept(Exporter exporter, Object... o2) {
		return exporter.export(this, o2);
	}
}
