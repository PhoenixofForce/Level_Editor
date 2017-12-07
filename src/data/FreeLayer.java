package data;

import java.util.HashMap;
import java.util.Map;

public class FreeLayer implements Layer {

	private float depth;
	private Map<Loc, String> images;

	//TODO: Max width and height
	public FreeLayer(float depth) {
		this.depth = depth;
		this.images = new HashMap<>();
	}

	public Map<Loc, String> getImages() {
		return images;
	}

	@Override
	public float depth() {
		return depth;
	}

	@Override
	public void set(String name, float x, float y) {
		images.put(new Loc(x, y), name);
	}
}
