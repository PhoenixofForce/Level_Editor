package window.elements;

import data.TextureHandler;
import window.Window;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * used to show the user all imported textures
 */
public class ImageList extends JPanel{

	private Modifier mod;							//Modifier to add tags

	private JPanel helpPanel;						//helping panel to put the textField in the correct position
	private JTextField textField;					//textField that allows the user to search for textures
	private JScrollPane imagePane;					//scrollPane to make the place for the textures bigger
	private JList images;				//JList of all imported images
	private DefaultListModel<ImageIcon> listModel;	//the listModel of the List

	private Map<String, ImageIcon> icons;			//map of texturenames and corresponding imageIcons

	public ImageList(Window w) {
		this.setLayout(new BorderLayout());
		helpPanel = new JPanel();
		helpPanel.setLayout(new BorderLayout());

		textField = new JTextField("");
		helpPanel.add(textField, BorderLayout.PAGE_START);

		//when typing into the textField the filter is to the imageList
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filter();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filter();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filter();
			}
		});

		listModel = new DefaultListModel<>();
		images = new JList<ImageIcon>(listModel)/* {
			public String getToolTipText(MouseEvent evt) {
				int index = locationToIndex(evt.getPoint());
				//if(index < 0) return "";
				ImageIcon item = getModel().getElementAt(index);
				for(String s: icons.keySet()) {
					if(icons.get(s).equals(item)) return s;
				}
				return "ERROR";
			}
		}*/;


		images.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		images.setLayoutOrientation(JList.HORIZONTAL_WRAP);

		this.update();
		images.setSelectedIndex(0);

		imagePane = new JScrollPane(images);
		helpPanel.add(imagePane, BorderLayout.PAGE_END);

		this.add(helpPanel, BorderLayout.PAGE_END);

		//images.addListSelectionListener(e -> System.out.println(getSelectedImageName()));	//For Debugging

		mod = new Modifier(w);
		this.add(mod, BorderLayout.PAGE_START);
	}

	/**
	 * loads possible new images from the TextureHandler
	 */
	public void update() {
		if(icons == null) {
			icons = new HashMap<>();
		}

		Map<String, ImageIcon> all = TextureHandler.getAllImages();
		for(String s: all.keySet()) {
			if(!icons.keySet().contains(s)) {
				icons.put(s, all.get(s));

			}
		}
		filter();
	}

	/**
	 * applies the filter with the text of the textField
	 */
	private void filter() {
		if(textField == null) return;
		filter(textField.getText());
	}

	/**
	 * shows only textures which names contains the user input
	 * @param filter the userinput
	 */
	private void filter(String filter) {
		if(listModel == null) return;
		for (String s : icons.keySet()) {
			//Remove image if name does not contain the filter and if the image still is in the list
			if (!s.toLowerCase().contains(filter.toLowerCase())) {
				if (listModel.contains(icons.get(s))) {
					listModel.removeElement(icons.get(s));
				}
			}
			//Adds image if name contains the filter and the image is not in the list
			else {
				if (!listModel.contains(icons.get(s))) {
					listModel.addElement(icons.get(s));
				}
			}
		}
	}

	/**
	 * @return the selected image
	 */
	public ImageIcon getSelectedIcon() {
		return listModel.get(images.getSelectedIndex());
	}

	/**
	 * @return the name if the selected image
	 */
	public String getSelectedImageName() {
		if(images.getSelectedIndex() < 0) return null;
		for(String s: icons.keySet()) {
			if(icons.get(s).equals(getSelectedIcon())) return s;
		}

		return null;
	}

	/**
	 * @return the modigier
	 */
	public Modifier getModifier() {
		return mod;
	}

	/**
	 * resizes the elements depending on width and height of the window
	 * @param width
	 * @param height
	 */
	public void reSize(int width, int height) {
		Dimension d = new Dimension(width/4, height/2);
		imagePane.setPreferredSize(d);
		imagePane.setSize(d);
		textField.setPreferredSize(new Dimension(d.width, 25));
		textField.setSize(new Dimension(d.width, 25));
	}
}
