package window.tools;

import data.GameMap;
import data.Location;
import data.layer.Layer;
import window.Tools;
import window.Window;
import window.commands.CommandHistory;
import window.commands.SelectionChangeCommand;
import window.elements.MapViewer;
import window.elements.Selection;

import java.awt.*;

public class SelectTool implements Tool {

    private Location startClick;

    @Override
    public boolean onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        if(button == 0 && startClick == null) {
            startClick = mapPosition;
            return true;
        }

        else if(button == 0 && startClick != null) {
            Location last = mapPosition;
            int x = (int)(Math.min(last.x, startClick.x));
            int y = (int)(Math.min(last.y, startClick.y));

            if(x < 0) x = 0;
            if(y < 0) y = 0;

            int w = (int)(Math.max(last.x, startClick.x)) - x + 1;
            int h = (int)(Math.max(last.y, startClick.y)) - y + 1;

            if(w < 0 || h < 0 || x > map.getWidth() || y > map.getWidth()) {
                startClick = null;
                new SelectionChangeCommand(window.getMapViewer(), selection, null).execute(history);
                return false;
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
            return true;
        }

        else if(button == 2) {
            new SelectionChangeCommand(window.getMapViewer(), selection, null).execute(history);
            startClick = null;
            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return button == 0;
    }

    public Location getStartClick() {
        return startClick;
    }

    public void eraseStartClick() {
        startClick = null;
    }
}
