package window.commands;

import data.layer.layerobjects.Tag;
import data.layer.layerobjects.TagObject;
import window.elements.Modifier;

public class TagAddCommand implements Command {

	private final Modifier mod;
	private final TagObject tagObject;
	private final String 	tagName;

	public TagAddCommand(Modifier mod, TagObject tagObject, String tagName) {
		this.mod = mod;
		this.tagObject = tagObject;
		this.tagName = tagName;
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		this.tagObject.addTag(new Tag(tagName));
		commandHistory.addCommand(this);
	}

	@Override
	public void redo() {
		this.tagObject.addTag(new Tag(tagName));
		mod.setTagObject(tagObject);
	}

	@Override
	public void undo() {
		this.tagObject.removeTag(tagName);
		mod.setTagObject(tagObject);
	}

	@Override
	public boolean hasDifferentEffectThanLastCommand(Command lastCommand) {
		return true;
	}
}
