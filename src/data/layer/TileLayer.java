package data.layer;

import data.layer.layerobjects.GO;
import data.TextureHandler;

import java.awt.Graphics;

import java.util.List;

public class TileLayer implements Layer {

	private float depth;
	private String[][] tileNames;

	private int width, height, tileSize;

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
			return true;
		}
		return false;
	}

	@Override
	public GO select(float x, float y) {
		return null;
	}

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
		String out = "[layer; " + depth + "; " + width + "; " + height + "; ";

		int startX = Math.max(0, (int) Math.floor(sx));
		int startY = Math.max(0, (int) Math.floor(sy));
		int endX = bx == -1? tileNames[0].length: Math.min(tileNames[0].length, (int) Math.ceil(bx));
		int endY = by == -1? tileNames.length: Math.min(tileNames.length, (int) Math.ceil(by));

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