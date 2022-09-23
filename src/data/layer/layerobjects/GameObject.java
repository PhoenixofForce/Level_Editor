package data.layer.layerobjects;

import data.io.exporter.Exporter;

/**
 * A GameObject that has a position with width and height, it can have tags and must have a texture
 */
public class GameObject extends TagObject {

	public float x, y, width, height;			//x, y coordinates, width and height of the object
	public String name;							//name of the corresponding textur

	public GameObject(String name, float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.name = name;
	}

	/**
	 * moves this object in the given direction
	 * @param dx x offset
	 * @param dy y offset
	 */
	public void move(float dx, float dy) {
		x += dx;
		y += dy;
	}

	@Override
	public GameObject clone() {
		GameObject g =  new GameObject(name, x, y, width, height);
		for(int i = 0; i < getTags().size(); i++) g.addTag(getTags().get(i).clone());
		return g;
	}

	/**
	 * converts class to saveable text-format
	 * @return
	 */
	@Override
	public String getText() {
		return name + " (" + x + " | " + y + ")";
	}

	@Override
	public Object accept(Exporter exporter, Object... o2) {
		return exporter.export(this, o2);
	}
}
