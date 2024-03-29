package backend;

import static json.restrictions.ScenarioRestriction.COMPONENTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import backend.Element.ElementInstance;
import backend.Map.MapPosition;
import backend.component.ConnectionSet;
import backend.pages.Counter;
import backend.pages.CounterSecondaryType;
import backend.pages.ElementChoice;
import backend.pages.ElementChoiceType;
import backend.pages.GroupCounter;
import backend.pages.PageContext;
import backend.pages.PageInstance;
import backend.pages.PositionCounter;
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
import json.restrictions.PackageRestriction;
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
	private HashMap<String, Counter> counterMap = new HashMap<String, Counter>();
	int chanceRange;
	String currentPackage;
	
	public void setCurrentPackage(String currentPackage) {
		this.currentPackage = currentPackage;
	}

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
				element.makeInstances(1, null, null);
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
	
	private void loadMaps() throws Exception
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
				RestrictedJson<ImageRestriction> computerImageData = 
						mapData.getRestrictedJson(MapElementRestriction.COMPUTER_IMAGE, ImageRestriction.class);
				if (computerImageData == null)
					computerImageData = imageData;
				String mapElementTypeString = mapData.getString(MapElementRestriction.MAP_ELEMENT_TYPE);
				MapElementType mapElementType = MapElementType.valueOf(mapElementTypeString.toUpperCase());
				
				if (mapElementType == null)
					throw new Exception("Unrecognised map element type: " + mapElementTypeString);
				
				element.addMap(map, imageData, computerImageData, mapElementType);
				RestrictedJson<TooltipRestriction> tooltipData = 
						mapData.getRestrictedJson(MapElementRestriction.TOOLTIP, TooltipRestriction.class);
				element.addTooltip(map, tooltipData);
				String factionIdentifier = mapData.getString(MapElementRestriction.FACTION_IDENTIFIER);
				if (factionIdentifier != null)
					element.addFactionIdentifier(map, factionIdentifier);	
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
	
	public RestrictedJson<PageRestriction> getPageTemplate(String packageName, String key)
	{
		RestrictedJson<PackageRestriction> packageData = this.getPackageData(packageName);
		JsonEntityMap<RestrictedJson<PageRestriction>> pageTemplateMap = 
				packageData.getRestrictedJsonMap(PackageRestriction.PAGES, PageRestriction.class);
		return pageTemplateMap.getMemberBy(key);
	}
	
	private RestrictedJson<PackageRestriction> getPackageData(String packageName)
	{
		JsonEntityMap<RestrictedJson<PackageRestriction>> packageMap = 
				scenarioJson.getRestrictedJsonMap(ScenarioRestriction.PACKAGEMAP, PackageRestriction.class);
		return packageMap.getMemberBy(packageName);
	}
	
	public RestrictedJson<RedirectRestriction> getRedirect(String packageName, String key)
	{
		RestrictedJson<PackageRestriction> packageData = this.getPackageData(packageName);
		JsonEntityMap<RestrictedJson<RedirectRestriction>> redirectMap = 
				packageData.getRestrictedJsonMap(PackageRestriction.REDIRECTS, RedirectRestriction.class);
		return redirectMap.getMemberBy(key);
	}
	
	public RestrictedJson<RandomRedirectRestriction> getRandomRedirect(String packageName, String key)
	{
		RestrictedJson<PackageRestriction> packageData = this.getPackageData(packageName);
		JsonEntityMap<RestrictedJson<RandomRedirectRestriction>> redirectMap = 
				packageData.getRestrictedJsonMap(PackageRestriction.RANDOM_REDIRECTS, RandomRedirectRestriction.class);
		return redirectMap.getMemberBy(key);
	}
	
	public void loadPage(ElementChoice elementChoice) throws Exception
	{
		Pages.getScenario().loadPage(elementChoice.keyword, elementChoice.context, elementChoice.elementInstance, elementChoice.elementGroup, 
				elementChoice, null);
	}
	
	public void loadPage(ElementChoice elementChoice, MapPosition position) throws Exception
	{
		Pages.getScenario().loadPage(elementChoice.keyword, elementChoice.context, elementChoice.elementInstance, elementChoice.elementGroup, 
				elementChoice, position);
	}
	
	public void loadPage(String keyword, PageContext oldContext, ElementInstance elementInstance, ElementGroup elementGroup,
			ElementChoice elementChoice, MapPosition position) throws Exception
	{
		String packageName;
		String pageName; 
		if (keyword.contains("."))
		{
			String[] keywordParts = keyword.split("\\.");
			if (keywordParts.length != 2)
				throw new Exception("Invalid keyword: " + keyword);
			packageName = keywordParts[0];
			pageName = keywordParts[1];
			this.currentPackage = packageName;
		}
		else
		{
			packageName = this.currentPackage;
			pageName = keyword;
		}
	
		RestrictedJson<PageRestriction> pageJson = this.getPageTemplate(packageName, pageName);
		RestrictedJson<RedirectRestriction> redirectJson = this.getRedirect(packageName, pageName);
		RestrictedJson<RandomRedirectRestriction> randomRedirectJson = this.getRandomRedirect(packageName, pageName);
		
		PageContext pageContext;
		
		if (oldContext != null)
			pageContext = oldContext;
		else
			pageContext = new PageContext(pageName);
		
		if (elementInstance != null)
			pageContext.addElementInstance(elementInstance);
		
		if (elementInstance != null)
			pageContext.setSelectedElementGroup(elementGroup);
		
		if (elementChoice != null)
			pageContext.setElementChoice(elementChoice);
		
		if (pageJson != null)
		{		
			PageInstance pageInstance = new PageInstance(this, pageContext, pageJson, position);
			Pages.getPageWindow().update(pageInstance);
		}
		else if (redirectJson != null)
		{
			RedirectInstance redirectInstance = new RedirectInstance(this, pageContext, redirectJson, position);
			redirectInstance.load(elementInstance, elementChoice);
		}
		else if (randomRedirectJson != null)
		{
			RandomRedirectInstance randomRedirectInstance = new RandomRedirectInstance(this, pageContext, randomRedirectJson, position);
			randomRedirectInstance.load(elementInstance, elementChoice);			
		}
		else
		{
			throw new Exception("Can't find anything to load with the name: " + pageName);
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
			this.elementMap.put(elementJsonEntry.getKey(), new Element(elementJsonEntry));
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
	
	public void addPositionCounter(Map map, String name, CounterSecondaryType counterSecondaryType)
	{
		PositionCounter positionCounter = new PositionCounter(map, counterSecondaryType);
		this.counterMap.put(name, positionCounter);
	}
	
	public void addGroupCounter(String name, CounterSecondaryType counterSecondaryType, ElementGroup elementGroup)
	{
		GroupCounter groupCounter = new GroupCounter(counterSecondaryType, elementGroup);
		this.counterMap.put(name, groupCounter);
	}
	
	public void incrementCounter(String name)
	{
		Counter counter = this.counterMap.get(name);
		counter.increment();
	}
	
	public PositionCounter getPositionCounter(String name) throws Exception
	{
		Counter counter = this.counterMap.get(name);
		if (counter == null)
		{
			throw new Exception(name + " is not a counter.");
		}
		else if (counter instanceof PositionCounter)
		{
			PositionCounter positionCounter = (PositionCounter) counter;
			return positionCounter;
		}
		else
		{
			throw new Exception(name + " is not a PositionCounter");
		}
	}
	
	public MapPosition getMapPositionFromPositionCounter(String name) throws Exception
	{
		return this.getPositionCounter(name).getMapPosition();
	}
	
	public ElementInstance getElementInstanceFromGroupCounter(Element element, String name) throws Exception
	{
		Counter counter = this.counterMap.get(name);
		if (counter == null)
		{
			throw new Exception(name + " is not a counter.");
		}
		else if (counter instanceof GroupCounter)
		{
			GroupCounter groupCounter = (GroupCounter) counter;
			ElementInstance elementInstance = groupCounter.getSelectedElement();
			if (elementInstance.getElement() == element)
				return elementInstance;
			else
				return null;
		}
		else
		{
			throw new Exception(name + " is not a GroupCounter");
		}
	}
	
	public boolean isCounterFinished(String name)
	{
		Counter counter = this.counterMap.get(name);
		return counter.isFinished();
	}
}
