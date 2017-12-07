package data;

public interface Layer {
	/**
	 *	Drawing priority
	 *
	 * @return the depth of the layer
	 */
	float depth();

	/**
	 * Used to set an object on a layer
	 *
	 * @param name name of the texture
	 * @param x x-position
	 * @param y y-position
	 */
	void set(String name, float x, float y);
}
