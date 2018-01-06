package data;

import java.awt.*;

public class Area extends TagObject {
	private float x1, y1;
	private float x2, y2;
	private Color color;
	private float zoom;

	public Area(float x1, float y1, float x2, float y2, Color color) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.color = color;

		this.zoom = 1;
	}

	public void update() {
		if (x1 > x2) {
			float c = x1;
			x1 = x2;
			x2 = c;
		} else if (y1 > y2) {
			float c = y1;
			y1 = y2;
			y2 = c;
		}
	}

	public boolean equalsFirstPoint(float x, float y) {
		return x == x1 && y == y1;
	}

	public boolean equalsSecondPoint(float x, float y) {
		return x == x2 && y == y2;
	}

	public float getX1() {
		return x1;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public float getY1() {
		return y1;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}

	public float getX2() {
		return x2;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
		this.y2 = y2;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	@Override
	public String getText() {
		return String.format("(%f/%f) (%f/%f) (%d/%d/%d)", x1, y1, x2, y2, color.getRed(), color.getGreen(), color.getBlue());
	}
}
