package window.commands;

import data.Location;
import data.layer.Layer;
import data.layer.TileLayer;

public class SetCommand implements Command {

	private Layer layer;
	private String prevTexture, nextTexture;
	private Location pos;
	private boolean drag;
	private int autoTile;

	public SetCommand(Layer layer, String texture, Location position, boolean drag, int autoTile) {
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
	public void execute(History history) {
		if(layer instanceof TileLayer) {

			//Normal compare
			if(prevTexture == null || (!(autoTile > 0 && prevTexture.split("_")[1].equals("block") && nextTexture.split("_")[1].equals("block"))
					&& nextTexture!= null && (!prevTexture.equals(nextTexture)))) {

				redo();
				history.addCommand(this);

				return;
			}

			//Autotile compare
			else if(autoTile > 0) {
				if(prevTexture != null && nextTexture != null) {
					String[] prevParts = prevTexture.split("_");
					String[] nextParts = nextTexture.split("_");

					if(prevParts[1].equals("block") && nextParts[1].equals("block") && prevParts.length == nextParts.length) {
						for(int i = 2; i < nextParts.length-1; i++) {
							if (!prevParts[i].equals(nextParts[i])) {
								redo();
								history.addCommand(this);

								return;
							}
						}
					}
				}
			}
		} else {
			redo();
			history.addCommand(this);
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
