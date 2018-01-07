package data.layer;

import data.layer.layerobjects.Area;
import data.layer.layerobjects.Tag;

import java.awt.Graphics;
import java.awt.Color;
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
			if (area.getSmallerX() <= x && area.getBiggerX() >= x && area.getSmallerY() <= y && area.getBiggerY() >= y) return  area;
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
			g.fillRect((int) (a.getSmallerX() * tileSize), (int) (a.getSmallerY() * tileSize), (int) ((a.getBiggerX()-a.getSmallerX())*tileSize) + 1, (int) ((a.getBiggerY() - a.getSmallerY())*tileSize) + 1);
			g.fillRect((int) (a.getX1() * tileSize), (int) (a.getY1() * tileSize), 1, 1);
			g.fillRect((int) (a.getX2() * tileSize), (int) (a.getY2() * tileSize), 1, 1);
		}
	}

	@Override
	public String toMapFormat(List<String> names) {
		String out = "";

		for(Area a: areas) {
			String tags = "";
			for(int i = 0; i < a.getTags().size(); i++) {
				Tag t = a.getTags().get(i);
				tags += t.toMapFormat() + (i < a.getTags().size()-1? "; ": "");
			}
			out += "[area; " + a.getSmallerX() + "; " + a.getSmallerY() + "; " + a.getBiggerX() + "; " + a.getBiggerY() + (a.getTags().size() > 0? "; " + tags: "") + "]\n";
		}

		return out;
	}
}
