package window.elements.layer;

import javax.swing.*;

public abstract class LayerControll extends JToolBar {

	private JButton add, remove, options;

	/*	TODO:
		-turn layers on and off


	 */

	public LayerControll() {
		add = new JButton("+");
		remove = new JButton("-");
		//options = new JButton("Options");

		this.add(add);
		this.add(remove);
		//this.add(options);
		this.setFloatable(false);
		this.setRollover(true);

		this.add.addActionListener(e-> {
			//TODO: Rework: one window opens
			String s = JOptionPane.showInputDialog(new JFrame(), "Name of the new Layer");
			Object[] options = new Object[]{"Tilebased", "Pixelbased"};
			int m = JOptionPane.showOptionDialog(new JFrame(), "Type of the new Layer", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			onAdd(s, m==1);
		});
		this.remove.addActionListener(e -> onRemove());
	}

	public void onAdd(String name, boolean type) {

	}

	public void onRemove() {

	}

}
