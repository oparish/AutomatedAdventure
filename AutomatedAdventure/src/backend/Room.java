package backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import backend.Element.ElementInstance;
import frontEnd.RoomPanel;
import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.RestrictionType;
import json.restrictions.RoomRestriction;
import json.restrictions.TemplateRestriction;
import main.Main;

public class Room
{
	private RestrictedJson<RoomRestriction> roomJson;
	private HashMap<Chance, ArrayList<Template>> templates = new HashMap<Chance, ArrayList<Template>>();
	HashMap<String, ElementInstance> elementInstances = new HashMap<String, ElementInstance>();
	
	public HashMap<String, ElementInstance> getElementInstances() {
		return elementInstances;
	}

	public Room(RestrictedJson<RoomRestriction> roomJson, Scenario scenario, HashMap<String, ElementInstance> elementInstances)
	{
		this.roomJson = roomJson;
		this.elementInstances = elementInstances;
		this.loadTemplates(scenario);
	}
	
	private void loadTemplates(Scenario scenario)
	{
		JsonEntityArray<RestrictedJson<TemplateRestriction>> templates = 
				this.roomJson.getRestrictedJsonArray(RoomRestriction.TEMPLATES, TemplateRestriction.class);
		for (int i = 0; i < templates.getLength(); i++)
		{
			RestrictedJson<TemplateRestriction> templateJson = templates.getMemberAt(i);
			Template template = new Template(scenario, templateJson);
			Chance chance = template.getChance(scenario);
			
			ArrayList<Template> templateList = this.templates.get(chance);
			if (templateList == null)
			{
				templateList = new ArrayList<Template>();
				this.templates.put(chance, templateList);
			}		
			templateList.add(template);
		}
	}
	
	public ElementInstance getElementInstance(Element element)
	{
		return this.getElementInstance(element);
	}
	
	public void setElementInstance(Element element, ElementInstance elementInstance)
	{
		this.elementInstances.put(element.getName(), elementInstance);
	}
	
	public Template getRandomTemplate(Scenario scenario)
	{
		int range = scenario.getChanceRange();
		int random = Main.getRndm(range);
		int number = 0;
		Chance chosenChance = null;
		for (Chance chance : scenario.getChanceList())
		{
			int percent = chance.getPercentage();
			number += percent;
			if (random < number)
			{
				chosenChance = chance;
				break;
			}
		}
			
		ArrayList<Template> templateList = this.getCheckedTemplateList(scenario, chosenChance);

		while (templateList == null || templateList.isEmpty())
		{
			chosenChance = scenario.getChanceByPriority(chosenChance.getPriority() + 1);
			templateList = this.getCheckedTemplateList(scenario, chosenChance);
		}	
		
		int randomIndex = Main.getRndm(templateList.size());
		return templateList.get(randomIndex);
	}
	
	private ArrayList<Template> getCheckedTemplateList(Scenario scenario, Chance chosenChance)
	{
		ArrayList<Template> checkedTemplateList = new ArrayList<Template>();
		ArrayList<Template> originalTemplateList = this.templates.get(chosenChance);
		
		if (originalTemplateList == null)
			return null;
		
		for (Template template : originalTemplateList)
		{
			if (template.checkConditions(scenario))
			{
				checkedTemplateList.add(template);
			}
		}
		
		return checkedTemplateList;
	}

	public String getName()
	{
		return this.roomJson.getJsonEntityString(RoomRestriction.NAME);
	}
	
	public static Room getRoom(RestrictedJson<RoomRestriction> roomJson, Scenario scenario, HashMap<String, ElementInstance> elementInstances)
	{
		RestrictionType restrictionType = roomJson.getSubType();
		switch(restrictionType)
		{
			case CHALLENGEROOM:
				return new ChallengeRoom(roomJson, scenario, elementInstances);
			case TIMEDROOM:
				return new TimedRoom(roomJson, scenario, elementInstances);
			default:
				return null;
		}
	}
	
}
