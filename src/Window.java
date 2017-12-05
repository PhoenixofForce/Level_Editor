import javax.swing.*;
import java.awt.*;

public class Window extends JFrame{

	private LayerPane layers;
	private Buttons buttons;
	private MapViewer mapViewer;

	public Window() {
		this.setTitle("Level Editor");
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);

		layers = new LayerPane();
		this.add(layers, BorderLayout.LINE_START);
		buttons = new Buttons();
		this.add(buttons, BorderLayout.PAGE_START);
		mapViewer = new MapViewer();
		this.add(mapViewer, BorderLayout.CENTER);
	}

}
