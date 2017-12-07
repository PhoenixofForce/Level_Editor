package window;

import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserInputs {

	public static void createLayer(Window w, LayerPane lp) {
		w.setEnabled(false);

		JFrame frame = new JFrame("Adding layer");
		JTextField nameInput = new JTextField("name");
		JSlider layerType = new JSlider(JSlider.HORIZONTAL, 0, 1, 1);
		JLabel layerText = new JLabel("Tile - Pixel");
		JButton create = new JButton("Create");
		JTextField depthInput = new JTextField("");
		depthInput.setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {
				JTextField textField = ((JTextField) input);
				try {

					Float isFloat = Float.parseFloat(textField.getText());
					textField.setBackground(Color.WHITE);
					return true;
				} catch (NumberFormatException e) {
					textField.setBackground(new Color(255, 150, 150));
					return false;
				}

			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				w.setEnabled(true);
				w.toFront();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				frame.dispose();
				w.setEnabled(true);
				w.toFront();
			}
		});

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setVisible(true);
		Insets i = frame.getInsets();
		frame.setSize(250 + i.left + i.right, 150 + i.top + i.bottom);
		frame.setAlwaysOnTop(true);

		frame.add(nameInput);
		nameInput.setBounds(5, 5, 100, 25);

		frame.add(layerType);
		layerType.setBounds(5, 35, 100, 25);
		layerType.setMinimum(0);
		layerType.setMaximum(1);
		layerType.setMajorTickSpacing(1);

		frame.add(layerText);
		layerText.setBounds(5, 60, 100, 25);

		frame.add(depthInput);
		depthInput.setBounds(5, 90, 100, 25);

		frame.add(create);
		create.setBounds(5, 120, 100, 25);
		create.addActionListener(e -> {
			String name = nameInput.getText();
			lp.add(name, layerType.getValue() == 1, Float.parseFloat(depthInput.getText()));
			frame.dispose();
			w.setEnabled(true);
			w.toFront();
		});
	}

}
