package window.keyCombinations;

import data.GameMap;
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

    private KeyCombinationTrigger copyTrigger;
    private KeyCombinationTrigger pasteTrigger;
    private CommandHistory history;

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
            if(mv.getSelectedLayer() == null || !(mv.getSelectedLayer() instanceof TileLayer selectedLayer)) return;
            if(copyLayer != null && mv.getSelectedLayer() instanceof TileLayer) new MergeCopyLayerCommand(window.getMapViewer(), (TileLayer) mv.getSelectedLayer(), copyLayer).execute(history);

            String copiedMap = "";
            for(int x = 0;  x < map.getWidth(); x++) {
                boolean hadInSel = false;
                for(int y = 0;  y < map.getHeight(); y++) {
                    if(mv.getSelection().getArea().contains(x * map.getTileSize(), y * map.getTileSize())) {
                        copiedMap += (selectedLayer.getTileNames()[y][x] == null? "[x]": selectedLayer.getTileNames()[y][x]) + " ";
                        hadInSel = true;
                    }
                }
                if(hadInSel) copiedMap += "\n";
            }
            ClipBoardUtil.StringToClip(copiedMap);
        }

        if(pasteTrigger.isFulfilled(e)) {
            if(copyLayer != null && mv.getSelectedLayer() instanceof TileLayer) new MergeCopyLayerCommand(window.getMapViewer(), (TileLayer) mv.getSelectedLayer(), copyLayer).execute(history);
            mv.setTool(Tools.MOVE);

            mv.setCopyLayer(new FreeLayer(0.5f, map.getWidth(), map.getHeight(), map.getTileSize()));

            new PasteCommand(window.getMapViewer(), mv.getCopyLayer(), mv.windowToMapPosition(mv.getWidth()/2, mv.getHeight()/2), mv.getSelection(), map.getTileSize()).execute(history);
        }
    }
}
