import data.maps.GameMap;
import data.maps.IsoGameMap;
import data.maps.SquareGameMap;
import window.Window;
import java.io.*;

public class Main {

	public static void main(String[] args) {
		/*

			TODO: High Importance:
			- CTRl + LeftClick => copy Texture (color picker)
			- Selection => delete selected tiles
			- move selection on tile/area layers as well

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

		Window window = new Window(new IsoGameMap(20,20,16, 9));

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
