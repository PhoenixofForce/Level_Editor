package window.commands;

import data.Location;
import data.layer.FreeLayer;
import window.elements.Selection;

import java.util.ArrayList;
import java.util.List;

public class SelectionMoveCommand implements Command{

	private final int tileSize;
	private final boolean isRight;
	private final FreeLayer copyLayer;
	private final Selection toMove;
	private final List<Location> distances;

	public SelectionMoveCommand(int tileSize, Selection toMove, FreeLayer copyLayer, boolean isRight) {
		this.tileSize = tileSize;
		this.toMove = toMove;

		this.copyLayer = copyLayer;
		this.isRight = isRight;

		this.distances = new ArrayList<>();
	}

	public SelectionMoveCommand(int tileSize, Selection toMove, Location from, Location to, FreeLayer copyLayer, boolean isRight) {
		this.tileSize = tileSize;
		this.toMove = toMove;

		this.distances = new ArrayList<>();
		this.copyLayer = copyLayer;
		this.isRight = isRight;

		int dx = Math.round((float)tileSize * (to.x-from.x));
		int dy = Math.round((float)tileSize*(to.y-from.y));
		this.distances.add(new Location(dx, dy));
		toMove.translate(dx, dy);

		if(copyLayer != null && isRight) copyLayer.moveAll(to.x-from.x, to.y-from.y);
	}

	public void add(Location from, Location to) {
		int dx = Math.round((float)tileSize * (to.x-from.x));
		int dy = Math.round((float)tileSize*(to.y-from.y));
		this.distances.add(new Location(dx, dy));
		toMove.translate(dx, dy);

		if(copyLayer != null && isRight) copyLayer.moveAll(to.x-from.x, to.y-from.y);
	}

	public void round() {
		this.distances.add(toMove.roundPosition(tileSize));
	}

	@Override
	public void execute(CommandHistory commandHistory) {
	}

	@Override
	public void redo() {
		for (Location distance : distances) {
			toMove.translate((int) distance.x, (int) distance.y);
			if (copyLayer != null && isRight)
				copyLayer.moveAll(distance.x / (float) tileSize, distance.y / (float) tileSize);
		}
	}

	@Override
	public void undo() {
		for(int i = distances.size() - 1; i >= 0; i--) {
			toMove.translate(-(int) distances.get(i).x, -(int)distances.get(i).y);
			if(copyLayer != null && isRight) copyLayer.moveAll(-distances.get(i).x/ (float) tileSize, -distances.get(i).y/ (float) tileSize);
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
