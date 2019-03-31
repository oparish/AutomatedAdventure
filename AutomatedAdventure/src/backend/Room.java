package backend;

import frontEnd.RoomPanel;

public class Room
{
	private RoomPanel roomPanel;
	
	public Room()
	{
		
	}
	
	public void setName(String name)
	{
		this.roomPanel.setLabelText(name);
	}

	public RoomPanel getRoomPanel() {
		return roomPanel;
	}

	public void setRoomPanel(RoomPanel roomPanel) {
		this.roomPanel = roomPanel;
	}
}
