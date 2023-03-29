package window.keyCombinations;

import data.Location;
import data.maps.GameMap;
import data.layer.FreeLayer;
import data.layer.TileLayer;
import util.ClipBoardUtil;
import window.Tools;
import window.Window;
import window.commands.CommandHistory;
import window.commands.MergeCopyLayerCommand;
import window.commands.PasteCommand;
import window.elements.MapViewer;

import java.awt.event.KeyEvent;

public class CopyPasteCombination implements KeyCombination {

    private final KeyCombinationTrigger copyTrigger;
    private final KeyCombinationTrigger pasteTrigger;
    private final CommandHistory history;

    public CopyPasteCombination(CommandHistory history) {
        this.copyTrigger = KeyCombinationTrigger.withControl('c');
        this.pasteTrigger = KeyCombinationTrigger.withControl('v');
        this.history = history;
    }

    @Override
    public void update(KeyEvent e) {
        Window window = Window.INSTANCE;
        GameMap map =  window.getMap();
        MapViewer mv = window.getMapViewer();
        FreeLayer copyLayer = mv.getCopyLayer();

        if (copyTrigger.isFulfilled(e)) {
            if(window.getSelectedLayer() == null || !(window.getSelectedLayer() instanceof TileLayer selectedLayer)) return;
            if(copyLayer != null && window.getSelectedLayer() instanceof TileLayer) new MergeCopyLayerCommand(window.getMapViewer(), (TileLayer) window.getSelectedLayer(), copyLayer).execute(history);

            String copiedMap = "";
            for(int x = 0;  x < map.getWidth(); x++) {
                boolean hadInSel = false;
                for(int y = 0;  y < map.getHeight(); y++) {
                    Location worldPosition = map.mapToWorldSpace(new Location(x, y));
                    if(mv.getSelection().getArea().contains(worldPosition.x, worldPosition.y)) {
                        copiedMap += (selectedLayer.getTileNames()[y][x] == null? "[x]": selectedLayer.getTileNames()[y][x]) + " ";
                        hadInSel = true;
                    }
                }
                if(hadInSel) copiedMap += "\n";
            }
            ClipBoardUtil.StringToClip(copiedMap);
        }

        if(pasteTrigger.isFulfilled(e)) {
            if(copyLayer != null && window.getSelectedLayer() instanceof TileLayer) new MergeCopyLayerCommand(window.getMapViewer(), (TileLayer) window.getSelectedLayer(), copyLayer).execute(history);
            mv.setSelectedTool(Tools.MOVE);

            mv.setCopyLayer(new FreeLayer(0.5f, map.getWidth(), map.getHeight(), map.getTileWidth(), map.getTileHeight()));

            new PasteCommand(window.getMapViewer(), mv.getCopyLayer(), mv.screenToWorldSpace(mv.getWidth()/2, mv.getHeight()/2), mv.getSelection(), map.getTileWidth(), map.getTileHeight()).execute(history);
        }
    }
}
