package backend;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.json.JsonObject;

import frontEnd.RoomPanel;
import frontEnd.RoomsWindow;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ElementDataRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.RestrictionType;
import json.restrictions.RoomRestriction;
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
					(JsonEntityArray<RestrictedJson<ElementDataRestriction>>) this.elementJson.getChild(ElementRestriction.ELEMENT_DATA);
			for (int j = 0; j < elementData.getLength(); j++)
			{
				RestrictedJson<ElementDataRestriction> elementDetail = (RestrictedJson<ElementDataRestriction>) elementData.getEntityAt(i);
				JsonEntityArray<JsonEntityString> options = (JsonEntityArray<JsonEntityString>) elementDetail.getChild(ElementDataRestriction.OPTIONS);
				String name = ((JsonEntityString) elementDetail.getChild(ElementDataRestriction.NAME)).renderAsString();
				values.add(options.getRandomIndex());			
			}	
			this.instances.add(new ElementInstance(values));
		}
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
		
		public String renderAsString()
		{
			JsonEntityArray<RestrictedJson<ElementDataRestriction>> elementJson = 
					(JsonEntityArray<RestrictedJson<ElementDataRestriction>>) Element.this.elementJson.getChild(ElementRestriction.ELEMENT_DATA);
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < values.size(); i++)
			{
				RestrictedJson<ElementDataRestriction> elementData = (RestrictedJson<ElementDataRestriction>) elementJson.getEntityAt(i);
				JsonEntityString name = (JsonEntityString) elementData.getChild(ElementDataRestriction.NAME);
				JsonEntityArray<JsonEntityString> options = (JsonEntityArray<JsonEntityString>) elementData.getChild(ElementDataRestriction.OPTIONS);				
				JsonEntityString value = (JsonEntityString) options.getEntityAt(this.values.get(i));
				
				stringBuilder.append(name.renderAsString() + " : " + value.renderAsString() + "\r\n");
			}	
			return stringBuilder.toString();
		}
	}
	
	public static void main(String[] args)
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		JsonEntityArray<RestrictedJson<ElementRestriction>> elements = (JsonEntityArray<RestrictedJson<ElementRestriction>> ) scenarioJson.getChild(ScenarioRestriction.ELEMENTS);
		RestrictedJson<ElementRestriction> elementJson = (RestrictedJson<ElementRestriction>) elements.getEntityAt(0);
		Element element = new Element(elementJson, 1);
		System.out.println(element.getInstance(0).renderAsString());
	}
}
