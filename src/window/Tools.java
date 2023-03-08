package window;

import window.tools.*;

import java.awt.*;

public enum Tools {
	BRUSH(0, Color.GREEN, new BrushTool()),
	ERASER(1, Color.RED, new EraserTool()),
	BUCKET(2, Color.BLUE, new FillTool()),
	SELECT(3, Color.YELLOW, new SelectTool()),
	MOVE(4, Color.YELLOW, new MoveTool());

	private final int pos;
	private final Color c;
	private final ToolImplementation implementation;

	Tools(int a, Color c, ToolImplementation implementation) {
		this.pos = a;
		this.c = c;
		this.implementation = implementation;
	}

	public Tools next() {
		return Tools.values()[(pos+1)%Tools.values().length];
	}

	public Tools pre() {
		int npos = (pos - 1);
		while(npos < 0) npos += Tools.values().length;
		return Tools.values()[npos];
	}

	public Color getColor() {
		return c;
	}
	public int getIndex() {
		return pos;
	}

	public ToolImplementation getImplementation() {
		return implementation;
	}

	public static Tools get(int i) {
		int j = Tools.values().length;
		while(i < 0) i+= j;
		return Tools.values()[i%j];
	}
}
