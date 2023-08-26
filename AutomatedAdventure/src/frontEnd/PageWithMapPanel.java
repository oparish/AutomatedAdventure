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
import main.Main;

public class PageWithMapPanel extends PagePanel
{
	MapPanel mapPanel;
	
	public PageWithMapPanel(Map map) throws Exception
	{
		super();
	
		this.setLayout(new GridBagLayout());
		this.mapPanel = new MapPanel(map);
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(1, 1));
		textPanel.add(new JScrollPane(this.textArea));
		
		this.add(textPanel, Main.setupConstraints(0, 0, 1, 1));
		
		JPanel mapOuterPanel = new JPanel();
		mapOuterPanel.setLayout(new GridLayout(1, 1));
		mapOuterPanel.add(new JScrollPane(this.mapPanel));
		
		this.add(mapOuterPanel, Main.setupConstraints(0, 1, 1, 3));
		this.mapPanel.setEnabled(false);
	}
	
	public void secondaryUpdate(PageInstance pageInstance) throws Exception
	{
		this.mapPanel.repaintMap();
		this.mapPanel.secondaryUpdate();
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
