package data;

import java.util.ArrayList;
import java.util.List;

public abstract class TagObject {
	private List<Tag> tags;

	public TagObject() {
		tags = new ArrayList<>();
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

	public abstract String getText();
}
