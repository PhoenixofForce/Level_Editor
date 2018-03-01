import window.Window;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		/*	TODO:
			More Important:
			-settings (?)
			-STRG+Z
			-Improved Save-Directory

			Less Important
			-view used ressources (?)
			-copy paste
			-selection => much later (maybe)
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
