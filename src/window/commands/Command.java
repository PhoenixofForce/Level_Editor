package window.commands;

public interface Command {

	void execute(CommandHistory commandHistory);
	void redo();
	void undo();
}
