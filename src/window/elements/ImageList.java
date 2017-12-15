package window.elements;

import data.TextureHandler;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ImageList extends JPanel{

	private JScrollPane imagePane;
	private JList<ImageIcon> images;
	private DefaultListModel<ImageIcon> listModel;

	private Map<String, ImageIcon> icons;

	public ImageList() {

		this.setLayout(new BorderLayout());

		listModel = new DefaultListModel<>();
		images = new JList<>(listModel);

		images.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		images.setLayoutOrientation(JList.HORIZONTAL_WRAP);

		update();
		images.setSelectedIndex(0);

		imagePane = new JScrollPane(images);
		this.add(imagePane, BorderLayout.PAGE_END);

		images.addListSelectionListener(e -> System.out.println(getSelectedImageName()));
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
				listModel.addElement(all.get(s));
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

	public void reSize(int width, int height) {
		Dimension d = new Dimension(width/4, height/2);
		imagePane.setPreferredSize(d);
		imagePane.setSize(d);
	}

}
