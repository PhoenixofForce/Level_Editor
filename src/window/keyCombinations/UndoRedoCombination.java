package window.keyCombinations;

import window.commands.CommandHistory;

import java.awt.event.KeyEvent;

public class UndoRedoCombination implements KeyCombination {

    private KeyCombinationTrigger undoTrigger, redoTrigger;
    private CommandHistory history;

    public UndoRedoCombination(CommandHistory history) {
        this.undoTrigger = KeyCombinationTrigger.withControl('z');
        this.redoTrigger = KeyCombinationTrigger.withControl('y');
        this.history = history;
    }

    @Override
    public void update(KeyEvent e) {
         if (undoTrigger.isFulfilled(e)) {
             history.undo();
        }

         if (redoTrigger.isFulfilled(e)) {
             history.redo();
        }
    }
}
