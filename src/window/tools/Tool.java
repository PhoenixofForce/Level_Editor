package window.tools;

import data.Location;
import data.layer.Layer;
import window.commands.CommandHistory;
import window.elements.Selection;

public interface Tool {

    //sometimes this is called for release sometimes for pressed
    boolean onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed);
    boolean onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed);

}
