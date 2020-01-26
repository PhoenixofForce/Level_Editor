package window.commands;

import java.util.Stack;

public class CommandHistory {

	private Stack<Command> commandHistory;
	private Stack<Command> commandFuture;

	public CommandHistory() {
		commandHistory = new Stack<>();
		commandFuture = new Stack<>();
	}

	public void addCommand(Command c) {
		if(canUndo() && !c.isWorthy(commandHistory.peek())) return;

		commandHistory.push(c);
		commandFuture.clear();
	}

	public void undo() {
		if(!canUndo()) return;

		Command c = commandHistory.pop();
		if(c != null) {
			c.undo();
			commandFuture.push(c);

			if(c instanceof MergeCopyLayerCommand) undo();
		}
	}

	public void redo() {
		if(!canRedo()) return;

		Command c = commandFuture.pop();
		if(c != null) {
			c.redo();
			commandHistory.push(c);

			if(canRedo() && commandFuture.peek() instanceof MergeCopyLayerCommand) redo();
		}
	}

	private boolean canUndo() {
		return commandHistory.size() > 0;
	}

	private boolean canRedo() {
		return commandFuture.size() > 0;
	}
}
