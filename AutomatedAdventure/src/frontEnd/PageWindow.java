package frontEnd;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import backend.Scenario;
import backend.pages.PageContext;
import backend.pages.PageInstance;

public class PageWindow extends MyWindow
{
	PagePanel firstPanel;
	PagePanel secondPanel;
	boolean first = true;
	
	public PageWindow()
	{
		super();
		this.setLayout(new GridLayout(1, 2));
		this.firstPanel = new PagePanel();
		this.add(this.firstPanel);
		this.secondPanel = new PagePanel();
		this.add(this.secondPanel);
	}
	
	public void update(PageInstance pageInstance) throws Exception
	{
		if (this.first)
		{
			this.firstPanel.update(pageInstance);
			this.secondPanel.clear();
		}
		else
		{
			this.secondPanel.update(pageInstance);
			this.firstPanel.clear();
		}
		
		this.first = !this.first;
	}
}
