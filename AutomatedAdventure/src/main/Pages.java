package main;

import java.io.Console;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import javax.json.JsonObject;
import javax.swing.ToolTipManager;

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
import json.restrictions.PackageRestriction;
import json.restrictions.PageRestriction;
import json.restrictions.PanelRestriction;
import json.restrictions.RedirectRestriction;
import json.restrictions.ScenarioRestriction;

public class Pages
{
	private static final String MAIN = "main";
	private static final String INITIAL = "initial";
	private static Scenario scenario;
	private static PageWindow pageWindow;
	
	public static void main(String[] args) throws Exception
	{
		ToolTipManager.sharedInstance().setInitialDelay(0);
		
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = 
				new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		Pages.scenario = new Scenario(scenarioJson);	
		
		JsonEntityMap<RestrictedJson<PackageRestriction>> packageMap = 
				scenarioJson.getRestrictedJsonMap(ScenarioRestriction.PACKAGEMAP, PackageRestriction.class);
		RestrictedJson<PackageRestriction> packageRestriction = packageMap.getMemberBy(MAIN);
		JsonEntityMap<RestrictedJson<PageRestriction>> pageTemplateMap = 
				packageRestriction.getRestrictedJsonMap(PackageRestriction.PAGES, PageRestriction.class);
		RestrictedJson<PageRestriction> pageJson = pageTemplateMap.getMemberBy(INITIAL);
		PageInstance pageInstance = new PageInstance(Pages.scenario, new PageContext(INITIAL), pageJson, null);
		Pages.scenario.setCurrentPackage(MAIN);
		JsonEntityMap<RestrictedJson<PanelRestriction>> panelMap = 
				scenarioJson.getRestrictedJsonMap(ScenarioRestriction.PANELS, PanelRestriction.class);
		
		Pages.pageWindow = new PageWindow(Pages.scenario, panelMap);
		Pages.pageWindow.update(pageInstance);
		Pages.pageWindow.showWindow();
	}
	
	public static Scenario getScenario()
	{
		return Pages.scenario;
	}
	
	public static boolean checkNumberComparison(ElementInstance elementInstance, String comparatorText, String elementNumberName, int value) throws Exception
	{
		Comparator comparator = Comparator.fromText(comparatorText);		
		int elementNumber = elementInstance.getNumberValueByName(elementNumberName);
		return Pages.checkComparison(comparator, elementNumber, value);
	}
	
	public static boolean checkStringComparison(ElementInstance elementInstance, String elementQualityName, String qualityValue) throws Exception
	{	
		String quality = elementInstance.getDetailValueByName(elementQualityName);
		return quality.equals(qualityValue);
	}
	
	public static boolean checkComparison(Comparator comparator, int firstValue, int secondValue) throws Exception
	{
		switch(comparator)
		{
			case GREATER_THAN:
				return (firstValue > secondValue);
			case GREATER_THAN_OR_EQUAL:
				return (firstValue >= secondValue);
			case LESS_THAN:
				return (firstValue < secondValue);
			case LESS_THAN_OR_EQUAL:
				return (firstValue <= secondValue);
			case EQUAL:
				return (firstValue == secondValue);
			case NOT_EQUAL:
				return (firstValue != secondValue);
		}
		throw new Exception("Unrecognised comparator type.");
	}
	
	public static PageWindow getPageWindow()
	{
		return Pages.pageWindow;
	}
}
