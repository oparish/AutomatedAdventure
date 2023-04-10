package json.restrictions;

import java.util.ArrayList;

import backend.Element;
import backend.Element.ElementInstance;
import backend.ElementGroup;
import backend.Faction;
import backend.Map;
import backend.Map.MapPosition;
import backend.PositionType;
import backend.Scenario;
import backend.component.ConnectionSet;
import backend.pages.Comparator;
import backend.pages.PageContext;
import backend.pages.PositionCounter;
import json.JsonEntityArray;
import json.RestrictedJson;
import main.Pages;

public enum ContextConditionRestriction implements RestrictionPointer
{
	TYPE(Restriction.TYPE, true), STRING_VALUE(Restriction.STRING_VALUE, true), NUMBER_VALUE(Restriction.NUMBER_VALUE, true), 
	ELEMENT_NAME(Restriction.ELEMENT_NAME, true), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY, true), 
	CONNECTION_NAME(Restriction.CONNECTION_NAME, true), COUNTER_NAME(Restriction.COUNTER_NAME, true), 
	COUNTER_CONDITION(Restriction.COUNTER_CONDITION, true), GROUP_CONDITION_TYPE(Restriction.GROUP_CONDITION_TYPE, true), 
	SELECTION_TYPE(Restriction.SELECTION_TYPE, true), GROUP_NAME(Restriction.GROUP_NAME, true), MAP_NAME(Restriction.MAP_NAME, true), 
	RANGE_FOR_GROUP(Restriction.RANGE_FOR_GROUP, true), POSITION_TYPE(Restriction.POSITION_TYPE, true), 
	POSITION_COUNTER_NAME(Restriction.POSITION_COUNTER_NAME, true);
	
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
	
	private static boolean makeCountCheck(ElementGroup elementGroup, String elementQuality, int number, String elementValue)
	{
		Integer elementInteger = null;
		if (elementValue.matches("^\\d*$"))
			elementInteger = Integer.valueOf(elementValue);
		int result = 0;
		for (ElementInstance elementInstance : elementGroup.getElementInstances())
		{
			String elementStringValue = elementInstance.getStringValue(elementQuality);
			if (elementStringValue != null)
			{
				if (elementStringValue.equals(elementValue))
					result++;
				continue;
			}
			
			if (elementInteger != null)
			{
				Integer elementNumberValue = elementInstance.getNumberValueByName(elementQuality);
				if (elementNumberValue != null && elementNumberValue.equals(elementInteger))
				{
					result++;
				}
			}
		}
		
		return result >= number;
	}
	
	private static boolean makeGroupCheck(Map map, String groupConditionString, ElementGroup elementGroup, String elementQuality, 
			String comparatorText, Integer number, String elementValue) throws Exception
	{
		GroupConditionType groupConditionType = GroupConditionType.getByName(groupConditionString);
		switch (groupConditionType)
		{
		case COUNT:
			return ContextConditionRestriction.makeCountCheck(elementGroup, elementQuality, number, elementValue);
		case FACTION_CONFLICT_CHECK:
			return ContextConditionRestriction.makeFactionConflictCheck(map, elementGroup);
		case FACTION_CONTEST:
		default:
			return ContextConditionRestriction.makeFactionContestCheck(map, elementGroup, elementQuality, comparatorText);
		}
	}
	
	private static boolean makeFactionConflictCheck(Map map, ElementGroup elementGroup)
	{
		if (elementGroup == null)
			return false;
		boolean playerPresence = false;
		boolean computerPresence = false;
		for (ElementInstance elementInstance : elementGroup.getElementInstances())
		{
			Faction faction = elementInstance.getFaction(map);
			if (faction == null)
				return false;
			else if (faction == Faction.PLAYER && computerPresence)
				return true;
			else if (faction == Faction.COMPUTER && playerPresence)
				return true;
			else if (faction == Faction.PLAYER)
				playerPresence = true;
			else
				computerPresence = true;
		}
		return false;
	}
	
	private static boolean makeFactionContestCheck(Map map, ElementGroup elementGroup, String elementQuality, String comparatorText) throws Exception
	{
		if (elementGroup == null)
			return false;
		int playerTotal = 0;
		int computerTotal = 0;
		for (ElementInstance elementInstance : elementGroup.getElementInstances())
		{
			Faction faction = elementInstance.getFaction(map);
			if (faction == Faction.PLAYER)
				playerTotal += elementInstance.getNumberValueByName(elementQuality);
			else if (faction == Faction.COMPUTER)
				computerTotal += elementInstance.getNumberValueByName(elementQuality);			
		}
		Comparator comparator = Comparator.fromText(comparatorText);
		return Pages.checkComparison(comparator, playerTotal, computerTotal);
	}
	
	private static ElementGroup getElementGroupFromRange(MapPosition mapPosition, int range)
	{
		ElementGroup elementGroup = new ElementGroup(new ArrayList<ElementInstance>());
		Map map = mapPosition.getMap();
		for (int j = (0 - range); j <= range; j++)
		{
			int x = mapPosition.x + j;
			if (!map.checkXValidity(x))
			{
				continue;
			}
			
			for (int k = (0 - range); k <= range; k++)
			{
				int y = mapPosition.y + k;
				if (!map.checkYValidity(y))
				{
					continue;
				}
				MapPosition newMapPosition = map.getMapPosition(x, y);
				ArrayList<ElementInstance> elementInstances = newMapPosition.getElementInstances();
				elementGroup.addInstances(elementInstances);
			}
		}
		return elementGroup;
	}
	
	public static boolean checkCondition(Scenario scenario, RestrictedJson<ContextConditionRestriction> contextConditionData, 
			ElementInstance selectedInstance, ElementGroup passedElementGroup) throws Exception
	{		
		String elementName = contextConditionData.getString(ContextConditionRestriction.ELEMENT_NAME);
		String comparatorText = contextConditionData.getString(ContextConditionRestriction.TYPE);
		String elementQualityName = contextConditionData.getString(ContextConditionRestriction.ELEMENT_QUALITY);
		Integer numberValue = contextConditionData.getNumber(ContextConditionRestriction.NUMBER_VALUE);
		String stringValue = contextConditionData.getString(ContextConditionRestriction.STRING_VALUE);
		String connectionName = contextConditionData.getString(ContextConditionRestriction.CONNECTION_NAME);
		String counterName = contextConditionData.getString(ContextConditionRestriction.COUNTER_NAME);
		String counterConditionString = contextConditionData.getString(ContextConditionRestriction.COUNTER_CONDITION);
		String groupConditionString = contextConditionData.getString(ContextConditionRestriction.GROUP_CONDITION_TYPE);
		String selectionTypeString = contextConditionData.getString(ContextConditionRestriction.SELECTION_TYPE);
		String mapName = contextConditionData.getString(ContextConditionRestriction.MAP_NAME);
		Integer rangeForGroup = contextConditionData.getNumber(ContextConditionRestriction.RANGE_FOR_GROUP);
		String positionTypeString = contextConditionData.getString(ContextConditionRestriction.POSITION_TYPE);
		String positionCounterName = contextConditionData.getString(ContextConditionRestriction.POSITION_COUNTER_NAME);
		
		MapPosition mapPosition = null;
		

		
		if (positionTypeString != null)
		{
			PositionType positionType = PositionType.valueOf(positionTypeString.toUpperCase());
			switch (positionType)
			{
				case POSITIONCOUNTER:
					PositionCounter positionCounter = scenario.getPositionCounter(positionCounterName);
					mapPosition = positionCounter.getMapPosition();
					break;
				case SELECTEDPOSITION:
					Map map = scenario.getMapByName(mapName);
					mapPosition = map.getSelectedPosition();
					break;
			}
		}
		
		ElementGroup elementGroup;
		
		if (rangeForGroup != null)
		{
			if (mapPosition == null)
				throw new Exception("Can't perform this operation without a specified map position.");
			else
			{
				elementGroup = ContextConditionRestriction.getElementGroupFromRange(mapPosition, rangeForGroup);
			}
		}
		else
		{
			elementGroup = passedElementGroup;
		}
		
		SelectionType selectionType;
		if (selectionTypeString == null)
		{
			selectionType = SelectionType.SINGLE_SELECT;
		}
		else
		{
			selectionType = SelectionType.fromString(selectionTypeString);
		}
		
		Element element = scenario.getElement(elementName);
		
		if (selectionType == SelectionType.FROM_GROUP)
		{
			String groupName = contextConditionData.getString(ContextConditionRestriction.GROUP_NAME);
			selectedInstance = scenario.getElementInstanceFromGroupCounter(element, groupName);
		}
		else if (connectionName != null)
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
			Map map = scenario.getMapByName(mapName);
			return ContextConditionRestriction.makeGroupCheck(map, groupConditionString, elementGroup, elementQualityName, comparatorText, 
					numberValue, stringValue);
		}
		else if (elementQualityName == null)
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
		else if (stringValue != null)
		{
			if (Pages.checkStringComparison(selectedInstance, elementQualityName, stringValue))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (Pages.checkNumberComparison(selectedInstance, comparatorText, elementQualityName, numberValue))
		{
			return true;
		}				
		else
		{
			return false;
		}
	}
}
