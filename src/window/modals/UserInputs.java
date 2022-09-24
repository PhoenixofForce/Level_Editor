package window.modals;

import data.GameMap;
import window.Window;
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
	public static void createLayer(window.Window window, LayerPane layerPane) {
		JDialog frame = new JDialog();
		frame.setTitle("Add Layer");
		frame.setModal(true);

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

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setLocationRelativeTo(window);

		frame.getContentPane().add(nameInput);
		nameInput.setBounds(10, 10, 180, 20);

		frame.getContentPane().add(layerType);
		layerType.setBounds(10, 40, 180, 20);

		frame.getContentPane().add(depthInput);
		depthInput.setBounds(10, 70, 180, 20);

		frame.getContentPane().add(create);
		create.setBounds(10, 100, 180, 20);

		ActionListener a = e->{
			String name = nameInput.getText();
			layerPane.createLayer(name, layerType.getSelectedIndex(), Float.parseFloat(depthInput.getText()));
			frame.dispose();
		};

		create.addActionListener(a);
		depthInput.addActionListener(a);

		frame.setPreferredSize(new Dimension(200 + 16, 130 + 39));
		frame.pack();
		frame.setVisible(true);
	}
	
	/** Creates window so the user can set the settings of the new tag
	*   @param window to handle focus
	*   @param mod to add modifier 
	**/
	public static void tagName(window.Window window, Modifier mod) {
		JDialog frame = new JDialog();
		frame.setTitle("New Tag");
		frame.setModal(true);

		JTextField nameIN = new JTextField("Name");
		JButton create = new JButton("Create");

		addSelectOnClick(nameIN);

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setLocationRelativeTo(window);

		frame.getContentPane().add(nameIN);
		nameIN.setBounds(10, 10, 180, 20);

		frame.getContentPane().add(create);
		create.setBounds(10, 40, 180, 20);

		ActionListener a = e->{
			mod.add(nameIN.getText());
			frame.dispose();
		};

		create.addActionListener(a);
		nameIN.addActionListener(a);

		frame.setPreferredSize(new Dimension(200 + 16, 70 + 39));
		frame.pack();
		frame.setVisible(true);
	}

	/** Creates window so the user can set the settings of the new map
	*   @param window to handle focus
	**/
	public static void newMap(window.Window window) {
		JDialog frame = new JDialog();
		frame.setTitle("New Map");
		frame.setModal(true);

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

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setLocationRelativeTo(window);

		frame.getContentPane().add(tileSizeIn);
		tileSizeIn.setBounds(10, 10, 180, 20);

		frame.getContentPane().add(mapWidthIn);
		mapWidthIn.setBounds(10, 40, 180, 20);

		frame.getContentPane().add(mapHeightIn);
		mapHeightIn.setBounds(10, 70, 180, 20);

		frame.getContentPane().add(create);
		create.setBounds(10, 100, 180, 20);


		ActionListener a = e-> {
				window.setMap(new GameMap(window, Integer.parseInt(mapWidthIn.getText()), Integer.parseInt(mapHeightIn.getText()), Integer.parseInt(tileSizeIn.getText())), true);
				frame.dispose();
		};
		create.addActionListener(a);
		mapHeightIn.addActionListener(a);

		frame.setPreferredSize(new Dimension(200 + 16, 130 + 39));
		frame.pack();
		frame.setVisible(true);
	}

	/** Creates window so the user can confirm his action
	*   @param message is shown to the user
	*   @param actionListener runs when confirming the action
	**/
	public static void confirm(Window window, String message, ActionListener actionListener) {
		String parsedMessage = "<html>";
		while(message.length() > 0) {
			int cappedMessageLength = Math.min(30, message.length());

			int length = message.substring(0, cappedMessageLength).lastIndexOf(" ");
			if(length <= 0 || message.length() < 30) length = cappedMessageLength;

			parsedMessage += message.substring(0, length);
			message = message.substring(length).trim();

			if(message.length() > 0 && (parsedMessage.charAt(parsedMessage.length() - 1) + message.charAt(0) + "").matches("[A-z]{2}")) {

				parsedMessage += "-";
			}
			parsedMessage += "<br>";
		}
		parsedMessage += "</html>";

		JDialog frame = new JDialog();
		frame.setTitle("Confirm");
		frame.setModal(true);

		JButton confirm = new JButton("Confirm");
		JButton cancel = new JButton("Cancel");
		JLabel text = new JLabel(parsedMessage);

		frame.setResizable(false);
		frame.setLayout(null);
		frame.setLocationRelativeTo(window);

		frame.getContentPane().add(text);
		text.setBounds(10, 10, 180, 40);

		frame.getContentPane().add(cancel);
		cancel.setBounds(10, 60, 85, 20);

		frame.getContentPane().add(confirm);
		confirm.setBounds(105, 60, 85, 20);

		cancel.addActionListener(e -> {
			frame.dispose();
		});

		confirm.addActionListener(e -> {
			frame.dispose();

			actionListener.actionPerformed(new ActionEvent(window, 0, null));
		});

		frame.setPreferredSize(new Dimension(200 + 16, 90 + 39));
		frame.pack();
		frame.setVisible(true);
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
