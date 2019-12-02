package data.layer;

import data.Location;
import data.layer.layerobjects.GO;
import data.TextureHandler;
import window.Window;

import java.awt.Graphics;

import java.awt.geom.Area;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * a layer where the user can place textures on a grid
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

	private Window w;

	public TileLayer(Window w, float depth, String[][] tiles, int tileSize) {
		this.depth = depth;
		this.tileNames = tiles;
		this.tileSize = tileSize;
		this.width = tiles[0].length;
		this.height = tiles.length;
		r = new Random();
		this.w = w;
	}

	public TileLayer(Window w, float depth, int width, int height, int tileSize) {
		this(w, depth, new String[height][width], tileSize);
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
		if(0==w.getAutoTile()) return;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			String name = tileNames[y][x];
			if (name == null) {
				if(center) {
					update(x+1, y, false);
					update(x-1, y, false);
					update(x, y-1, false);
					update(x, y+1, false);
					if(w.getAutoTile() == 2) {
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
				if(w.getAutoTile() == 1) {
					if (y != 0 && tileNames[y - 1][x] != null && tileNames[y - 1][x].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= 12;
					if (y != height - 1 && tileNames[y + 1][x] != null && tileNames[y + 1][x].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= 4;
					if (x != 0 && tileNames[y][x - 1] != null && tileNames[y][x - 1].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= 3;
					if (x != width - 1 && tileNames[y][x + 1] != null && tileNames[y][x + 1].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= 1;
				} else if(w.getAutoTile() == 2) {

					if (y != 0 && tileNames[y - 1][x] != null && tileNames[y - 1][x].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= 2;
					if (y != height - 1 && tileNames[y + 1][x] != null && tileNames[y + 1][x].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= 64;
					if (x != 0 && tileNames[y][x - 1] != null && tileNames[y][x - 1].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= 8;
					if (x != width - 1 && tileNames[y][x + 1] != null && tileNames[y][x + 1].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= 16;

					boolean w = (out&8)!=0, n = (out&2)!=0, e = (out&16)!=0, s = (out&64)!=0;
					if (x!= 0 && y != 0 && tileNames[y - 1][x-1] != null && tileNames[y - 1][x-1].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= w && n? 1: 0;
					if (x!= width-1 && y != 0 && tileNames[y - 1][x+1] != null && tileNames[y - 1][x+1].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= e && n? 4: 0;

					if (x!= 0 && y != height-1 && tileNames[y + 1][x-1] != null && tileNames[y + 1][x-1].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= w && s? 32: 0;
					if (x!= width-1 && y != height-1 && tileNames[y + 1][x+1] != null && tileNames[y + 1][x+1].startsWith(spriteSheet + "_block_" + blockName + "_"))
						out ^= e && s? 128: 0;


					out = find(MAPPING, out);
				}

				name = spriteSheet+"_block_"+blockName+"_" + out;

				if(blockPart.equalsIgnoreCase(name.split("_")[3])) {
					if(center) {
						update(x+1, y, false);
						update(x-1, y, false);
						update(x, y-1, false);
						update(x, y+1, false);
						if(w.getAutoTile() == 2) {
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
			if(w.getAutoTile() == 2) {
				update(x + 1, y - 1, false);
				update(x - 1, y + 1, false);
				update(x - 1, y - 1, false);
				update(x + 1, y + 1, false);
			}
		}
	}

	@Override
	public void drag(float x, float y, float targetX, float targetY) {

	}

	@Override
	public boolean remove(float x2, float y2) {
		int x = (int) x2;
		int y = (int) y2;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			tileNames[y][x] = null;

			update(x+1, y, false);
			update(x-1, y, false);
			update(x, y-1, false);
			update(x, y+1, false);
			if(w.getAutoTile() == 2) {
				update(x + 1, y - 1, false);
				update(x - 1, y + 1, false);
				update(x - 1, y - 1, false);
				update(x + 1, y + 1, false);
			}
			return true;
		}
		return false;
	}

	public void fill(Area sel, String name, float x2, float y2) {
		int x = (int) x2;
		int y = (int) y2;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			String oldName = tileNames[y][x];
			boolean bool1 = check(name, x, y);
			boolean bool2 = (oldName != null && name != null && name.contains("block") && oldName.contains("block") && name.split("_")[2].equalsIgnoreCase(oldName.split("_")[2]));
			if(bool1 || bool2) return;

			Stack<Location> stack = new Stack<>();
			stack.push(new Location(x, y));

			while (!stack.isEmpty()) {
				Location i = stack.pop();

				if (check(oldName, i.x, i.y) && (sel == null || (sel != null && sel.contains(i.x*w.getMap().getTileSize(), i.y*w.getMap().getTileSize())))) {
					set(name, i.x, i.y, false);

					if (i.x > 0) stack.push(new Location(i.x - 1, i.y));
					if (i.y > 0) stack.push(new Location(i.x, i.y - 1));
					if (i.x < width - 1) stack.push(new Location(i.x + 1, i.y));
					if (i.y < height - 1) stack.push(new Location(i.x, i.y + 1));
				}
			}
		}
	}

	public void fill(Area sel, String name) {
		int x = sel.getBounds().x+1;
		int y = sel.getBounds().y+1;
		if (x >= 0 && y >= 0 && x < width && y < height) {

			Stack<Location> stack = new Stack<>();
			stack.push(new Location(x, y));

			while (!stack.isEmpty()) {
				Location i = stack.pop();

				if ((sel == null || (sel != null && sel.contains(i.x*w.getMap().getTileSize(), i.y*w.getMap().getTileSize())))) {
					set(name, i.x, i.y, false);

					if (i.x > 0) stack.push(new Location(i.x - 1, i.y));
					if (i.y > 0) stack.push(new Location(i.x, i.y - 1));
					if (i.x < width - 1) stack.push(new Location(i.x + 1, i.y));
					if (i.y < height - 1) stack.push(new Location(i.x, i.y + 1));
				}
			}
		}
	}

	private boolean check(String oldName, float y, float x) {
		String name = tileNames[(int)x][(int)y];
		boolean bool1 = (oldName == null && name == null);
		boolean bool2 = ((name != null && oldName != null) && name.equals(oldName));
		boolean bool3 = (w.getAutoTile() > 0 && oldName != null && name != null && name.contains("block") && oldName.contains("block") && name.split("_")[2].equalsIgnoreCase(oldName.split("_")[2]));
		return bool1 || bool2 || bool3;
	}

	@Override
	public GO select(float x, float y) {
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
	public void draw(Graphics g) {
		for (int x = 0; x < tileNames[0].length; x++) {
			for (int y = 0; y < tileNames.length; y++) {
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
		TileLayer out = new TileLayer(w, depth, width, height, tileSize);
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				out.tileNames[y][x] = tileNames[y][x];
			}
		}
		return out;
	}

	@Override
	public String toMapFormat(List<String> names, float sx, float sy, float bx, float by) {
		int startX = Math.max(0, (int) Math.floor(sx));
		int startY = Math.max(0, (int) Math.floor(sy));
		int endX = bx == -1? tileNames[0].length: Math.min(tileNames[0].length, (int) Math.ceil(bx))+1;
		int endY = by == -1? tileNames.length: Math.min(tileNames.length, (int) Math.ceil(by)+1);

		int width = endX - startX;
		int height = endY - startY;

		String out = "[layer; " + depth + "; " + width + "; " + height + "; ";

		System.out.println(startX + " " + startY);
		System.out.println(endX + " " + endY);

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				out += (names != null? names.indexOf(tileNames[y][x])+1: tileNames[y][x]) + (y < tileNames.length-1? ", ": "");
			}

			if(x < tileNames[0].length-1) out += "; ";
		}

		return out + "]\n";
	}

	private static int find(int[] a, int f) {
		for(int i = 0; i < a.length; i++) {
			if(a[i]==f) return i;
		}
		return -1;
	}
}
