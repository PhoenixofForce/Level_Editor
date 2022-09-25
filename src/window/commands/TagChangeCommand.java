package window.commands;

import data.layer.layerobjects.TagObject;
import window.elements.Modifier;

public class TagChangeCommand implements Command {

	private final Modifier mod;
	private final TagObject tagObject;
	private final String 	tagName;
	private final String oldContent;
	private final String newContent;

	public TagChangeCommand(Modifier mod, TagObject tagObject, String tagName, String oldContent, String newContent) {
		this.mod = mod;
		this.tagObject = tagObject;
		this.tagName = tagName;
		this.oldContent = oldContent;
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
