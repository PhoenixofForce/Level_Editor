package data;

import java.util.ArrayList;
import java.util.List;

public class GO {

	public float x, y, width, height;
	public String name;

	private List<Tag> tags;

	public GO(String name, float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.name = name;

		tags = new ArrayList<>();
	}

	public void move(float dx, float dy) {
		x += dx;
		y += dy;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public Tag getTag(String name) {
		if(name == null) return null;
		for(Tag t: tags) {
			if(name.equals(t.getName())) return t;
		}
		return null;
	}

	public void addTag(Tag t) {
		this.tags.add(t);
	}

	public void removeTag(String name) {
		for(Tag t: tags) {
			if(t.getName().equals(name)) {
				tags.remove(t);
				return;
			}
 		}
	}
}
