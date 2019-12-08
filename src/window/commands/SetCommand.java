package window.commands;

import data.Location;
import data.Util;
import data.layer.Layer;
import data.layer.TileLayer;
import window.elements.Modifier;

import java.util.ArrayList;
import java.util.List;

public class SetCommand implements Command {

	private Modifier mod;
	private Layer layer;
	private List<String> prevTextures, nextTextures;
	private List<Location> positions;
	private boolean drag;
	private int autoTile;

	public SetCommand(Modifier mod, Layer layer, String texture, Location position, boolean drag, int autoTile) {
		this.mod = mod;
		this.layer = layer;

		prevTextures = new ArrayList<>();
		nextTextures = new ArrayList<>();
		positions = new ArrayList<>();

		if(layer instanceof TileLayer) {
			if(position.x >= 0 && position.x < ((TileLayer) layer).getTileNames()[0].length &&
					position.y >= 0 && position.y < ((TileLayer) layer).getTileNames().length) {

				String prevTexture = ((TileLayer) layer).getTileNames()[(int) position.y][(int) position.x];
				if(!Util.textureEquals(autoTile, prevTexture, texture)) {
					nextTextures.add(texture);
					positions.add(position);
					prevTextures.add(prevTexture);

					layer.set(texture, position.x, position.y, drag);
				}
			}

		} else {
			nextTextures.add(texture);
			positions.add(position);

			layer.set(texture, position.x, position.y, drag);
		}

		this.drag = drag;
		this.autoTile = autoTile;
	}

	public void add(Location position, String texture) {
		if(layer instanceof TileLayer) {
			if(position.x >= 0 && position.x < ((TileLayer) layer).getTileNames()[0].length &&
					position.y >= 0 && position.y < ((TileLayer) layer).getTileNames().length) {

				String prevTexture = ((TileLayer) layer).getTileNames()[(int) position.y][(int) position.x];
				if(!Util.textureEquals(autoTile, prevTexture, texture)) {
					nextTextures.add(texture);
					positions.add(position);
					prevTextures.add(prevTexture);

					layer.set(texture, position.x, position.y, drag);
				}
			}

		} else {
			nextTextures.add(texture);
			positions.add(position);

			layer.set(texture, position.x, position.y, drag);
		}
	}

	@Override
	public void execute(CommandHistory commandHistory) {

	}

	@Override
	public void redo() {
		for(int i = 0; i < nextTextures.size(); i++) {
			layer.set(nextTextures.get(i), positions.get(i).x, positions.get(i).y, drag);
		}
	}

	@Override
	public void undo() {
		if(layer instanceof TileLayer && prevTextures.size() == positions.size()) {
			for(int i = 0; i < prevTextures.size(); i++) {
				layer.set(prevTextures.get(i), positions.get(i).x, positions.get(i).y, drag);
			}
		} else {
			for(int i = 0; i < positions.size(); i++) {
				layer.remove(positions.get(i).x, positions.get(i).y);
			}
			mod.setTagObject(null);
		}
	}

	@Override
	public boolean isWorthy(Command lastCommand) {
		return !equals(lastCommand) && positions.size() > 0;
	}

	@Override
	public boolean equals(Object o2) {
		return false;
	}
}
