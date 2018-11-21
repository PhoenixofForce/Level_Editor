package window;

import java.awt.*;

public enum Tools {
	BRUSH(0, Color.GREEN), ERASER(1, Color.RED), BUCKET(2, Color.BLUE), SELECT(3, Color.YELLOW);

	private int pos;
	private Color c;
	Tools(int a, Color c) {
		this.pos = a;
		this.c = c;
	}

	public Tools next() {
		return Tools.values()[(pos+1)%Tools.values().length];
	}
	public Color getColor() {
		return c;
	}

	public static Tools get(int i) {
		int j = Tools.values().length;
		while(i < 0) i+= j;
		return Tools.values()[i%j];
	}
}
