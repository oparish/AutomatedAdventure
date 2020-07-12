package frontEnd;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import backend.Scenario;
import backend.pages.PageContext;
import backend.pages.PageInstance;
import json.RestrictedJson;
import json.restrictions.PageRestriction;

public class PagePanel extends JPanel
{
	JTextArea textArea;
	ChoiceBox choiceBox;
	
	public PagePanel()
	{
		super();
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		this.textArea.setLineWrap(true);
		this.textArea.setWrapStyleWord(true);
		this.choiceBox = new ChoiceBox();
		JPanel innerPanel = new JPanel();
		this.setLayout(new GridBagLayout());
		innerPanel.add(this.textArea);
		innerPanel.setLayout(new GridLayout(1, 1));
		this.add(innerPanel, this.getConstraints(1, 1, 1, 1));
		this.add(this.choiceBox, this.getConstraints(1, 2, 1, 0));
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	private GridBagConstraints getConstraints(int gridx, int gridy, int weightx, int weighty)
	{
		GridBagConstraints gdc = new GridBagConstraints();
		gdc.gridx = gridx;
		gdc.gridy = gridy;
		gdc.weightx = weightx;
		gdc.weighty = weighty;	
		gdc.fill = GridBagConstraints.BOTH;
		return gdc;
	}
	
	public void update(PageInstance pageInstance) throws Exception
	{
		this.textArea.setText(pageInstance.getText());
		this.choiceBox.updateChoiceBox(pageInstance);
	}
	
	public void clear()
	{
		this.textArea.setText("");
		this.choiceBox.clear();
	}
}
