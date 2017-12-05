import javax.swing.*;
import java.awt.*;

public class LayerPane extends JPanel {

	private JList jList;
	private DefaultListModel listModel;

	private JToolBar toolBar;

	public LayerPane() {
		this.setLayout(new BorderLayout());

		listModel = new DefaultListModel();
		jList = new JList(listModel);

		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);

		listModel.addElement(new String("Background Layer"));
		listModel.addElement(new String("Tile Layer"));
		listModel.addElement(new String("Object Layer"));
		listModel.addElement(new String("Mob Layer"));

		this.add(jList, BorderLayout.PAGE_START);

		toolBar = new JToolBar();
		toolBar.add(new JButton("+"));
		toolBar.add(new JButton("-"));
		toolBar.setFloatable(false);

		this.add(toolBar, BorderLayout.PAGE_END);
	}

}
