package frontEnd;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class RoomPanel extends JTextArea
{	
	public RoomPanel()
	{
		super();
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}
}
