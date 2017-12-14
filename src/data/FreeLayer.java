package data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FreeLayer implements Layer {

	private float depth;
	private List<GO> images;

	public FreeLayer(float depth) {
		this.depth = depth;
		this.images = new ArrayList<>();
	}


	public List<GO> getImages() {
		return images;
	}

	@Override
	public float depth() {
		return depth;
	}

	@Override
	public void set(String name, float x, float y) {
		BufferedImage image = TextureHandler.getImagePng(name);
		float width = image.getWidth() / 8.0f;
		float height = image.getHeight() / 8.0f;

		if (x < 0 || x + width > 100 || y < 0 || y +height > 100) return;
		if (find(x, y) != null) return;

		images.add(new GO(name, x, y, width, height));
	}

	@Override
	public void drag(float x, float y, float targetX, float targetY) {
		GO go = select(x, y);
		if (go == null) return;
		go.move(targetX-x, targetY-y);
		if (go.x < 0 || go.x + go.width > 100 || go.y < 0 || go.y + go.height > 100) go.move(x-targetX, y-targetY);
	}

	@Override
	public GO select(float x, float y) {
		GO go = find(x, y);
		if (go != null) {
			images.remove(go);
			images.add(go);
		}
		return go;
	}

	private GO find(float x, float y) {
		for (int i = images.size()-1; i >= 0; i--) {
			GO go = images.get(i);
			if (go.x <= x && go.y <= y && go.x+go.width >= x && go.y + go.height >= y) {
				return go;
			}
		}
		return null;
	}

	@Override
	public void draw(Graphics g) {
		for(int i = 0; i < images.size(); i++) {
			GO go = images.get(i);
			g.drawImage(TextureHandler.getImagePng(go.name), (int)(go.x*8), (int)(go.y*8), null);
		}
	}
}
