package main;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import javax.json.JsonObject;

import backend.Element;
import backend.NumberRange;
import backend.Scenario;
import backend.Element.ElementInstance;
import backend.pages.Comparator;
import backend.pages.ElementChoice;
import backend.pages.PageContext;
import backend.pages.PageInstance;
import frontEnd.PageWindow;
import json.JsonEntityMap;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.PageRestriction;
import json.restrictions.RedirectRestriction;
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
		PageInstance pageInstance = new PageInstance(Pages.scenario, new PageContext(), pageJson);
		
		Pages.pageWindow = new PageWindow();
		Pages.pageWindow.showWindow();
		Pages.pageWindow.update(pageInstance);
	}
	
	public static Scenario getScenario()
	{
		return Pages.scenario;
	}
	
	public static boolean checkComparison(ElementInstance elementInstance, String comparatorText, String elementNumberName, int value) throws Exception
	{
		Comparator comparator = Comparator.fromText(comparatorText);		
		int elementNumber = elementInstance.getNumberValueByName(elementNumberName);
		return Pages.checkComparison(comparator, elementNumber, value);
	}
	
	public static boolean checkComparison(Comparator comparator, int elementNumber, int value) throws Exception
	{
		switch(comparator)
		{
			case GREATER_THAN:
				return (elementNumber > value);
			case GREATER_THAN_OR_EQUAL:
				return (elementNumber >= value);
			case LESS_THAN:
				return (elementNumber < value);
			case LESS_THAN_OR_EQUAL:
				return (elementNumber <= value);
			case EQUAL:
				return (elementNumber == value);
			case NOT_EQUAL:
				return (elementNumber != value);
		}
		throw new Exception("Unrecognised comparator type.");
	}
	
	public static PageWindow getPageWindow()
	{
		return Pages.pageWindow;
	}
}
