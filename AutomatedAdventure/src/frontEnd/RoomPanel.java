package frontEnd;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import backend.Room;
import backend.State;
import backend.Template;
import backend.Element.ElementInstance;

@SuppressWarnings("serial")
public class RoomPanel extends JPanel
{	
	Room room;
	JTextArea textArea;
	JLabel label;
	
	public RoomPanel(Room room)
	{
		super();
		this.setupComponents();
		this.room = room;
	}
	
	private void setupComponents()
	{
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		this.label = new JLabel();
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setLayout(new GridBagLayout());
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.gridx = 0;
		labelConstraints.gridy = 0;
		labelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.label, labelConstraints);
		GridBagConstraints textAreaConstraints = new GridBagConstraints();
		textAreaConstraints.gridx = 0;
		textAreaConstraints.gridy = 1;
		textAreaConstraints.weightx = 1;
		textAreaConstraints.weighty = 1;
		textAreaConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.textArea, textAreaConstraints);
	}
	
	public void update(HashMap<String, ElementInstance> elementInstances, HashMap<String, State> states)
	{	
		Template template = this.room.getRandomTemplate();
		String text = template.getAlteredTemplateString(elementInstances, states);
		this.textArea.setText(text);
	}
	
	public void setLabelText(String labelText)
	{
		this.label.setText(labelText);
	}
}
