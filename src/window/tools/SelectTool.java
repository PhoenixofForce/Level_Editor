package window.tools;

import data.GameMap;
import data.Location;
import data.layer.Layer;
import window.EditorError;
import window.Window;
import window.commands.CommandHistory;
import window.commands.SelectionChangeCommand;
import window.elements.MapViewer;
import window.Selection;

import java.awt.*;
import java.util.Optional;

public class SelectTool implements ToolImplementation {

    private Location startClick;

    @Override
    public Optional<EditorError> onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        if(button == 0 && startClick == null) {
            startClick = mapPosition;
            return Optional.empty();
        }

        else if(button == 0 && startClick != null) {
            int x = (int)(Math.min(mapPosition.x, startClick.x));
            int y = (int)(Math.min(mapPosition.y, startClick.y));

            if(x < 0) x = 0;
            if(y < 0) y = 0;

            int w = (int)(Math.max(mapPosition.x, startClick.x)) - x + 1;
            int h = (int)(Math.max(mapPosition.y, startClick.y)) - y + 1;

            if(w < 0 || h < 0 || x > map.getWidth() || y > map.getWidth()) {
                startClick = null;
                new SelectionChangeCommand(window.getMapViewer(), selection, null).execute(history);
                return Optional.of(new EditorError("", false, false));
            }

            if(x + w > map.getWidth()) w = map.getWidth() - x;
            if(y + h > map.getHeight()) h = map.getHeight() - y;

            Rectangle r = new Rectangle(x * map.getTileSize(), y * map.getTileSize(), w * map.getTileSize(),h * map.getTileSize());
            if(selection == null || (!shiftPressed && !controlPressed)) {
                Selection newSelection = new Selection();
                newSelection.add(r);

                new SelectionChangeCommand(window.getMapViewer(), selection, newSelection).execute(history);
            } else if(shiftPressed && !controlPressed) {
                Selection newSelection = selection.clone();
                newSelection.add(r);

                new SelectionChangeCommand(window.getMapViewer(), selection, newSelection).execute(history);
            }
            else if(!shiftPressed && controlPressed) {
                Selection newSelection = selection.clone();
                newSelection.subtract(r);

                new SelectionChangeCommand(window.getMapViewer(), selection, newSelection).execute(history);
            }
            startClick = null;
            return Optional.empty();
        }

        else if(button == 2) {
            new SelectionChangeCommand(window.getMapViewer(), selection, null).execute(history);
            startClick = null;
            return Optional.empty();
        }

        return Optional.of(new EditorError("", false, false));
    }

    @Override
    public Optional<EditorError> onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return Optional.of(new EditorError("", false, false));
    }

    public Location getStartClick() {
        return startClick;
    }

    public void eraseStartClick() {
        startClick = null;
    }
}
