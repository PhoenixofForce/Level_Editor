import data.maps.IsoGameMap;
import data.maps.SquareGameMap;
import window.Window;
import java.io.*;

public class Main {

	public static void main(String[] args) {
		/*
			TODO: Working on Isometric Levels
			- [x] Fix the shape of the tile highlighter
			- [ ] find a way for Selections to work
			- [ ] Check(and fix) other tools for functionality
			- [ ] Check what does not work as intended because of tileSize split
			- [ ] Fix PNG-Exporter
			- [ ] Fix TileLayer out of window check in draw()
			- [ ] Check bounds (maxX, minX, ...) calculation
			- [ ] write tileHeight, and map-type to uMap

			TODO: High Importance:
			- CTRl + LeftClick => copy Texture (color picker)
			- Selection => delete selected tiles
			- move selection on tile/area layers as well
			- recolor tile selector, when there already is a tile on the current position
			- fill tool does not work correctly

			TODO: Normal Importance
			- Refactor window.modal
			- Improved Save-Directory
				* extract saving, opening, exporting, ... mechanisms from MenuBar class
			- change layer settings
				* name, z-val, (type?)
			- view and manage imported sprite sheets
			- settings (?)
				* change color of tools
				* change key bindings

			TODO: Nice to Have:
			- presets
				* set default preset that is loaded here
				* select from preset maps when creating new map
			- open/view last edited files
		 */

		Window window = new Window(new IsoGameMap(30, 30, 16, 9));

		if (args.length > 0) {
			try {
				window.open(new File(args[0]));
			} catch (Exception e) {
				System.err.println("Could not open file " + args[0]);
			}
		} else {
			//Load Preset

			window.getMap().addTileLayer("Background", 1.0f)
					.addTileLayer("Tiles", 0.5f)
					.addFreeLayer("Objects", 0)
					.addAreaLayer("Camera", -1f);

			window.updateMap();
		}
	}
}
