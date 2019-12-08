package window.commands;

import data.Location;
import data.layer.Layer;
import data.layer.layerobjects.TagObject;
import window.elements.Modifier;

import java.util.ArrayList;
import java.util.List;

public class RemoveCommand implements Command {

	private Modifier mod;
	private Layer layer;
	private List<Location> positions;
	private List<TagObject> toDeletes;

	public RemoveCommand(Modifier mod, Layer layer, Location position) {
		this.mod = mod;
		this.layer = layer;

		this.positions = new ArrayList<>();

		this.toDeletes = new ArrayList<>();

		TagObject toDelete = layer.remove(position.x, position.y);
		if(toDelete != null) {
			this.positions.add(position);
			toDeletes.add(toDelete);
			mod.setTagObject(null);
		}
	}

	@Override
	public void execute(CommandHistory commandHistory) {

	}

	public void add(Location position) {
		TagObject toDelete = layer.remove(position.x, position.y);
		if(toDelete != null) {
			this.positions.add(position);
			toDeletes.add(toDelete);
			mod.setTagObject(null);
		}
	}

	@Override
	public void undo() {
		for(int i = 0; i < toDeletes.size(); i++) {
			layer.add(toDeletes.get(i));
		}
	}

	@Override
	public boolean isWorthy(Command lastCommand) {
		return !equals(lastCommand) && positions.size() > 0;
	}

	@Override
	public void redo() {
		for(int i = 0; i < positions.size(); i++) {
			layer.remove(positions.get(i).x, positions.get(i).y);
		}
		mod.setTagObject(null);
	}

	@Override
	public boolean equals(Object o2) {
		return false;
	}
}
