package main;

import static json.restrictions.ScenarioRestriction.ENDINGS;
import static json.restrictions.component.EndingRestriction.COMPONENT_NAME;
import static json.restrictions.component.EndingRestriction.COMPONENT_STATE_NAME;

import java.util.ArrayList;
import java.util.HashMap;

import javax.json.JsonObject;

import backend.Scenario;
import backend.component.ComponentInstance;
import json.JsonEntityArray;
import json.JsonEntityMap;
import json.RestrictedJson;
import json.restrictions.ScenarioRestriction;
import json.restrictions.component.ComponentChangeRestriction;
import json.restrictions.component.ComponentRestriction;
import json.restrictions.component.ComponentStateRestriction;
import json.restrictions.component.EndingRestriction;
import json.restrictions.component.TriggerRestriction;

public class Components
{
	RestrictedJson<ScenarioRestriction> scenarioJson;
	HashMap<String, RestrictedJson<ComponentRestriction>> componentMap;
	
	public static void main(String[] args)
	{
		Components components = new Components();
	}
	
	public Components()
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		this.scenarioJson = 
				new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		
		JsonEntityMap<RestrictedJson<ComponentRestriction>> componentMap = scenarioJson.getRestrictedJsonMap(ScenarioRestriction.COMPONENTS, ComponentRestriction.class);
		JsonEntityArray<RestrictedJson<EndingRestriction>> endings = scenarioJson.getRestrictedJsonArray(ENDINGS, EndingRestriction.class);
		RestrictedJson<EndingRestriction> endingJson = endings.getMemberAt(0);
		ComponentData componentData = new ComponentData();
		String componentName = endingJson.getString(EndingRestriction.COMPONENT_NAME);
		componentData.stateName = endingJson.getString(EndingRestriction.COMPONENT_STATE_NAME);
		componentData.componentJson = componentMap.getMemberBy(componentName);
		ArrayList<ComponentInstance> instanceList = new ArrayList<ComponentInstance>();
		
		do
		{
			ComponentInstance newInstance = new ComponentInstance(componentData.componentJson);
			instanceList.add(newInstance);
		}
		while((componentData = this.getPrecedingComponent(componentMap, componentData)) != null);
		
		for (ComponentInstance componentInstance : instanceList)
		{
			System.out.println(componentInstance.getComponentJson().renderAsString());
		}
	}
	
	private ComponentData getPrecedingComponent(JsonEntityMap<RestrictedJson<ComponentRestriction>> componentMap, ComponentData componentData)
	{
		JsonEntityArray<RestrictedJson<ComponentChangeRestriction>> changeArray = 
				componentData.componentJson.getRestrictedJsonArray(ComponentRestriction.COMPONENT_CHANGES, ComponentChangeRestriction.class);
		RestrictedJson<ComponentChangeRestriction> changeJson = this.getChangeFromState(changeArray, componentData.stateName);
		RestrictedJson<TriggerRestriction> triggerJson;
		
		if (changeJson == null)
			return null;
		
		while ((triggerJson = changeJson.getRestrictedJson(ComponentChangeRestriction.TRIGGER, TriggerRestriction.class)) == null)
		{
			String precedingStateName = changeJson.getString(ComponentChangeRestriction.OLD_COMPONENT_STATE_NAME);
			changeJson = this.getChangeFromState(changeArray, precedingStateName);
			if (changeJson == null)
				return null;
		}
		
		ComponentData newComponentData = new ComponentData();
		newComponentData.componentJson = componentMap.getMemberBy(triggerJson.getString(TriggerRestriction.COMPONENT_NAME));
		newComponentData.stateName = triggerJson.getString(TriggerRestriction.COMPONENT_STATE_NAME);
		
		return newComponentData;
	}
	
	private RestrictedJson<ComponentChangeRestriction> getChangeFromState(JsonEntityArray<RestrictedJson<ComponentChangeRestriction>> changeArray, String stateName)
	{		
		for (int i = 0; i < changeArray.getLength(); i++)
		{
			RestrictedJson<ComponentChangeRestriction> changeJson = changeArray.getMemberAt(i);
			if (changeJson.getString(ComponentChangeRestriction.NEW_COMPONENT_STATE_NAME).equals(stateName))
			{
				return changeJson;
			}
		}
		return null;
	}
	
	private class ComponentData
	{
		public RestrictedJson<ComponentRestriction> componentJson;
		public String stateName;
	}

}
