package backend;

import java.util.HashMap;

import backend.Element.ElementInstance;
import json.RestrictedJson;
import json.restrictions.room.RoomRestriction;

public class TimedRoom extends Room 
{

	public TimedRoom(RestrictedJson<RoomRestriction> roomJson, Scenario scenario,
			HashMap<String, ElementInstance> elementInstances)
	{
		super(roomJson, scenario, elementInstances);
	}

}
