package frontEnd;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import main.Main;

@SuppressWarnings("serial")
public class RoomsWindow extends JFrame
{
	private static final int WIDTH = 1000;	
	private static final int HEIGHT = 1000;
	
	public RoomsWindow()
	{
		super();
		this.setSize(new Dimension(WIDTH, HEIGHT));
	}
	
	public static void main(String[] args)
	{
		RoomsWindow window = new RoomsWindow();
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(2, 3));
		
		for (int i = 0; i < 6; i++)
		{
			innerPanel.add(new RoomPanel());
		}
		
		window.add(innerPanel);
		window.showWindow();
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
