package data.layer;

import data.io.exporter.Exporter;
import data.Location;
import util.Util;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.TagObject;
import data.TextureHandler;
import window.Window;

import java.awt.Graphics;

import java.awt.geom.Area;
import java.util.*;

/**
 * a layer where the user can place textures on a grid
 * this whole class could benefit from a rewrite
 */
public class TileLayer implements Layer {

	private static final int[] AUTOTILE_ID_TO_INDEX = new int[]{   0,  16,   24,   8,
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

	private final float depth;					//the drawing depth
	private final String[][] tileNames;			//the texture grid

	private final int width;
	private final int height;
	private final int tileSize;	//width, height and tilesize of the map
	private final Random r;

	private final Window window;
	public TileLayer(float depth, String[][] tiles, int tileSize) {
		this.depth = depth;
		this.tileNames = tiles;
		this.tileSize = tileSize;
		this.width = tiles[0].length;
		this.height = tiles.length;

		r = new Random();
		this.window = Window.INSTANCE;
	}

	public TileLayer(float depth, int width, int height, int tileSize) {
		this(depth, new String[height][width], tileSize);
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

	private void update(int x, int y, boolean center) {
		if(window.getAutoTile() == 0) return;

		if (x >= 0 && y >= 0 && x < width && y < height && tileNames[y][x] != null) {
			String name = tileNames[y][x];

			//if new tile is a block
			if (name.split("_")[1].equalsIgnoreCase("block")) {
				String tileNameStart = name.substring(0, name.lastIndexOf("_"));

				int out = calcAutoTileIndex(x, y, name);
				name = tileNameStart + "_" + out;

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

	/*
	 * The autotiling works by assigning each surrounding tile an id, so that each id is a multiple of 2
	 * (bc they have one 1 in binary)
	 *
	 * ( 1 )( 2 )( 4 )
	 * ( 8 )( x )( 16)
	 * ( 32)( 64)(128)
	 *
	 * If a tile is present the output gets xored(the corresponding bit gets activated).
	 * In Autotiling mode 1 just the 4 directly adjacent are considered, in mode 2 also the adjacent corners(if the edges next to the corner are present)
	 *
	 * Also, because the resulting number is not obvious they get mapped to a different labeling scheme, so the tiles can fit in my preferred layout
	 *
	 */
	private int calcAutoTileIndex(int x, int y, String name) {
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

			boolean w = (out&8)!=0,
					n = (out&2)!=0,
					e = (out&16)!=0,
					s = (out&64)!=0;

			/* Check corners only if adjacent edges are present
				1#.   Corner 1 gets checked
				##.
				2..   Corner 2 does not
			 */
			if (x!= 0 && y != 0 && tileNames[y - 1][x-1] != null && Util.textureEquals(this.window.getAutoTile(), tileNames[y - 1][x - 1], name))
				out ^= w && n? 1: 0;
			if (x!= width-1 && y != 0 && tileNames[y - 1][x+1] != null && Util.textureEquals(this.window.getAutoTile(), tileNames[y - 1][x + 1], name))
				out ^= e && n? 4: 0;

			if (x!= 0 && y != height-1 && tileNames[y + 1][x-1] != null && Util.textureEquals(this.window.getAutoTile(), tileNames[y + 1][x - 1], name))
				out ^= w && s? 32: 0;
			if (x!= width-1 && y != height-1 && tileNames[y + 1][x+1] != null && Util.textureEquals(this.window.getAutoTile(), tileNames[y + 1][x + 1], name))
				out ^= e && s? 128: 0;

			out = Util.arrayIndexOf(AUTOTILE_ID_TO_INDEX, out);
		}

		return out;
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

			update(x, y, true);
			return oldName == null? null: new GameObject(oldName, x, y, 1, 1);
		}
		return null;
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

				if (Util.textureEquals(window.getAutoTile(), oldName, tileNames[(int) i.y][(int) i.x]) && (sel == null || sel.contains(i.x * window.getMap().getTileSize(), i.y * window.getMap().getTileSize()))) {
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
			for (String[] tileName : tileNames) {
				if (tileName[x] == null) continue;
				if (x < smallestX) {
					smallestX = x;
					break;
				}
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
			for (String[] tileName : tileNames) {
				if (tileName[x] == null) continue;
				if (x > smallestX) {
					smallestX = x;
					break;
				}
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
		TileLayer out = new TileLayer(depth, width, height, tileSize);
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
	public Object accept(Exporter exporter, Object... o2) {
		return exporter.export(this, o2);
	}
}
