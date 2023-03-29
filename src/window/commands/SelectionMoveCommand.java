package window.commands;

import data.Location;
import data.layer.FreeLayer;
import window.Selection;

import java.util.ArrayList;
import java.util.List;

public class SelectionMoveCommand implements Command{

	private final int tileWidth, tileHeight;
	private final boolean isRight;
	private final FreeLayer copyLayer;
	private final Selection toMove;
	private final List<Location> distances;

	public SelectionMoveCommand(int tileWidth, int tileHeight, Selection toMove, FreeLayer copyLayer, boolean isRight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.toMove = toMove;

		this.copyLayer = copyLayer;
		this.isRight = isRight;

		this.distances = new ArrayList<>();
	}

	public SelectionMoveCommand(int tileWidth, int tileHeight, Selection toMove, Location from, Location to, FreeLayer copyLayer, boolean isRight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.toMove = toMove;

		this.distances = new ArrayList<>();
		this.copyLayer = copyLayer;
		this.isRight = isRight;

		add(from, to);
	}

	public void add(Location from, Location to) {
		int dx = Math.round((float)tileWidth * (to.x-from.x));
		int dy = Math.round((float)tileHeight*(to.y-from.y));
		this.distances.add(new Location(dx, dy));
		toMove.translate(dx, dy);

		if(copyLayer != null && isRight) {
			copyLayer.moveAll(to.x-from.x, to.y-from.y);
		}
	}

	public void round() {
		this.distances.add(toMove.roundPosition(tileWidth, tileHeight));
	}

	@Override
	public void execute(CommandHistory commandHistory) {
	}

	@Override
	public void redo() {
		for (Location distance : distances) {
			toMove.translate((int) distance.x, (int) distance.y);
			if (copyLayer != null && isRight)
				copyLayer.moveAll(distance.x / (float) tileWidth, distance.y / (float) tileHeight);
		}
	}

	@Override
	public void undo() {
		for(int i = distances.size() - 1; i >= 0; i--) {
			toMove.translate(-(int) distances.get(i).x, -(int)distances.get(i).y);
			if(copyLayer != null && isRight) copyLayer.moveAll(-distances.get(i).x/ (float) tileWidth, -distances.get(i).y/ (float) tileHeight);
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
