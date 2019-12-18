package window.commands;

import window.elements.MapViewer;
import window.elements.Selection;

public class SelectionChangeCommand implements Command {

	private MapViewer mv;
	private Selection oldSelection, newSelection;
	public SelectionChangeCommand(MapViewer mv, Selection oldSelection, Selection newSelection) {
		this.mv = mv;
		this.oldSelection = oldSelection;
		this.newSelection = newSelection;
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		redo();
		commandHistory.addCommand(this);
	}

	@Override
	public void redo() {
		mv.setSelection(newSelection);
	}

	@Override
	public void undo() {
		mv.setSelection(oldSelection);
	}

	@Override
	public boolean isWorthy(Command lastCommand) {
		return (!(oldSelection == null && newSelection == null)) || (oldSelection != null && newSelection != null && !oldSelection.equals(newSelection));
	}
}
