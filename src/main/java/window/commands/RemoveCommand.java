package window.commands;

import data.Location;
import data.layer.Layer;
import data.layer.layerobjects.TagObject;
import window.elements.Modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RemoveCommand implements Command {

	private final Modifier mod;
	private final Layer layer;
	private final List<Location> positions;
	private final List<TagObject> toDeletes;

	public RemoveCommand(Modifier mod, Layer layer, Location position) {
		this.mod = mod;
		this.layer = layer;

		this.positions = new ArrayList<>();

		this.toDeletes = new ArrayList<>();

		Optional<TagObject> toDelete = layer.remove(position.x, position.y);
		if(toDelete.isPresent()) {
			this.positions.add(position);
			toDeletes.add(toDelete.get());
			mod.setTagObject(null);
		}
	}

	@Override
	public void execute(CommandHistory commandHistory) {

	}

	public void add(Location position) {
		Optional<TagObject> toDelete = layer.remove(position.x, position.y);
		if(toDelete.isPresent()) {
			this.positions.add(position);
			toDeletes.add(toDelete.get());
			mod.setTagObject(null);
		}
	}

	@Override
	public void undo() {
		for (TagObject toDelete : toDeletes) {
			layer.add(toDelete);
		}
	}

	@Override
	public void redo() {
		for (Location position : positions) {
			layer.remove(position.x, position.y);
		}
		mod.setTagObject(null);
	}

	@Override
	public boolean hasDifferentEffectThanLastCommand(Command lastCommand) {
		return !equals(lastCommand) && !positions.isEmpty();
	}

	@Override
	public boolean equals(Object o2) {
		return false;
	}
}
