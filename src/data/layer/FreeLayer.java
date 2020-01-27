package data.layer;

import data.exporter.Exporter;
import data.Location;
import data.layer.layerobjects.GO;
import data.layer.layerobjects.Tag;
import data.layer.layerobjects.TagObject;
import data.TextureHandler;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * A Layer in which the user can place textures freely
 */
public class FreeLayer implements Layer {

	private float depth;						//drawing depth of this layer
	private final List<GO> images;				//list of all placed textures

	private int width, height, tileSize;		//width, height and tilesize of the map

	public FreeLayer(float depth, int width, int height, int tileSize) {
		this.depth = depth;
		this.images = new ArrayList<>();

		this.width = width;
		this.height = height;
		this.tileSize = tileSize;
	}

	/**
	 * @return a list of placed textures
	 */
	public List<GO> getImages() {
		return images;
	}

	@Override
	public float depth() {
		return depth;
	}

	@Override
	public void set(String name, float x, float y, boolean drag) {
		if (drag) return;
		BufferedImage image = TextureHandler.getImagePng(name);
		float width = image.getWidth() / (float) tileSize;
		float height = image.getHeight() / (float) tileSize;

		if (x < 0 || x + width > this.width || y < 0 || y + height > this.height) return;
		//if (find(x, y) != null) return;

		synchronized (images) {
			images.add(new GO(name, x, y, width, height));
		}
	}

	@Override
	public boolean drag(float x, float y, float targetX, float targetY) {
		GO go = select(x, y);
		if (go == null) return false;
		go.move(targetX - x, targetY - y);
		if (go.x < 0 || go.x + go.width > this.width || go.y < 0 || go.y + go.height > this.height) {
			go.move(x - targetX, y - targetY);
			return false;
		}
		
		return true;
	}

	@Override
	public TagObject remove(float x, float y) {
		GO go = find(x, y);
		if(go == null) return null;

		synchronized (images) {
			images.remove(go);
		}

		return go;
	}

	@Override
	public GO select(float x, float y) {
		GO go = find(x, y);
		if (go != null) {
			synchronized (images) {
				images.remove(go);
				images.add(go);
			}
		}
		return go;
	}

	public void moveAll(float dx, float dy) {
		for(int i = 0; i < images.size(); i++) {
			GO go = images.get(i);

			go.move(dx, dy);
			if (go.x < 0 || go.x + go.width > this.width || go.y < 0 || go.y + go.height > this.height)
				go.move(-dx, -dy);
		}
	}

	public void roundAll(int tileSize) {
		float smallestX = Integer.MAX_VALUE,
			smallestY = Integer.MAX_VALUE;

		for(int i = 0; i < images.size(); i++) {
			GO r = images.get(i);
			if(smallestX > r.x) smallestX = r.x;
			if(smallestY > r.y) smallestY = r.y;
		}

		float 	dx = (smallestX%1),
				dy = (smallestY%1);
		moveAll(dx < 0.5f? -dx: 1-dx, dy < 0.5f? -dy: 1-dy);
	}

	/**
	 * @param x given x coordinate
	 * @param y given y coordinate
	 * @return a texture that is at the point where the user clicked
	 */
	private GO find(float x, float y) {
		for (int i = images.size() - 1; i >= 0; i--) {
			GO go = images.get(i);
			if (go.x <= x && go.y <= y && go.x + go.width > x && go.y + go.height > y) {
				return go;
			}
		}
		return null;
	}

	@Override
	public void draw(Graphics g, Location l1, Location l2) {
		synchronized (images) {
			for (GO go: images) {
				g.drawImage(TextureHandler.getImagePng(go.name), (int) (go.x * tileSize), (int) (go.y * tileSize), null);
			}
		}
	}

	@Override
	public float smallestX() {
		float smallestX = Integer.MAX_VALUE;
		for(GO g: images) if(g.x < smallestX) smallestX = g.x;
		return smallestX == Integer.MAX_VALUE? -1: smallestX;
	}

	@Override
	public float smallestY() {
		float smallestY = Integer.MAX_VALUE;
		for(GO g: images) if(g.y < smallestY) smallestY = g.y;
		return smallestY == Integer.MAX_VALUE? -1: smallestY;
	}

	@Override
	public float biggestX() {
		float smallestX = Integer.MIN_VALUE;
		for(GO g: images) if(g.x > smallestX) smallestX = g.x;
		return smallestX == Integer.MIN_VALUE? -1: smallestX;
	}

	@Override
	public float biggestY() {
		float smallestY = Integer.MIN_VALUE;
		for(GO g: images) if(g.y > smallestY) smallestY = g.y;
		return smallestY == Integer.MIN_VALUE? -1: smallestY;
	}

	@Override
	public FreeLayer clone() {
		FreeLayer out = new FreeLayer(depth, width, height, tileSize);
		for(int i = 0; i < images.size(); i++) out.images.add(images.get(i).clone());
		return out;
	}

	@Override
	public void add(TagObject to) {
		if(to instanceof GO) {
			synchronized (images) {
				images.add((GO) to);
			}
		}
	}

	@Override
	public String accept(Exporter exporter, Object o2) {
		Object[] args = (Object[]) o2;

		String out = "";
		synchronized (images) {
			for(GO g: getImages()) {

				out += exporter.export(g, new Object[]{args[0], args[1], args[2], depth});
			}
		}

		return out;
	}
}
