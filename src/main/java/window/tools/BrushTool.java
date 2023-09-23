package window.tools;

import data.Location;
import data.layer.AreaLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import window.EditorError;
import window.Window;
import window.commands.CommandHistory;
import window.commands.SetCommand;
import window.Selection;
import window.elements.MapViewer;

import java.util.Optional;

public class BrushTool implements ToolImplementation {

    @Override
    public Optional<EditorError> onMouseClick(CommandHistory history, int button, Layer selectedLayer, String selectedTexture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return set(history, button, selectedLayer, selectedTexture, mapPosition, selection, false);
    }

    @Override
    public Optional<EditorError> onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return set(history, button, layer, texture, mapPosition, selection, true);
    }

    private Optional<EditorError> set(CommandHistory history, int button, Layer selectedLayer, String selectedTexture, Location pos, Selection selection, boolean isDragging) {
        if(button != 0) return Optional.of(new EditorError("", false, false));

        Window window = Window.INSTANCE;
        MapViewer mv = window.getMapViewer();
        if (selectedLayer == null || (selectedTexture == null && !(selectedLayer instanceof AreaLayer)) /*|| layerPane.isHidden(selectedLayer)*/) {
            String error = "You need to select a layer and texture first";
            return Optional.of(new EditorError(error, false, true));
        }

        float screenX = pos.x * window.getTileSize();
        float screenY = pos.y * window.getTileSize();
        boolean positionOutsideOfSelection = selection != null && !selection.getArea().contains(screenX, screenY);
        if(selectedLayer instanceof TileLayer && positionOutsideOfSelection) {
            String error = "You can only draw inside of the selection";
            return Optional.of(new EditorError(error, false, true));
        }

        if(mv.getBulkCommand() == null) {
            mv.setBulkCommand(new SetCommand(window.getTagModifier(), selectedLayer,
                                                selectedTexture, pos, isDragging, window.getAutoTile()));
        } else {
            SetCommand sc = (SetCommand) mv.getBulkCommand();
            sc.add(pos, selectedTexture);
        }

        return Optional.empty();
    }
}
