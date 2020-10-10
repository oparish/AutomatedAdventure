package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.json.JsonObject;

import backend.Map.MapPosition;
import backend.pages.PageInstance;
import json.JsonEntityArray;
import json.JsonEntityMap;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ElementDataRestriction;
import json.restrictions.ElementNumberRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.ElementSetMemberRestriction;
import json.restrictions.ElementSetRestriction;
import json.restrictions.ImageRestriction;
import json.restrictions.ScenarioRestriction;
import json.restrictions.TooltipRestriction;
import main.Main;

public class Element
{
	RestrictedJson<ElementRestriction> elementJson;
	ArrayList<ElementInstance> instances = new ArrayList<ElementInstance>();
	HashMap<Map, RestrictedJson<ImageRestriction>> mapMap = new HashMap<Map, RestrictedJson<ImageRestriction>>();
	HashMap<Map, RestrictedJson<TooltipRestriction>> tooltipMap = new HashMap<Map, RestrictedJson<TooltipRestriction>>();
	
	public ArrayList<ElementInstance> getInstances() {
		return instances;
	}

	public Element(RestrictedJson<ElementRestriction> elementJson) throws Exception
	{
		this.elementJson = elementJson;
	}
	
	public void addMap(Map map, RestrictedJson<ImageRestriction> imageData)
	{
		mapMap.put(map, imageData);
	}
	
	public void addTooltip(Map map, RestrictedJson<TooltipRestriction> tooltip)
	{
		tooltipMap.put(map, tooltip);
	}
	
	public void makeInstances(int number) throws Exception
	{	
		int existingInstances = this.instances.size();
		for (int i = 0; i < number; i++)
		{
			this.makeInstance(number, existingInstances);
		}
	}
	
	public boolean getUnique()
	{
		return this.elementJson.getBoolean(ElementRestriction.UNIQUE);
	}
	
	public ElementInstance getUniqueInstance()
	{
		return this.instances.get(0);
	}
	
	private void makeInstance(int number, int existingInstances) throws Exception
	{
		HashMap<String, Integer> detailValues = this.getElementDataValues(number, existingInstances);
		HashMap<String, Integer> numberValues = this.getElementNumberValues();
		ArrayList<Integer> setValues = this.getElementSetValues(number, existingInstances);
		HashMap<Map, MapPosition> positionMap = this.getPositionMap();
		this.instances.add(new ElementInstance(detailValues, numberValues, setValues, positionMap));
	}
	
	public RestrictedJson<ImageRestriction> getMapImageData(Map map)
	{
		return this.mapMap.get(map);
	}
	
	public RestrictedJson<TooltipRestriction> getTooltip(Map map)
	{
		return this.tooltipMap.get(map);
	}
	
	private HashMap<Map, MapPosition> getPositionMap() throws Exception
	{
		HashMap<Map, MapPosition> positionMap = new HashMap<Map, MapPosition>();
		for(Entry<Map, RestrictedJson<ImageRestriction>> entry : this.mapMap.entrySet())
		{
			Map map = entry.getKey();
			MapPosition mapPosition = map.getRandomPosition();
			positionMap.put(map, mapPosition);
		}
		return positionMap;
	}
	
	private HashMap<String, Integer> getElementNumberValues()
	{
		HashMap<String, Integer> values = new HashMap<String, Integer>();
		JsonEntityMap<RestrictedJson<ElementNumberRestriction>> elementNumbers = 
				this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_NUMBERS, ElementNumberRestriction.class);
		if (elementNumbers == null)
			return values;
		
		HashMap<String, RestrictedJson<ElementNumberRestriction>> elementMap = elementNumbers.getEntityMap();
		for (Entry<String, RestrictedJson<ElementNumberRestriction>> entry : elementMap.entrySet())
		{			
			String key = entry.getKey();
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
		
		return values;
	}
	
	private ArrayList<Integer> getElementSetValues(int number, int existingInstances) throws Exception
	{
		ArrayList<Integer> values = new ArrayList<Integer>();
		JsonEntityArray<RestrictedJson<ElementSetRestriction>> elementSets = 
				this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_SETS, ElementSetRestriction.class);
		
		if (elementSets == null)
			return values;
		
