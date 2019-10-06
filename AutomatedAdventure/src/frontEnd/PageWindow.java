package frontEnd;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class PageWindow extends MyWindow
{
	JTextArea textArea;
	
	public PageWindow()
	{
		super();
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(1, 1));
		innerPanel.add(this.textArea);
		this.add(innerPanel);
	}
	
	public void update(String text)
	{
		this.textArea.append(text);
	}
}
