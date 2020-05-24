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
import json.restrictions.PageRestriction;
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
		
		JsonEntityMap<RestrictedJson<PageRestriction>> pageTemplateMap = scenarioJson.getRestrictedJsonMap(ScenarioRestriction.PAGES, PageRestriction.class);
		RestrictedJson<PageRestriction> pageJson = pageTemplateMap.getMemberBy("initial");
		String pageValue = pageJson.getString(PageRestriction.VALUE);
		PageInstance pageInstance = new PageInstance(Pages.scenario, new PageContext(), pageValue);
		
		Pages.pageWindow = new PageWindow();
		Pages.pageWindow.showWindow();
		Pages.pageWindow.update(pageInstance);
	}
	
	public static void loadPage(ElementChoice elementChoice)
	{
		String pageTemplate = Pages.scenario.getPageTemplate(elementChoice.keyword);
		PageContext pageContext;
		
		if (elementChoice.context != null)
			pageContext = elementChoice.context;
		else
			pageContext = new PageContext();
		
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
