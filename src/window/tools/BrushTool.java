package window.tools;

import data.Location;
import data.layer.AreaLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import window.Window;
import window.commands.CommandHistory;
import window.commands.SetCommand;
import window.elements.Selection;

public class BrushTool implements Tool {

    @Override
    public boolean onMouseClick(CommandHistory history, int button, Layer selectedLayer, String selectedTexture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return set(history, button, selectedLayer, selectedTexture, mapPosition, selection, false);
    }

    @Override
    public boolean onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return set(history, button, layer, texture, mapPosition, selection, true);
    }

    private boolean set(CommandHistory history, int button, Layer selectedLayer, String selectedTexture, Location pos, Selection selection, boolean isDragging) {
        if(button != 0) return false;

        Window window = Window.INSTANCE;
        if (selectedLayer == null || (selectedTexture == null && !(selectedLayer instanceof AreaLayer)) /*|| layerPane.isHidden(selectedLayer)*/) {
            return false;
        }

        boolean positionOutsideOfSelection = selection != null && !selection.getArea().contains(pos.x * window.getMap().getTileSize(), pos.y * window.getMap().getTileSize());
        if(selectedLayer instanceof TileLayer && positionOutsideOfSelection)
            return false;

        if(window.getMapViewer().getBulkCommand() == null) {
            window.getMapViewer().setBulkCommand(new SetCommand(window.getMapViewer().getModifier(), selectedLayer, selectedTexture, pos, isDragging, window.getAutoTile()));
        } else {
            SetCommand sc = (SetCommand) window.getMapViewer().getBulkCommand();
            sc.add(pos, selectedTexture);
        }

        return true;
    }
}
