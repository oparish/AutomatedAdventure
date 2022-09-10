package json.restrictions;

import backend.Element;
import backend.Element.ElementInstance;
import backend.ElementGroup;
import backend.Faction;
import backend.Scenario;
import backend.component.ConnectionSet;
import backend.pages.Comparator;
import backend.pages.PageContext;
import json.JsonEntityArray;
import json.RestrictedJson;
import main.Pages;

public enum ContextConditionRestriction implements RestrictionPointer
{
	TYPE(Restriction.TYPE, true), NUMBER_VALUE(Restriction.NUMBER_VALUE, true), ELEMENT_NAME(Restriction.ELEMENT_NAME, true), 
	ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY, true), CONNECTION_NAME(Restriction.CONNECTION_NAME, true), 
	COUNTER_NAME(Restriction.COUNTER_NAME, true), COUNTER_CONDITION(Restriction.COUNTER_CONDITION, true), 
	GROUP_CONDITION_TYPE(Restriction.GROUP_CONDITION_TYPE, true);
	
	private Restriction restriction;
	private boolean optional;
			
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ContextConditionRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ContextConditionRestriction(Restriction restriction)
	{
		this.optional = false;
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
	
	public static boolean checkCondition(Scenario scenario, 
			JsonEntityArray<RestrictedJson<ContextConditionRestriction>> contextConditionDataArray, ElementInstance selectedInstance, 
			ElementGroup elementGroup) 
					throws Exception
	{
		boolean check = true;
		
		for (int i = 0; i < contextConditionDataArray.getLength(); i++)
		{
			RestrictedJson<ContextConditionRestriction> contextConditionData = contextConditionDataArray.getMemberAt(i);
			if (!ContextConditionRestriction.checkCondition(scenario, contextConditionData, selectedInstance, elementGroup))
			{
				check = false;
				break;
			}
		}
		
		return check;
	}
	
	private static boolean makeGroupCheck(String groupConditionString, ElementGroup elementGroup, String elementQuality, String comparatorText) throws Exception
	{
		GroupConditionType groupConditionType = GroupConditionType.getByName(groupConditionString);
		switch (groupConditionType)
		{
		case FACTION_CONFLICT_CHECK:
			return ContextConditionRestriction.makeFactionConflictCheck(elementGroup);
		case FACTION_CONTEST:
		default:
			return ContextConditionRestriction.makeFactionContestCheck(elementGroup, elementQuality, comparatorText);
		}
	}
	
	private static boolean makeFactionConflictCheck(ElementGroup elementGroup)
	{
		if (elementGroup == null)
			return false;
		boolean playerPresence = false;
		boolean computerPresence = false;
		for (ElementInstance elementInstance : elementGroup.getElementInstances())
		{
			if (elementInstance.getFaction() == Faction.PLAYER && computerPresence)
				return true;
			else if (elementInstance.getFaction() == Faction.COMPUTER && playerPresence)
				return true;
			else if (elementInstance.getFaction() == Faction.PLAYER)
				playerPresence = true;
			else
				computerPresence = true;
		}
		return false;
	}
	
	private static boolean makeFactionContestCheck(ElementGroup elementGroup, String elementQuality, String comparatorText) throws Exception
	{
		if (elementGroup == null)
			return false;
		int playerTotal = 0;
		int computerTotal = 0;
		for (ElementInstance elementInstance : elementGroup.getElementInstances())
		{
			if (elementInstance.getFaction() == Faction.PLAYER)
				playerTotal += elementInstance.getNumberValueByName(elementQuality);
			else
				computerTotal += elementInstance.getNumberValueByName(elementQuality);			
		}
		Comparator comparator = Comparator.fromText(comparatorText);
		return Pages.checkComparison(comparator, playerTotal, computerTotal);
	}
	
	public static boolean checkCondition(Scenario scenario, RestrictedJson<ContextConditionRestriction> contextConditionData, 
			ElementInstance selectedInstance, ElementGroup elementGroup) throws Exception
	{			
		String comparatorText = contextConditionData.getString(ContextConditionRestriction.TYPE);
		String elementNumberName = contextConditionData.getString(ContextConditionRestriction.ELEMENT_QUALITY);
		Integer value = contextConditionData.getNumber(ContextConditionRestriction.NUMBER_VALUE);
		String connectionName = contextConditionData.getString(ContextConditionRestriction.CONNECTION_NAME);
		String counterName = contextConditionData.getString(ContextConditionRestriction.COUNTER_NAME);
		String counterConditionString = contextConditionData.getString(ContextConditionRestriction.COUNTER_CONDITION);
		String groupConditionString = contextConditionData.getString(ContextConditionRestriction.GROUP_CONDITION_TYPE);
		
		if (connectionName != null)
		{
			ConnectionSet connectionSet = scenario.getConnectionSet(connectionName);
			selectedInstance = connectionSet.get(selectedInstance);
		}
		
		if (counterName != null)
		{
			CounterCondition counterCondition = CounterCondition.valueOf(counterConditionString.toUpperCase());

			switch (counterCondition)
			{
			case COMPLETED:
			default:
				return scenario.isCounterFinished(counterName);
			}
		}
		else if (groupConditionString != null)
		{
			return ContextConditionRestriction.makeGroupCheck(groupConditionString, elementGroup, elementNumberName, comparatorText);
		}
		else if (elementNumberName == null)
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
