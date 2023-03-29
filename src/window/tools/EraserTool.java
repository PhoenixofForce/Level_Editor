package window.tools;

import data.maps.GameMap;
import data.Location;
import data.layer.Layer;
import data.layer.TileLayer;
import window.EditorError;
import window.Window;
import window.commands.CommandHistory;
import window.commands.RemoveCommand;
import window.elements.MapViewer;
import window.Selection;

import java.util.Optional;

public class EraserTool implements ToolImplementation {
    @Override
    public Optional<EditorError> onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return remove(history, button, layer,texture, mapPosition, selection, false);
    }

    @Override
    public Optional<EditorError> onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return remove(history, button, layer,texture, mapPosition, selection, true);
    }

    private Optional<EditorError> remove(CommandHistory history, int button, Layer selectedLayer, String texture, Location pos, Selection selection, boolean isDragged) {
        if(button != 0) return Optional.of(new EditorError("", false, false));

        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        boolean positionOutsideOfSelection = selection != null && !selection.getArea().contains(pos.x * map.getTileSize(), pos.y * map.getTileSize());
        if(selectedLayer instanceof TileLayer && positionOutsideOfSelection) return Optional.of(new EditorError("You can only erase inside of the selection", false, true));

        if(window.getMapViewer().getBulkCommand() == null) {
            mv.setBulkCommand(new RemoveCommand(Window.INSTANCE.getModifier(), selectedLayer, pos));
        } else {
            RemoveCommand rc = (RemoveCommand) mv.getBulkCommand();
            rc.add(pos);
        }

        return Optional.empty();
    }

}
