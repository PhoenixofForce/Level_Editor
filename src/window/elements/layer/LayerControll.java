package window.elements.layer;

import window.UserInputs;
import window.Window;

import javax.swing.*;

public abstract class LayerControll extends JToolBar {

	private JButton add, remove, options;

	/*	TODO:
		-turn layers on and off


	 */

	public LayerControll(Window w, LayerPane lp) {
		add = new JButton("+");
		remove = new JButton("-");
		//options = new JButton("Options");

		this.add(add);
		this.add(remove);
		//this.add(options);
		this.setFloatable(false);
		this.setRollover(true);

		this.add.addActionListener(e-> {
			UserInputs.createLayer(w, lp);
		});
		this.remove.addActionListener(e -> onRemove());
	}

	public void onRemove() {

	}

}
