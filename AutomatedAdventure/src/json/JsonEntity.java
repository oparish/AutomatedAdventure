package json;

import javax.json.JsonObject;

import json.restrictions.ScenarioRestriction;
import main.Main;

public interface JsonEntity
{

	public String renderAsString();
	
	public static void main(String[] args)
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson restrictedJson = new RestrictedJson(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		System.out.println(restrictedJson.renderAsString());
	}
}
