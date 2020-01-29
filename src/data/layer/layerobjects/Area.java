package data.layer.layerobjects;

import data.exporter.Exporter;

import java.awt.Color;

/**
 * Saves area that user has drawn
 */
public class Area extends TagObject {
	private float x1, y1;		//position of the first corner
	private float x2, y2;		//position of the second corner
	private Color color;		//color with that the area gets rendered

	public Area(float x1, float y1, float x2, float y2, Color color) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.color = color;
	}

	/**
	 *
	 * @return the smaller x value
	 */
	public float getSmallerX() {
		return Math.min(x1, x2);
	}

	/**
	 *
	 * @return the bigger x value
	 */
	public float getBiggerX() {
		return Math.max(x1, x2);
	}

	/**
	 *
	 * @return the smaller y value
	 */
	public float getSmallerY() {
		return Math.min(y1, y2);
	}

	/**
	 *
	 * @return the bigger y value
	 */
	public float getBiggerY() {
		return Math.max(y1, y2);
	}

	/**
	 * checks if given coordinates are the coordinates of the first corner
	 * @param x x value of the point to check
	 * @param y y value of the point to check
	 * @return true if the given coordinates match with the first corner
	 */
	public boolean equalsFirstPoint(float x, float y) {
		return x == x1 && y == y1;
	}

	/**
	 * checks if given coordinates are the coordinates of the second corner
	 * @param x x value of the point to check
	 * @param y y value of the point to check
	 * @return true if the given coordinates match with the second corner
	 */
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

	@Override
	public Area clone() {
		Area a =  new Area(x1, y1, x2, y2, color);
		for(int i = 0; i < getTags().size(); i++) a.addTag(getTags().get(i).clone());
		return a;
	}

	/**
	 * converts class to saveable text-format
	 * @return
	 */
	@Override
	public String getText() {
		return String.format("(%f/%f) (%f/%f) (%d/%d/%d)", x1, y1, x2, y2, color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	public Object accept(Exporter exporter, Object... o2) {
		return exporter.export(this, o2);
	}
}
