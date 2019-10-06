package frontEnd;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import backend.Room;
import backend.Scenario;
import backend.State;
import backend.Element.ElementInstance;
import main.Main;

@SuppressWarnings("serial")
public class RoomsWindow extends MyWindow
{	
	Scenario scenario;
	ArrayList<RoomPanel> panels = new ArrayList<RoomPanel>();
	
	public RoomsWindow(Scenario scenario)
	{
		super();
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(2, 3));
		
		this.scenario = scenario;
		ArrayList<Room> rooms = this.scenario.getRooms();
		for (int i = 0; i < rooms.size(); i++)
		{
			Room room = rooms.get(i);
			RoomPanel roomPanel = new RoomPanel(room);
			roomPanel.setLabelText(room.getName());
			this.panels.add(roomPanel);
			innerPanel.add(roomPanel);
		}
		this.add(innerPanel);

	}
	
	public RoomPanel getRoomPanel(int index)
	{
		return this.panels.get(index);
	}
	
	public void update(int intervalIndex)
	{
		for (int i = 0; i < this.panels.size(); i++)
		{
			RoomPanel panel = this.panels.get(i);
			panel.update(this.scenario, this.scenario.getStates(), this.scenario.getCurrentInterval());
		}
	}
}
