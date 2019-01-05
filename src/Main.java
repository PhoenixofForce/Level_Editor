import window.Window;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		/*	TODO:
			More Important:
			-STRG+Z
			-Improved Save-Directory
			-presets
			-change layer settings
			-open last/ view last edited files

			Less Important
			-view used ressources (?)
			-copy paste
			-selection => copy, delete....
			-settings (?)
		 */

		Window window = new Window();

		if (args.length > 0) {
			try {
				window.open(new File(args[0]));
			} catch (Exception e) {
				System.err.println("Could not open file " + args[0]);
			}
		}
	}
}
