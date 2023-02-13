package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.json.JsonObject;

import backend.Map.MapPosition;
import backend.pages.PageInstance;
import json.JsonEntityArray;
import json.JsonEntityMap;
import json.JsonEntityNumber;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ElementDataRestriction;
import json.restrictions.ElementNumberRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.ElementSetMemberRestriction;
import json.restrictions.ElementSetRestriction;
import json.restrictions.ImageRestriction;
import json.restrictions.InstanceDetailsRestriction;
import json.restrictions.MapElementRestriction;
import json.restrictions.MapPositionRestriction;
import json.restrictions.MapRestriction;
import json.restrictions.ScenarioRestriction;
import json.restrictions.TooltipRestriction;
import main.Main;
import main.Pages;

public class Element
{
	private static final String FACTION = "faction";
	
	RestrictedJson<ElementRestriction> elementJson;
	ArrayList<ElementInstance> instances = new ArrayList<ElementInstance>();
	HashMap<Map, RestrictedJson<ImageRestriction>> mapMap = new HashMap<Map, RestrictedJson<ImageRestriction>>();
	HashMap<Map, MapElementType> typeMap = new HashMap<Map, MapElementType>();
	HashMap<Map, RestrictedJson<TooltipRestriction>> tooltipMap = new HashMap<Map, RestrictedJson<TooltipRestriction>>();
	HashMap<String, ElementInstance> uniqueMap = new HashMap<String, ElementInstance>();
	String name;
	
	public ArrayList<ElementInstance> getInstances() {
		return instances;
	}

