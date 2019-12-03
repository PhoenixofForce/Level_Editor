package window.commands;

import data.Location;
import data.Util;
import data.layer.TileLayer;

import java.awt.geom.Area;
import java.util.List;

public class FillCommand implements Command {

	private TileLayer layer;
	private String prevTexture, nextTexture;
	private Location pos;
	private Area area;
	private int autoTile;

	private List<Location> changes;

	public FillCommand(TileLayer layer, String texture, Location position, Area area, int autoTile) {
		this.layer = layer;
		this.prevTexture = layer.getTileNames()[(int)position.y][(int)position.x];
		this.area = area;
		this.nextTexture = texture;
		this.pos = position;
		this.autoTile = autoTile;
	}

	@Override
	public void execute(CommandHistory commandHistory) {

		changes = layer.fill(area, nextTexture, pos.x, pos.y);
		if(!Util.textureEquals(autoTile, prevTexture, nextTexture)) {

			commandHistory.addCommand(this);
		}

	}

	@Override
	public void redo() {
		for(Location i: changes) {
			layer.set(nextTexture, i.x, i.y, false);
		}
	}

	@Override
	public void undo() {
		for(Location i: changes) {
			layer.set(prevTexture, i.x, i.y, false);
		}
	}

	@Override
	public boolean equals(Object o2) {
		if(o2 instanceof FillCommand) {
			FillCommand fc = (FillCommand) o2;
			return  fc.layer.equals(layer) &&
					((fc.prevTexture == null && prevTexture == null) ||
							(fc.prevTexture != null && prevTexture != null && fc.prevTexture.equals(prevTexture))) &&
					(fc.nextTexture == null && nextTexture == null || fc.nextTexture.equalsIgnoreCase(nextTexture)) &&
					fc.area == area &&
					Math.floor(fc.pos.x) == Math.floor(pos.x)&& Math.floor(fc.pos.y) == Math.floor(pos.y);
		}
		return false;
	}
}
