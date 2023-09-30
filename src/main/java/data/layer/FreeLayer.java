package data.layer;

import data.io.exporter.Exporter;
import data.Location;
import data.io.exporter.ExporterData;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.TagObject;
import data.TextureHandler;
import lombok.Getter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FreeLayer implements Layer {

	private final float depth;						//drawing depth of this layer

	@Getter
	private final List<GameObject> images;				//list of all placed textures

	private final int width;
	private final int height;
	private final int tileSize;		//width, height and tilesize of the map

	public FreeLayer(float depth, int width, int height, int tileSize) {
		this.depth = depth;
		this.images = new ArrayList<>();

		this.width = width;
		this.height = height;
		this.tileSize = tileSize;
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

		synchronized (images) {
			images.add(new GameObject(name, x, y, width, height));
		}
	}

	@Override
	public boolean drag(float x, float y, float targetX, float targetY) {
		Optional<TagObject> optionalGameObject = select(x, y);
		if (optionalGameObject.isEmpty()) return false;
		GameObject gameObject = (GameObject) optionalGameObject.get();

		gameObject.move(targetX - x, targetY - y);
		if (isGameObjectInBounds(gameObject)) {
			gameObject.move(x - targetX, y - targetY);
			return false;
		}

		return true;
	}

	@Override
	public Optional<TagObject> remove(float x, float y) {
		GameObject gameObject = find(x, y);
		if(gameObject == null) return Optional.empty();

		synchronized (images) {
			images.remove(gameObject);
		}

		return Optional.of(gameObject);
	}

	@Override
	public Optional<TagObject> select(float x, float y) {
		GameObject gameObject = find(x, y);
		if (gameObject != null) {
			synchronized (images) {
				images.remove(gameObject);
				images.add(gameObject);
			}
		}
		return Optional.ofNullable(gameObject);
	}

	@Override
	public Optional<String> textureAt(float x, float y) {
		return select(x, y).map(GameObject.class::cast).map(go -> go.name);
	}

	public void moveAll(float dx, float dy) {
		for (GameObject gameObject : images) {
			gameObject.move(dx, dy);
			if (isGameObjectInBounds(gameObject))
				gameObject.move(-dx, -dy);
		}
	}

	public void roundAll(int tileSize) {
		float smallestX = Integer.MAX_VALUE,
				smallestY = Integer.MAX_VALUE;

		for (GameObject r : images) {
			if (smallestX > r.x) smallestX = r.x;
			if (smallestY > r.y) smallestY = r.y;
		}

		float 	dx = (smallestX%1),
				dy = (smallestY%1);
		moveAll(dx < 0.5f? -dx: 1-dx, dy < 0.5f? -dy: 1-dy);
	}

	private GameObject find(float x, float y) {
		for (int i = images.size() - 1; i >= 0; i--) {
			GameObject gameObject = images.get(i);
			if (gameObject.containsPoint(x, y)) {
				return gameObject;
			}
		}
		return null;
	}

	private boolean isGameObjectInBounds(GameObject gameObject) {
		return gameObject.x < 0 || gameObject.x + gameObject.width > this.width ||
				gameObject.y < 0 || gameObject.y + gameObject.height > this.height;
	}

	@Override
	public void draw(Graphics g, Location l1, Location l2) {
		synchronized (images) {
			for (GameObject gameObject : images) {
				g.drawImage(TextureHandler.getImagePng(gameObject.name),
						(int) (gameObject.x * tileSize), (int) (gameObject.y * tileSize), null);
			}
		}
	}

	@Override
	public Location smallestPoint() {
		float smallestX = Integer.MAX_VALUE;
		for(GameObject g: images) if(g.x < smallestX) smallestX = g.x;
		smallestX =  smallestX == Integer.MAX_VALUE? -1: smallestX;

		float smallestY = Integer.MAX_VALUE;
		for(GameObject g: images) if(g.y < smallestY) smallestY = g.y;
		smallestY =  smallestY == Integer.MAX_VALUE? -1: smallestY;

		return new Location(smallestX, smallestY);
	}

	@Override
	public Location biggestPoint() {
		float biggestX = Integer.MIN_VALUE;
		for(GameObject g: images) if(g.x > biggestX) biggestX = g.x;
		biggestX = biggestX == Integer.MIN_VALUE? -1: biggestX;

		float biggestY = Integer.MIN_VALUE;
		for(GameObject g: images) if(g.y > biggestY) biggestY = g.y;
		biggestY = biggestY == Integer.MIN_VALUE? -1: biggestY;

		return new Location(biggestX, biggestY);
	}


	@Override
	public FreeLayer clone() {
		FreeLayer out = new FreeLayer(depth, width, height, tileSize);
		for(int i = 0; i < images.size(); i++) out.images.add(images.get(i).clone());
		return out;
	}

	@Override
	public void add(TagObject to) {
		if(to instanceof GameObject) {
			synchronized (images) {
				images.add((GameObject) to);
			}
		}
	}

	@Override
	public Object accept(Exporter exporter, ExporterData data) {

		Object out = exporter.export(this, data);
		synchronized (images) {
			for(GameObject g: getImages()) {
				out = exporter.append(out, exporter.export(g, data, depth));
			}
		}

		return out;
	}
}
