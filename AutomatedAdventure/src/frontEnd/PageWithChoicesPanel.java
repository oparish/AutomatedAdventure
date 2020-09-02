package frontEnd;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import backend.pages.PageInstance;

public class PageWithChoicesPanel extends PagePanel
{
	ChoiceBox choiceBox;
	
	public PageWithChoicesPanel()
	{
		super();
		this.setLayout(new GridBagLayout());
		
		JPanel innerPanel = new JPanel();
		innerPanel.add(new JScrollPane(this.textArea));
		innerPanel.setLayout(new GridLayout(1, 1));
		this.add(innerPanel, this.getConstraints(0, 0, 1, 1));
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.choiceBox = new ChoiceBox();
		this.add(this.choiceBox, this.getConstraints(0, 1, 1, 0));
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
		super.update(pageInstance);
		this.choiceBox.updateChoiceBox(pageInstance);
	}
	
	public void clear()
	{
		super.clear();
		this.choiceBox.clear();
	}
}
