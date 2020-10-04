package json.restrictions;

import backend.Element;
import backend.Element.ElementInstance;
import backend.Scenario;
import backend.component.ConnectionSet;
import backend.pages.PageContext;
import json.JsonEntityArray;
import json.RestrictedJson;
import main.Pages;

public enum ContextConditionRestriction implements RestrictionPointer
{
	TYPE(Restriction.TYPE), NUMBER_VALUE(Restriction.NUMBER_VALUE), ELEMENT_NAME(Restriction.ELEMENT_NAME), 
	ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY), CONNECTION_NAME(Restriction.CONNECTION_NAME);
	
	private Restriction restriction;
	
	private ContextConditionRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
	
	public static boolean checkCondition(Scenario scenario, 
			JsonEntityArray<RestrictedJson<ContextConditionRestriction>> contextConditionDataArray, ElementInstance selectedInstance) 
					throws Exception
	{
		boolean check = true;
		
		for (int i = 0; i < contextConditionDataArray.getLength(); i++)
		{
			RestrictedJson<ContextConditionRestriction> contextConditionData = contextConditionDataArray.getMemberAt(i);
			if (!ContextConditionRestriction.checkCondition(scenario, contextConditionData, selectedInstance))
			{
				check = false;
				break;
			}
		}
		
		return check;
	}
	
	public static boolean checkCondition(Scenario scenario, RestrictedJson<ContextConditionRestriction> contextConditionData, 
			ElementInstance selectedInstance) throws Exception
	{			
		String comparatorText = contextConditionData.getString(ContextConditionRestriction.TYPE);
		String elementNumberName = contextConditionData.getString(ContextConditionRestriction.ELEMENT_QUALITY);
		Integer value = contextConditionData.getNumber(ContextConditionRestriction.NUMBER_VALUE);
		String connectionName = contextConditionData.getString(ContextConditionRestriction.CONNECTION_NAME);
		
		if (connectionName != null)
		{
			ConnectionSet connectionSet = scenario.getConnectionSet(connectionName);
			selectedInstance = connectionSet.get(selectedInstance);
		}
		
		if (elementNumberName == null)
		{
			if (selectedInstance != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}		
		else if (Pages.checkComparison(selectedInstance, comparatorText, elementNumberName, value))
		{
			return true;
		}				
		else
		{
			return false;
		}
	}
}
