package window.keyCombinations;

import window.elements.Camera;

import java.awt.event.KeyEvent;

public class ZoomCombination implements KeyCombination {

    private final KeyCombinationTrigger zoomInTrigger;
    private final KeyCombinationTrigger zoomOutTrigger;
    private final Camera camera;

    public ZoomCombination(Camera camera) {
        this.zoomInTrigger = KeyCombinationTrigger.withControl('+');
        this.zoomOutTrigger = KeyCombinationTrigger.withControl('-');
        this.camera = camera;
    }

    @Override
    public void update(KeyEvent e) {
        if(zoomInTrigger.isFulfilled(e)) {
            camera.setZoom(camera.zoom * (float) Math.pow(1.2, 1));
        }

        if(zoomOutTrigger.isFulfilled(e)) {
            camera.setZoom(camera.zoom * (float) Math.pow(1.2, -1));
        }
    }

}
