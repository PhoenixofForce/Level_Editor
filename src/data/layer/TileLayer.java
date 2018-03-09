package data.layer;

import data.Location;
import data.layer.layerobjects.GO;
import data.TextureHandler;

import java.awt.Graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * a layer where the user can place textures on a grid
 */
public class TileLayer implements Layer {

	private float depth;					//the drawing depth
	private String[][] tileNames;			//the texture grid

	private int width, height, tileSize;	//width, height and tilesize of the map

	public TileLayer(float depth, String[][] tiles, int tileSize) {
		this.depth = depth;
		this.tileNames = tiles;
		this.tileSize = tileSize;
		this.width = tiles[0].length;
		this.height = tiles.length;
	}

	public TileLayer(float depth, int width, int height, int tileSize) {
		tileNames = new String[height][width];
		this.depth = depth;

		this.width = width;
		this.height = height;
		this.tileSize = tileSize;
	}

	@Override
	public void set(String name, float x2, float y2, boolean drag) {
		int x = (int) x2;
		int y = (int) y2;
		if (x >= 0 && y >= 0 && x < width && y < height) {
			tileNames[y][x] = name;
			if(name != null && name.split("_")[1].equalsIgnoreCase("block")) {
				update(x, y);
				update(x+1, y);
				update(x-1, y);
				update(x, y-1);
				update(x, y+1);
			}
		}
	}

	private void update(int x, int y) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			String name = tileNames[y][x];
			if (name == null) return;
			if (name.split("_")[1].equalsIgnoreCase("block")) {
				int out = 0;
				if (y != 0 && tileNames[y - 1][x] != null && tileNames[y - 1][x].startsWith(name.substring(0, name.lastIndexOf("_")+1)))
					out += 2;
				if (y != width-1&&tileNames[y + 1][x] != null && tileNames[y + 1][x].startsWith(name.substring(0, name.lastIndexOf("_")+1)))
					out += 4;
				if (x !=0 &&tileNames[y][x - 1] != null && tileNames[y][x - 1].startsWith(name.substring(0, name.lastIndexOf("_")+1)))
					out += 1;
				if (x != height-1&&tileNames[y][x + 1] != null && tileNames[y][x + 1].startsWith(name.substring(0, name.lastIndexOf("_")+1)))
					out += 8;
				
				switch (out) {
					case 0:
						name = name.substring(0, name.lastIndexOf("_")+1) + "0";
						break;
					case 1:
						name = name.substring(0, name.lastIndexOf("_")+1) + "3";
						break;
					case 2:
						name = name.substring(0, name.lastIndexOf("_")+1) + "12";
						break;
					case 3:
						name = name.substring(0, name.lastIndexOf("_")+1) + "15";
						break;
					case 4:
						name = name.substring(0, name.lastIndexOf("_")+1) + "4";
						break;
					case 5:
						name = name.substring(0, name.lastIndexOf("_")+1) + "7";
						break;
					case 6:
						name = name.substring(0, name.lastIndexOf("_")+1) + "8";
						break;
					case 7:
						name = name.substring(0, name.lastIndexOf("_")+1) + "11";
						break;
					case 8:
						name = name.substring(0, name.lastIndexOf("_")+1) + "1";
						break;
					case 9:
						name = name.substring(0, name.lastIndexOf("_")+1) + "2";
						break;
					case 10:
						name = name.substring(0, name.lastIndexOf("_")+1) + "13";
						break;
					case 11:
						name = name.substring(0, name.lastIndexOf("_")+1) + "14";
						break;
					case 12:
						name = name.substring(0, name.lastIndexOf("_")+1) + "5";
						break;
					case 13:
						name = name.substring(0, name.lastIndexOf("_")+1) + "6";
						break;
					case 14:
						name = name.substring(0, name.lastIndexOf("_")+1) + "9";
						break;
					case 15:
						name = name.substring(0, name.lastIndexOf("_")+1) + "10";
						break;
				}
			}
			tileNames[y][x] = name;
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

			update(x+1, y);
			update(x-1, y);
			update(x, y-1);
			update(x, y+1);
			return true;
		}
		return false;
	}

	@Override
	public void fill(String name, float x2, float y2) {
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

				if (check(oldName, i.x, i.y)) {
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
		boolean bool1 = (oldName == null && tileNames[(int)x][(int)y] == null);
		boolean bool2 = ((tileNames[(int)x][(int)y] != null && oldName != null) && tileNames[(int)x][(int)y].equals(oldName));
		return bool1 || bool2;
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
}
