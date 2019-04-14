package backend;

import java.util.ArrayList;
import java.util.HashMap;

import backend.Element.ElementInstance;
import frontEnd.RoomPanel;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.RoomRestriction;
import main.Main;

public class Room
{
	private RestrictedJson<RoomRestriction> roomJson;
	private ArrayList<Template> templates = new ArrayList<Template>();
	HashMap<String, ElementInstance> elementInstances = new HashMap<String, ElementInstance>();
	
	public HashMap<String, ElementInstance> getElementInstances() {
		return elementInstances;
	}

	public Room(RestrictedJson<RoomRestriction> roomJson, HashMap<String, ElementInstance> elementInstances)
	{
		this.roomJson = roomJson;
		this.elementInstances = elementInstances;
		this.loadTemplates();
	}
	
	private void loadTemplates()
	{
		JsonEntityArray<JsonEntityString> templateJson = this.roomJson.getStringArray(RoomRestriction.TEMPLATES);
		for (int i = 0; i < templateJson.getLength(); i++)
		{
			String templateString = templateJson.getMemberAt(i).renderAsString();
			this.templates.add(new Template(templateString));
		}
	}
	
	public ElementInstance getElementInstance(Element element)
	{
		return this.getElementInstance(element);
	}
	
	public void setElementInstance(Element element, ElementInstance elementInstance)
	{
		this.elementInstances.put(element.getName(), elementInstance);
	}
	
	public Template getRandomTemplate()
	{
		int randomIndex = Main.getRndm(templates.size());
		return this.templates.get(randomIndex);
	}

	public String getName()
	{
		return this.roomJson.getJsonEntityString(RoomRestriction.NAME).renderAsString();
	}
	
}
