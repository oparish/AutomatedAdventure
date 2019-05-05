package backend;

import java.util.HashMap;

import backend.Element.ElementInstance;
import json.RestrictedJson;
import json.restrictions.RoomRestriction;

public class ChallengeRoom extends Room
{

	public ChallengeRoom(RestrictedJson<RoomRestriction> roomJson, Scenario scenario,
			HashMap<String, ElementInstance> elementInstances)
	{
		super(roomJson, scenario, elementInstances);
	}

}
