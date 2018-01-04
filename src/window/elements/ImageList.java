package window.elements;

import data.TextureHandler;
import window.Window;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;

public class ImageList extends JPanel{

	private Modifier mod;

	private JPanel helpPanel;
	private JTextField textField;
	private JScrollPane imagePane;
	private JList<ImageIcon> images;
	private DefaultListModel<ImageIcon> listModel;

	private Map<String, ImageIcon> icons;

	public ImageList(Window w) {

		this.setLayout(new BorderLayout());
		helpPanel = new JPanel();
		helpPanel.setLayout(new BorderLayout());

		textField = new JTextField("");
		helpPanel.add(textField, BorderLayout.PAGE_START);

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
		images = new JList<>(listModel);

		images.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		images.setLayoutOrientation(JList.HORIZONTAL_WRAP);

		this.update();
		images.setSelectedIndex(0);

		imagePane = new JScrollPane(images);
		helpPanel.add(imagePane, BorderLayout.PAGE_END);

		this.add(helpPanel, BorderLayout.PAGE_END);

		//images.addListSelectionListener(e -> System.out.println(getSelectedImageName()));

		mod = new Modifier(w);
		this.add(mod, BorderLayout.PAGE_START);
	}

	/**
	 * loads new images from the TextureHandler
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

	private void filter() {
		if(textField == null) return;
		filter(textField.getText());
	}

	private void filter(String filter) {
		if(listModel == null) return;
		for (String s : icons.keySet()) {
			if (!s.toLowerCase().contains(filter.toLowerCase())) {
				if (listModel.contains(icons.get(s))) {
					listModel.removeElement(icons.get(s));
				}
			} else {
				if (!listModel.contains(icons.get(s))) {
					listModel.addElement(icons.get(s));
				}
			}
		}
	}

	public ImageIcon getSelectedIcon() {
		return listModel.get(images.getSelectedIndex());
	}

	public String getSelectedImageName() {
		if(images.getSelectedIndex() < 0) return null;
		for(String s: icons.keySet()) {
			if(icons.get(s).equals(getSelectedIcon())) return s;
		}

		return null;
	}

	public Modifier getModifier() {
		return mod;
	}

	public void reSize(int width, int height) {
		Dimension d = new Dimension(width/4, height/2);
		imagePane.setPreferredSize(d);
		imagePane.setSize(d);
		textField.setPreferredSize(new Dimension(d.width, 25));
		textField.setSize(new Dimension(d.width, 25));
	}
}
