package window.keyCombinations;

import java.awt.event.KeyEvent;

public class LambdaCombination implements KeyCombination {

    private final KeyCombinationTrigger trigger;
    private final Runnable onTrigger;

    public LambdaCombination(char keyChar, Runnable onTrigger) {
        this.trigger = KeyCombinationTrigger.withControl(keyChar);
        this.onTrigger = onTrigger;
    }

    @Override
    public void update(KeyEvent e) {
        if(trigger.isFulfilled(e)) {
            onTrigger.run();
        }
    }
}
