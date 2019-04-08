package backend;

import java.util.ArrayList;

import json.JsonEntityArray;
import json.RestrictedJson;
import json.restrictions.ElementRestriction;
import json.restrictions.RoomRestriction;
import json.restrictions.ScenarioRestriction;

public class Scenario
{
	RestrictedJson<ScenarioRestriction> scenarioJson;
	ArrayList<Room> rooms = new ArrayList<Room>();
	ArrayList<Element> elements = new ArrayList<Element>();
	
	public Scenario(RestrictedJson<ScenarioRestriction> scenarioJson)
	{
		this.scenarioJson = scenarioJson;
		
		JsonEntityArray<RestrictedJson<RoomRestriction>> roomJsonArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ROOMS, RoomRestriction.class);
		for (int i = 0; i < roomJsonArray.getLength(); i++)
		{
			RestrictedJson<RoomRestriction> roomJson = roomJsonArray.getRestrictedJson(i, RoomRestriction.class);
			this.rooms.add(new Room(roomJson));
		}
		
		JsonEntityArray<RestrictedJson<ElementRestriction>> elementJsonArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ELEMENTS, ElementRestriction.class);
		
		for (int i = 0; i < elementJsonArray.getLength(); i++)
		{
			RestrictedJson<ElementRestriction> elementJson = elementJsonArray.getRestrictedJson(i, ElementRestriction.class);
			this.elements.add(new Element(elementJson, 1));
		}
		
	}
}
