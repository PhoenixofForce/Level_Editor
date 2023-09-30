package window.elements;

import data.TextureHandler;
import lombok.Getter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;

public class ImageList extends JPanel{

	private final JTextField filterInput;
	private final JScrollPane imagePane;
	private final JList<ImageIcon> imageDisplay;
	private final DefaultListModel<ImageIcon> imageDisplayData;

	private Map<String, ImageIcon> icons;
	@Getter
	private String selectedImageName;

	public ImageList(Modifier m) {
		this.setLayout(new BorderLayout());

		JPanel helpPanel = new JPanel();
		helpPanel.setLayout(new BorderLayout());

		filterInput = new JTextField("");
		helpPanel.add(filterInput, BorderLayout.PAGE_START);

		//when typing into the textField the filter is to the imageList
		filterInput.getDocument().addDocumentListener(new DocumentListener() {
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

		imageDisplayData = new DefaultListModel<>();
		imageDisplay = new JList<>(imageDisplayData);

		imageDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		imageDisplay.setLayoutOrientation(JList.HORIZONTAL_WRAP);		//this and next line ensure, that the pane scrolls up and down and not (left and right or both)
		imageDisplay.setVisibleRowCount(-1);

		this.update();
		imageDisplay.setSelectedIndex(0);

		imagePane = new JScrollPane(imageDisplay);
		helpPanel.add(imagePane, BorderLayout.PAGE_END);

		this.add(helpPanel, BorderLayout.PAGE_END);

		imageDisplay.addListSelectionListener(e -> {
			if(imageDisplay.getSelectedIndex() < 0) return;
			for(String s: icons.keySet()) {
				if(icons.get(s).equals(getSelectedIcon())) selectedImageName = s;
			}
		});	//For Debugging

		this.add(m, BorderLayout.PAGE_START);
	}

	public void update() {
		if(icons == null) {
			icons = new HashMap<>();
		}

		Map<String, ImageIcon> all = TextureHandler.getAllImages();
		for(String s: all.keySet()) {
			if(!icons.containsKey(s)) {
				icons.put(s, all.get(s));
			}

			if(icons.containsKey(s) && !all.get(s).equals(icons.get(s))) {
				imageDisplayData.removeElement(icons.get(s));
				icons.put(s, all.get(s));
				imageDisplayData.addElement(icons.get(s));
			}
		}
		filter();
	}

	private void filter() {
		if(filterInput == null) return;
		filter(filterInput.getText());
	}

	private void filter(String filter) {
		if(imageDisplayData == null) return;
		for (String s : icons.keySet()) {

			//Remove image if name does not contain the filter and if the image still is in the list
			if (!s.toLowerCase().contains(filter.toLowerCase())) {
				if (imageDisplayData.contains(icons.get(s))) {
					imageDisplayData.removeElement(icons.get(s));
				}
			}

			//Adds image if name contains the filter and the image are not in the list
			else {
				if (!imageDisplayData.contains(icons.get(s))) {
					imageDisplayData.addElement(icons.get(s));
				}
			}
		}
	}

	public ImageIcon getSelectedIcon() {
		return imageDisplayData.get(imageDisplay.getSelectedIndex());
	}

	public void setSelectedImageByName(String name) {
		ImageIcon toSelect = icons.get(name);
		int index = imageDisplayData.indexOf(toSelect);
		imageDisplay.setSelectedIndex(index);
		selectedImageName = name;
	}

	public void reSize(int width, int height) {
		Dimension d = new Dimension(width/4, height/2 - 25);
		Dimension d2 = new Dimension(width/4, height);
		this.setSize(d2);
		this.setPreferredSize(d2);

		imagePane.setPreferredSize(d);
		imagePane.setSize(d);

		filterInput.setPreferredSize(new Dimension(d.width, 25));
		filterInput.setSize(new Dimension(d.width, 25));
	}
}
