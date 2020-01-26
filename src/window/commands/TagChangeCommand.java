package window.commands;

import data.layer.layerobjects.TagObject;
import window.elements.Modifier;

public class TagChangeCommand implements Command {

	private Modifier mod;
	private TagObject tagObject;
	private String 	tagName,
					oldContent,
					newContent;

	public TagChangeCommand(Modifier mod, TagObject tagObject, String tagName, String oldConent, String newContent) {
		this.mod = mod;
		this.tagObject = tagObject;
		this.tagName = tagName;
		this.oldContent = oldConent;
		this.newContent = newContent;
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		this.tagObject.getTag(this.tagName).setAction(this.newContent);
		commandHistory.addCommand(this);
	}

	@Override
	public void redo() {
		this.tagObject.getTag(this.tagName).setAction(this.newContent);
		mod.setTagObject(tagObject);
	}

	@Override
	public void undo() {
		this.tagObject.getTag(this.tagName).setAction(this.oldContent);
		mod.setTagObject(tagObject);
	}

	@Override
	public boolean isWorthy(Command lastCommand) {
		return !oldContent.equals(newContent);
	}
}
