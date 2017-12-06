package window.elements;

import data.TextureHandler;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageList extends JPanel{

	private JScrollPane imagePane;
	private JList<ImageIcon> images;
	private DefaultListModel listModel;

	public ImageList() {

		this.setLayout(new BorderLayout());

		listModel = new DefaultListModel();
		images = new JList(listModel);

		images.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		images.setLayoutOrientation(JList.HORIZONTAL_WRAP);

		update();
		images.setSelectedIndex(0);

		//this.add(images, BorderLayout.PAGE_START);
		imagePane = new JScrollPane(images);
		imagePane.setPreferredSize(new Dimension(200, 400));			//TODO: Responsive
		this.add(imagePane, BorderLayout.PAGE_END);
	}

	public void update() {
		for(BufferedImage img: TextureHandler.getAllImages()) {
			if(img != null) listModel.addElement(new ImageIcon(img));
		}
	}

}
