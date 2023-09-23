package window;

import lombok.Getter;
import window.tools.*;

import java.awt.*;

@Getter
public enum Tools {
	BRUSH(0, Color.GREEN, new BrushTool()),
	ERASER(1, Color.RED, new EraserTool()),
	BUCKET(2, Color.BLUE, new FillTool()),
	SELECT(3, Color.YELLOW, new SelectTool()),
	MOVE(4, Color.YELLOW, new MoveTool());

	private final int position;
	private final Color color;
	private final ToolImplementation implementation;

	Tools(int position, Color ccolor, ToolImplementation implementation) {
		this.position = position;
		this.color = ccolor;
		this.implementation = implementation;
	}

	public Tools next() {
		return Tools.values()[(position +1)%Tools.values().length];
	}

	public Tools pre() {
		int npos = (position - 1);
		while(npos < 0) npos += Tools.values().length;
		return Tools.values()[npos];
	}

	public static Tools get(int i) {
		int j = Tools.values().length;
		while(i < 0) i+= j;
		return Tools.values()[i%j];
	}
}
