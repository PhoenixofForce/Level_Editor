import javax.swing.*;

public class Buttons extends JToolBar{

	private JButton newMap, saveMap;

	public Buttons() {
		this.setFloatable(false);
		this.setRollover(true);

		newMap = new JButton("New");
		this.add(newMap);
		saveMap = new JButton("Save");
		this.add(saveMap);
	}

}
