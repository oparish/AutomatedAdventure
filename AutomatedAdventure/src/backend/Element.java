package backend;

import java.util.ArrayList;
import javax.json.JsonObject;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ElementDataRestriction;
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
						int instanceValueIndex = instance.values.get(j);
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
			this.instances.add(new ElementInstance(values));
		}
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
		ArrayList<Integer> values;
		
		private ElementInstance(ArrayList<Integer> values)
		{
			this.values = values;
		}
		
		public Element getElement()
		{
			return Element.this;
		}
		
		public String getValue(int index)
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			RestrictedJson<ElementDataRestriction> elementData = elementJson.getMemberAt(index);
			JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
			JsonEntityString value = options.getMemberAt(this.values.get(index));
			return value.renderAsString();
		}
		
		public String getValueByName(String dataName)
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			for (int i = 0; i < values.size(); i++)
			{
				RestrictedJson<ElementDataRestriction> elementData = elementJson.getMemberAt(i);
				String name = elementData.getString(ElementDataRestriction.NAME);
				if (dataName.equals(name))
					return this.getValue(i);
			}
			return null;
		}
		
		public String renderAsString()
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < values.size(); i++)
			{
				RestrictedJson<ElementDataRestriction> elementData = elementJson.getMemberAt(i);
				String name = elementData.getString(ElementDataRestriction.NAME);
				JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
				JsonEntityString value = options.getMemberAt(this.values.get(i));
				
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
