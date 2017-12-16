package window;

import data.GameMap;
import window.elements.Modifier;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserInputs {

	public static void createLayer(Window window, LayerPane layerPane) {
		window.setEnabled(false);

		JFrame frame = new JFrame("Adding layer");
		JTextField nameInput = new JTextField("name");
		JSlider layerType = new JSlider(JSlider.HORIZONTAL, 0, 1, 1);
		JLabel layerText = new JLabel("Tile - Pixel");
		JButton create = new JButton("Create");
		JTextField depthInput = new JTextField("depth");
		depthInput.setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {
				JTextField textField = ((JTextField) input);
				try {

					Float.parseFloat(textField.getText());
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
				window.setEnabled(true);
				window.toFront();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				frame.dispose();
				window.setEnabled(true);
				window.toFront();
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
			layerPane.addLayer(name, layerType.getValue() == 1, Float.parseFloat(depthInput.getText()));
			frame.dispose();
			window.setEnabled(true);
			window.toFront();
		});
	}
	public static void tagName(Window window, Modifier mod) {
		window.setEnabled(false);

		JFrame frame = new JFrame("New...");
		JTextField nameIN = new JTextField("Name");
		JButton create = new JButton("Create");


		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				window.setEnabled(true);
				window.toFront();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				frame.dispose();
				window.setEnabled(true);
				window.toFront();
			}
		});

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setVisible(true);
		Insets i = frame.getInsets();
		frame.setSize(250 + i.left + i.right, 65 + i.top + i.bottom);
		frame.setAlwaysOnTop(true);

		frame.add(nameIN);
		nameIN.setBounds(5, 5, 100, 25);


		frame.add(create);
		create.setBounds(5, 35, 100, 25);
		create.addActionListener(e -> {
			mod.add(nameIN.getText());
			frame.dispose();
			window.setEnabled(true);
			window.toFront();
		});
	}

	public static void newMap(Window window) {
		window.setEnabled(false);

		JFrame frame = new JFrame("New...");
		JTextField tileSizeIn = new JTextField("Tile size");
		JButton create = new JButton("Create");
		JTextField mapWidthIn = new JTextField("Map width");
		JTextField mapHeightIn = new JTextField("Map height");

		InputVerifier inputVerifier = new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {
				JTextField textField = ((JTextField) input);
				try {

					Integer.parseInt(textField.getText());
					textField.setBackground(Color.WHITE);
					return true;
				} catch (NumberFormatException e) {
					textField.setBackground(new Color(255, 150, 150));
					return false;
				}

			}
		};
		mapWidthIn.setInputVerifier(inputVerifier);
		mapHeightIn.setInputVerifier(inputVerifier);
		tileSizeIn.setInputVerifier(inputVerifier);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				window.setEnabled(true);
				window.toFront();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				frame.dispose();
				window.setEnabled(true);
				window.toFront();
			}
		});

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setVisible(true);
		Insets i = frame.getInsets();
		frame.setSize(250 + i.left + i.right, 125 + i.top + i.bottom);
		frame.setAlwaysOnTop(true);

		frame.add(tileSizeIn);
		tileSizeIn.setBounds(5, 5, 100, 25);

		frame.add(mapWidthIn);
		mapWidthIn.setBounds(5, 35, 100, 25);

		frame.add(mapHeightIn);
		mapHeightIn.setBounds(5, 65, 100, 25);

		frame.add(create);
		create.setBounds(5, 95, 100, 25);
		create.addActionListener(e -> {
			window.setMap(new GameMap(Integer.parseInt(mapWidthIn.getText()), Integer.valueOf(mapHeightIn.getText()), Integer.parseInt(tileSizeIn.getText())));

			frame.dispose();
			window.setEnabled(true);
			window.toFront();
		});
	}


}
