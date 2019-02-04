package window;

import data.GameMap;
import window.elements.Modifier;
import window.elements.layer.LayerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * used to allow the user to make better inputs
 */
public class UserInputs {

	/** Creates window so the user can set the settings of the new layer
	*   @param window to handle focus
	*   @param layerPane to add layer 
	**/
	public static void createLayer(Window window, LayerPane layerPane) {
		window.setEnabled(false);

		JFrame frame = new JFrame("Adding layer");
		JTextField nameInput = new JTextField("name");
		JComboBox<String> layerType = new JComboBox<>();
		layerType.addItem("Tile");
		layerType.addItem("Area");
		layerType.addItem("Pixel");

		JButton create = new JButton("Create");
		JTextField depthInput = new JTextField("depth");

		addSelectOnClick(nameInput);
		addSelectOnClick(depthInput);

		//allow float only
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

		//resets focuc when closing
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
				window.toFront();
				frame.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				window.setEnabled(true);
				window.toFront();
				frame.dispose();
			}
		});

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setVisible(true);
		Insets i = frame.getInsets();
		frame.setSize(250 + i.left + i.right, 135 + i.top + i.bottom);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(window);

		frame.add(nameInput);
		nameInput.setBounds(5, 5, 100, 25);

		frame.add(layerType);
		layerType.setBounds(5, 35, 100, 25);

		frame.add(depthInput);
		depthInput.setBounds(5, 65, 100, 25);

		frame.add(create);
		create.setBounds(5, 105, 100, 25);

		ActionListener a = e->{
			String name = nameInput.getText();
			layerPane.addLayer(name, layerType.getSelectedIndex(), Float.parseFloat(depthInput.getText()));
			window.setEnabled(true);
			window.toFront();
			frame.dispose();
		};

		create.addActionListener(a);
		depthInput.addActionListener(a);
	}
	
	/** Creates window so the user can set the settings of the new tag
	*   @param window to handle focus
	*   @param mod to add modifier 
	**/
	public static void tagName(Window window, Modifier mod) {
		window.setEnabled(false);

		JFrame frame = new JFrame("New...");
		JTextField nameIN = new JTextField("Name");
		JButton create = new JButton("Create");

		addSelectOnClick(nameIN);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
				window.toFront();
				frame.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				window.setEnabled(true);
				window.toFront();
				frame.dispose();
			}
		});

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setVisible(true);
		Insets i = frame.getInsets();
		frame.setSize(250 + i.left + i.right, 65 + i.top + i.bottom);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(window);

		frame.add(nameIN);
		nameIN.setBounds(5, 5, 100, 25);


		frame.add(create);
		create.setBounds(5, 35, 100, 25);

		ActionListener a = e->{
			mod.add(nameIN.getText());
			window.setEnabled(true);
			window.toFront();
			frame.dispose();
		};

		create.addActionListener(a);
		nameIN.addActionListener(a);
	}

	/** Creates window so the user can set the settings of the new map
	*   @param window to handle focus
	**/
	public static void newMap(Window window) {
		window.setEnabled(false);

		JFrame frame = new JFrame("New...");
		JTextField tileSizeIn = new JTextField("Tile size");
		JButton create = new JButton("Create");
		JTextField mapWidthIn = new JTextField("Map width");
		JTextField mapHeightIn = new JTextField("Map height");

		addSelectOnClick(tileSizeIn);
		addSelectOnClick(mapHeightIn);
		addSelectOnClick(mapWidthIn);

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
				window.setEnabled(true);
				window.toFront();
				frame.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				window.setEnabled(true);
				window.toFront();
				frame.dispose();
			}
		});

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setVisible(true);
		Insets i = frame.getInsets();
		frame.setSize(250 + i.left + i.right, 125 + i.top + i.bottom);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(window);

		frame.add(tileSizeIn);
		tileSizeIn.setBounds(5, 5, 100, 25);

		frame.add(mapWidthIn);
		mapWidthIn.setBounds(5, 35, 100, 25);

		frame.add(mapHeightIn);
		mapHeightIn.setBounds(5, 65, 100, 25);

		frame.add(create);
		create.setBounds(5, 95, 100, 25);


		ActionListener a = e-> {
				window.setMap(new GameMap(window, Integer.parseInt(mapWidthIn.getText()), Integer.valueOf(mapHeightIn.getText()), Integer.parseInt(tileSizeIn.getText())), true);

				window.setEnabled(true);
				window.toFront();
				frame.dispose();
		};
		create.addActionListener(a);
		mapHeightIn.addActionListener(a);
	}

	/** Creates window so the user can confirm his action
	*   @param message is shown to the user
	*   @param actionListener runs when confirming the action
	**/
	public static void confirm(Window window, String message, ActionListener actionListener) {
		window.setEnabled(false);
		JFrame frame = new JFrame("Confirm");

		JButton confirm = new JButton("Confirm");
		JButton cancel = new JButton("Cancel");
		JLabel text = new JLabel(message);

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setVisible(true);
		Insets i = frame.getInsets();
		frame.setSize(250 + i.left + i.right, 95 + i.top + i.bottom);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(window);

		frame.add(cancel);
		cancel.setBounds(5, 65, 240, 25);

		frame.add(confirm);
		confirm.setBounds(5, 35, 240, 25);

		frame.add(text);
		text.setBounds(5, 5, 240, 25);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
				window.toFront();
				frame.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				window.setEnabled(true);
				window.toFront();
				frame.dispose();
			}
		});

		cancel.addActionListener(e -> {
			window.setEnabled(true);
			window.toFront();
			frame.dispose();
		});

		confirm.addActionListener(e -> {
			window.setEnabled(true);
			window.toFront();
			frame.dispose();

			actionListener.actionPerformed(new ActionEvent(window, 0, null));
		});
	}








	private static void addSelectOnClick(JTextField textField) {
		textField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {

			}
		});
	}
}
