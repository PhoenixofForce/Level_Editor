package data;

/**
 * a position
 */
public class Location {

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