	public Element(Entry<String, RestrictedJson<ElementRestriction>> entry) throws Exception
	{
		this.elementJson = entry.getValue();
		this.name = entry.getKey();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void addMap(Map map, RestrictedJson<ImageRestriction> imageData, MapElementType type)
	{
		mapMap.put(map, imageData);
		typeMap.put(map, type);
	}
	
	public void addTooltip(Map map, RestrictedJson<TooltipRestriction> tooltip)
	{
		tooltipMap.put(map, tooltip);
	}
	
	public ArrayList<ElementInstance> makeInstances(int number, Map map, MapPosition mapPosition) throws Exception
	{	
		ArrayList<ElementInstance> instances = new ArrayList<ElementInstance>();
		int existingInstances = this.instances.size();
		for (int i = 0; i < number; i++)
		{
			ElementInstance elementInstance = this.makeInstance(number, existingInstances, map, mapPosition);
			instances.add(elementInstance);
		}
		return instances;
	}
	
	public MapElementType getMapElementType(Map map)
	{
		return this.typeMap.get(map);
	}
	
	public boolean getUnique()
	{
		return this.elementJson.getBoolean(ElementRestriction.UNIQUE);
	}
	
	public ElementInstance getUniqueInstance()
	{
		return this.instances.get(0);
	}
	
	public ElementInstance getUniqueInstance(String uniqueName)
	{
		return this.uniqueMap.get(uniqueName);
	}
	
	private ElementInstance makeInstance(int number, int existingInstances, Map map, MapPosition mapPosition) throws Exception
	{
		HashMap<String, Integer> detailValues = new HashMap<String, Integer>();
		HashMap<String, Integer> numberValues = new HashMap<String, Integer>();
		HashMap<String, Integer> setValues = new HashMap<String, Integer>();
		HashMap<Map, MapPosition> positionMap = new HashMap<Map, MapPosition>();
		
		return this.makeInstance(number, existingInstances, detailValues, numberValues, setValues, positionMap, map, mapPosition);
	}
	
	private HashMap<String, Integer> convertDetailsMap(JsonEntityMap<JsonEntityString> detailStringJsonMap) throws Exception
	{
		HashMap<String, JsonEntityString> detailStringMap = detailStringJsonMap.getEntityMap();
		HashMap<String, Integer> detailValues = new HashMap<String, Integer>();
		JsonEntityMap<RestrictedJson<ElementDataRestriction>> elementDataJsonMap = 
				this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
		HashMap<String, RestrictedJson<ElementDataRestriction>> elementDataMap = elementDataJsonMap.getEntityMap();
		for (Entry<String, JsonEntityString> entry : detailStringMap.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue().renderAsString();
			RestrictedJson<ElementDataRestriction> dataJson = elementDataMap.get(key);
			JsonEntityArray<JsonEntityString> options = dataJson.getStringArray(ElementDataRestriction.OPTIONS);
			boolean valid = false;
			for (int i = 0; i < options.getLength(); i++)
			{
				JsonEntityString jsonString = options.getMemberAt(i);
				String detailString = jsonString.renderAsString();
				if (detailString.equals(value))
				{
					valid = true;
					detailValues.put(key, i);
					break;
				}
			}
			if (!valid)
				throw new Exception("No value matching " + value);
		}
		return detailValues;
	}
	
	private HashMap<String, Integer> convertNumberMap(JsonEntityMap<JsonEntityNumber> numberJsonMap) throws Exception
	{
		HashMap<String, Integer> numberValues = new HashMap<String, Integer>();
		HashMap<String, JsonEntityNumber> numberMap = numberJsonMap.getEntityMap();
		for (Entry<String, JsonEntityNumber> entry : numberMap.entrySet())
		{
			String key = entry.getKey();
			int value = entry.getValue().getValue();
			numberValues.put(key, value);
		}
		return numberValues;
	}
	
	private HashMap<Map, MapPosition> convertMapMap(JsonEntityMap<RestrictedJson<MapPositionRestriction>> mapJsonMap)
	{
		HashMap<Map, MapPosition> positionMap = new HashMap<Map, MapPosition>();
		HashMap<String, RestrictedJson<MapPositionRestriction>> mapDataMap = mapJsonMap.getEntityMap();
		for (Entry<String, RestrictedJson<MapPositionRestriction>> entry : mapDataMap.entrySet())
		{
			String key = entry.getKey();
			RestrictedJson<MapPositionRestriction> mapPositionData = entry.getValue();
			Map map = Pages.getScenario().getMapByName(key);
			int x = mapPositionData.getNumber(MapPositionRestriction.X);
			int y = mapPositionData.getNumber(MapPositionRestriction.Y);
			MapPosition mapPosition = map.getMapPosition(x, y);
			positionMap.put(map, mapPosition);
		}
		return positionMap;
	}
	
	public void makeInstance(RestrictedJson<InstanceDetailsRestriction> instanceDetailsData)
			throws Exception
	{
		this.makeInstance(instanceDetailsData, null, null, null);
	}
	
	public ElementInstance makeInstance(RestrictedJson<InstanceDetailsRestriction> instanceDetailsData, String uniqueName, Map map, 
			MapPosition mapPosition)
			throws Exception
	{
		JsonEntityMap<JsonEntityString> detailStringJsonMap = instanceDetailsData.getStringMap(InstanceDetailsRestriction.STRING_MAP);
		HashMap<String, Integer> detailValues;
		if (detailStringJsonMap!= null)
			detailValues = this.convertDetailsMap(detailStringJsonMap);
		else
			detailValues = new HashMap<String, Integer>();		
		
		JsonEntityMap<JsonEntityNumber> numberJsonMap = instanceDetailsData.getNumberMap(InstanceDetailsRestriction.NUMBER_MAP);
		HashMap<String, Integer> numberValues;		
		if (numberJsonMap!= null)
			numberValues = this.convertNumberMap(numberJsonMap);
		else
			numberValues = new HashMap<String, Integer>();
			
		JsonEntityMap<JsonEntityNumber> setJsonMap = instanceDetailsData.getNumberMap(InstanceDetailsRestriction.SET_MAP);
		HashMap<String, Integer> setValues;
		if (setJsonMap != null)
			setValues = this.convertNumberMap(setJsonMap);
		else
			setValues = new HashMap<String, Integer>();
		
		JsonEntityMap<RestrictedJson<MapPositionRestriction>> mapJsonMap = 
				instanceDetailsData.getRestrictedJsonMap(InstanceDetailsRestriction.MAP_MAP, MapPositionRestriction.class);		
		HashMap<Map, MapPosition> positionMap;
		if (mapJsonMap != null)
			positionMap = this.convertMapMap(mapJsonMap);
		else
			positionMap = new HashMap<Map, MapPosition>();
		
		ElementInstance elementInstance = this.makeInstance(1, this.instances.size(), detailValues, numberValues, setValues, positionMap, map, mapPosition);
		if (uniqueName != null)
			this.uniqueMap.put(uniqueName, elementInstance);
		
		return elementInstance;
	}
	
	private ElementInstance makeInstance(int number, int existingInstances, HashMap<String, Integer> detailValues, HashMap<String, Integer> numberValues,
			HashMap<String, Integer> setValues, HashMap<Map, MapPosition> positionMap, Map map, MapPosition mapPosition) throws Exception
	{
		this.completeElementDataValues(detailValues, number, existingInstances);
		this.completeElementNumberValues(numberValues);
		this.completeElementSetValues(setValues, number, existingInstances);
		if (map != null)
			positionMap.put(map, mapPosition);
		this.completePositionMap(positionMap);
		ElementInstance elementInstance = new ElementInstance(detailValues, numberValues, setValues, positionMap);
		this.instances.add(elementInstance);
		return elementInstance;
	}
	
	public RestrictedJson<ImageRestriction> getMapImageData(Map map)
	{
		return this.mapMap.get(map);
	}
	
	public RestrictedJson<TooltipRestriction> getTooltip(Map map)
	{
		return this.tooltipMap.get(map);
	}
	
	private void completePositionMap(HashMap<Map, MapPosition> positionMap) throws Exception
	{
		for(Entry<Map, RestrictedJson<ImageRestriction>> entry : this.mapMap.entrySet())
		{
			Map map = entry.getKey();
			if (positionMap.containsKey(map))
				continue;
			MapPosition mapPosition = map.getRandomPosition();
			positionMap.put(map, mapPosition);
		}
	}
	
	private void completeElementNumberValues(HashMap<String, Integer> values)
	{
		JsonEntityMap<RestrictedJson<ElementNumberRestriction>> elementNumbers = 
				this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_NUMBERS, ElementNumberRestriction.class);
		if (elementNumbers == null)
			return;
		
		HashMap<String, RestrictedJson<ElementNumberRestriction>> elementMap = elementNumbers.getEntityMap();
		for (Entry<String, RestrictedJson<ElementNumberRestriction>> entry : elementMap.entrySet())
		{			
			String key = entry.getKey();
			if (values.containsKey(key))
				continue;
			RestrictedJson<ElementNumberRestriction> elementNumber = entry.getValue();
			int minValue = elementNumber.getNumber(ElementNumberRestriction.MIN_VALUE);
			int maxValue = elementNumber.getNumber(ElementNumberRestriction.MAX_VALUE);
			Integer multiplier = elementNumber.getNumber(ElementNumberRestriction.MULTIPLIER);
			int diff = maxValue - minValue;
			int rndm = diff != 0 ? Main.getRndm(diff) : 0;
			int result = minValue + rndm;
			if (multiplier != null)
				result = result * multiplier;
			values.put(key, result);
		}
	}
	
