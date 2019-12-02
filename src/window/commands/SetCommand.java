package window.commands;

import data.Location;
import data.layer.Layer;
import data.layer.TileLayer;

public class SetCommand implements Command {

	private Layer layer;
	private String prevTexture, nextTexture;
	private Location pos;
	private boolean drag;

	public SetCommand(Layer layer, String texture, Location position, boolean drag) {
		this.layer = layer;
		if(layer instanceof TileLayer) {
			prevTexture = ((TileLayer) layer).getTileNames()[(int)position.x][(int)position.y];
		}
		this.nextTexture = texture;
		this.pos = position;
		this.drag = drag;
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
		}
	}
}
