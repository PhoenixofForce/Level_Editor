package window.tools;

import data.maps.GameMap;
import data.Location;
import data.layer.Layer;
import data.layer.TileLayer;
import window.EditorError;
import window.Window;
import window.commands.CommandHistory;
import window.commands.FillCommand;
import window.elements.MapViewer;
import window.Selection;

import java.util.Optional;

public class FillTool implements ToolImplementation {

    @Override
    public Optional<EditorError> onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return fill(history, button, layer, texture, mapPosition, selection);
    }

    @Override
    public Optional<EditorError> onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return fill(history, button, layer, texture, mapPosition, selection);
    }

    private Optional<EditorError> fill(CommandHistory history, int button, Layer layer, String selectedTexture, Location pos, Selection selection) {
        if(!(button == 0 || button == 2)) return Optional.of(new EditorError("", false, false));
        if(!(layer instanceof TileLayer tl)) return Optional.of(new EditorError("You can only fill on tilelayer", false, true));

        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        boolean brushMode = button == 2;

        boolean positionOutsideOfSelection = selection != null && !selection.getArea().contains(pos.x * map.getTileSize(), pos.y * map.getTileSize());
        if(positionOutsideOfSelection) return Optional.of(new EditorError("You can only fill inside the selection", false, true));

        //tl.fill(selection == null? null: selection.getArea(), rem? null: selectedTexture, pos.x, pos.y);
        new FillCommand(tl, brushMode? null: selectedTexture, pos, selection == null? null: selection.getArea(), window.getAutoTile()).execute(history);
        return Optional.empty();
    }

}
