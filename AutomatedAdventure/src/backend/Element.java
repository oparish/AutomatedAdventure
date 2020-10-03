package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.json.JsonObject;

import backend.Map.MapPosition;
import backend.pages.PageInstance;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ElementDataRestriction;
import json.restrictions.ElementNumberRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.ElementSetMemberRestriction;
import json.restrictions.ElementSetRestriction;
import json.restrictions.ImageRestriction;
import json.restrictions.ScenarioRestriction;
import main.Main;

public class Element
{
	RestrictedJson<ElementRestriction> elementJson;
	ArrayList<ElementInstance> instances = new ArrayList<ElementInstance>();
	HashMap<Map, RestrictedJson<ImageRestriction>> mapMap = new HashMap<Map, RestrictedJson<ImageRestriction>>();
	
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
		ArrayList<Integer> detailValues = this.getElementDataValues(number, existingInstances);
		ArrayList<Integer> numberValues = this.getElementNumberValues();
		ArrayList<Integer> setValues = this.getElementSetValues(number, existingInstances);
		HashMap<Map, MapPosition> positionMap = this.getPositionMap();
		this.instances.add(new ElementInstance(detailValues, numberValues, setValues, positionMap));
	}
	
	public RestrictedJson<ImageRestriction> getMapImageData(Map map)
	{
		return this.mapMap.get(map);
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
	
	private ArrayList<Integer> getElementNumberValues()
	{
		ArrayList<Integer> values = new ArrayList<Integer>();
		JsonEntityArray<RestrictedJson<ElementNumberRestriction>> elementNumbers = 
				this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_NUMBERS, ElementNumberRestriction.class);
		if (elementNumbers == null)
			return values;
		
		for (int j = 0; j < elementNumbers.getLength(); j++)
		{			
			RestrictedJson<ElementNumberRestriction> elementNumber = elementNumbers.getMemberAt(j);
			int minValue = elementNumber.getNumber(ElementNumberRestriction.MIN_VALUE);
			int maxValue = elementNumber.getNumber(ElementNumberRestriction.MAX_VALUE);
			Integer multiplier = elementNumber.getNumber(ElementNumberRestriction.MULTIPLIER);
			int diff = maxValue - minValue;
			int rndm = diff != 0 ? Main.getRndm(diff) : 0;
			int result = minValue + rndm;
			if (multiplier != null)
				result = result * multiplier;
			values.add(result);
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
			JsonEntityArray<RestrictedJson<ElementSetMemberRestriction>> members = elementSet.getRestrictedJsonArray(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
			RestrictedJson<ElementSetMemberRestriction> firstMember = members.getMemberAt(0);
			JsonEntityArray<JsonEntityString> options = firstMember.getStringArray(ElementSetMemberRestriction.OPTIONS);
			if (existingInstances + number > options.size())
			{
				throw new Exception("Not enough element set options for a unique selection in " + 
						elementSet.getString(ElementSetRestriction.NAME) + ".");
			}
			values.add(this.getUniqueValue(options, j, true));
			
		}
		return values;
	}
	
	private int getUniqueValue(JsonEntityArray<JsonEntityString> options, int dataIndex, boolean setValues)
	{
		ArrayList<Integer> valueIndexList = new ArrayList<Integer>();
		
		for (int k = 0; k < options.size(); k++)
		{
			valueIndexList.add(k);
		}
		
		for (ElementInstance instance : this.instances)
		{
			int instanceValueIndex;
			
			if (setValues)
				instanceValueIndex = instance.setValues.get(dataIndex);
			else
				instanceValueIndex = instance.detailValues.get(dataIndex);
				
			for (int k = 0; k < options.size(); k++)
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
	
	private ArrayList<Integer> getElementDataValues(int number, int existingInstances) throws Exception
	{
		ArrayList<Integer> values = new ArrayList<Integer>();
		JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementData = 
				this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
		for (int j = 0; j < elementData.getLength(); j++)
		{
			RestrictedJson<ElementDataRestriction> elementDetail = elementData.getMemberAt(j);
			boolean unique = elementDetail.getBoolean(ElementDataRestriction.UNIQUE);			
			JsonEntityArray<JsonEntityString> options = elementDetail.getStringArray(ElementDataRestriction.OPTIONS);
			if (unique && (existingInstances + number) > options.size())
			{
				throw new Exception("Not enough element options for a unique selection in " + 
						elementDetail.getString(ElementDataRestriction.NAME) + ".");
			}
			if (unique)
			{
				values.add(this.getUniqueValue(options, j, false));
			}
			else
			{
				values.add(options.getRandomIndex());	
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
		ArrayList<Integer> detailValues;
		ArrayList<Integer> numberValues;
		ArrayList<Integer> setValues;
		HashMap<Map, MapPosition> positionMap = new HashMap<Map, MapPosition>();
		
		private ElementInstance(ArrayList<Integer> detailValues, ArrayList<Integer> numberValues, ArrayList<Integer> setValues, 
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
			Integer id = this.getNumberIDByName(elementNumber);
			int number = this.numberValues.get(id);
			number = PageInstance.performSum(number, sumSign, value);
			this.numberValues.set(id, number);
		}
		
		public String getDetailValue(int index)
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			RestrictedJson<ElementDataRestriction> elementData = elementJson.getMemberAt(index);
			JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
			JsonEntityString value = options.getMemberAt(this.detailValues.get(index));
			return value.renderAsString();
		}
		
		public String getSetValue(int index, int innerIndex)
		{
			JsonEntityArray<RestrictedJson<ElementSetRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_SETS, ElementSetRestriction.class);
			RestrictedJson<ElementSetRestriction> elementData = elementJson.getMemberAt(index);
			JsonEntityArray<RestrictedJson<ElementSetMemberRestriction>> elementMembers = elementData.getRestrictedJsonArray(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
			RestrictedJson<ElementSetMemberRestriction> memberData = elementMembers.getMemberAt(innerIndex);
			JsonEntityArray<JsonEntityString> options = memberData.getStringArray(ElementSetMemberRestriction.OPTIONS);	
			JsonEntityString value = options.getMemberAt(this.setValues.get(index));
			return value.renderAsString();
		}
				
		public String getDetailValueByName(String dataName)
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			for (int i = 0; i < detailValues.size(); i++)
			{
				RestrictedJson<ElementDataRestriction> elementData = elementJson.getMemberAt(i);
				String name = elementData.getString(ElementDataRestriction.NAME);
				if (dataName.equals(name))
					return this.getDetailValue(i);
			}
			
			JsonEntityArray<RestrictedJson<ElementSetRestriction>> elementSetJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_SETS, ElementSetRestriction.class);
			for (int i = 0; i < setValues.size(); i++)
			{
				RestrictedJson<ElementSetRestriction> elementData = elementSetJson.getMemberAt(i);
				JsonEntityArray<RestrictedJson<ElementSetMemberRestriction>> membersJson = elementData.getRestrictedJsonArray(ElementSetRestriction.MEMBERS, ElementSetMemberRestriction.class);
				for (int j = 0; j < membersJson.size(); j++)
				{
					RestrictedJson<ElementSetMemberRestriction> memberJson = membersJson.getMemberAt(j);
					String name = memberJson.getString(ElementSetMemberRestriction.NAME);
					if (dataName.equals(name))
						return this.getSetValue(i, j);
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
		
		public Integer getNumberIDByName(String numberName)
		{
			JsonEntityArray<RestrictedJson<ElementNumberRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_NUMBERS, ElementNumberRestriction.class);
			for (int i = 0; i < numberValues.size(); i++)
			{
				RestrictedJson<ElementNumberRestriction> elementNumber = elementJson.getMemberAt(i);
				String name = elementNumber.getString(ElementNumberRestriction.NAME);
				if (numberName.equals(name))
					return i;
			}
			return null;
		}
		
		public Integer getNumberValueByName(String numberName)
		{
			Integer id = this.getNumberIDByName(numberName);
			if (id == null)
				return null;
			return this.numberValues.get(id);
		}
		
		public String renderAsString()
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < detailValues.size(); i++)
			{
				RestrictedJson<ElementDataRestriction> elementData = elementJson.getMemberAt(i);
				String name = elementData.getString(ElementDataRestriction.NAME);
				JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
				JsonEntityString value = options.getMemberAt(this.detailValues.get(i));
				
				stringBuilder.append(name + " : " + value.renderAsString() + "\r\n");
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
