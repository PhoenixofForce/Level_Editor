package window.elements;

import window.Debouncer;
import data.layer.layerobjects.Tag;

import data.layer.layerobjects.TagObject;
import window.modals.UserInputs;
import window.Window;
import window.commands.TagAddCommand;
import window.commands.TagChangeCommand;
import window.commands.TagRemoveCommand;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * used to add tags to an object and change them
 */
public class Modifier extends JPanel{

	private final Window window;
	private final Modifier instance;

	private TagObject selectedGameObject;			//Object which tags are being edited
	private final JLabel goStats;				//Label which shows texture name and coordinate of object
	private final JButton add;
	private final JButton remove;			//Buttons to add and remove tags

	private final JTextArea tagValueInput;			//TextArea to input tag values
	private final JComboBox<String> tagSelector;		//ComboBox to select from existing Tags

	private final DocumentListener changeListener;			//Listener that saves all changes

	public Modifier(Window window) {
		instance = this;

		this.window = window;
		this.setLayout(new BorderLayout());

		goStats = new JLabel("");
		this.add(goStats, BorderLayout.PAGE_START);

		tagValueInput = new JTextArea();
		tagValueInput.setEditable(true);

		changeListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				changeListener();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				changeListener();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				changeListener();
			}
		};

		//ScrollPane to optimize textArea
		JScrollPane scrollPane = new JScrollPane(tagValueInput);
		scrollPane.setPreferredSize(new Dimension(0, 200));
		this.add(scrollPane, BorderLayout.PAGE_END);

		tagSelector = new JComboBox<>();
		this.add(tagSelector, BorderLayout.CENTER);
		tagSelector.addActionListener(e -> {
			Tag t = selectedGameObject.getTag((String) tagSelector.getSelectedItem());
			if(t != null) {
				tagValueInput.setText(t.getAction());
			}
		});

		add = new JButton("+");
		this.add(add, BorderLayout.LINE_START);
		add.addActionListener(e -> UserInputs.tagName(window, this));

		remove = new JButton("-");
		this.add(remove, BorderLayout.LINE_END);
		remove.addActionListener(e -> {
			new TagRemoveCommand(instance, selectedGameObject, (String) tagSelector.getSelectedItem()).execute(window.getMapViewer().getCommandHistory());
			tagSelector.removeItem(tagSelector.getSelectedItem());
			if(tagSelector.getItemCount() == 0) tagValueInput.setText("");
		});

		setTagObject(null);
	}

	private void changeListener() {
		boolean objectNotNullAndTagSelected = selectedGameObject != null &&  tagSelector.getSelectedItem() != null;
		if (objectNotNullAndTagSelected && selectedGameObject.getTag((String) tagSelector.getSelectedItem()) != null) {
			String tagName = (String) tagSelector.getSelectedItem();
			String newTagContent = tagValueInput.getText();
			String oldTagContent = selectedGameObject.getTag(tagName).getAction();

			Debouncer.debounce("modifier_input_input", () -> new TagChangeCommand(instance, selectedGameObject, tagName, oldTagContent, newTagContent).execute(window.getMapViewer().getCommandHistory()), 250);
		}
	}

	/**
	 * Adds a new Tag to the currently selected object
	 *
	 * @param name Name of the new Tag
	*/
	public void addTagToObject(String name) {
		//Resets textArea it was disabled
		if(tagSelector.getSelectedItem() == null) {
			tagValueInput.setEnabled(true);
			tagValueInput.getDocument().addDocumentListener(changeListener);
		}
		//Adds Tag to object
		new TagAddCommand(instance, selectedGameObject, name).execute(window.getMapViewer().getCommandHistory());

		//Adds TagName to ComboBox and selects it
		tagSelector.addItem(name);
		tagSelector.setSelectedItem(name);

		//Sets text from textArea to action of the tag
		Tag tag = selectedGameObject.getTag((String) tagSelector.getSelectedItem());
		if (tag != null) {
			tagValueInput.getDocument().removeDocumentListener(changeListener);
			tagValueInput.setText(tag.getAction());
			tagValueInput.getDocument().addDocumentListener(changeListener);
		}
	}

	/**
	 * Sets selected Object
	 *
	 * @param tagObject the new selected TagObject
	 */
	public void setTagObject(TagObject tagObject) {
		if(tagObject == null) tagObject = window.getMap();

		//Reset JObjects => Enabling if object is initialized, setting texts empty
		add.setEnabled(tagObject != null);
		remove.setEnabled(tagObject != null);

		tagValueInput.setEnabled(tagObject != null);
		tagValueInput.getDocument().removeDocumentListener(changeListener);

		tagSelector.setEnabled(tagObject != null);
		tagSelector.removeAllItems();
		goStats.setText("");
		tagValueInput.setText("");
		//Return after reset if object is null
		if(tagObject == null) return;

		//Set selected object to the new one
		this.selectedGameObject = tagObject;

		//Count existing tags and add existing tags to comboBox
		int tagCount = 0;
		for(Tag t: tagObject.getTags()) {
			tagSelector.addItem(t.getName());
			tagCount++;
		}

		//Set selected Tag in comboBox to the first, if there are entries
		if(tagCount > 0) tagSelector.setSelectedIndex(0);

		//If an entry is selected set the input text to the action of the tag
		if(tagSelector.getSelectedItem() != null) {
			Tag selectedTag = selectedGameObject.getTag((String) tagSelector.getSelectedItem());

			if (selectedTag != null) {
				tagValueInput.setText(selectedTag.getAction());
			}
			tagValueInput.getDocument().addDocumentListener(changeListener);
		} else tagValueInput.setEnabled(false);

		//Set label text to tagObject information
		goStats.setText(tagObject.getText());
	}
}
