package data;

import java.awt.*;

public interface Layer {
	float depth();
	void event(String name, float x, float y);
	void draw(Graphics g);
}
