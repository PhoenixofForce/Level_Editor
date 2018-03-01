package data.layer.layerobjects;

public class Tag {

	private String name, action;

	public Tag(String n) {
		this.name = n;
		action = "";
	}

	public Tag(String n, String a) {
		this.name = n;
		this.action = a.replaceAll("δ", ";");
	}

	public void setAction(String a) {
		this.action = a;
	}

	public String toMapFormat() {
		return String.format("[tag; %s; %s]", name, action.replaceAll(";", "δ").replaceAll("\n", ""));
	}

	public String getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object b) {
		if(b instanceof Tag) {
			Tag t = (Tag) b;
			return t.action.equals(action) && t.name.equals(name);
		}

		return false;
	}
}
