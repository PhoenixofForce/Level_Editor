package window.elements.layer;

import window.UserInputs;
import window.Window;

import javax.swing.*;

public class LayerControl extends JToolBar {

	private JButton add, remove, options;

	/*	TODO:
		-turn layers on and off


	 */

	public LayerControl(Window window, LayerPane layerPane) {
		add = new JButton("+");
		remove = new JButton("-");
		//options = new JButton("Options");

		this.add(add);
		this.add(remove);
		//this.add(options);
		this.setFloatable(false);
		this.setRollover(true);

		this.add.addActionListener(e-> {
			UserInputs.createLayer(window, layerPane);
		});
		this.remove.addActionListener(e -> layerPane.removeLayer());
	}

	@Override
	public void enable() {
		add.setEnabled(true);
		remove.setEnabled(true);
	}

	@Override
	public void disable() {
		add.setEnabled(false);
		remove.setEnabled(false);
	}


}
