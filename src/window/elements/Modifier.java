package window.elements;

import data.layer.layerobjects.Tag;

import data.layer.layerobjects.TagObject;
import window.UserInputs;
import window.Window;

import javax.swing.*;
import java.awt.*;

public class Modifier extends JPanel{

	private TagObject object;
	private JLabel goStats;
	private JButton add, remove;

	private JTextField input;
	private JComboBox<String> attChooser;

	public Modifier(Window w) {

		this.setLayout(new BorderLayout());

		goStats = new JLabel();
		this.add(goStats, BorderLayout.PAGE_START);

		input = new JTextField();
		this.add(input, BorderLayout.PAGE_END);
		input.addActionListener(e -> {
			object.getTag((String) attChooser.getSelectedItem()).setAction(input.getText());
		});

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
			attChooser.removeItem(attChooser.getSelectedItem());
			object.removeTag((String) attChooser.getSelectedItem());
		});

		setTagObject(null);
	}

	public void add(String name) {
		if(attChooser.getSelectedItem() == null) {
			input.setEnabled(true);
		}
		object.addTag(new Tag(name));

		attChooser.addItem(name);
		attChooser.setSelectedItem(name);

		Tag t = object.getTag((String) attChooser.getSelectedItem());
		if (t != null) {
			input.setText(t.getAction());
		}
	}

	public void setTagObject(TagObject obj) {
		add.setEnabled(obj != null);
		remove.setEnabled(obj != null);
		input.setEnabled(obj != null);
		attChooser.setEnabled(obj != null);
		attChooser.removeAllItems();
		goStats.setText("");
		input.setText(null);
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
		} else input.setEnabled(false);

		goStats.setText(obj.getText());
	}
}