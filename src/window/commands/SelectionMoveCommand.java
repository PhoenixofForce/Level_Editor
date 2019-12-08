package window.commands;

import data.Location;
import window.elements.Selection;

import java.util.ArrayList;
import java.util.List;

public class SelectionMoveCommand implements Command{

	private int tileSize;
	private Selection toMove;
	private List<Location> distances;

	public SelectionMoveCommand(int tileSize, Selection toMove) {
		this.tileSize = tileSize;
		this.toMove = toMove;

		this.distances = new ArrayList<>();
	}

	public SelectionMoveCommand(int tileSize, Selection toMove, Location from, Location to) {
		this.tileSize = tileSize;
		this.toMove = toMove;

		this.distances = new ArrayList<>();

		int dx = Math.round((float)tileSize * (to.x-from.x));
		int dy = Math.round((float)tileSize*(to.y-from.y));
		this.distances.add(new Location(dx, dy));
		toMove.translate(dx, dy);
	}

	public void add(Location from, Location to) {
		int dx = Math.round((float)tileSize * (to.x-from.x));
		int dy = Math.round((float)tileSize*(to.y-from.y));
		this.distances.add(new Location(dx, dy));
		toMove.translate(dx, dy);
	}

	public void round() {
		this.distances.add(toMove.roundPosition(tileSize));
	}

	@Override
	public void execute(CommandHistory commandHistory) {
	}

	@Override
	public void redo() {
		for(int i = 0; i < distances.size(); i++) {
			toMove.translate((int) distances.get(i).x, (int) distances.get(i).y);
		}
	}

	@Override
	public void undo() {
		for(int i = distances.size() - 1; i >= 0; i--) {
			toMove.translate(-(int) distances.get(i).x, -(int)distances.get(i).y);
		}
	}

	@Override
	public  boolean equals(Object o2) {
		return false;
	}

	@Override
	public boolean isWorthy(Command lastCommand) {
		return !equals(lastCommand);
	}
}
