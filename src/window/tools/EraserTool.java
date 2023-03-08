package window.tools;

import data.GameMap;
import data.Location;
import data.layer.Layer;
import data.layer.TileLayer;
import window.Window;
import window.commands.CommandHistory;
import window.commands.RemoveCommand;
import window.elements.MapViewer;
import window.elements.Selection;

public class EraserTool implements Tool {
    @Override
    public boolean onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return remove(history, button, layer,texture, mapPosition, selection, false);
    }

    @Override
    public boolean onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return remove(history, button, layer,texture, mapPosition, selection, true);
    }

    private boolean remove(CommandHistory history, int button, Layer selectedLayer, String texture, Location pos, Selection selection, boolean isDragged) {
        if(button != 0) return false;

        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        boolean positionOutsideOfSelection = selection != null && !selection.getArea().contains(pos.x * map.getTileSize(), pos.y * map.getTileSize());
        if(selectedLayer instanceof TileLayer && positionOutsideOfSelection) return false;

        if(window.getMapViewer().getBulkCommand() == null) {
            mv.setBulkCommand(new RemoveCommand(mv.getModifier(), selectedLayer, pos));
        } else {
            RemoveCommand rc = (RemoveCommand) mv.getBulkCommand();
            rc.add(pos);
        }

        return true;
    }

}
