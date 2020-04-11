package backend;

import java.util.ArrayList;
import javax.json.JsonObject;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ElementDataRestriction;
import json.restrictions.ElementNumberRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.ScenarioRestriction;
import main.Main;

public class Element
{
	RestrictedJson<ElementRestriction> elementJson;
	ArrayList<ElementInstance> instances = new ArrayList<ElementInstance>();
	
	public ArrayList<ElementInstance> getInstances() {
		return instances;
	}

	public Element(RestrictedJson<ElementRestriction> elementJson)
	{
		this.elementJson = elementJson;
	}
	
	public void makeInstances(int number) throws Exception
	{	
		int existingInstances = this.instances.size();
		for (int i = 0; i < number; i++)
		{
			this.makeInstance(number, existingInstances);
		}
	}
	
	private void makeInstance(int number, int existingInstances) throws Exception
	{
		ArrayList<Integer> detailValues = this.getElementDataValues(number, existingInstances);
		ArrayList<Integer> numberValues = this.getElementNumberValues();
		this.instances.add(new ElementInstance(detailValues, numberValues));
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
			int diff = maxValue - minValue;
			int result = minValue + Main.getRndm(diff);
			values.add(result);
		}
		
		return values;
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
				ArrayList<Integer> valueIndexList = new ArrayList<Integer>();
				
				for (int k = 0; k < options.size(); k++)
				{
					valueIndexList.add(k);
				}
				
				for (ElementInstance instance : this.instances)
				{
					int instanceValueIndex = instance.detailValues.get(j);
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
				values.add(valueIndexList.get(rnd));
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
		
		private ElementInstance(ArrayList<Integer> detailValues, ArrayList<Integer> numberValues)
		{
			this.detailValues = detailValues;
			this.numberValues = numberValues;
		}
		
		public Element getElement()
		{
			return Element.this;
		}
		
		public void adjustNumber(String elementNumber, int value)
		{
			Integer id = this.getNumberIDByName(elementNumber);
			int number = this.numberValues.get(id);
			number += value;
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
