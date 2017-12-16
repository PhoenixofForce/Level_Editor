package window.elements;

import data.GO;

import javax.swing.*;
import java.awt.*;

public class Modifier extends JPanel{

	private GO object;
	private JLabel goStats;
	private JButton add, remove;

	private JTextField input;
	private JComboBox attChooser;

	public Modifier() {

	}

	public void setGO(GO obj) {
		add.setEnabled(obj != null);
		remove.setEnabled(obj != null);
		input.setEnabled(obj != null);
		attChooser.setEnabled(obj != null);
		if(obj == null) return;

		this.object = obj;
		goStats.setText(obj.name + " (" + obj.x + " | " + obj.y);
	}


}