		for (int j = 0; j < elementSets.getLength(); j++)
		{
			RestrictedJson<ElementSetRestriction> elementSet = elementSets.getMemberAt(j);			
			JsonEntityMap<RestrictedJson<ElementSetMemberRestriction>> members = 
					elementSet.getRestrictedJsonMap(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
			HashMap<String, RestrictedJson<ElementSetMemberRestriction>> memberMap = members.getEntityMap();
			
			for (Entry<String, RestrictedJson<ElementSetMemberRestriction>> entry : memberMap.entrySet())
			{
				String key = entry.getKey();
				RestrictedJson<ElementSetMemberRestriction> value= entry.getValue();
				JsonEntityArray<JsonEntityString> options = value.getStringArray(ElementSetMemberRestriction.OPTIONS);
				if (existingInstances + number > options.size())
				{
					throw new Exception("Not enough element set options for a unique selection in " + 
							key + ".");
				}
				else
				{
					values.add(this.getUniqueValueForSet(options.size(), j));
					break;
				}		
			}			
		}
		return values;
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
	
	private int getUniqueValueForSet(int optionsSize, int dataIndex)
	{
		ArrayList<Integer> valueIndexList = new ArrayList<Integer>();
		
		for (int k = 0; k < optionsSize; k++)
		{
			valueIndexList.add(k);
		}
		
		for (ElementInstance instance : this.instances)
		{
			int instanceValueIndex = instance.setValues.get(dataIndex);
				
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
	
	private HashMap<String, Integer> getElementDataValues(int number, int existingInstances) throws Exception
	{
		HashMap<String, Integer> values = new HashMap<String, Integer>();
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
			if (unique)
			{
				values.put(key, this.getUniqueValueForDetail(options.size(), key));
			}
			else
			{
				values.put(key, options.getRandomIndex());	
			}			
		}
		return values;
	}
	
	public ElementInstance getRandomInstance()
	{
		int randomIndex = Main.getRndm(this.instances.size());
		return this.instances.get(randomIndex);
	}
	
	public ElementInstance createInstance() throws Exception
	{
		this.makeInstances(1);
		return this.instances.get(this.instances.size() - 1);
	}
	
	public ElementInstance getInstance(int index)
	{
		return this.instances.get(index);
	}
	
	public class ElementInstance
	{
		HashMap<String, Integer> detailValues;
		HashMap<String, Integer> numberValues;
		ArrayList<Integer> setValues;
		HashMap<Map, MapPosition> positionMap = new HashMap<Map, MapPosition>();
		
		private ElementInstance(HashMap<String, Integer> detailValues, HashMap<String, Integer> numberValues, ArrayList<Integer> setValues, 
				HashMap<Map, MapPosition> positionMap)
		{
			this.detailValues = detailValues;
			this.numberValues = numberValues;
			this.setValues = setValues;
			this.positionMap = positionMap;
		}
		
		public Element getElement()
		{
			return Element.this;
		}
		
		public MapPosition getMapPosition(Map map)
		{
			return this.positionMap.get(map);
		}
		
		public void adjustNumber(String elementNumber, String sumSign, int value) throws Exception
		{
			int number = this.numberValues.get(elementNumber);
			number = PageInstance.performSum(number, sumSign, value);
			this.numberValues.put(elementNumber, number);
		}

		public String getSetValue(int index, String key)
		{
			JsonEntityArray<RestrictedJson<ElementSetRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_SETS, ElementSetRestriction.class);
			RestrictedJson<ElementSetRestriction> elementData = elementJson.getMemberAt(index);
			JsonEntityMap<RestrictedJson<ElementSetMemberRestriction>> elementMembers = elementData.getRestrictedJsonMap(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
			RestrictedJson<ElementSetMemberRestriction> memberData = elementMembers.getMemberBy(key);
			JsonEntityArray<JsonEntityString> options = memberData.getStringArray(ElementSetMemberRestriction.OPTIONS);	
			JsonEntityString value = options.getMemberAt(this.setValues.get(index));
			return value.renderAsString();
		}
				
		public String getDetailValueByName(String dataName)
		{
			JsonEntityMap<RestrictedJson<ElementDataRestriction>> elementJson = 
					Element.this.elementJson.getRestrictedJsonMap(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			HashMap<String, RestrictedJson<ElementDataRestriction>> elementMap = elementJson.getEntityMap();
			if (elementMap.containsKey(dataName))
			{
				RestrictedJson<ElementDataRestriction> elementData = elementJson.getMemberBy(dataName);
				JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
				JsonEntityString value = options.getMemberAt(this.detailValues.get(dataName));
				return value.renderAsString();
			}
		
			JsonEntityArray<RestrictedJson<ElementSetRestriction>> elementSetJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_SETS, ElementSetRestriction.class);
			for (int i = 0; i < setValues.size(); i++)
			{
				RestrictedJson<ElementSetRestriction> elementData = elementSetJson.getMemberAt(i);
				JsonEntityMap<RestrictedJson<ElementSetMemberRestriction>> membersJson = 
						elementData.getRestrictedJsonMap(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
				HashMap<String, RestrictedJson<ElementSetMemberRestriction>> membersMap = membersJson.getEntityMap();
				if (membersMap.containsKey(dataName))
				{
					return this.getSetValue(i, dataName); 
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
