package backend;

import java.util.ArrayList;
import java.util.HashMap;

import backend.Element.ElementInstance;
import json.JsonEntityArray;
import json.JsonEntityMap;
import json.RestrictedJson;
import json.restrictions.MapElementRestriction;
import json.restrictions.MapRestriction;
import main.Main;

public class Map
{
	Scenario scenario;
	public Scenario getScenario() {
		return scenario;
	}

	RestrictedJson<MapRestriction> mapData;
	public RestrictedJson<MapRestriction> getMapData() {
		return mapData;
	}

	ArrayList<MapPosition> openMapPositions = new ArrayList<MapPosition>();
	HashMap<ElementInstance, ChangeInPosition> changeInPositionMap = new HashMap<ElementInstance, ChangeInPosition>();
	
	public HashMap<ElementInstance, ChangeInPosition> getChangeInPositionMap() {
		return changeInPositionMap;
	}

	public Map(Scenario scenario, RestrictedJson<MapRestriction> mapData)
	{
		this.scenario = scenario;
		this.mapData = mapData;
		int width = this.mapData.getNumber(MapRestriction.WIDTH);
		int height = this.mapData.getNumber(MapRestriction.HEIGHT);
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				this.openMapPositions.add(new MapPosition(i, j));
			}
		}
	}
	
	public void addChangeInPosition(ElementInstance elementInstance, MapPosition oldPosition, MapPosition newPosition)
	{
		ChangeInPosition changeInPosition = new ChangeInPosition(oldPosition, newPosition);
		this.changeInPositionMap.put(elementInstance, changeInPosition);
	}
	
	public void completeMove()
	{
		this.changeInPositionMap = new HashMap<ElementInstance, ChangeInPosition>();
	}
	
	public MapPosition getRandomPosition() throws Exception
	{
		if (this.openMapPositions.size() == 0)
			throw new Exception("No map position available.");
		
		int rnd = Main.getRndm(this.openMapPositions.size());
		MapPosition mapPosition = this.openMapPositions.get(rnd);
		this.openMapPositions.remove(mapPosition);
		return mapPosition;
	}
	
	public ArrayList<MapPosition> getOpenMapPositions()
	{
		return this.openMapPositions;
	}
	
	public MapPosition getMapPosition(int x, int y)
	{
		for(MapPosition mapPosition : this.openMapPositions)
		{
			if (mapPosition.x == x && mapPosition.y == y)
				return mapPosition;
		}
		return null;
	}
	
	public int getWidth()
	{
		return this.mapData.getNumber(MapRestriction.WIDTH);
	}
	
	public int getHeight()
	{
		return this.mapData.getNumber(MapRestriction.HEIGHT);
	}
	
	public int getTileSize()
	{
		return this.mapData.getNumber(MapRestriction.TILE_SIZE);
	}
	
	public ArrayList<Element> getElements()
	{
		ArrayList<Element> elements = new ArrayList<Element>();
		JsonEntityMap<RestrictedJson<MapElementRestriction>> elementMap = 
				this.mapData.getRestrictedJsonMap(MapRestriction.MAP_ELEMENTS, MapElementRestriction.class);
		HashMap<String, RestrictedJson<MapElementRestriction>> innerMap = elementMap.getEntityMap();
		for (String elementName : innerMap.keySet())
		{
			Element element = this.scenario.getElement(elementName);
			elements.add(element);
		}
		return elements;
	}
	
	public class MapPosition
	{
		public int x;
		public int y;
		public boolean occupied = false;
		
		public MapPosition(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	public class ChangeInPosition
	{
		public MapPosition oldPosition;
		public MapPosition newPosition;
		
		public ChangeInPosition(MapPosition oldPosition, MapPosition newPosition)
		{
			this.oldPosition = oldPosition;
			this.newPosition = newPosition;
		}
	}
}
