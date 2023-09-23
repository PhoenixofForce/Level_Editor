package window.tools;

import data.Location;
import data.layer.Layer;
import window.EditorError;
import window.Selection;
import window.Window;
import window.commands.CommandHistory;
import window.commands.DragCommand;
import window.elements.MapViewer;

import java.util.Optional;

public class DragTool implements ToolImplementation {

    @Override
    public Optional<EditorError> onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return Optional.of(new EditorError("", false, false));
    }

    @Override
    public Optional<EditorError> onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location to, Selection selection, boolean shiftPressed, boolean controlPressed) {
        if(button != 2) return Optional.of(new EditorError("", false, false));

        Window window = Window.INSTANCE;
        MapViewer mv = window.getMapViewer();

        Location from = mv.getLastMousePosInMapPosition();
        boolean success = layer.drag(from.x, from.y, to.x, to.y);

        if(success) {
            if(mv.getBulkCommand() == null) {
                mv.setBulkCommand(new DragCommand(layer, from, to));
            } else {
                DragCommand dc = (DragCommand) mv.getBulkCommand();
                dc.setTo(from, to);
            }
        }

        return Optional.empty();
    }
}
