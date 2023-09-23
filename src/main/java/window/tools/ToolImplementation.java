package window.tools;

import data.Location;
import data.layer.Layer;
import window.EditorError;
import window.commands.CommandHistory;
import window.Selection;

import java.util.Optional;

public interface ToolImplementation {

    //sometimes this is called for release sometimes for pressed
    Optional<EditorError> onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed);
    Optional<EditorError> onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed);

}
