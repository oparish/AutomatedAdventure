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
import json.restrictions.ConditionRestriction;
import json.restrictions.ElementRestriction;
import json.restrictions.ScenarioRestriction;
import json.restrictions.StateRestriction;
import json.restrictions.TemplateRestriction;
import json.restrictions.room.RoomRestriction;
import main.Main;

public class Template
{
	RestrictedJson<TemplateRestriction> templateJson;
	ArrayList<Condition> conditions = new ArrayList<Condition>();
	
	private static final Pattern elementPattern = Pattern.compile("\\[element:(.*?):(.*?)\\]");
	private static final Pattern statePattern = Pattern.compile("\\[state:(.*?)\\]");
	private static final Pattern changeStatePattern = Pattern.compile("\\[changeState:(.*?):(.*?)\\]");
	private static final Pattern changeElementPattern = Pattern.compile("\\[changeElement:(.*?)\\]");
	private static final Pattern intervalPattern = Pattern.compile("\\[interval\\]");
	
	public Template(Scenario scenario, RestrictedJson<TemplateRestriction> templateJson)
	{
		this.templateJson = templateJson;
		this.loadConditions(scenario);
	}
	
	public boolean checkConditions(Scenario scenario)
	{
		for (Condition condition : this.conditions)
		{
			if (!condition.check(scenario))
				return false;
		}
		return true;
	}
	
	private void loadConditions(Scenario scenario)
	{
		JsonEntityArray<RestrictedJson<ConditionRestriction>> conditions = 
				this.templateJson.getRestrictedJsonArray(TemplateRestriction.CONDITIONS, ConditionRestriction.class);
		for (int i = 0; i < conditions.getLength(); i++)
		{
			RestrictedJson<ConditionRestriction> conditionJson = conditions.getMemberAt(i);
			String typeString = conditionJson.getString(ConditionRestriction.TYPE);
			String valueString = conditionJson.getString(ConditionRestriction.VALUE);
			this.conditions.add(Condition.createCondition(scenario, typeString, valueString));
		}
	}
	
	public String getAlteredTemplateString(HashMap<String, ElementInstance> elementInstances, HashMap<String, State> states, 
			Interval interval)
	{
		String alteredTemplateString = this.getContent();
		alteredTemplateString = this.checkForElementChanges(elementInstances, alteredTemplateString);
		alteredTemplateString = this.checkForStateChanges(states, alteredTemplateString);
		alteredTemplateString = this.checkForIntervals(interval, alteredTemplateString);
		alteredTemplateString = this.checkForElements(elementInstances, alteredTemplateString);
		alteredTemplateString = this.checkForStates(states, alteredTemplateString);
		return alteredTemplateString;
	}
	
	private String checkForStateChanges(HashMap<String, State> states, String alteredTemplateString)
	{
		Matcher changeStateMatcher = changeStatePattern.matcher(alteredTemplateString);
		while(changeStateMatcher.find())
		{
			String stateName = changeStateMatcher.group(1);
			String newStateValue = changeStateMatcher.group(2);
			State state = states.get(stateName);
			state.changeStateValue(newStateValue);
			alteredTemplateString = alteredTemplateString.replace(changeStateMatcher.group(0), "");
		}
		return alteredTemplateString;
	}
	
	public Chance getChance(Scenario scenario)
	{
		return scenario.getChance(this.getChanceName());
	}
	
	private String getContent()
	{
		return this.templateJson.getString(TemplateRestriction.CONTENT);
	}
	
	private String getChanceName()
	{
		return this.templateJson.getString(TemplateRestriction.CHANCE_NAME);
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
			elementInstances.put(elementName, element.getRandomInstance());
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
		String replacement = elementInstance.getDetailValueByName(elementData);
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
