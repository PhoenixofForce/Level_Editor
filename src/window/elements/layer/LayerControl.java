package window.elements.layer;

import window.modals.UserInputs;
import window.Window;

import javax.swing.*;

/**
 * jcomponent to add and remove layers
 */
public class LayerControl extends JToolBar {

	private JButton add, remove, toggleHidden, options;

	public LayerControl(Window window, LayerPane layerPane) {
		add = new JButton("+");
		remove = new JButton("-");
		toggleHidden = new JButton("*");
		//options = new JButton("Options");

		this.add(add);
		this.add(remove);
		this.add(toggleHidden);
		//this.add(options);
		this.setFloatable(false);
		this.setRollover(true);

		this.add.addActionListener(e-> {
			UserInputs.createLayer(window, layerPane);
		});
		this.remove.addActionListener(e -> {
			UserInputs.confirm(window, "Do you really want to delete this layer?", e2 -> layerPane.removeLayer());
		});

		this.toggleHidden.addActionListener(e -> layerPane.toggleHidden());
	}

	@Override
	public void enable() {
		add.setEnabled(true);
		remove.setEnabled(true);
		toggleHidden.setEnabled(true);
	}

	@Override
	public void disable() {
		add.setEnabled(false);
		remove.setEnabled(false);
		toggleHidden.setEnabled(false);
	}


}
