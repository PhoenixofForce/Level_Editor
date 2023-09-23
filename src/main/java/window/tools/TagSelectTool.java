package window.tools;

import data.Location;
import data.layer.Layer;
import data.layer.layerobjects.TagObject;
import window.EditorError;
import window.Selection;
import window.Window;
import window.commands.CommandHistory;

import java.util.Optional;

public class TagSelectTool implements ToolImplementation {

    @Override
    public Optional<EditorError> onMouseClick(CommandHistory history, int button, Layer layer, String texture, Location pos, Selection selection, boolean shiftPressed, boolean controlPressed) {
        if(button != 2) {
            return Optional.of(new EditorError("", false, false));
        }

        Window window = Window.INSTANCE;
        TagObject obj = layer.select(pos.x, pos.y);

        window.getTagModifier().setTagObject(obj);

        return Optional.empty();
    }

    @Override
    public Optional<EditorError> onMouseDrag(CommandHistory history, int button, Layer layer, String texture, Location mapPosition, Selection selection, boolean shiftPressed, boolean controlPressed) {
        return Optional.of(new EditorError("", false, false));
    }
}
