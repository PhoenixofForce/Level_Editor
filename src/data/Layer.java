package data;

import java.awt.*;
import java.util.List;

public interface Layer {

	float depth();

	void set(String name, float x, float y, boolean drag);
	GO select(float x, float y);
	void drag(float x, float y, float targetX, float targetY);
	boolean remove(float x, float y);

	void draw(Graphics g);


	String toMapFormat(List<String> names);
}
