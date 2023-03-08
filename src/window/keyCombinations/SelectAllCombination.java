package window.keyCombinations;

import data.GameMap;
import window.Tools;
import window.Window;
import window.commands.CommandHistory;
import window.commands.SelectionChangeCommand;
import window.elements.MapViewer;
import window.elements.Selection;
import window.tools.SelectTool;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SelectAllCombination implements KeyCombination {

    private KeyCombinationTrigger trigger;
    private CommandHistory history;

    public SelectAllCombination(CommandHistory history) {
        this.trigger = KeyCombinationTrigger.withControl('a');
        this.history = history;
    }

    @Override
    public void update(KeyEvent e) {
        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        if (trigger.isFulfilled(e)) {
            if(mv.getSelection() != null) new SelectionChangeCommand(mv, mv.getSelection(), null).execute(history);
            else {
                Selection newSelection = new Selection();
                newSelection.add(new Rectangle(0, 0, map.getWidth() * map.getTileSize(), map.getHeight() * map.getTileSize()));

                new SelectionChangeCommand(window.getMapViewer(), mv.getSelection(), newSelection).execute(history);
            }
            ((SelectTool) Tools.SELECT.getImplementation()).eraseStartClick();
        }
    }
}
