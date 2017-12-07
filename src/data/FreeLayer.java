package data;

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


	public List<GO> getImages() {
		return images;
	}

	@Override
	public float depth() {
		return depth;
	}

	@Override
	public void set(String name, float x, float y) {
		System.out.println(x + " " + y);
		images.add(new GO(new Loc(x, y), name));
	}
}
