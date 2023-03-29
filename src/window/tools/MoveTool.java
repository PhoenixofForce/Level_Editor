package window.tools;

import data.maps.GameMap;
import data.Location;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import window.EditorError;
import window.Window;
import window.commands.CommandHistory;
import window.commands.MergeCopyLayerCommand;
import window.commands.SelectedTilesMoveCommand;
import window.commands.SelectionMoveCommand;
import window.elements.MapViewer;
import window.Selection;

import java.util.Optional;

public class MoveTool implements ToolImplementation {

    @Override
    public Optional<EditorError> onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        if(selection == null) return Optional.of(new EditorError("You can only move with a selection", false, true));

        if(button == 2 || button == 0) {
            Window window = Window.INSTANCE;
            GameMap map =  window.getMap();
            MapViewer mv = window.getMapViewer();
            FreeLayer copyLayer = mv.getCopyLayer();

            if(mv.getBulkCommand() == null)
                mv.setBulkCommand(new SelectionMoveCommand(map.getTileWidth(), map.getTileHeight(), selection, null, true));

            SelectionMoveCommand smc = (SelectionMoveCommand) mv.getBulkCommand();
            smc.round();

            if(button == 2  && copyLayer != null) copyLayer.roundAll(map.getTileWidth(), map.getTileHeight());
            return Optional.empty();
        }

        return Optional.of(new EditorError("", false, false));
    }

    @Override
    public Optional<EditorError> onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        if(selection == null) return Optional.of(new EditorError("You can only move with a selection", false, true));

        if(button == 0 || button == 2) {
            if(button == 2) {
                Window window = Window.INSTANCE;
                GameMap map =  window.getMap();
                MapViewer mv = window.getMapViewer();
                FreeLayer copyLayer = mv.getCopyLayer();

                if(!(layer instanceof TileLayer selectedLayer)) return Optional.of(new EditorError("You can only move on a tile layer", false, true));
                if(copyLayer == null) {
                    //mv.setCopyLayer(new FreeLayer(selectedLayer.depth(), map.getWidth(), map.getHeight(), map.getTileSize()));
                    history.addCommand(new SelectedTilesMoveCommand(window.getMapViewer(), selectedLayer, selection, map.getTileWidth(), map.getTileHeight()));
                }
            }

            moveSelection(history, layer, selection, mapPosition, button == 2);
            return Optional.empty();
        }

        return Optional.of(new EditorError("", false, false));
    }

    private void moveSelection(CommandHistory history, Layer selectedLayer, Selection selection, Location to, boolean isRightClick) {
        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        Location from = mv.getLastMousePosInMapPosition();

        if(mv.getBulkCommand() == null) {
            mv.setBulkCommand(new SelectionMoveCommand(map.getTileWidth(), map.getTileHeight(), selection, from, to, mv.getCopyLayer(), isRightClick));
        } else {
            SelectionMoveCommand smc = (SelectionMoveCommand) mv.getBulkCommand();
            smc.add(from, to);
        }

        if(!(isRightClick && mv.getCopyLayer() != null)) {
            if(mv.getCopyLayer() != null && selectedLayer instanceof TileLayer) new MergeCopyLayerCommand(mv, (TileLayer) selectedLayer, mv.getCopyLayer()).execute(history);
        }
    }
}
