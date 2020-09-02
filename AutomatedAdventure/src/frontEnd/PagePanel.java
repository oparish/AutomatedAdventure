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

public abstract class PagePanel extends JPanel
{
	JTextArea textArea;
	
	public PagePanel()
	{
		super();
		this.createComponents();
	}
	
	private void createComponents()
	{
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		this.textArea.setLineWrap(true);
		this.textArea.setWrapStyleWord(true);
	}
	
	public void update(PageInstance pageInstance) throws Exception
	{
		this.textArea.setText(pageInstance.getText());
	}
	
	public void clear()
	{
		this.textArea.setText("");
	}
}
