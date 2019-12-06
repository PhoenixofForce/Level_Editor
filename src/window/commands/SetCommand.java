package window.commands;

import data.Location;
import data.Util;
import data.layer.Layer;
import data.layer.TileLayer;
import window.elements.Modifier;

public class SetCommand implements Command {

	private Modifier mod;
	private Layer layer;
	private String prevTexture, nextTexture;
	private Location pos;
	private boolean drag;
	private int autoTile;

	public SetCommand(Modifier mod, Layer layer, String texture, Location position, boolean drag, int autoTile) {
		this.mod = mod;
		this.layer = layer;
		if(layer instanceof TileLayer) {
			prevTexture = ((TileLayer) layer).getTileNames()[(int)position.y][(int)position.x];
		}
		this.nextTexture = texture;
		this.pos = position;
		this.drag = drag;
		this.autoTile = autoTile;
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		redo();
		if(layer instanceof TileLayer) {
			if(!Util.textureEquals(autoTile, prevTexture, nextTexture)) {
				commandHistory.addCommand(this);
			} 
		} else {
			commandHistory.addCommand(this);
		}
	}

	@Override
	public void redo() {
		layer.set(nextTexture, pos.x, pos.y, drag);
	}

	@Override
	public void undo() {
		if(layer instanceof TileLayer && prevTexture != null) {
			layer.set(prevTexture, pos.x, pos.y, drag);
		} else {
			layer.remove(pos.x, pos.y);
			mod.setTagObject(null);
		}
	}

	@Override
	public boolean equals(Object o2) {
		if(o2 instanceof SetCommand) {
			SetCommand sc = (SetCommand) o2;
			return  sc.layer.equals(layer) &&
					/*((sc.prevTexture == null && prevTexture == null) ||
							(sc.prevTexture != null && prevTexture != null && sc.prevTexture.equals(prevTexture))) &&*/
					sc.nextTexture.equalsIgnoreCase(nextTexture) &&
					sc.drag == drag &&
					(layer instanceof TileLayer) && Math.floor(sc.pos.x) == Math.floor(pos.x)&& Math.floor(sc.pos.y) == Math.floor(pos.y);
		}
		return false;
	}
}
