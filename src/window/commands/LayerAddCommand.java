package window.commands;

import data.GameMap;
import data.layer.Layer;
import window.elements.layer.LayerPane;

public class LayerAddCommand implements Command {

	private final LayerPane layerPane;
	private final GameMap map;
	private final String name;
	private final Layer layer;

	public LayerAddCommand(LayerPane layerPane, GameMap map, Layer layer, String name) {
		this.layerPane = layerPane;
		this.map = map;
		this.layer = layer;
		this.name = name;
	}

	@Override
	public void execute(CommandHistory commandHistory) {
		redo();
		commandHistory.addCommand(this);
	}

	@Override
	public void redo() {
		map.addLayer(name, layer);
		layerPane.addLayer(name, layer);
	}

	@Override
	public void undo() {
		map.removeLayer(name);
		layerPane.removeLayer(name);
	}

	@Override
	public boolean isWorthy(Command lastCommand) {
		return true;
	}
}
