package window.keyCombinations;

import java.awt.event.KeyEvent;

public class LambdaCommand implements KeyCombination {

    private KeyCombinationTrigger trigger;
    private Runnable onTrigger;

    public LambdaCommand(char keyChar, Runnable onTrigger) {
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
