package data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AreaLayer implements Layer {
	private float depth;

	private List<Area> areas;
	private int tileSize;

	public AreaLayer(float depth, int tileSize) {
		this.depth = depth;
		this.tileSize = tileSize;

		areas = new ArrayList<>();
	}

	@Override
	public float depth() {
		return depth;
	}

	private Area find(float x, float y) {
		for (int i = areas.size()-1; i >= 0; i--) {
			Area area = areas.get(i);
			if (area.equalsFirstPoint(x, y)) return area;
			if (area.equalsSecondPoint(x, y)) return area;
		}

		return null;
	}

	@Override
	public void set(String name, float x, float y, boolean drag) {
		if (drag) return;
		areas.add(new Area(x, y, x, y, new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.5f)));
	}

	@Override
	public Area select(float x, float y) {
		Area area = find(x, y);

		if (area != null) {
			areas.remove(area);
			areas.add(area);
		}

		return area;
	}

	@Override
	public void drag(float x, float y, float targetX, float targetY) {
		Area area = find(x, y);

		if (area != null) {
			if (area.equalsSecondPoint(x, y)) {
				area.setX2(targetX);
				area.setY2(targetY);
			} else {
				area.setX1(targetX);
				area.setY1(targetY);
			}
			area.update();
		}
	}

	@Override
	public boolean remove(float x, float y) {
		Area area = find(x, y);

		if (area != null) {
			areas.remove(area);
			return true;
		}

		return false;
	}

	@Override
	public void draw(Graphics g) {
		for (Area a: areas) {
			g.setColor(a.getColor());
			g.fillRect((int) (a.getX1() * tileSize), (int) (a.getY1() * tileSize), (int) ((a.getX2()-a.getX1())*tileSize) + 1, (int) ((a.getY2() - a.getY1())*tileSize) + 1);
			g.fillRect((int) (a.getX1() * tileSize), (int) (a.getY1() * tileSize), 1, 1);
			g.fillRect((int) (a.getX2() * tileSize), (int) (a.getY2() * tileSize), 1, 1);
		}
	}

	@Override
	public String toMapFormat(List<String> names) {
		return null;
	}
}
