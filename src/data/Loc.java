package data;

public class Loc {

	public float x, y;

	/** Used to save a point
	 *
	 * @param x x-pos
	 * @param y y-pos
	 */
	public Loc(float x, float y) {
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
