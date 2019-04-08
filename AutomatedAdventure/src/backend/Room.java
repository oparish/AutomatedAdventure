package backend;

import frontEnd.RoomPanel;
import json.RestrictedJson;
import json.restrictions.RoomRestriction;

public class Room
{
	private RoomPanel roomPanel;
	private RestrictedJson<RoomRestriction> roomJson;
	
	public Room(RestrictedJson<RoomRestriction> roomJson)
	{
		this.roomJson = roomJson;
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
