package data.layer.layerobjects;

import data.io.exporter.Exporter;
import data.io.exporter.ExporterData;
import lombok.Getter;

/**
 * Class to store tags the user entered
 */
@Getter
public class Tag implements Exporter.Exportable {

	private final String name;
	private String action;		//name and content of the tag

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

	@Override
	public Tag clone() {
		return new Tag(name, action);
	}

	@Override
	public boolean equals(Object b) {
		if(b instanceof Tag t) {
			return t.action.equals(action) && t.name.equals(name);
		}

		return false;
	}

	@Override
	public Object accept(Exporter exporter, ExporterData data) {
		return exporter.export(this, data);
	}
}
