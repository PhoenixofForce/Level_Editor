package data.layer;

import data.layer.layerobjects.TagObject;

import java.awt.Graphics;
import java.util.List;

public interface Layer {

	float depth();

	void set(String name, float x, float y, boolean drag);
	TagObject select(float x, float y);
	void drag(float x, float y, float targetX, float targetY);
	boolean remove(float x, float y);

	void draw(Graphics g);


	String toMapFormat(List<String> names);
}
