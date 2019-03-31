package frontEnd;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class RoomPanel extends JPanel
{	
	JTextArea textArea;
	JLabel label;
	
	public RoomPanel()
	{
		super();
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
	
	public void setText(String text)
	{
		this.textArea.setText(text);
	}
	
	public void setLabelText(String labelText)
	{
		this.label.setText(labelText);
	}
}
