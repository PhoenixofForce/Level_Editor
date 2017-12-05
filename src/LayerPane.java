import javax.swing.*;
import java.awt.*;

public class LayerPane extends JList {

	private DefaultListModel listModel;

	public LayerPane() {
		listModel = new DefaultListModel();
		this.setModel(listModel);

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setLayoutOrientation(VERTICAL);

		listModel.addElement(new String("Layer 0"));
		listModel.addElement(new String("Layer 1"));
	}

}
