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

}
