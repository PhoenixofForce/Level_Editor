package data.layer.layerobjects;

import data.exporter.Exporter;

/**
 * Class to store tags the user entered
 */
public class Tag implements Exporter.Exportable {

	private String name, action;		//name and content of the tag

	/**
	 * @param n name of the tag
	 */
	public Tag(String n) {
		this.name = n;
		action = "";
	}

	/**
	 * @param n name of the tag
	 * @param a action of the tag
	 */
	public Tag(String n, String a) {
		this.name = n;
		this.action = a.replaceAll("δ", ";");
	}

	/**
	 * replaces the current action
	 * @param a the new action of this tag
	 */
	public void setAction(String a) {
		this.action = a;
	}

	/**
	 * converts class to saveable text-format
	 * @return
	 */
	/*public String toMapFormat() {
		return String.format("[tag; %s; %s]", name, action.replaceAll(";", "δ").replaceAll("\n", ""));
	}*/

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
	public String accept(Exporter exporter, Object o2) {
		return exporter.export(this, o2);
	}
}
