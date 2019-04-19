package backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import backend.Element.ElementInstance;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ChanceRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.IntervalRestriction;
import json.restrictions.RoomRestriction;
import json.restrictions.ScenarioRestriction;
import json.restrictions.StateRestriction;
import main.Main;

public class Scenario
{
	RestrictedJson<ScenarioRestriction> scenarioJson;
	ArrayList<Room> rooms = new ArrayList<Room>();
	ArrayList<Element> elements = new ArrayList<Element>();
	ArrayList<Interval> intervals = new ArrayList<Interval>();
	HashMap<String, Chance> chances = new HashMap<String, Chance>();
	HashMap<Integer, Chance> chanceByPriority = new HashMap<Integer, Chance>();
	int chanceRange;
	
	public int getChanceRange() {
		return chanceRange;
	}

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
		this.loadChances();
		this.loadElements();
		this.loadStates();
		this.loadIntervals();
		this.loadRooms();
	}
	
	private void loadChances()
	{
		JsonEntityArray<RestrictedJson<ChanceRestriction>> chanceArray = 
				this.scenarioJson.getRestrictedJsonArray(ScenarioRestriction.CHANCES, ChanceRestriction.class);
		for (int i = 0; i < chanceArray.getLength(); i++)
		{
			RestrictedJson<ChanceRestriction> chanceJson = chanceArray.getMemberAt(i);
			String name = chanceJson.getJsonEntityString(ChanceRestriction.NAME).renderAsString();
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
			String name = intervalJson.getJsonEntityString(IntervalRestriction.NAME).renderAsString();
			int time = intervalJson.getJsonEntityNumber(IntervalRestriction.TIME).getValue();
			this.intervals.add(new Interval(name, time));
		}
	}
	
	public int getIntervalTime(int intervalIndex)
	{
		Interval interval = this.intervals.get(intervalIndex);
		return interval.getTime();
	}
	
	public Interval getCurrentInterval()
	{
		Main main = Main.main;
		int counter = main.getIntervalCounter();
		return this.getInterval(counter);
	}
	
	public Chance getChance(String chanceName)
	{
		return this.chances.get(chanceName);
	}
	
	public Interval getInterval(int intervalIndex)
	{
		return this.intervals.get(intervalIndex);
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
			for (Element element : this.elements)
			{
				elementInstances.put(element.getName(), element.getRandomInstance());
			}
			this.rooms.add(new Room(roomJson, this, elementInstances));
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
