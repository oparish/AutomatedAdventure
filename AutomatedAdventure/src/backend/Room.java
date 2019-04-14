package backend;

import java.util.ArrayList;

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
	
	public Room(RestrictedJson<RoomRestriction> roomJson)
	{
		this.roomJson = roomJson;
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
