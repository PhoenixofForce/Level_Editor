package window.elements;

import data.Location;
import data.layer.Layer;
import data.layer.TileLayer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import window.Tools;
import window.Window;
import window.commands.MergeCopyLayerCommand;
import window.tools.DragTool;
import window.tools.SelectTool;
import window.tools.TagSelectTool;
import window.tools.ToolImplementation;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class MapViewerMouseAdapter extends MouseAdapter {

    private int lastMousePosX;
    private int lastMousePosY;

    private int lastMiddleClickX;
    private int lastMiddleClickY;

    private boolean mouseEntered = false;

    private final Window window;
    private final MapViewer mapViewer;

    private final ToolImplementation tagSelectTool, dragTool;

    public MapViewerMouseAdapter(MapViewer mapViewer) {
        window = Window.INSTANCE;
        this.mapViewer = mapViewer;

        tagSelectTool = new TagSelectTool();
        dragTool = new DragTool();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseEntered = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseEntered = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastMousePosX = e.getX();
        lastMousePosY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int button = 0;

        if(SwingUtilities.isMiddleMouseButton(e)) button = 1;
        else if(SwingUtilities.isRightMouseButton(e)) button = 2;

        boolean toolExecutionRan;
        if(pickTextureConditionSatisfied(e, button)) {
            pickTexture(e);
        } else {
           toolExecutionRan = mapViewer.executeToolAction(button, e.getX(), e.getY(), true, e.isShiftDown(), e.isControlDown());

           if(!toolExecutionRan) {
                if (button == 1) {
                    mapViewer.getCamera().move((e.getX() - lastMousePosX) / mapViewer.getCamera().zoom,(e.getY() - lastMousePosY) / mapViewer.getCamera().zoom);
                } else if (button == 2) {
                    mapViewer.executeToolAction(dragTool, button, e.getX(), e.getY(), true, false, false);
                }
            }
        }

        lastMousePosX = e.getX();
        lastMousePosY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mapViewer.requestFocus();
        mapViewer.grabFocus();

        if(pickTextureConditionSatisfied(e, e.getButton() - 1)) {
            pickTexture(e);
        } else {
            boolean executedToolAction =  false;
            if(mapViewer.getSelectedTool() != Tools.MOVE)
                executedToolAction = mapViewer.executeToolAction(e.getButton() - 1, e.getX(), e.getY(), false, e.isShiftDown(), e.isControlDown());
            if(executedToolAction) return;
        }

        if (e.getButton() == 3) mapViewer.executeToolAction(tagSelectTool, 2, e.getX(), e.getY(), false, false, false);
        else if (e.getButton() == 2) {
            //Save clicked position
            lastMiddleClickX = e.getX();
            lastMiddleClickY = e.getY();
        } else if (e.getButton() == 4) {
            window.getTagModifier().setTagObject(window.getMap());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        boolean shift = e.isShiftDown();
        boolean control = e.isControlDown();

        //when the difference on middle mouse click and middle mouse release is smaller than => swap between tools
        if (e.getButton() == 2 && Math.abs(lastMiddleClickX - e.getX()) <= 10 && Math.abs(lastMiddleClickY - e.getY()) <= 10) {
            if(!shift) {
                mapViewer.setSelectedTool(mapViewer.getSelectedTool().next());
                if(mapViewer.getSelectedTool() == Tools.MOVE && mapViewer.getSelection() == null) {
                    mapViewer.setSelectedTool(mapViewer.getSelectedTool().next());
                }
            }
            else {
                mapViewer.setSelectedTool(mapViewer.getSelectedTool().pre());
                if(mapViewer.getSelectedTool() == Tools.MOVE && mapViewer.getSelection() == null){
                    mapViewer.setSelectedTool(mapViewer.getSelectedTool().pre());
                }
            }
            window.getToolbar().update(mapViewer.getSelectedTool());

            ((SelectTool) Tools.SELECT.getImplementation()).eraseStartClick();
            if(mapViewer.getCopyLayer() != null && window.getSelectedLayer() instanceof TileLayer tileLayer)
                new MergeCopyLayerCommand(window.getMapViewer(), tileLayer, mapViewer.getCopyLayer())
                        .execute(mapViewer.getCommandHistory());
        }

        if(pickTextureConditionSatisfied(e, e.getButton() - 1)) {
            pickTexture(e);
        } else {
            mapViewer.executeToolAction(e.getButton() - 1, e.getX(), e.getY(), false, shift, control);

            if(mapViewer.getBulkCommand() != null) {
                mapViewer.getCommandHistory().addCommand(mapViewer.getBulkCommand());
                mapViewer.setBulkCommand(null);
            }
        }
    }

    private void pickTexture(MouseEvent e) {
        Location mapCoordinates = mapViewer.windowToMapPosition(e.getX(), e.getY());
        Optional<String> texture = window.getSelectedLayer().textureAt(mapCoordinates.x, mapCoordinates.y);

        Iterator<String> layerIterator = window.getMap().getLayers().keySet().iterator();
        while(texture.isEmpty() && layerIterator.hasNext()) {
            Layer layer = window.getMap().getLayer(layerIterator.next());
            texture = layer.textureAt(mapCoordinates.x, mapCoordinates.y);
        }

        texture.ifPresent(s -> window.getImageDisplay().setSelectedImageByName(s));
    }

    private boolean pickTextureConditionSatisfied(MouseEvent e, int button) {
        boolean isLeftClick = button == 0;
        boolean altDown = e.isAltDown();
        boolean relevantToolSelected = List.of(Tools.BRUSH, Tools.BUCKET).contains(mapViewer.getSelectedTool());

        return isLeftClick && altDown && relevantToolSelected;
    }
}
