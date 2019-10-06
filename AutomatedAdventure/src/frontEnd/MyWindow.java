package frontEnd;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import main.Main;

public abstract class MyWindow extends JFrame
{
	private static final int WIDTH = 1000;	
	private static final int HEIGHT = 1000;
	
	public MyWindow()
	{
		super();
		this.setSize(new Dimension(WIDTH, HEIGHT));
		this.addOnCloseListener();
	}
	
	public void showWindow()
	{
		Dimension screenCentre = Main.findScreenCentre();
		int x = screenCentre.width - this.getWidth()/2;
		int y = screenCentre.height - this.getHeight()/2;
		this.setLocation(x, y);
		this.setVisible(true);
	}
	
	private void addOnCloseListener()
	{	
		this.addWindowListener(new WindowAdapter() {  
            public void windowClosing(WindowEvent e) {  
                System.exit(0);  
            }});
	}
}
