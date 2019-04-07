package backend;

import javax.json.JsonObject;

import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.RoomRestriction;
import json.restrictions.ScenarioRestriction;
import main.Main;

public class Template
{
	String templateString;
	
	public Template(String templateString)
	{
		this.templateString = templateString;
	}
	
	public static void main(String[] args)
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		JsonEntityArray<RestrictedJson<RoomRestriction>> rooms = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ROOMS, RoomRestriction.class);
		RestrictedJson<RoomRestriction> room = rooms.getRestrictedJson(0, RoomRestriction.class);
		JsonEntityArray<JsonEntityString> templates = room.getStringArray(RoomRestriction.TEMPLATES);
		JsonEntityString template = templates.getJsonEntityString(0);
		System.out.println(template.renderAsString());
	}
}
