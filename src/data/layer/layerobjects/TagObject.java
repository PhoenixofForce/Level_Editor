package data.layer.layerobjects;

import data.io.exporter.Exporter;

import java.util.ArrayList;
import java.util.List;

public abstract class TagObject implements Exporter.Exportable {

	private final List<Tag> tags;			//list of applied tags
	public TagObject() {
		tags = new ArrayList<>();
	}

	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * gets a tag by its name
	 * @param name name of the tag to get
	 * @return the tag which has the name
	 */
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

	public Tag removeTag(String name) {
		for(Tag t: tags) {
			if(t.getName().equals(name)) {
				tags.remove(t);
				return t;
			}
		}

		return null;
	}

	public abstract String getText();
}
