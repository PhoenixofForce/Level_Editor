package window.commands;

public interface Command {

	void execute(History history);
	void redo();
	void undo();
}
