package data.layer;

import data.io.exporter.Exporter;
import data.Location;
import data.layer.layerobjects.TagObject;

import java.awt.Graphics;
import java.util.Optional;

/**
 * a class that shares all methods all layer need
 */
public interface Layer extends Exporter.Exportable {

	float depth();

	default void set(String name, float x, float y, boolean drag) {

	}

	default Optional<String> textureAt(float x, float y) {
		return Optional.empty();
	}

	default Optional<TagObject> select(float x, float y) {
		return Optional.empty();
	}

	default boolean drag(float x, float y, float targetX, float targetY) {
		return false;
	}

	default void add(TagObject to) {

	}

	default Optional<TagObject> remove(float x, float y) {
		return Optional.empty();
	}

	void draw(Graphics g, Location topLeft, Location downRight);

	Location smallestPoint();
	Location biggestPoint();
}