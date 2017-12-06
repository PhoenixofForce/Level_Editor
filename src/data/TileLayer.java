package data;

import java.util.ArrayList;
import java.util.List;

public class TileLayer implements Layer {

	public static void test() {
		TileLayer tl = new TileLayer(new String[][]{{"tiles_sky", "tiles_sky", "tiles_sky"}, {"tiles_grass", "tiles_dirt", "tiles_stone"}, {"tiles_sky", "tiles_sky", "tiles_sky"}});
		System.out.println(tl.toMapFormat());
	}

	private float depth;
	private String[][] tileNames;

	public TileLayer(int width, int height, float depth) {
		tileNames = new String[width][height];
		this.depth = depth;
	}

	private TileLayer(String[][] map) {
		tileNames = map;
		depth = 0.3f;
	}

	@Override
	public String toMapFormat() {
		List<String> names = new ArrayList<>();
		for(String[] sa: tileNames) for(String s: sa) if(!names.contains(s)) names.add(s);

		String out = "";

		for(int i = 0; i < names.size(); i++) {
			out += "#" + i + " - " + names.get(i) + "\n";
		}

		out += String.format("[map; " + depth + "; %d; %d;", width(), height());

		for(int x = 0; x < width(); x++) {
			for(int y = 0; y < height(); y++) {
				out += names.indexOf(tileNames[y][x]) + (y == height()-1? "": ",");
			}
			out += x != width()-1?";": "";
		}


		return out + "]";
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
