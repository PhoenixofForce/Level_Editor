package window.elements.layer;

import javax.swing.*;
import java.awt.*;

public class LayerPane extends JPanel {

	private JList jList;
	private DefaultListModel listModel;

	private LayerControll lc;

	public LayerPane() {
		this.setLayout(new BorderLayout());

		listModel = new DefaultListModel();
		jList = new JList(listModel);

		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);

		listModel.addElement(new String("Background layer"));
		listModel.addElement(new String("Tile layer"));
		listModel.addElement(new String("Object layer"));
		listModel.addElement(new String("Mob layer"));

		this.add(jList, BorderLayout.PAGE_START);

		lc = new LayerControll();
		this.add(lc, BorderLayout.PAGE_END);
	}

}
