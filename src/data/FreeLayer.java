package data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FreeLayer implements Layer {

	private float depth;
	private List<GO> images;

	//TODO: Max width and height
	public FreeLayer(float depth) {
		this.depth = depth;
		this.images = new ArrayList<>();
	}

	/**
	 *
	 * @return all GameObjects saved in this layer
	 */
	public List<GO> getImages() {
		return images;
	}

	@Override
	public float depth() {
		return depth;
	}

	@Override
	public void event(String name, float x, float y) {
		System.out.println(x + " " + y);
		images.add(new GO(new Location(x, y), name));
	}

	@Override
	public void draw(Graphics g) {
		for(int i = 0; i < images.size(); i++) {
			Location location = images.get(i).location;
			String name = images.get(i).name;
			g.drawImage(TextureHandler.getImagePng(name), (int)(location.x*8), (int)(location.y*8), null);
		}
	}
}
