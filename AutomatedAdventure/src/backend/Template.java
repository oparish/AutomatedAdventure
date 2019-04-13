package backend;

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
import main.Main;

public class Template
{
	String templateString;
	Pattern elementPattern = Pattern.compile("\\[element:(.*?):(.*?)\\]");
	Pattern statePattern = Pattern.compile("\\[state:(.*?)\\]");
	
	public Template(String templateString)
	{
		this.templateString = templateString;
	}
	
	public String getAlteredTemplateString(HashMap<String, ElementInstance> elementInstances, HashMap<String, State> states)
	{
		String alteredTemplateString = this.templateString;
		Matcher elementMatcher = elementPattern.matcher(alteredTemplateString);
		while(elementMatcher.find())
		{
				String replacement = this.getElementReplacement(elementMatcher.group(1), elementMatcher.group(2), elementInstances);
				alteredTemplateString = alteredTemplateString.replace(elementMatcher.group(0), replacement);
		}
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
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		JsonEntityArray<RestrictedJson<RoomRestriction>> rooms = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ROOMS, RoomRestriction.class);
		RestrictedJson<RoomRestriction> room = rooms.getMemberAt(0);
		JsonEntityArray<JsonEntityString> templates = room.getStringArray(RoomRestriction.TEMPLATES);
		JsonEntityString templateString = templates.getMemberAt(0);
		Template template = new Template(templateString.renderAsString());
		
		JsonEntityArray<RestrictedJson<ElementRestriction>> elements = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.ELEMENTS, ElementRestriction.class);
		RestrictedJson<ElementRestriction> elementJson = elements.getMemberAt(0);
		Element element = new Element(elementJson, 1);
		
		HashMap<String, ElementInstance> instances = new HashMap<String, ElementInstance>();
		instances.put(element.getName(), element.getInstance(0));
		
		JsonEntityArray<RestrictedJson<StateRestriction>> states = scenarioJson.getRestrictedJsonArray(ScenarioRestriction.STATES, StateRestriction.class);
		RestrictedJson<StateRestriction> stateJson = states.getMemberAt(0);
		State state = new State(stateJson, 0);
		HashMap<String, State> stateMap = new HashMap<String, State>();
		JsonEntityString nameJson = stateJson.getJsonEntityString(StateRestriction.NAME);
		stateMap.put(nameJson.renderAsString(), state);
		
		String result = template.getAlteredTemplateString(instances, stateMap);
		System.out.println(result);
	}
}
