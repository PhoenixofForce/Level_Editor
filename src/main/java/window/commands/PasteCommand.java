package window.commands;

import data.Location;
import data.layer.FreeLayer;
import util.ClipBoardUtil;
import window.elements.MapViewer;
import window.Selection;

import java.awt.*;

public class PasteCommand implements Command {

	private final MapViewer mv;
	private final FreeLayer copyLayer;
	private final Location l;
	private SelectionChangeCommand smc;
	private final Selection selection;
	private final int tileSize;

	public PasteCommand(MapViewer mv, FreeLayer copyLayer, Location screenMiddle, Selection selection, int tileSize) {
		this.mv = mv;
		this.copyLayer = copyLayer;
		this.l = screenMiddle;
		this.selection = selection;
		this.tileSize = tileSize;
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		String in = ClipBoardUtil.ClipToString();
		String[] lines = in.split("\n");
		int lineC = lines.length;
		int textureC = 0;
		//TODO: Warning when textures not existing or invalid copy

		if(lineC == 0) return;
		for(int x = 0; x < lineC; x++) {
			String[] textures = in.split("\n")[x].split(" ");
			textureC = textures.length;
			for(int y = 0; y < textureC; y++) {
				String texture = textures[y];
				if(!texture.equalsIgnoreCase("[x]")) copyLayer.set(texture, x+l.x, y+l.y, false);
			}
		}

		copyLayer.roundAll(tileSize);

		Selection newSelection = new Selection();
		newSelection.add(new Rectangle(Math.round(l.x* tileSize), Math.round(l.y* tileSize), lineC*tileSize, textureC *tileSize));
		newSelection.roundPosition(tileSize);

		smc = new SelectionChangeCommand(mv, selection, newSelection);
		smc.redo();

		commandHistory.addCommand(this);
	}

	@Override
	public void redo() {
		mv.setCopyLayer(copyLayer);
		smc.redo();
	}

	@Override
	public void undo() {
		mv.setCopyLayer(null);
		smc.undo();
	}

	@Override
	public boolean hasDifferentEffectThanLastCommand(Command lastCommand) {
		return true;
	}
}
