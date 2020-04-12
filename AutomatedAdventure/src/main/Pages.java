package main;

import java.util.HashMap;

import javax.json.JsonObject;

import backend.Scenario;
import backend.pages.ElementChoice;
import backend.pages.PageContext;
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
	
	public static void main(String[] args) throws Exception
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = 
				new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		Pages.scenario = new Scenario(scenarioJson);	
		
		JsonEntityMap<JsonEntityString> pageTemplateMap = scenarioJson.getStringMap(ScenarioRestriction.PAGETEMPLATES);
		String pageTemplate = pageTemplateMap.getMemberBy("initial").renderAsString();
		PageInstance pageInstance = new PageInstance(Pages.scenario, new PageContext(), pageTemplate);
		
		Pages.pageWindow = new PageWindow(Pages.scenario);
		Pages.pageWindow.showWindow();
		Pages.pageWindow.update(pageInstance);
	}
	
	public static void loadPage(ElementChoice elementChoice)
	{
		String pageTemplate = Pages.scenario.getPageTemplate(elementChoice.keyword);
		PageContext pageContext = new PageContext();
		if (elementChoice.elementInstance != null)
			pageContext.addElementInstance(elementChoice.elementInstance);
		PageInstance pageInstance = new PageInstance(Pages.scenario, pageContext, pageTemplate);
		try {
			Pages.pageWindow.update(pageInstance);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
