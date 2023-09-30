package window.commands;

import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.TagObject;
import window.elements.MapViewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MergeCopyLayerCommand implements Command{

	private final MapViewer mv;
	private final TileLayer selectedLayer;
	private final FreeLayer copyLayer;

	private final List<TagObject> overwritten;

	public MergeCopyLayerCommand(MapViewer mv, TileLayer selectedLayer, FreeLayer copyLayer) {
		this.mv = mv;
		this.selectedLayer = selectedLayer;
		this.copyLayer = copyLayer;

		overwritten = new ArrayList<>();
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		//TODO: SelectedLayer could be other Layer
		for(GameObject g: copyLayer.getImages()) {
			Optional<TagObject> removedObject = selectedLayer.remove((int) g.x, (int) g.y);

			GameObject removed = (GameObject) removedObject.orElse(new GameObject(null, g.x, g.y, 1, 1));
			overwritten.add(removed);
			selectedLayer.set(g.name, (int)g.x, (int)g.y, false);
		}

		mv.setCopyLayer(null);
		commandHistory.addCommand(this);
	}

	@Override
	public void redo() {
		for(GameObject g: copyLayer.getImages()) {
			selectedLayer.set(g.name, (int)g.x, (int)g.y, false);
		}

		mv.setCopyLayer(null);
	}

	@Override
	public void undo() {
		for(TagObject to: overwritten) {
			selectedLayer.add(to);
		}

		mv.setCopyLayer(copyLayer);
	}

	@Override
	public boolean hasDifferentEffectThanLastCommand(Command lastCommand) {
		return true;
	}
}