	private void completeElementSetValues(HashMap<String, Integer> values, int number, int existingInstances) throws Exception
	{
		JsonEntityMap<RestrictedJson<ElementSetRestriction>> elementSets = 
				this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_SETS, ElementSetRestriction.class);
		
		if (elementSets == null)
			return;
		
		HashMap<String, RestrictedJson<ElementSetRestriction>> setMap = elementSets.getEntityMap();
		
		for (Entry<String, RestrictedJson<ElementSetRestriction>> entry : setMap.entrySet())
		{
			if (values.containsKey(entry.getKey()))
			{
				continue;
			}
			RestrictedJson<ElementSetRestriction> elementSet = elementSets.getMemberBy(entry.getKey());	
			JsonEntityMap<RestrictedJson<ElementSetMemberRestriction>> members = 
					elementSet.getRestrictedJsonMap(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
			HashMap<String, RestrictedJson<ElementSetMemberRestriction>> memberMap = members.getEntityMap();
			
			for (Entry<String, RestrictedJson<ElementSetMemberRestriction>> innerEntry : memberMap.entrySet())
			{
				String key = innerEntry.getKey();
				RestrictedJson<ElementSetMemberRestriction> value= innerEntry.getValue();
				JsonEntityArray<JsonEntityString> options = value.getStringArray(ElementSetMemberRestriction.OPTIONS);
				if (existingInstances + number > options.size())
				{
					throw new Exception("Not enough element set options for a unique selection in " + 
							key + ".");
				}
				else
				{
					values.put(entry.getKey(), this.getUniqueValueForSet(options.size(), entry.getKey()));
					break;
				}		
			}			
		}
	}
	
