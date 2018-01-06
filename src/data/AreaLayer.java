package data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AreaLayer implements Layer {
	private float depth;

	private List<Area> areas;
	private int width, height, tileSize;

	private Area selected;

	public AreaLayer(float depth, int width, int height, int tileSize) {
		this.depth = depth;
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;

		areas = new ArrayList<>();
	}

	@Override
	public float depth() {
		return depth;
	}

	private Area find(float x, float y, boolean extended) {
		for (int i = areas.size()-1; i >= 0; i--) {
			Area area = areas.get(i);
			if (area.equalsFirstPoint(x, y)) return area;
			if (area.equalsSecondPoint(x, y)) return area;
		}

		if(!extended) return null;

		for (int i = areas.size()-1; i >= 0; i--) {
			Area area = areas.get(i);
			if (Math.min(area.getX1(), area.getX2()) <= x && Math.max(area.getX1(), area.getX2()) >= x && Math.min(area.getY1(), area.getY2()) <= y && Math.max(area.getY1(), area.getY2()) >= y) return  area;
		}
		return null;
	}

	@Override
	public void set(String name, float x, float y, boolean drag) {
		if (x < 0 || y < 0 || x >= width || y >= height) return;

		if (drag) return;
		areas.add(new Area(x, y, x, y, new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.5f)));
	}

	@Override
	public Area select(float x, float y) {
		Area area = find(x, y, true);

		if (area != null) {
			areas.remove(area);
			areas.add(area);
		}

		selected = area;
		return area;
	}

	@Override
	public void drag(float x, float y, float targetX, float targetY) {
		if (targetX < 0 || targetY < 0 || targetX >= width || targetY >= height) return;
		Area area = selected;

		if (area != null) {
			if (area.equalsSecondPoint(x, y)) {
				area.setX2(targetX);
				area.setY2(targetY);
			} else if(area.equalsFirstPoint(x, y)){
				area.setX1(targetX);
				area.setY1(targetY);
			} else {
				if (area.getX1() + (targetX-x) < 0 || area.getY1() + (targetY-y) < 0 || area.getX1() + (targetX-x) >= width || area.getY1() + (targetY-y) >= height) return;
				if (area.getX2() + (targetX-x) < 0 || area.getY2() + (targetY-y) < 0 || area.getX2() + (targetX-x) >= width || area.getY2() + (targetY-y) >= height) return;

				area.setX1(area.getX1() + (targetX-x));
				area.setX2(area.getX2() + (targetX-x));
				area.setY1(area.getY1() + (targetY-y));
				area.setY2(area.getY2() + (targetY-y));
			}
		}
	}

	@Override
	public boolean remove(float x, float y) {
		Area area = find(x, y, true);

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
			g.fillRect((int) (Math.min(a.getX1(), a.getX2()) * tileSize), (int) (Math.min(a.getY1(), a.getY2()) * tileSize), (int) ((Math.max(a.getX2(), a.getX1())-Math.min(a.getX2(), a.getX1()))*tileSize) + 1, (int) ((Math.max(a.getY2(), a.getY1()) - Math.min(a.getY2(), a.getY1()))*tileSize) + 1);
			g.fillRect((int) (a.getX1() * tileSize), (int) (a.getY1() * tileSize), 1, 1);
			g.fillRect((int) (a.getX2() * tileSize), (int) (a.getY2() * tileSize), 1, 1);
		}
	}

	@Override
	public String toMapFormat(List<String> names) {
		String out = "";

		//TODO: Export AreaLayer

		return out;
	}
}
