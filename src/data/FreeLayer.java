package data;

import java.util.HashMap;
import java.util.Map;

public class FreeLayer implements Layer {

	private float depth;
	private Map<Loc, String> images;

	public FreeLayer(float depth) {
		this.depth = depth;
		this.images = new HashMap<>();
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