	private int getUniqueValueForDetail(int optionsSize, String key)
	{
		ArrayList<Integer> valueIndexList = new ArrayList<Integer>();
		
		for (int k = 0; k < optionsSize; k++)
		{
			valueIndexList.add(k);
		}
		
		for (ElementInstance instance : this.instances)
		{
			int instanceValueIndex = instance.detailValues.get(key);
				
			for (int k = 0; k < optionsSize; k++)
			{
				if (valueIndexList.get(k) == instanceValueIndex)
				{
					valueIndexList.remove(k);
					break;
				}
			}
		}
		
		int rnd = Main.getRndm(valueIndexList.size());
		return valueIndexList.get(rnd);
	}
	
	private int getUniqueValueForSet(int optionsSize, String setName)
	{
		ArrayList<Integer> valueIndexList = new ArrayList<Integer>();
		
		for (int k = 0; k < optionsSize; k++)
		{
			valueIndexList.add(k);
		}
		
		for (ElementInstance instance : this.instances)
		{
			int instanceValueIndex = instance.setValues.get(setName);
				
			for (int k = 0; k < optionsSize; k++)
			{
				if (valueIndexList.get(k) == instanceValueIndex)
				{
					valueIndexList.remove(k);
					break;
				}
			}
		}
		
		int rnd = Main.getRndm(valueIndexList.size());
		return valueIndexList.get(rnd);
	}
	
	private void completeElementDataValues(HashMap<String, Integer> values, int number, int existingInstances) throws Exception
	{
		JsonEntityMap<RestrictedJson<ElementDataRestriction>> elementData = 
				this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
		HashMap<String, RestrictedJson<ElementDataRestriction>> elementMap = elementData.getEntityMap();
		for (Entry<String, RestrictedJson<ElementDataRestriction>> entry : elementMap.entrySet())
		{
			String key = entry.getKey();
			RestrictedJson<ElementDataRestriction> elementDetail = entry.getValue();
			boolean unique = elementDetail.getBoolean(ElementDataRestriction.UNIQUE);			
			JsonEntityArray<JsonEntityString> options = elementDetail.getStringArray(ElementDataRestriction.OPTIONS);
			if (unique && (existingInstances + number) > options.size())
			{
				throw new Exception("Not enough element options for a unique selection in " + 
						key + ".");
			}
			if (values.containsKey(key))
			{
				continue;
			}
			else if (unique)
			{
				values.put(key, this.getUniqueValueForDetail(options.size(), key));
			}
			else
			{
				values.put(key, options.getRandomIndex());	
			}			
		}
	}
	
	public ElementInstance getRandomInstance()
	{
		int randomIndex = Main.getRndm(this.instances.size());
		return this.instances.get(randomIndex);
	}
	
	public ElementInstance createInstance() throws Exception
	{
		this.makeInstances(1, null, null);
		return this.instances.get(this.instances.size() - 1);
	}
	
	public ElementInstance getInstance(int index)
	{
		return this.instances.get(index);
	}
	
