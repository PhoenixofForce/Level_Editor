package window.tools;

import data.GameMap;
import data.Location;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;
import window.Tools;
import window.Window;
import window.commands.CommandHistory;
import window.commands.MergeCopyLayerCommand;
import window.commands.SelectedTilesMoveCommand;
import window.commands.SelectionMoveCommand;
import window.elements.MapViewer;
import window.elements.Selection;

public class MoveTool implements Tool {

    @Override
    public boolean onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        if(selection == null) return false;

        if(button == 2 || button == 0) {
            Window window = Window.INSTANCE;
            GameMap map =  window.getMap();
            MapViewer mv = window.getMapViewer();
            FreeLayer copyLayer = mv.getCopyLayer();

            if(mv.getBulkCommand() == null)
                mv.setBulkCommand(new SelectionMoveCommand(map.getTileSize(), selection, null, true));

            SelectionMoveCommand smc = (SelectionMoveCommand) mv.getBulkCommand();
            smc.round();

            if(button == 2  && copyLayer != null) copyLayer.roundAll(map.getTileSize());
            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        if(selection == null) return false;

        if(button == 0 || button == 2) {
            if(button == 2) {
                Window window = Window.INSTANCE;
                GameMap map =  window.getMap();
                MapViewer mv = window.getMapViewer();
                FreeLayer copyLayer = mv.getCopyLayer();

                if(!(layer instanceof TileLayer selectedLayer)) return false;
                if(copyLayer == null) {
                    //mv.setCopyLayer(new FreeLayer(selectedLayer.depth(), map.getWidth(), map.getHeight(), map.getTileSize()));
                    history.addCommand(new SelectedTilesMoveCommand(window.getMapViewer(), selectedLayer, selection, map.getTileSize()));
                }
            }

            moveSelection(history, layer, selection, mapPosition, button == 2);
            return true;
        }

        return false;
    }

    private void moveSelection(CommandHistory history, Layer selectedLayer, Selection selection, Location to, boolean isRightClick) {
        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();

        Location from = mv.getLastMousePosInMapPosition();

        if(mv.getBulkCommand() == null) {
            mv.setBulkCommand(new SelectionMoveCommand(map.getTileSize(), selection, from, to, mv.getCopyLayer(), isRightClick));
        } else {
            SelectionMoveCommand smc = (SelectionMoveCommand) mv.getBulkCommand();
            smc.add(from, to);
        }

        if(!(isRightClick && mv.getCopyLayer() != null)) {
            if(mv.getCopyLayer() != null && selectedLayer instanceof TileLayer) new MergeCopyLayerCommand(mv, (TileLayer) selectedLayer, mv.getCopyLayer()).execute(history);
        }
    }
}
