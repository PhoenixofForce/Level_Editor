import window.Window;
import java.io.*;

public class Main {

	public static void main(String[] args) {
		/*	TODO:
			More Important:
			-selection => delete....

			-Improved Save-Directory
			-presets
			-change layer settings
			-open last/ view last edited files
			-view used resources (delete loaded sheets, reload, change path)
			-application settings (button remapping, colors, ...)
		 */

		Window window = new Window();

		if (args.length > 0) {
			try {
				window.open(new File(args[0]));
			} catch (Exception e) {
				System.err.println("Could not open file " + args[0]);
			}
		} else {
			//Load Preset
		}
	}
}
