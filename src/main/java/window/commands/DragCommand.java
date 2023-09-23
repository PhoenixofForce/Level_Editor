package window.commands;

import java.util.ArrayList;
import java.util.List;

import data.Location;
import data.layer.Layer;

public class DragCommand implements Command {

	private final Layer layer;
	private final List<Location> from;
    private final List<Location> to;

	public DragCommand( Layer layer, Location from, Location to) {
		this.layer = layer;
		this.from = new ArrayList<>();
		this.from.add(from);
		this.to = new ArrayList<>();
		this.to.add(to);
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		redo();
		commandHistory.addCommand(this);
	}
	
	public void setTo(Location newFrom, Location newTo) {
		this.from.add(newFrom);
		this.to.add(newTo);
	}

	@Override
	public void redo() {
		layer.select(from.get(0).x, from.get(0).y);
		for(int i = 0; i < from.size(); i++) {
			layer.drag(from.get(i).x, from.get(i).y, to.get(i).x, to.get(i).y);
		}
	}

	@Override
	public void undo() {
		layer.select(from.get(from.size()-1).x, from.get(from.size()-1).y);
		for(int i = from.size()-1; i >= 0; i--) {
			layer.drag(to.get(i).x, to.get(i).y, from.get(i).x, from.get(i).y);
		}
	}

	@Override
	public boolean hasDifferentEffectThanLastCommand(Command lastCommand) {
		return !equals(lastCommand);
	}

	@Override
	public boolean equals(Object o2) {
		if(o2 instanceof DragCommand) {
			return  false/*fc.layer.equals(layer) &&
					fc.from.equals(from) && 
					fc.to.equals(to)*/;
		}
		return false;
	}
}
