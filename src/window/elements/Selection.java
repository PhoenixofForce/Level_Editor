package window.elements;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

public class Selection {

	private Area currentArea;
	private List<Pair> rectangles;

	public Selection() {
		currentArea = new Area();
		rectangles = new ArrayList<>();

	}

	public boolean add(Rectangle r) {
		if(!currentArea.contains(r)) {
			rectangles.add(new Pair(r, Action.ADD));
			toArea();
			return true;
		}
		return false;
	}

	public boolean subtract(Rectangle r) {
		if(currentArea.intersects(r)) {
			rectangles.add(new Pair(r, Action.SUBTRACT));
			toArea();
			return true;
		}

		return false;
	}

	public void translate(int offX, int offY) {
		for(int i = 0; i < rectangles.size(); i++) {
			Pair p =  rectangles.get(i);
			p.r.x += offX;
			p.r.y += offY;
		}
		toArea();
	}

	public void roundPosition(int tileSize) {
		int smallestX = Integer.MAX_VALUE,
			smallestY = Integer.MAX_VALUE;

		for(int i = 0; i < rectangles.size(); i++) {
			Rectangle r = rectangles.get(i).r;
			if(smallestX > r.x) smallestX = r.x;
			if(smallestY > r.y) smallestY = r.y;
		}

		int dx = smallestX%tileSize,
			dy = smallestY%tileSize;
		translate(dx < tileSize/2? -dx: tileSize-dx, dy < tileSize/2? -dy: tileSize-dy);
	}

	public Area getArea() {
		return currentArea;
	}

	private void toArea() {
		Area a = new Area();

		for(int i = 0; i < rectangles.size(); i++) {
			Pair p = rectangles.get(i);

			switch (p.a) {
				case ADD:
					a.add(new Area(p.r));
					break;
				case SUBTRACT:
					a.subtract(new Area(p.r));
					break;
			}
		}

		currentArea = a;
		optimize();
	}

	private void optimize() {
		if(currentArea.isEmpty()) {
			currentArea = new Area();
			rectangles.clear();
			return;
		}
		if(currentArea.isRectangular()) {
			Rectangle p = areaToShape();
			rectangles.clear();
			rectangles.add(new Pair(p, Action.ADD));
			currentArea = new Area(p);
		}
	}

	private Rectangle areaToShape() {
		PathIterator iterator = currentArea.getPathIterator(null);
		float[] floats = new float[6];

		int x1 = Integer.MIN_VALUE, y1 = Integer.MIN_VALUE, x2 = Integer.MAX_VALUE, y2 = Integer.MAX_VALUE;

		while (!iterator.isDone()) {
			int type = iterator.currentSegment(floats);
			int x = (int) floats[0];
			int y = (int) floats[1];
			if(type != PathIterator.SEG_CLOSE) {
				if(x > x1) x1 = x;
				if(x < x2) x2 = x;
				if(y > y1) y1 = y;
				if(y < y2) y2 = y;
			}
			iterator.next();
		}

		return new Rectangle(x2, y2, x1-x2, y1-y2);
	}

	public enum Action {
		ADD, SUBTRACT;
	}

	private class Pair {

		private Rectangle r;
		private Action a;

		public Pair(Rectangle r, Action a) {
			this.r = r;
			this.a = a;
		}
	}
}
