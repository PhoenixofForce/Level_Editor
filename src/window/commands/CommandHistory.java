package window.commands;

import window.elements.MapViewer;

import java.util.Stack;

public class CommandHistory {

	private final MapViewer mapViewer;

	private int historySaveIndex = 0;
	private final Stack<Command> commandHistory;
	private final Stack<Command> commandFuture;

	public CommandHistory(MapViewer mapViewer) {
		this.mapViewer = mapViewer;

		commandHistory = new Stack<>();
		commandFuture = new Stack<>();
	}

	public void addCommand(Command c) {
		if(canUndo() && !c.isWorthy(commandHistory.peek())) return;

		if(commandHistory.size() < historySaveIndex) historySaveIndex = 0;
		commandHistory.push(c);
		commandFuture.clear();

		mapViewer.updateTitle();
	}

	public void undo() {
		if(!canUndo()) return;

		Command c = commandHistory.pop();
		if(c != null) {
			c.undo();
			commandFuture.push(c);
			mapViewer.updateTitle();

			if(c instanceof MergeCopyLayerCommand) undo();
		}
	}

	public void redo() {
		if(!canRedo()) return;

		Command c = commandFuture.pop();
		if(c != null) {
			c.redo();
			commandHistory.push(c);
			mapViewer.updateTitle();

			if(canRedo() && commandFuture.peek() instanceof MergeCopyLayerCommand) redo();
		}
	}

	private boolean canUndo() {
		return commandHistory.size() > 0;
	}

	private boolean canRedo() {
		return commandFuture.size() > 0;
	}

	public boolean isCurrentlySaved() {
		return historySaveIndex == commandHistory.size();
	}

	public void save() {
		historySaveIndex = commandHistory.size();
	}
}
