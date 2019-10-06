package main;

import java.util.HashMap;

import javax.json.JsonObject;

import backend.Scenario;
import backend.pages.PageInstance;
import frontEnd.PageWindow;
import json.JsonEntityMap;
import json.JsonEntityString;
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
		
		JsonEntityMap<JsonEntityString> pageTemplateMap = scenarioJson.getStringMap(ScenarioRestriction.PAGETEMPLATES);
		String pageTemplate = pageTemplateMap.getMemberBy("initial").renderAsString();
		PageInstance pageInstance = new PageInstance(pageTemplate);
		
		PageWindow pageWindow = new PageWindow();
		pageWindow.showWindow();
		pageWindow.update(pageInstance.getText());
	}
}
