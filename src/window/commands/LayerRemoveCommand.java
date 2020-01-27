package window.commands;

import data.GameMap;
import data.layer.Layer;
import window.elements.layer.LayerPane;

public class LayerRemoveCommand implements Command {

	private LayerPane layerPane;
	private GameMap map;
	private String name;
	private Layer layer;

	public LayerRemoveCommand(LayerPane layerPane, GameMap map, String name) {
		this.layerPane = layerPane;
		this.map = map;
		this.name = name;
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		this.layer = map.removeLayer(name);
		layerPane.removeLayer(name);
		commandHistory.addCommand(this);
	}

	@Override
	public void redo() {
		map.removeLayer(name);
		layerPane.removeLayer(name);
	}

	@Override
	public void undo() {
		map.addLayer(name, layer);
		layerPane.addLayer(name, layer);
	}

	@Override
	public boolean isWorthy(Command lastCommand) {
		return true;
	}
}