	public void removeInstance(ElementInstance elementInstance)
	{
		this.instances.remove(elementInstance);
	}
	
	public class ElementInstance implements ReportInstance
	{
		HashMap<Map, Route> routeMap = new HashMap<Map, Route>();
		HashMap<String, Integer> detailValues;
		HashMap<String, Integer> numberValues;
		HashMap<String, Integer> setValues;
		HashMap<Map, MapPosition> positionMap = new HashMap<Map, MapPosition>();
		
		private ElementInstance(HashMap<String, Integer> detailValues, HashMap<String, Integer> numberValues, 
				HashMap<String, Integer> setValues, HashMap<Map, MapPosition> positionMap)
		{
			this.detailValues = detailValues;
			this.numberValues = numberValues;
			this.setValues = setValues;
			this.positionMap = positionMap;
			for (MapPosition mapPosition : positionMap.values())
			{
				mapPosition.elementInstances.add(this);
			}
		}
		
		public Element getElement()
		{
			return Element.this;
		}
		
		public void resetRoutePos(Map map)
		{
			Route route = this.routeMap.get(map);
			route.resetPosition();
		}
		
		public Faction getFaction()
		{
			String factionString = this.getStringValue(FACTION);
			if (factionString == null)
				return null;
			Faction faction = Faction.match(factionString);
			return faction;
		}
		
		public MapPosition incrementRoutePos(Map map)
		{
			Route route = this.routeMap.get(map);
			Faction faction = this.getFaction();
			route.incrementPosition(faction);
			RouteState routeState = route.getRouteState();
			MapPosition position = this.getPosition(map);
			
			if (routeState == RouteState.COMPLETED)
			{
				this.clearRoute(map);
			}
			
			return position;
		}
		
		public void setRoute(Map map, Route route)
		{
			this.routeMap.put(map, route);
		}
		
		public MapPosition decrementRoutePos(Map map)
		{
			Route route = this.routeMap.get(map);
			Faction faction = this.getFaction();
			route.decrementPosition(faction);
			RouteState routeState = route.getRouteState();
			MapPosition position = this.getPosition(map);
			
			if (routeState == RouteState.COMPLETED)
			{
				this.clearRoute(map);
			}
			
			return position;
		}
		
		public MapPosition getPosition(Map map)
		{
			Route route = this.routeMap.get(map);
			return route.getPosition();
		}

		public void clearRoute(Map map)
		{
			this.routeMap.put(map, null);
		}
		
		public MapPosition getMapPosition(Map map)
		{
			return this.positionMap.get(map);
		}
		
		public Route getRoute(Map map)
		{
			return this.routeMap.get(map);
		}
		
		public void setMapPosition(Map map, MapPosition position) throws Exception
		{
			MapPosition mapPosition = map.getMapPosition(position.x, position.y);
			MapPosition prevPosition = this.getMapPosition(map);
			if (prevPosition != mapPosition)
			{
				this.positionMap.put(map, mapPosition);
				prevPosition.elementInstances.remove(this);
				mapPosition.elementInstances.add(this);
			}
		}
		
		public void adjustNumber(String elementNumber, String sumSign, int value) throws Exception
		{
			int number = this.numberValues.get(elementNumber);
			number = PageInstance.performSum(number, sumSign, value);
			this.numberValues.put(elementNumber, number);
		}

		public String getSetValue(String setName, String key)
		{
			JsonEntityMap<RestrictedJson<ElementSetRestriction>> elementJson = 
					Element.this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_SETS, ElementSetRestriction.class);
			RestrictedJson<ElementSetRestriction> elementData = elementJson.getMemberBy(setName);
			JsonEntityMap<RestrictedJson<ElementSetMemberRestriction>> elementMembers = 
					elementData.getRestrictedJsonMap(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
			RestrictedJson<ElementSetMemberRestriction> memberData = elementMembers.getMemberBy(key);
			JsonEntityArray<JsonEntityString> options = memberData.getStringArray(ElementSetMemberRestriction.OPTIONS);	
			JsonEntityString value = options.getMemberAt(this.setValues.get(setName));
			return value.renderAsString();
		}
				
