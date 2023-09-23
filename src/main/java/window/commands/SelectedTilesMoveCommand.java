package window.commands;

import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.TagObject;
import window.elements.MapViewer;
import window.Selection;

import java.util.ArrayList;
import java.util.List;

public class SelectedTilesMoveCommand implements Command{

	private final TileLayer layer;
	private final FreeLayer copyLayer;
	private final List<TagObject> resetTextures;
	private final List<int[]> resetPositions;

	private final MapViewer mv;

	public SelectedTilesMoveCommand(MapViewer mv, TileLayer layer, Selection selection, int tileSize) {
		this.layer = layer;
		this.copyLayer = new FreeLayer(layer.depth(), layer.getTileNames()[0].length, layer.getTileNames().length, tileSize);
		this.mv = mv;

		resetPositions = new ArrayList<>();
		resetTextures = new ArrayList<>();

		for(int x = 0;  x < layer.getTileNames()[0].length; x++) {
			for(int y = 0;  y < layer.getTileNames().length; y++) {
				if(selection.getArea().contains(x*tileSize, y*tileSize)) {
					if(layer.getTileNames()[y][x] != null) {
						copyLayer.set(layer.getTileNames()[y][x], x, y, false);
						resetPositions.add(new int[]{x, y});
					}
				}
			}
		}

		redo();
	}

	@Override
	public void execute(CommandHistory commandHistory) {
	}

	@Override
	public void redo() {
		for(int[] i: resetPositions) resetTextures.add(layer.remove(i[0], i[1]));
		mv.setCopyLayer(copyLayer);
	}

	@Override
	public void undo() {
		for (TagObject resetTexture : resetTextures) layer.add(resetTexture);
		mv.setCopyLayer(null);

	}

	@Override
	public boolean hasDifferentEffectThanLastCommand(Command lastCommand) {
		return true;
	}
}
