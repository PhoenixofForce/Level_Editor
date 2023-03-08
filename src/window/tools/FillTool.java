package window.tools;

import data.GameMap;
import data.Location;
import data.layer.Layer;
import data.layer.TileLayer;
import window.Window;
import window.commands.CommandHistory;
import window.commands.FillCommand;
import window.elements.MapViewer;
import window.elements.Selection;

public class FillTool implements Tool {

    @Override
    public boolean onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return fill(history, button, layer, texture, mapPosition, selection);
    }

    @Override
    public boolean onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return fill(history, button, layer, texture, mapPosition, selection);
    }

    private boolean fill(CommandHistory history, int button, Layer layer, String selectedTexture, Location pos, Selection selection) {
        if(!(button == 0 || button == 2)) return false;
        if(!(layer instanceof TileLayer tl)) return false;

        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        boolean brushMode = button == 2;

        boolean positionOutsideOfSelection = selection != null && !selection.getArea().contains(pos.x * map.getTileSize(), pos.y * map.getTileSize());
        if(positionOutsideOfSelection) return false;

        //tl.fill(selection == null? null: selection.getArea(), rem? null: selectedTexture, pos.x, pos.y);
        new FillCommand(tl, brushMode? null: selectedTexture, pos, selection == null? null: selection.getArea(), window.getAutoTile()).execute(history);
        return true;
    }

}
