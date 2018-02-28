package window.elements;

import data.layer.layerobjects.Tag;

import data.layer.layerobjects.TagObject;
import window.UserInputs;
import window.Window;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class Modifier extends JPanel{

	private TagObject object;			//object which tags are beeing edited
	private JLabel goStats;				//label which shows texture name and coordinate of object
	private JButton add, remove;			//buttons to add and remove tags

	private JTextArea input;			//textArea to input tag values
	private JScrollPane scrollPane;
	private JComboBox<String> attChooser;

	private DocumentListener dc;

	public Modifier(Window w) {

		this.setLayout(new BorderLayout());

		goStats = new JLabel();
		this.add(goStats, BorderLayout.PAGE_START);

		input = new JTextArea();
		input.setEditable(true);

		dc = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (object != null && attChooser.getItemAt(attChooser.getSelectedIndex()) != null &&  object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())) != null) object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())).setAction(input.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (object != null && attChooser.getItemAt(attChooser.getSelectedIndex()) != null &&  object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())) != null) object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())).setAction(input.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (object != null && attChooser.getItemAt(attChooser.getSelectedIndex()) != null &&  object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())) != null) object.getTag(attChooser.getItemAt(attChooser.getSelectedIndex())).setAction(input.getText());
			}
		};

		scrollPane = new JScrollPane(input);
		scrollPane.setPreferredSize(new Dimension(0, 200));
		this.add(scrollPane, BorderLayout.PAGE_END);

		attChooser = new JComboBox();
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
			object.removeTag((String) attChooser.getSelectedItem());
			attChooser.removeItem(attChooser.getSelectedItem());
			if(attChooser.getItemCount() == 0) input.setText("");
		});

		setTagObject(null);
	}

	public void add(String name) {
		if(attChooser.getSelectedItem() == null) {
			input.setEnabled(true);
			input.getDocument().addDocumentListener(dc);
		}
		object.addTag(new Tag(name));

		attChooser.addItem(name);
		attChooser.setSelectedItem(name);

		Tag t = object.getTag((String) attChooser.getSelectedItem());
		if (t != null) {
			input.getDocument().removeDocumentListener(dc);
			input.setText(t.getAction());
			input.getDocument().addDocumentListener(dc);
		}
	}

	public void setTagObject(TagObject obj) {
		add.setEnabled(obj != null);
		remove.setEnabled(obj != null);

		input.setEnabled(obj != null);
		input.getDocument().removeDocumentListener(dc);

		attChooser.setEnabled(obj != null);
		attChooser.removeAllItems();
		goStats.setText("");
		input.setText("");
		if(obj == null) return;

		this.object = obj;

		int c = 0;
		for(Tag t: obj.getTags()) {
			attChooser.addItem(t.getName());
			c++;
		}

		if(c > 0) attChooser.setSelectedIndex(0);
		if(attChooser.getSelectedItem() != null) {
			Tag t = object.getTag((String) attChooser.getSelectedItem());
			if (t != null) {
				input.setText(t.getAction());
			}
			input.getDocument().addDocumentListener(dc);
		} else input.setEnabled(false);

		goStats.setText(obj.getText());
	}
}
