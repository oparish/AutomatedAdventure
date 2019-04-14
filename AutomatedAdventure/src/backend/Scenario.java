package backend;

import java.util.ArrayList;
import java.util.HashMap;

import backend.Element.ElementInstance;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ElementRestriction;
import json.restrictions.RoomRestriction;
import json.restrictions.ScenarioRestriction;
import json.restrictions.StateRestriction;

public class Scenario
{
	RestrictedJson<ScenarioRestriction> scenarioJson;
	ArrayList<Room> rooms = new ArrayList<Room>();
	ArrayList<Element> elements = new ArrayList<Element>();
	public ArrayList<Element> getElements() {
		return elements;
	}

	HashMap<String, State> states = new HashMap<String, State>();
	
	public HashMap<String, State> getStates() {
		return states;
	}

	public Scenario(RestrictedJson<ScenarioRestriction> scenarioJson)
	{
		this.scenarioJson = scenarioJson;
		this.loadElements();
		this.loadStates();
		this.loadRooms();
	}
	
	public int getCheckTime()
	{
		return this.scenarioJson.getJsonEntityNumber(ScenarioRestriction.CHECKTIME).getValue();
	}
	
	private void loadRooms()
	{
		JsonEntityArray<RestrictedJson<RoomRestriction>> roomJsonArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ROOMS, RoomRestriction.class);
		for (int i = 0; i < roomJsonArray.getLength(); i++)
		{
			RestrictedJson<RoomRestriction> roomJson = roomJsonArray.getMemberAt(i);
			HashMap<String, ElementInstance> elementInstances = new HashMap<String, ElementInstance>();
			for (Element element : this.elements)
			{
				elementInstances.put(element.getName(), element.getRandomInstance());
			}
			this.rooms.add(new Room(roomJson, elementInstances));
		}
	}
	
	private void loadElements()
	{
		JsonEntityArray<RestrictedJson<ElementRestriction>> elementJsonArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ELEMENTS, ElementRestriction.class);
		
		for (int i = 0; i < elementJsonArray.getLength(); i++)
		{
			RestrictedJson<ElementRestriction> elementJson = elementJsonArray.getMemberAt(i);
			this.elements.add(new Element(elementJson));
		}
	}
	
	public ArrayList<Room> getRooms()
	{
		return this.rooms;
	}
	
	private void loadStates()
	{
		JsonEntityArray<RestrictedJson<StateRestriction>> stateJsonArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.STATES, StateRestriction.class);
		for (int i = 0; i < stateJsonArray.getLength(); i++)
		{
			RestrictedJson<StateRestriction> stateJson = stateJsonArray.getMemberAt(i);
			JsonEntityString nameJson = stateJson.getJsonEntityString(StateRestriction.NAME);
			this.states.put(nameJson.renderAsString(), new State(stateJson, 0));
		}
	}
}
