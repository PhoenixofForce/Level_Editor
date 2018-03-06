package data.layer.layerobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * an object which has tags
 */
public abstract class TagObject {
	private List<Tag> tags;			//list of applied tags

	public TagObject() {
		tags = new ArrayList<>();
	}

	/**
	 *
	 * @return list of all tags
	 */
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
