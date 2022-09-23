package data.layer.layerobjects;

import data.io.exporter.Exporter;

/**
 * Class to store tags the user entered
 */
public class Tag implements Exporter.Exportable {

	private String name, action;		//name and content of the tag

	public Tag(String n) {
		this.name = n;
		action = "";
	}

	public Tag(String n, String a) {
		this.name = n;
		this.action = a;
	}

	/**
	 * replaces the current action
	 * @param a the new action of this tag
	 */
	public void setAction(String a) {
		this.action = a;
	}

	public String getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	@Override
	public Tag clone() {
		return new Tag(name, action);
	}

	@Override
	public boolean equals(Object b) {
		if(b instanceof Tag) {
			Tag t = (Tag) b;
			return t.action.equals(action) && t.name.equals(name);
		}

		return false;
	}

	@Override
	public Object accept(Exporter exporter, Object...   o2) {
		return exporter.export(this, o2);
	}
}
