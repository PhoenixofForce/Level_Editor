package window.commands;

import java.util.Stack;

public class History {

	private Stack<Command> commandHistory;
	private Stack<Command> commandFuture;

	public History() {
		commandHistory = new Stack<>();
		commandFuture = new Stack<>();
	}

	public void addCommand(Command c) {
		commandHistory.push(c);
	}

	public void undo() {
		Command c = commandHistory.pop();
		if(c != null) {
			c.undo();
			commandFuture.push(c);
		}
	}

	public void redo() {
		Command c = commandFuture.pop();
		if(c != null) {
			c.undo();
			commandHistory.push(c);
		}
	}
}
