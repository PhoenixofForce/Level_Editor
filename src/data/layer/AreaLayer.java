package data.layer;

import data.exporter.Exporter;
import data.Location;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.TagObject;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A Layer in which the user can mark areas
 */
public class AreaLayer implements Layer {
	private float depth;					//drawing depth of this layer

	private List<Area> areas;				//list of all areas
	private int width, height, tileSize;	//width, height and tilesize of the map

	private Area selected;					//currently selected area

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

	/**
	 * gets an area at the point the user clicked
	 * @param x the x coordinate the user clicked
	 * @param y the y coordinate the user clicked
	 * @param extended false if only corner-clicks count, true if "body"-clicks also count
	 * @return the area the user clicked
	 */
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

		//brings area to the top
		if (area != null) {
			areas.remove(area);
			areas.add(area);
		}

		selected = area;
		return area;
	}

	@Override
	public boolean drag(float x, float y, float targetX, float targetY) {
		if (targetX < 0 || targetY < 0 || targetX >= width || targetY >= height) return false;
		Area area = selected;


		if (area != null) {
			//Doent work with tileSize=3 :(

			//Move second point
			if (area.equalsSecondPoint(x, y)) {
				area.setX2(targetX);
				area.setY2(targetY);
			}

			//Move first point
			else if(area.equalsFirstPoint(x, y)){
				area.setX1(targetX);
				area.setY1(targetY);
			}

			//Moves whole area
			else {
				if (area.getX1() + (targetX-x) < 0 || area.getY1() + (targetY-y) < 0 || area.getX1() + (targetX-x) >= width || area.getY1() + (targetY-y) >= height) return false;
				if (area.getX2() + (targetX-x) < 0 || area.getY2() + (targetY-y) < 0 || area.getX2() + (targetX-x) >= width || area.getY2() + (targetY-y) >= height) return false;

				area.setX1(area.getX1() + (targetX-x));
				area.setX2(area.getX2() + (targetX-x));
				area.setY1(area.getY1() + (targetY-y));
				area.setY2(area.getY2() + (targetY-y));
			}

			return true;
		}

		return false;
	}

	@Override
	public TagObject remove(float x, float y) {
		Area area = find(x, y, true);

		if (area != null) {
			areas.remove(area);
			return area;
		}

		return null;
	}

	@Override
	public void draw(Graphics g, Location l1, Location l2) {
		for (Area a: areas) {
			g.setColor(a.getColor());
			g.fillRect((int) Math.floor(a.getSmallerX() * tileSize), (int) Math.floor(a.getSmallerY() * tileSize), (int) Math.round((a.getBiggerX()-a.getSmallerX())*tileSize) + 1, (int) Math.round((a.getBiggerY() - a.getSmallerY())*tileSize) + 1);
			g.fillRect((int) Math.floor(a.getX1() * tileSize), (int) Math.floor(a.getY1() * tileSize), 1, 1);
			g.fillRect((int) Math.floor(a.getX2() * tileSize), (int) Math.floor(a.getY2() * tileSize), 1, 1);
		}
	}

	@Override
	public float smallestX() {
		float smallestX = Integer.MAX_VALUE;
		for(Area a: areas) if(a.getSmallerX() < smallestX) smallestX = a.getSmallerX();
		return smallestX == Integer.MAX_VALUE? -1: smallestX;
	}

	@Override
	public float smallestY() {
		float smallestY = Integer.MAX_VALUE;
		for(Area a: areas) if(a.getSmallerY() < smallestY) smallestY = a.getSmallerX();
		return smallestY == Integer.MAX_VALUE? -1: smallestY;
	}

	@Override
	public float biggestX() {
		float smallestX = Integer.MIN_VALUE;
		for(Area a: areas) if(a.getBiggerX() > smallestX) smallestX = a.getBiggerX();
		return smallestX == Integer.MIN_VALUE? -1: smallestX;
	}

	@Override
	public float biggestY() {
		float smallestY = Integer.MIN_VALUE;
		for(Area a: areas) if(a.getBiggerY() > smallestY) smallestY = a.getBiggerX();
		return smallestY == Integer.MIN_VALUE? -1: smallestY;
	}

	@Override
	public AreaLayer clone() {
		AreaLayer out = new AreaLayer(depth, width, height, tileSize);
		for(int i = 0; i < areas.size(); i++) out.areas.add(areas.get(i).clone());
		return out;
	}

	@Override
	public void add(TagObject to) {
		if(to instanceof Area) {
			synchronized (areas) {
				areas.add((Area) to);
			}
		}
	}

	@Override
	public Object accept(Exporter exporter, Object... o2) {
		Object out = exporter.export(this, o2);
		for(int i = 0; i < areas.size(); i++) {
			out = exporter.append(out, areas.get(i).accept(exporter, o2));
		}
		return out;
	}
}
