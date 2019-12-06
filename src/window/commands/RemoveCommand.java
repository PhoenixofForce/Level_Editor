package window.commands;

import data.Location;
import data.layer.Layer;
import data.layer.TileLayer;
import data.layer.layerobjects.TagObject;
import window.elements.Modifier;

public class RemoveCommand implements Command {

	private Modifier mod;
	private Layer layer;
	private Location pos;
	
	private TagObject toDelete;

	public RemoveCommand(Modifier mod, Layer layer, Location position) {
		this.mod = mod;
		this.layer = layer;
		this.pos = position;
	}

	@Override
	public void execute(CommandHistory commandHistory) {

		toDelete = layer.remove(pos.x, pos.y);
		mod.setTagObject(null);
		if(toDelete != null) commandHistory.addCommand(this);
	}

	@Override
	public void undo() {
		layer.add(toDelete);
	}

	@Override
	public void redo() {
		layer.remove(pos.x, pos.y);
		mod.setTagObject(null);
	}

	@Override
	public boolean equals(Object o2) {
		if(o2 instanceof RemoveCommand) {
			RemoveCommand sc = (RemoveCommand) o2;
			return  sc.layer.equals(layer) &&
					/*((sc.prevTexture == null && prevTexture == null) ||
							(sc.prevTexture != null && prevTexture != null && sc.prevTexture.equals(prevTexture))) &&*/
					sc.toDelete.equals(toDelete) &&
					(layer instanceof TileLayer) && Math.floor(sc.pos.x) == Math.floor(pos.x)&& Math.floor(sc.pos.y) == Math.floor(pos.y);
		}
		return false;
	}
}
