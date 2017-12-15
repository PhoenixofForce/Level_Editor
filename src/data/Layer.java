package data;

import java.awt.*;

public interface Layer {
	float depth();
	void set(String name, float x, float y);
	GO select(float x, float y);
	void drag(float x, float y, float targetX, float targetY);
	void draw(Graphics g);

	String toMapFormat();
}
