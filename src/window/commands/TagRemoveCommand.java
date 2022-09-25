package window.commands;

import data.layer.layerobjects.Tag;
import data.layer.layerobjects.TagObject;
import window.elements.Modifier;

public class TagRemoveCommand implements Command {

	private final Modifier mod;
	private final TagObject tagObject;
	private final String 	tagName;
	private Tag removedTag;

	public TagRemoveCommand(Modifier mod, TagObject tagObject, String tagName) {
		this.mod = mod;
		this.tagObject = tagObject;
		this.tagName = tagName;
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		this.removedTag = tagObject.removeTag(tagName);
		commandHistory.addCommand(this);
	}

	@Override
	public void redo() {
		tagObject.removeTag(tagName);
		mod.setTagObject(tagObject);
	}

	@Override
	public void undo() {
		this.tagObject.addTag(removedTag);
		mod.setTagObject(tagObject);
	}

	@Override
	public boolean isWorthy(Command lastCommand) {
		return true;
	}
}
