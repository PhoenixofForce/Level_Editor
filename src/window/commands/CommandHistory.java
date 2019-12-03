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
		if(canUndo() && commandHistory.peek().equals(c)) return;

		commandHistory.push(c);
		commandFuture = new Stack<>();
	}

	public void undo() {
		if(!canUndo()) return;

		Command c = commandHistory.pop();
		if(c != null) {
			c.undo();
			commandFuture.push(c);
		}
	}

	public void redo() {
		if(!canRedo()) return;

		Command c = commandFuture.pop();
		if(c != null) {
			c.redo();
			commandHistory.push(c);
		}
	}

	private boolean canUndo() {
		return commandHistory.size() > 0;
	}

	private boolean canRedo() {
		return commandFuture.size() > 0;
	}
}
