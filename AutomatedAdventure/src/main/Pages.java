package main;

import javax.json.JsonObject;

import backend.Scenario;
import frontEnd.PageWindow;
import json.RestrictedJson;
import json.restrictions.ScenarioRestriction;

public class Pages
{
	private Scenario scenario;
	
	public static void main(String[] args)
	{
		new Pages();
	}
	
	public Pages()
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = 
				new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		this.scenario = new Scenario(scenarioJson);	
		
		PageWindow pageWindow = new PageWindow();
		pageWindow.showWindow();
		pageWindow.update("TEST");
	}
}
