package window.elements.layer;

import javax.swing.*;

public class LayerControll extends JToolBar {

	public LayerControll() {
		this.add(new JButton("+"));
		this.add(new JButton("-"));
		this.setFloatable(false);
		this.setRollover(true);
	}

}
