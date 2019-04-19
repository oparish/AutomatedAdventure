package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.JsonObject;

import backend.Element.ElementInstance;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.ElementRestriction;
import json.restrictions.RoomRestriction;
import json.restrictions.ScenarioRestriction;
import json.restrictions.StateRestriction;
import json.restrictions.TemplateRestriction;
import main.Main;

public class Template
{
	RestrictedJson<TemplateRestriction> templateJson;
	Pattern elementPattern = Pattern.compile("\\[element:(.*?):(.*?)\\]");
	Pattern statePattern = Pattern.compile("\\[state:(.*?)\\]");
	Pattern changeElementPattern = Pattern.compile("\\[changeElement:(.*?)\\]");
	Pattern intervalPattern = Pattern.compile("\\[interval\\]");
	
	public Template(RestrictedJson<TemplateRestriction> templateJson)
	{
		this.templateJson = templateJson;
	}
	
	public String getAlteredTemplateString(HashMap<String, ElementInstance> elementInstances, HashMap<String, State> states, 
			Interval interval)
	{
		String alteredTemplateString = this.getContent();
		alteredTemplateString = this.checkForElementChanges(elementInstances, alteredTemplateString);
		alteredTemplateString = this.checkForIntervals(interval, alteredTemplateString);
		alteredTemplateString = this.checkForElements(elementInstances, alteredTemplateString);
		alteredTemplateString = this.checkForStates(states, alteredTemplateString);
		return alteredTemplateString;
	}
	
	public Chance getChance(Scenario scenario)
	{
		return scenario.getChance(this.getChanceName());
	}
	
	private String getContent()
	{
		return this.templateJson.getJsonEntityString(TemplateRestriction.CONTENT).renderAsString();
	}
	
	private String getChanceName()
	{
		return this.templateJson.getJsonEntityString(TemplateRestriction.CHANCE_NAME).renderAsString();
	}
	
	
	private String checkForIntervals(Interval interval, String alteredTemplateString)
	{
		Matcher intervalMatcher = intervalPattern.matcher(alteredTemplateString);
		return intervalMatcher.replaceAll(interval.getName());
	}
	
	private String checkForElementChanges(HashMap<String, ElementInstance> elementInstances, String alteredTemplateString)
	{
		Matcher changeElementMatcher = changeElementPattern.matcher(alteredTemplateString);
		while(changeElementMatcher.find())
		{
			String elementName = changeElementMatcher.group(1);
			ElementInstance elementInstance = elementInstances.get(elementName);
			Element element = elementInstance.getElement();
			elementInstances.put(element.getName(), element.getRandomInstance());
			alteredTemplateString = alteredTemplateString.replace(changeElementMatcher.group(0), "");
		}
		return alteredTemplateString;
	}
	
	private String checkForElements(HashMap<String, ElementInstance> elementInstances, String alteredTemplateString)
	{
		Matcher elementMatcher = elementPattern.matcher(alteredTemplateString);
		while(elementMatcher.find())
		{
				String replacement = this.getElementReplacement(elementMatcher.group(1), elementMatcher.group(2), elementInstances);
				alteredTemplateString = alteredTemplateString.replace(elementMatcher.group(0), replacement);
		}
		return alteredTemplateString;
	}
	
	private String checkForStates(HashMap<String, State> states, String alteredTemplateString)
	{
		Matcher stateMatcher = statePattern.matcher(alteredTemplateString);
		while(stateMatcher.find())
		{
				String replacement = this.getStateReplacement(stateMatcher.group(1), states);
				alteredTemplateString = alteredTemplateString.replace(stateMatcher.group(0), replacement);
		}
		return alteredTemplateString;
	}
	
	private String getElementReplacement(String elementType, String elementData, HashMap<String, ElementInstance> elementInstances)
	{
		ElementInstance elementInstance = elementInstances.get(elementType);
		String replacement = elementInstance.getValueByName(elementData);
		return replacement;
	}
	
	private String getStateReplacement(String stateName, HashMap<String, State> states)
	{
		State state = states.get(stateName);
		String replacement = state.getValue();
		return replacement;
	}
	
	public static void main(String[] args)
	{
//		JsonObject jsonObject = Main.openJsonFile(null);
//		RestrictedJson<ScenarioRestriction> scenarioJson = new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
//		JsonEntityArray<RestrictedJson<RoomRestriction>> rooms = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ROOMS, RoomRestriction.class);
//		RestrictedJson<RoomRestriction> room = rooms.getMemberAt(0);
//		JsonEntityArray<JsonEntityString> templates = room.getStringArray(RoomRestriction.TEMPLATES);
//		JsonEntityString templateString = templates.getMemberAt(0);
//		Template template = new Template(templateString.renderAsString());
//		
//		JsonEntityArray<RestrictedJson<ElementRestriction>> elements = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ELEMENTS, ElementRestriction.class);
//		RestrictedJson<ElementRestriction> elementJson = elements.getMemberAt(0);
//		Element element = new Element(elementJson, 1);
//		
//		HashMap<String, ElementInstance> instances = new HashMap<String, ElementInstance>();
//		instances.put(element.getName(), element.getInstance(0));
//		
//		JsonEntityArray<RestrictedJson<StateRestriction>> states = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.STATES, StateRestriction.class);
//		RestrictedJson<StateRestriction> stateJson = states.getMemberAt(0);
//		State state = new State(stateJson, 0);
//		HashMap<String, State> stateMap = new HashMap<String, State>();
//		JsonEntityString nameJson = stateJson.getJsonEntityString(StateRestriction.NAME);
//		stateMap.put(nameJson.renderAsString(), state);
//		
//		String result = template.getAlteredTemplateString(instances, stateMap);
//		System.out.println(result);
	}
}
