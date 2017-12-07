package data;

public class TileLayer implements Layer {

	private float depth;
	private String[][] tileNames;

	public TileLayer(int width, int height, float depth) {
		tileNames = new String[width][height];
		this.depth = depth;
	}

	@Override
	public void set(String name, float x2, float y2) {
		int x = (int)x2;
		int y = (int)y2;
		if(x >= 0 && y >= 0 && x < width() && y < height()) {
			tileNames[y][x] = name;
		}
	}

	/**
	 *
	 * @return Map if textures
	 */
	public String[][] getTileNames() {
		return tileNames;
	}

	public int height() {
		return tileNames.length;
	}

	public int width() {
		return tileNames[0].length;
	}

	@Override
	public float depth() {
		return depth;
	}
}
