package backend;

import static json.restrictions.ScenarioRestriction.COMPONENTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import backend.Element.ElementInstance;
import backend.component.ConnectionSet;
import backend.pages.ElementChoice;
import backend.pages.PageContext;
import backend.pages.PageInstance;
import backend.pages.RandomRedirectInstance;
import backend.pages.RedirectInstance;
import json.JsonEntityArray;
import json.JsonEntityMap;
import json.JsonEntityNumber;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ChanceRestriction;
import json.restrictions.ContextConditionRestriction;
import json.restrictions.ElementConditionRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.ImageRestriction;
import json.restrictions.IntervalRestriction;
import json.restrictions.MapElementRestriction;
import json.restrictions.MapRestriction;
import json.restrictions.PageRestriction;
import json.restrictions.RandomRedirectRestriction;
import json.restrictions.RedirectRestriction;
import json.restrictions.ScenarioRestriction;
import json.restrictions.StateRestriction;
import json.restrictions.SumRestriction;
import json.restrictions.TooltipRestriction;
import json.restrictions.component.ComponentRestriction;
import json.restrictions.component.ConnectionRestriction;
import json.restrictions.room.RoomRestriction;
import main.Main;
import main.Pages;
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
	HashMap<String, Map> mapMap = new HashMap<String, Map>();
	int chanceRange;
	
	public ConnectionSet getConnectionSet(String connectionSetName)
	{
		return this.connections.get(connectionSetName);
	}
	
	public RestrictedJson<SumRestriction> getSum(String sumKey)
	{
		JsonEntityMap<RestrictedJson<SumRestriction>> sums = 
				this.scenarioJson.getRestrictedJsonMap(ScenarioRestriction.SUMS, SumRestriction.class);
		return sums.getMemberBy(sumKey);
	}
	
	public Map getMapByName(String mapName)
	{
		return this.mapMap.get(mapName);
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
		this.loadMaps();
		this.loadUniqueInstances();
	}
	
	private void loadUniqueInstances() throws Exception
	{
		for (Element element : this.elementMap.values())
		{
			if (element.getUnique())
				element.makeInstances(1);
		}			
	}
	
	private void loadConnections() throws Exception
	{
		JsonEntityMap<RestrictedJson<ConnectionRestriction>> connectionMap = 
				this.scenarioJson.getRestrictedJsonMap(ScenarioRestriction.CONNECTIONS, ConnectionRestriction.class);		
		HashMap<String, RestrictedJson<ConnectionRestriction>> innerMap = connectionMap.getEntityMap();
		for (Entry<String, RestrictedJson<ConnectionRestriction>> entry : innerMap.entrySet())
		{
			RestrictedJson<ConnectionRestriction> connectionJson = entry.getValue();
			String firstString = connectionJson.getString(ConnectionRestriction.FIRST);
			String secondString = connectionJson.getString(ConnectionRestriction.SECOND);
			Element firstElement = this.elementMap.get(firstString);
			Element secondElement = this.elementMap.get(secondString);
			this.connections.put(entry.getKey(), new ConnectionSet(firstElement, secondElement));
		}
	}
	
	private void loadMaps()
	{
		JsonEntityMap<RestrictedJson<MapRestriction>> mapMap = 
				this.scenarioJson.getRestrictedJsonMap(ScenarioRestriction.MAPS, MapRestriction.class);
		HashMap<String, RestrictedJson<MapRestriction>> innerMap = mapMap.getEntityMap();
		for (Entry<String, RestrictedJson<MapRestriction>> entry : innerMap.entrySet())
		{
			String mapName = entry.getKey();
			RestrictedJson<MapRestriction> mapJson = entry.getValue();
			Map map = new Map(this, mapJson);
			this.mapMap.put(mapName, map);
			JsonEntityMap<RestrictedJson<MapElementRestriction>> mapElementMap = 
					mapJson.getRestrictedJsonMap(MapRestriction.MAP_ELEMENTS, MapElementRestriction.class);
			HashMap<String, RestrictedJson<MapElementRestriction>> innerMapElementMap = mapElementMap.getEntityMap();
			for (Entry<String, RestrictedJson<MapElementRestriction>> innerEntry : innerMapElementMap.entrySet())
			{
				String elementName = innerEntry.getKey();
				Element element = this.getElement(elementName);
				RestrictedJson<MapElementRestriction> mapData = innerEntry.getValue();
				RestrictedJson<ImageRestriction> imageData = mapData.getRestrictedJson(MapElementRestriction.IMAGE, ImageRestriction.class);
				element.addMap(map, imageData);
				RestrictedJson<TooltipRestriction> tooltipData = 
						mapData.getRestrictedJson(MapElementRestriction.TOOLTIP, TooltipRestriction.class);
				element.addTooltip(map, tooltipData);
			}
		}
	}
	
	private void loadMode()
	{
		this.mode = Mode.valueOf(this.scenarioJson.getString(ScenarioRestriction.MODE).toUpperCase());
	}
	
	private void loadChances()
	{
		JsonEntityMap<RestrictedJson<ChanceRestriction>> chanceMap = 
				this.scenarioJson.getRestrictedJsonMap(ScenarioRestriction.CHANCES, ChanceRestriction.class);
		HashMap<String, RestrictedJson<ChanceRestriction>> innerMap = chanceMap.getEntityMap();
		for (Entry<String, RestrictedJson<ChanceRestriction>> entry : innerMap.entrySet())
		{
			RestrictedJson<ChanceRestriction> chanceJson = entry.getValue();
			int priority = chanceJson.getJsonEntityNumber(ChanceRestriction.PRIORITY).getValue();
			Chance chance = new Chance(chanceJson);
			this.chances.put(entry.getKey(), chance);
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
	
	public RestrictedJson<RedirectRestriction> getRedirect(String key)
	{
		JsonEntityMap<RestrictedJson<RedirectRestriction>> redirectMap = 
				scenarioJson.getRestrictedJsonMap(ScenarioRestriction.REDIRECTS, RedirectRestriction.class);
		return redirectMap.getMemberBy(key);
	}
	
	public RestrictedJson<RandomRedirectRestriction> getRandomRedirect(String key)
	{
		JsonEntityMap<RestrictedJson<RandomRedirectRestriction>> redirectMap = 
				scenarioJson.getRestrictedJsonMap(ScenarioRestriction.RANDOM_REDIRECTS, RandomRedirectRestriction.class);
		return redirectMap.getMemberBy(key);
	}
	
	public void loadPage(ElementChoice elementChoice) throws Exception
	{
		Pages.getScenario().loadPage(elementChoice.keyword, elementChoice.context, elementChoice.elementInstance);
	}
	
	public void loadPage(String keyword, PageContext oldContext, ElementInstance elementInstance) throws Exception
	{
		RestrictedJson<PageRestriction> pageJson = this.getPageTemplate(keyword);
		RestrictedJson<RedirectRestriction> redirectJson = this.getRedirect(keyword);
		RestrictedJson<RandomRedirectRestriction> randomRedirectJson = this.getRandomRedirect(keyword);
		
		PageContext pageContext;
		
		if (oldContext != null)
			pageContext = oldContext;
		else
			pageContext = new PageContext(keyword);
		
		if (elementInstance != null)
			pageContext.addElementInstance(elementInstance);
		
		if (pageJson != null)
		{		
			PageInstance pageInstance = new PageInstance(this, pageContext, pageJson);
			try
			{
				Pages.getPageWindow().update(pageInstance);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (redirectJson != null)
		{
			RedirectInstance redirectInstance = new RedirectInstance(this, pageContext, redirectJson);
			redirectInstance.load(elementInstance);
		}
		else if (randomRedirectJson != null)
		{
			RandomRedirectInstance randomRedirectInstance = new RandomRedirectInstance(this, pageContext, randomRedirectJson);
			randomRedirectInstance.load(elementInstance);			
		}
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
