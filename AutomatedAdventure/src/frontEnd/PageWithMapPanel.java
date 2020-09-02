package frontEnd;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import backend.Map;
import backend.pages.PageInstance;

public class PageWithMapPanel extends PagePanel
{
	MapPanel mapPanel;
	
	public PageWithMapPanel(Map map)
	{
		super();
	
		this.setLayout(new GridLayout(2, 1));
		this.mapPanel = new MapPanel(map);
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(1, 1));
		textPanel.add(new JScrollPane(this.textArea));
		
		this.add(textPanel);
		
		JPanel mapOuterPanel = new JPanel();
		mapOuterPanel.setLayout(new GridLayout(1, 1));
		mapOuterPanel.add(new JScrollPane(this.mapPanel));
		
		this.add(mapOuterPanel);
		this.mapPanel.setEnabled(false);
	}
	
	public void update(PageInstance pageInstance) throws Exception
	{
		super.update(pageInstance);
		this.mapPanel.update(pageInstance);
		this.mapPanel.setEnabled(true);
	}
	
	public void clear()
	{
		super.clear();
		this.mapPanel.setEnabled(false);
	}
}
