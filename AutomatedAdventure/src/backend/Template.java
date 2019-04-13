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
import main.Main;

public class Template
{
	private static final String ELEMENT = "element"; 
	
	String templateString;
	Pattern replacePattern = Pattern.compile("\\[(.*?):(.*?):(.*?)\\]");
	
	public Template(String templateString)
	{
		this.templateString = templateString;
	}
	
	public String getAlteredTemplateString(HashMap<String, ElementInstance> elementInstances)
	{
		String alteredTemplateString = this.templateString;
		Matcher matcher = replacePattern.matcher(alteredTemplateString);
		while(matcher.find())
		{
			if (matcher.group(1).equals(ELEMENT))
			{
				String replacement = this.getElementReplacement(matcher.group(2), matcher.group(3), elementInstances);
				alteredTemplateString = alteredTemplateString.replace(matcher.group(0), replacement);
			}

		}
		return alteredTemplateString;
	}
	
	private String getElementReplacement(String elementType, String elementData, HashMap<String, ElementInstance> elementInstances)
	{
		ElementInstance elementInstance = elementInstances.get(elementType);
		String replacement = elementInstance.getValueByName(elementData);
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
		
		String result = template.getAlteredTemplateString(instances);
		System.out.println(result);
	}
}
