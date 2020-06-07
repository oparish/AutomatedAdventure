package backend;

import static json.restrictions.ScenarioRestriction.COMPONENTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import backend.Element.ElementInstance;
import backend.component.ConnectionSet;
import json.JsonEntityArray;
import json.JsonEntityMap;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ChanceRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.IntervalRestriction;
import json.restrictions.PageRestriction;
import json.restrictions.ScenarioRestriction;
import json.restrictions.StateRestriction;
import json.restrictions.component.ComponentRestriction;
import json.restrictions.component.ConnectionRestriction;
import json.restrictions.room.RoomRestriction;
import main.Main;
import main.Rooms;

public class Scenario
{
	Mode mode;
	RestrictedJson<ScenarioRestriction> scenarioJson;
	ArrayList<Room> rooms = new ArrayList<Room>();
	HashMap<String, Element> elementMap = new HashMap<String, Element>();
	ArrayList<Interval> intervals = new ArrayList<Interval>();
	HashMap<String, Chance> chances = new HashMap<String, Chance>();
	HashMap<Integer, Chance> chanceByPriority = new HashMap<Integer, Chance>();
	HashMap<String, ConnectionSet> connections = new HashMap<String, ConnectionSet>();
	int chanceRange;
	
	public ConnectionSet getConnectionSet(String connectionSetName)
	{
		return this.connections.get(connectionSetName);
	}
	
	public int getChanceRange() {
		return chanceRange;
	}

	public Element getElement(String key)
	{
		return this.elementMap.get(key);
	}
	
	public HashMap<String, Element> getElements()
	{
		return this.elementMap;
	}
	
	public Mode getMode()
	{
		return this.mode;
	}

	HashMap<String, State> states = new HashMap<String, State>();
	
	public HashMap<String, State> getStates() {
		return states;
	}

	public Scenario(RestrictedJson<ScenarioRestriction> scenarioJson) throws Exception
	{
		this.scenarioJson = scenarioJson;
		this.loadChances();
		this.loadElements();
		this.loadStates();
		this.loadIntervals();
		this.loadRooms();
		this.loadMode();
		this.loadConnections();
	}
	
	private void loadConnections() throws Exception
	{
		JsonEntityArray<RestrictedJson<ConnectionRestriction>> connectionArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.CONNECTIONS, ConnectionRestriction.class);
		for (int i = 0; i < connectionArray.getLength(); i++)
		{
			RestrictedJson<ConnectionRestriction> connectionJson = connectionArray.getMemberAt(i);
			String firstString = connectionJson.getString(ConnectionRestriction.FIRST);
			String secondString = connectionJson.getString(ConnectionRestriction.SECOND);
			Element firstElement = this.elementMap.get(firstString);
			Element secondElement = this.elementMap.get(secondString);
			this.connections.put(connectionJson.getString(ConnectionRestriction.NAME), new ConnectionSet(firstElement, secondElement));
		}
	}
	
	private void loadMode()
	{
		this.mode = Mode.valueOf(this.scenarioJson.getString(ScenarioRestriction.MODE).toUpperCase());
	}
	
	private void loadChances()
	{
		JsonEntityArray<RestrictedJson<ChanceRestriction>> chanceArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.CHANCES, ChanceRestriction.class);
		for (int i = 0; i < chanceArray.getLength(); i++)
		{
			RestrictedJson<ChanceRestriction> chanceJson = chanceArray.getMemberAt(i);
			String name = chanceJson.getString(ChanceRestriction.NAME);
			int priority = chanceJson.getJsonEntityNumber(ChanceRestriction.PRIORITY).getValue();
			Chance chance = new Chance(chanceJson);
			this.chances.put(name, chance);
			this.chanceByPriority.put(priority, chance);	
		}
		
		this.chanceRange = 0;
		Collection<Chance> chances = this.getChanceList();
		for (Chance chance : chances)
		{
			this.chanceRange += chance.getPercentage();
		}
	}
	
	public Chance getChanceByPriority(int priority)
	{
		return this.chanceByPriority.get(priority);
	}
	
	public Collection<Chance> getChanceList()
	{
		return this.chances.values();
	}
	
	private void loadIntervals()
	{
		JsonEntityArray<RestrictedJson<IntervalRestriction>> intervalArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.INTERVALS, IntervalRestriction.class);	
		for (int i = 0; i < intervalArray.getLength(); i++)
		{
			RestrictedJson<IntervalRestriction> intervalJson = intervalArray.getMemberAt(i);
			String name = intervalJson.getString(IntervalRestriction.NAME);
			int time = intervalJson.getJsonEntityNumber(IntervalRestriction.TIME).getValue();
			this.intervals.add(new Interval(name, time));
		}
	}
	
	public RestrictedJson<PageRestriction> getPageTemplate(String key)
	{
		JsonEntityMap<RestrictedJson<PageRestriction>> pageTemplateMap = scenarioJson.getRestrictedJsonMap(ScenarioRestriction.PAGES, PageRestriction.class);
		return pageTemplateMap.getMemberBy(key);
	}
	
	public int getIntervalTime(int intervalIndex)
	{
		Interval interval = this.intervals.get(intervalIndex);
		return interval.getTime();
	}
	
	public Interval getCurrentInterval()
	{
		Rooms rooms = Rooms.rooms;
		int counter = rooms.getIntervalCounter();
		return this.getInterval(counter);
	}
	
	public Chance getChance(String chanceName)
	{
		return this.chances.get(chanceName);
	}
	
	public State getStateByName(String stateName)
	{
		return this.states.get(stateName);
	}
	
	public Interval getInterval(int intervalIndex)
	{
		return this.intervals.get(intervalIndex);
	}
	
	public Interval getIntervalByName(String intervalName)
	{
		for (Interval interval : intervals)
		{
			if (intervalName.equals(interval.getName()))
				return interval;
		}
		new Exception("Interval name " + intervalName + " not recognised.").printStackTrace();
		return null;
	}
	
	public int getIntervalsLength()
	{
		return this.intervals.size();
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
			this.rooms.add(Room.getRoom(roomJson, this, elementInstances));
		}
	}
	
	private void loadElements() throws Exception
	{
		JsonEntityMap<RestrictedJson<ElementRestriction>> elementJsonMap = 
				this.scenarioJson.getRestrictedJsonMap(ScenarioRestriction.ELEMENTS, ElementRestriction.class);
		
		for (Entry<String, RestrictedJson<ElementRestriction>> elementJsonEntry : elementJsonMap.getEntityMap().entrySet())
		{
			this.elementMap.put(elementJsonEntry.getKey(), new Element(elementJsonEntry.getValue()));
		}
	}
	
	public ElementInstance getRandomElementInstance(String elementType)
	{
		Element element = this.elementMap.get(elementType);
		return element.getRandomInstance();
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
			String name = stateJson.getString(StateRestriction.NAME);
			this.states.put(name, new State(stateJson, 0));
		}
	}
}
