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
	
	public Element(RestrictedJson<ElementRestriction> elementJson, int instances)
	{
		this.elementJson = elementJson;
		for (int i = 0; i < instances; i++)
		{
			ArrayList<Integer> values = new ArrayList<Integer>();
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementData = 
					this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			for (int j = 0; j < elementData.getLength(); j++)
			{
				RestrictedJson<ElementDataRestriction> elementDetail = elementData.getRestrictedJson(j, ElementDataRestriction.class);
				JsonEntityArray<JsonEntityString> options = elementDetail.getStringArray(ElementDataRestriction.OPTIONS);
				values.add(options.getRandomIndex());			
			}	
			this.instances.add(new ElementInstance(values));
		}
	}

	public String getName()
	{
		return this.elementJson.getJsonEntityString(ElementRestriction.NAME).renderAsString();
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
		
		public String getValue(int index)
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			RestrictedJson<ElementDataRestriction> elementData = elementJson.getRestrictedJson(index, ElementDataRestriction.class);
			JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
			JsonEntityString value = options.getJsonEntityString(this.values.get(index));
			return value.renderAsString();
		}
		
		public String getValueByName(String dataName)
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = Element.this.elementJson.getRestrictedJsonArray(ElementRestriction.ELEMENT_DATA, ElementDataRestriction.class);
			for (int i = 0; i < values.size(); i++)
			{
				RestrictedJson<ElementDataRestriction> elementData = elementJson.getRestrictedJson(i, ElementDataRestriction.class);
				JsonEntityString name = elementData.getJsonEntityString(ElementDataRestriction.NAME);
				if (dataName.equals(name.renderAsString()))
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
				RestrictedJson<ElementDataRestriction> elementData = elementJson.getRestrictedJson(i, ElementDataRestriction.class);
				JsonEntityString name = elementData.getJsonEntityString(ElementDataRestriction.NAME);
				JsonEntityArray<JsonEntityString> options = elementData.getStringArray(ElementDataRestriction.OPTIONS);				
				JsonEntityString value = options.getJsonEntityString(this.values.get(i));
				
				stringBuilder.append(name.renderAsString() + " : " + value.renderAsString() + "\r\n");
			}	
			return stringBuilder.toString();
		}
	}
	
	public static void main(String[] args)
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		JsonEntityArray<RestrictedJson<ElementRestriction>> elements = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ELEMENTS, ElementRestriction.class);
		RestrictedJson<ElementRestriction> elementJson = elements.getRestrictedJson(0, ElementRestriction.class);
		Element element = new Element(elementJson, 1);
		System.out.println(element.getInstance(0).renderAsString());
	}
}
