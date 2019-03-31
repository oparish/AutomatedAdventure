package frontEnd;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import main.Main;

@SuppressWarnings("serial")
public class RoomsWindow extends JFrame
{
	private static final int WIDTH = 1000;	
	private static final int HEIGHT = 1000;
	
	ArrayList<RoomPanel> panels = new ArrayList<RoomPanel>();
	
	public RoomsWindow()
	{
		super();
		this.setSize(new Dimension(WIDTH, HEIGHT));
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(2, 3));
		
		for (int i = 0; i < 6; i++)
		{
			RoomPanel roomPanel = new RoomPanel();
			this.panels.add(roomPanel);
			innerPanel.add(roomPanel);
		}
		this.add(innerPanel);
		this.addOnCloseListener();
	}
	
	private void addOnCloseListener()
	{	
		this.addWindowListener(new WindowAdapter() {  
            public void windowClosing(WindowEvent e) {  
                System.exit(0);  
            }});
	}
	
	public RoomPanel getRoomPanel(int index)
	{
		return this.panels.get(index);
	}
	
	public void updatePanel(int index, String text)
	{
		RoomPanel roomPanel = this.panels.get(index);
		roomPanel.setText(text);
	}
	
	public void showWindow()
	{
		Dimension screenCentre = Main.findScreenCentre();
		int x = screenCentre.width - this.getWidth()/2;
		int y = screenCentre.height - this.getHeight()/2;
		this.setLocation(x, y);
		this.setVisible(true);
	}

}
