package window.commands;

import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.TagObject;
import window.elements.MapViewer;

import java.util.ArrayList;
import java.util.List;

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
			GameObject removed = (GameObject) selectedLayer.remove((int)g.x, (int)g.y);
			overwritten.add(removed != null? removed: new GameObject(null, g.x, g.y, 1, 1));
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
	public boolean isWorthy(Command lastCommand) {
		return true;
	}
}
