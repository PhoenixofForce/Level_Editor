package data.layer.layerobjects;

import data.io.exporter.Exporter;
import data.io.exporter.ExporterData;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;

@Getter
@Setter
public class Area extends TagObject {
	private float x1, y1;		//position of the first corner
	private float x2, y2;		//position of the second corner
	private Color color;		//color with that the area gets rendered

	public Area(float x1, float y1, float x2, float y2, Color color) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.color = color;
	}

	public float getSmallerX() {
		return Math.min(x1, x2);
	}

	public float getBiggerX() {
		return Math.max(x1, x2);
	}

	public float getSmallerY() {
		return Math.min(y1, y2);
	}

	public float getBiggerY() {
		return Math.max(y1, y2);
	}

	public boolean equalsFirstPoint(float x, float y) {
		return x == x1 && y == y1;
	}

	public boolean equalsSecondPoint(float x, float y) {
		return x == x2 && y == y2;
	}

	@Override
	public Area clone() {
		Area a =  new Area(x1, y1, x2, y2, color);
		for(int i = 0; i < getTags().size(); i++) a.addTag(getTags().get(i).clone());
		return a;
	}

	@Override
	public String getText() {
		return String.format("(%f/%f) (%f/%f) (%d/%d/%d)",
				x1, y1, x2, y2,
				color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	public Object accept(Exporter exporter, ExporterData data) {
		return exporter.export(this, data);
	}
}
