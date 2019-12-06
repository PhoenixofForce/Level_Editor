package data.layer;

import data.Location;
import data.layer.layerobjects.TagObject;

import java.awt.Graphics;
import java.util.List;

/**
 * a class that shares all methods all layer need
 */
public interface Layer {

	/**
	 * @return drawing depth
	 */
	float depth();

	/**
	 * called when user click on map
	 * @param name name of the selected texture
	 * @param x x coordinate of the point clicked
	 * @param y y coordinate of the point clicked
	 * @param drag if the user dragged
	 */
	void set(String name, float x, float y, boolean drag);

	/**
	 * @param x x coordinate of the point clicked
	 * @param y y coordinate of the point clicked
	 * @return a tagobject that is at the position where the user clicked
	 */
	TagObject select(float x, float y);

	/**
	 * called when user drags across the map
	 * @param x x coordinate of the point where the dragging started
	 * @param y y coordinate of the point where the dragging started
	 * @param targetX x coordinate of the point where the dragging stopped
	 * @param targetY y coordinate of the point where the dragging stopped
	 */
	boolean drag(float x, float y, float targetX, float targetY);

	void add(TagObject to);
	
	/**
	 * removes a gameobject at a given
	 * @param x x coordinate of the point clicked
	 * @param y y coordinate of the point clicked
	 * @return the deleted object
	 */
	TagObject remove(float x, float y);

	/**
	 * draws the layer
	 * @param g the graphics object which should draw the layer
	 */
	void draw(Graphics g, Location topLeft, Location downRight);

	/**
	 * @return the smallest x value in this layer
	 */
	float smallestX();

	/**
	 * @return the smallest y value in this layer
	 */
	float smallestY();

	/**
	 * @return the biggest x value in this layer
	 */
	float biggestX();

	/**
	 * @return the biggest y value in this layer
	 */
	float biggestY();

	/**
	 *
	 * @param names a list of all used texture names
	 * @param smallestX the smallest x position in the whole map
	 * @param smallestY the smallest y position in the whole map
	 * @param biggestX the biggest x position in the whole map
	 * @param biggestY the biggest y position in the whole map
	 * @return the mapformat of this layer
	 */
	String toMapFormat(List<String> names, float smallestX, float smallestY, float biggestX, float biggestY);
}
