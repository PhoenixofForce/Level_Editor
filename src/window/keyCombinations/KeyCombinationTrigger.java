package window.keyCombinations;

import java.awt.event.KeyEvent;

public record KeyCombinationTrigger(char keyChar, boolean needsControl, boolean needsShift, boolean needsAlt) {

    public boolean isFulfilled(KeyEvent e) {
        int charKeyCode = KeyEvent.getExtendedKeyCodeForChar(keyChar);

        boolean charDown = e.getKeyCode() == charKeyCode || e.getKeyChar() == Character.toUpperCase(keyChar) || e.getKeyChar() == Character.toLowerCase(keyChar);
        boolean shiftDown = !needsShift || e.isShiftDown();
        boolean altDown = !needsAlt || e.isAltDown();
        boolean controlDown = !needsControl || e.isControlDown();

        System.out.println(keyChar + " " + charDown + " " + controlDown );

        return charDown && shiftDown && altDown && controlDown;
    }

    public static KeyCombinationTrigger simpleCombination(char keyChar) {
        return new KeyCombinationTrigger(keyChar, false, false, false);
    }

    public static KeyCombinationTrigger withControl(char keyChar) {
        return new KeyCombinationTrigger(keyChar, true, false, false);
    }

    public static KeyCombinationTrigger withShift(char keyChar) {
        return new KeyCombinationTrigger(keyChar, false, true, false);
    }

    public static KeyCombinationTrigger withAlt(char keyChar) {
        return new KeyCombinationTrigger(keyChar, false, false, true);
    }
}
