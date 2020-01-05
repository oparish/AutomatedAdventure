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
	private static Scenario scenario;
	private static PageWindow pageWindow;
	
	public static void main(String[] args)
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = 
				new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		Pages.scenario = new Scenario(scenarioJson);	
		
		JsonEntityMap<JsonEntityString> pageTemplateMap = scenarioJson.getStringMap(ScenarioRestriction.PAGETEMPLATES);
		String pageTemplate = pageTemplateMap.getMemberBy("initial").renderAsString();
		PageInstance pageInstance = new PageInstance(Pages.scenario, pageTemplate);
		
		Pages.pageWindow = new PageWindow(Pages.scenario);
		Pages.pageWindow.showWindow();
		Pages.pageWindow.update(pageInstance);
	}
	
	public static void loadPage(String key)
	{
		String pageTemplate = Pages.scenario.getPageTemplate(key);
		PageInstance pageInstance = new PageInstance(Pages.scenario, pageTemplate);
		Pages.pageWindow.update(pageInstance);
	}
	
}
