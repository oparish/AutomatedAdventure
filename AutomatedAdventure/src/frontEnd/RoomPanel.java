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
import backend.Scenario;
import backend.State;
import backend.Template;
import backend.Element.ElementInstance;
import backend.Interval;

@SuppressWarnings("serial")
public class RoomPanel extends JPanel
{	
	Room room;
	JTextArea textArea;
	JLabel label;
	ControlPanel controlPanel;
	
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
		this.controlPanel = new ControlPanel();
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
		GridBagConstraints controlPanelConstraints = new GridBagConstraints();
		controlPanelConstraints.gridx = 0;
		controlPanelConstraints.gridy = 2;
		controlPanelConstraints.weightx = 1;
		controlPanelConstraints.weighty = 1;
		controlPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.controlPanel, controlPanelConstraints);
	}
	
	public void update(Scenario scenario, HashMap<String, State> states, Interval interval)
	{	
		Template template = this.room.getRandomTemplate(scenario);
		String text = template.getAlteredTemplateString(this.room.getElementInstances(), scenario.getStates(), scenario.getCurrentInterval());
		this.textArea.setText(text);
	}
	
	public void setLabelText(String labelText)
	{
		this.label.setText(labelText);
	}
}
