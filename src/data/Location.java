package data;

public class Location {

	/*
	 * Our window is 800*600 pixel
	 *
	 * ScreenSpace
	 * +------------------+ 0
	 * |                  | |
	 * |        +---------| |
	 * |        |   x  MAP| |
	 * +------------------+ v height
	 * 0------------------> width
	 *
	 * X = (600, 550) from the top right of the panel
	 *
	 * World Space
	 * +------------------+ 0
	 * |                  | |
	 * |        +---------| |
	 * |        |   x  MAP| |
	 * +------------------+ v height
	 * 0------------------> width
	 *
	 * X = (50, 20) from the top right of the map
	 *
	 * Map Space
	 * +------------------+ 0
	 * |                  | |
	 * |        +----+----| |
	 * |        |   X| MAP| |
	 * +------------------+ v height
	 * 0------------------> width
	 *
	 * X = (0.9, 0.5) tile position from map axis, in tilesizes,
	 *
	 */

	public float x, y;

	public Location(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		float out = 17;
		out = out*31 + x;
		out = out*31 + y;
		return Math.round(out);
	}

	@Override
	public boolean equals(Object b) {
		if(b instanceof Location l) {
			return l.x == x && l.y == y;
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + x + " | " + y + ")";
	}

}
