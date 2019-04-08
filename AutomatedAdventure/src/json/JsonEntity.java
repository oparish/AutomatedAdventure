package json;

import javax.json.JsonObject;

import backend.Room;
import frontEnd.RoomPanel;
import frontEnd.RoomsWindow;
import json.restrictions.RoomRestriction;
import json.restrictions.ScenarioRestriction;
import main.Main;

public interface JsonEntity
{

	public String renderAsString();
	
	public static void main(String[] args)
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		JsonEntityArray<RestrictedJson<RoomRestriction>> jsonEntityArray = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ROOMS, RoomRestriction.class);
		System.out.println(jsonEntityArray.renderAsString());
		RestrictedJson<RoomRestriction> roomJson = (RestrictedJson<RoomRestriction>) jsonEntityArray.getRestrictedJson(0, RoomRestriction.class);
		JsonEntityString name = roomJson.getJsonEntityString(RoomRestriction.NAME);
		RoomsWindow roomsWindow = new RoomsWindow();
		RoomPanel roomPanel = roomsWindow.getRoomPanel(0);
		Room room = new Room(roomJson);
		room.setRoomPanel(roomPanel);
		room.setName(name.renderAsString());
		roomsWindow.setVisible(true);
	}
}