		public String getDetailValueByName(String dataName)
		{
			JsonEntityMap<RestrictedJson<ElementDataRestriction>> elementJson = 
					Element.this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			if (elementJson != null)
			{
				HashMap<String, RestrictedJson<ElementDataRestriction>> elementMap = elementJson.getEntityMap();
				if (elementMap.containsKey(dataName))
				{
					RestrictedJson<ElementDataRestriction> elementData = elementJson.getMemberBy(dataName);
					JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
					JsonEntityString value = options.getMemberAt(this.detailValues.get(dataName));
					return value.renderAsString();
				}
			}

			JsonEntityMap<RestrictedJson<ElementSetRestriction>> elementSetJson = Element.this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_SETS, ElementSetRestriction.class);
			if (elementSetJson != null)
			{
				HashMap<String, RestrictedJson<ElementSetRestriction>> setMap = elementSetJson.getEntityMap();
				for (Entry<String, RestrictedJson<ElementSetRestriction>> entry : setMap.entrySet())
				{
					JsonEntityMap<RestrictedJson<ElementSetMemberRestriction>> membersJson = 
							entry.getValue().getRestrictedJsonMap(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
					HashMap<String, RestrictedJson<ElementSetMemberRestriction>> membersMap = membersJson.getEntityMap();
					if (membersMap.containsKey(dataName))
					{
						return this.getSetValue(entry.getKey(), dataName); 
					}
				}
			}		

			return null;
		}
		
		public String getStringValue(String name)
		{
			String text = this.getDetailValueByName(name);
			if (text != null)
				return text;
			Integer number = this.getNumberValueByName(name);
			if (number != null)
				return String.valueOf(number);
			return null;
		}
		
		public Integer getNumberValueByName(String numberName)
		{
			return this.numberValues.get(numberName);
		}
		
		public String getValue(String key)
		{
			String value = this.getDetailValueByName(key);
			if (value == null)
				value = String.valueOf(this.getNumberValueByName(key));
			return value;
		}
		
		public String renderAsString()
		{
			JsonEntityMap<RestrictedJson<ElementDataRestriction>> elementJson = 
					Element.this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			HashMap<String, RestrictedJson<ElementDataRestriction>> elementMap = elementJson.getEntityMap();
			StringBuilder stringBuilder = new StringBuilder();
			for (Entry<String, RestrictedJson<ElementDataRestriction>> entry : elementMap.entrySet())
			{
				String key = entry.getKey();
				RestrictedJson<ElementDataRestriction> elementData = entry.getValue();
				JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
				JsonEntityString value = options.getMemberAt(this.detailValues.get(key));
				
				stringBuilder.append(key + " : " + value.renderAsString() + "\r\n");
			}	
			return stringBuilder.toString();
		}
		
		public AdjustmentInstance makeAdjustmentInstance(String qualityName, String value)
		{
			return new AdjustmentInstance(qualityName, value);
		}

		public class AdjustmentInstance implements ReportInstance
		{
			private String key;
			private String value;
			
			public AdjustmentInstance(String key, String value)
			{
				this.key = key;
				this.value = value;
			}
			
			@Override
			public String getStringValue(String name)
			{
				if (name.equals(this.key))
				{
					return value;
				}
				return ElementInstance.this.getStringValue(name);
			}

			@Override
			public Element getElement() {
				return Element.this;
			}
		}

	}
	
	public static void main(String[] args)
	{
//		JsonObject jsonObject = Main.openJsonFile(null);
//		RestrictedJson<ScenarioRestriction> scenarioJson = new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
//		JsonEntityArray<RestrictedJson<ElementRestriction>> elements = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ELEMENTS, ElementRestriction.class);
//		RestrictedJson<ElementRestriction> elementJson = elements.getMemberAt(0);
//		Element element = new Element(elementJson, 1);
//		System.out.println(element.getInstance(0).renderAsString());
	}
}
