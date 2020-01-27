package window.elements;

import data.Debouncer;
import data.layer.layerobjects.Tag;

import data.layer.layerobjects.TagObject;
import window.UserInputs;
import window.Window;
import window.commands.TagAddCommand;
import window.commands.TagChangeCommand;
import window.commands.TagRemoveCommand;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * used to add tags to objects and change them
 */
public class Modifier extends JPanel{

	private Window w;
	private Modifier instance;

	private TagObject object;			//Object which tags are beeing edited
	private JLabel goStats;				//Label which shows texture name and coordinate of object
	private JButton add, remove;			//Buttons to add and remove tags

	private JTextArea input;			//TextArea to input tag values
	private JScrollPane scrollPane;			//ScrollPane to optimize textArea
	private JComboBox<String> attChooser;		//ComboBox to select from existing Tags

	private DocumentListener dc;			//Listener that saves all changes

	public Modifier(Window w) {
		instance = this;

		this.w = w;
		this.setLayout(new BorderLayout());

		goStats = new JLabel();
		this.add(goStats, BorderLayout.PAGE_START);

		input = new JTextArea();
		input.setEditable(true);

		dc = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (object != null && attChooser.getItemAt(attChooser.getSelectedIndex()) != null &&  object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())) != null) {
					TagObject tagObject = object;
					int chooserIndex = attChooser.getSelectedIndex();
					String tagName = attChooser.getItemAt(chooserIndex);
					String newTagContent = input.getText();
					String oldTagConent = object.getTag(tagName).getAction();

					Debouncer.debounce("modifier_input_input", () -> {
						new TagChangeCommand(instance, tagObject, tagName, oldTagConent, newTagContent).execute(w.getMapViewer().getCommandHistory());
					}, 250);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (object != null && attChooser.getItemAt(attChooser.getSelectedIndex()) != null &&  object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())) != null) {
					TagObject tagObject = object;
					int chooserIndex = attChooser.getSelectedIndex();
					String tagName = attChooser.getItemAt(chooserIndex);
					String newTagContent = input.getText();
					String oldTagConent = object.getTag(tagName).getAction();

					Debouncer.debounce("modifier_input_input", () -> {
						new TagChangeCommand(instance, tagObject, tagName, oldTagConent, newTagContent).execute(w.getMapViewer().getCommandHistory());
					}, 250);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (object != null && attChooser.getItemAt(attChooser.getSelectedIndex()) != null &&  object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())) != null) {
					TagObject tagObject = object;
					int chooserIndex = attChooser.getSelectedIndex();
					String tagName = attChooser.getItemAt(chooserIndex);
					String newTagContent = input.getText();
					String oldTagConent = object.getTag(tagName).getAction();

					Debouncer.debounce("modifier_input_input", () -> {
						new TagChangeCommand(instance, tagObject, tagName, oldTagConent, newTagContent).execute(w.getMapViewer().getCommandHistory());
					}, 250);
				}
			}
		};

		scrollPane = new JScrollPane(input);
		scrollPane.setPreferredSize(new Dimension(0, 200));
		this.add(scrollPane, BorderLayout.PAGE_END);

		attChooser = new JComboBox<>();
		this.add(attChooser, BorderLayout.CENTER);
		attChooser.addActionListener(e -> {
			Tag t = object.getTag((String) attChooser.getSelectedItem());
			if(t != null) {
				input.setText(t.getAction());
			}
		});

		add = new JButton("+");
		this.add(add, BorderLayout.LINE_START);
		add.addActionListener(e -> {
			UserInputs.tagName(w, this);
		});

		remove = new JButton("-");
		this.add(remove, BorderLayout.LINE_END);
		remove.addActionListener(e -> {
			new TagRemoveCommand(instance, object, (String) attChooser.getSelectedItem()).execute(w.getMapViewer().getCommandHistory());
			attChooser.removeItem(attChooser.getSelectedItem());
			if(attChooser.getItemCount() == 0) input.setText("");
		});

		setTagObject(null);
	}

	/**
	 * Adds a new Tag to the currently selected object
	 *
	 * @param name Name of the new Tag
	*/
	public void add(String name) {
		//Resets textArea it was disabled
		if(attChooser.getSelectedItem() == null) {
			input.setEnabled(true);
			input.getDocument().addDocumentListener(dc);
		}
		//Adds Tag to object
		new TagAddCommand(instance, object, name).execute(w.getMapViewer().getCommandHistory());

		//Adds TagName to ComboBox and selects it
		attChooser.addItem(name);
		attChooser.setSelectedItem(name);

		//Sets text from textArea to action of the tag
		Tag t = object.getTag((String) attChooser.getSelectedItem());
		if (t != null) {
			input.getDocument().removeDocumentListener(dc);
			input.setText(t.getAction());
			input.getDocument().addDocumentListener(dc);
		}
	}

	/**
	 * Sets selected Object
	 *
	 * @param obj the new selected TagObject
	 */
	public void setTagObject(TagObject obj) {
		//Reset JObjects => Enabling if object is initialized, setting texts empty
		add.setEnabled(obj != null);
		remove.setEnabled(obj != null);

		input.setEnabled(obj != null);
		input.getDocument().removeDocumentListener(dc);

		attChooser.setEnabled(obj != null);
		attChooser.removeAllItems();
		goStats.setText("");
		input.setText("");
		//Return after reset if object is null
		if(obj == null) return;

		//Set selected object to the new one
		this.object = obj;

		//Count existing tags and add existing tags to comboBox
		int c = 0;
		for(Tag t: obj.getTags()) {
			attChooser.addItem(t.getName());
			c++;
		}

		//Set selected Tag in comboBox to the first, if there are entries
		if(c > 0) attChooser.setSelectedIndex(0);
		//If an entry is selected set the input text to the action of the tag
		if(attChooser.getSelectedItem() != null) {
			Tag t = object.getTag((String) attChooser.getSelectedItem());
			if (t != null) {
				input.setText(t.getAction());
			}
			input.getDocument().addDocumentListener(dc);
		} else input.setEnabled(false);

		//Set label text to tagObject information
		goStats.setText(obj.getText());
	}
}
