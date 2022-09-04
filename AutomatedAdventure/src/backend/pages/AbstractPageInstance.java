package backend.pages;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.Element;
import backend.Map;
import backend.Map.MapPosition;
import backend.Scenario;
import backend.Element.ElementInstance;
import backend.ElementGroup;
import backend.component.ConnectionSet;
import json.JsonEntityArray;
import json.RestrictedJson;
import json.restrictions.AdjustmentDataRestriction;
import json.restrictions.CounterAdjustmentRestriction;
import json.restrictions.CounterInitialisationRestriction;
import json.restrictions.ElementAdjustmentRestriction;
import json.restrictions.ElementAdjustmentType;
import json.restrictions.GroupChoiceRestriction;
import json.restrictions.PositionAdjustmentRestriction;
import json.restrictions.PositionAdjustmentType;
import json.restrictions.SumComponentRestriction;
import json.restrictions.SumRestriction;

public abstract class AbstractPageInstance
{
	protected static final Pattern selectedElementPattern = Pattern.compile("<selectedElement:([^<>]*):([^<>]*)>");
	protected static final Pattern selectedPositionPattern = Pattern.compile("<selectedPosition:([xy])>");
	protected static final Pattern connectionToSelectedElementPattern = Pattern.compile("<connectionToSelectedElement:([^<>]*):([^<>]*):([^<>]*)>");
	protected static final Pattern connectionToRepeatedElementPattern = Pattern.compile("<connectionToRepeatedElement:([^<>]*):([^<>]*)>");
	protected static final Pattern repeatForElementPattern = Pattern.compile("<repeatForElement:([^<>]*)>([\\s\\S]*)</repeatForElement>");
	protected static final Pattern conditionalRepeatForElementPattern = Pattern.compile("<conditionalRepeatForElement:([^<>]*):([^<>]*):([<>!]?=?):(-?\\d+)>([\\s\\S]*)</conditionalRepeatForElement>");
	protected static final Pattern repeatedElementPattern = Pattern.compile("<repeatedElement:([^<>]*)>");
	protected static final Pattern numberAdjustmentPattern = Pattern.compile("<numberAdjustment:([0-9]*)>");
	protected static final Pattern numberReferencePattern = Pattern.compile("^(.*):(.*)$");
	
	protected Scenario scenario;
	protected PageContext pageContext;
	MapPosition position;
	
	public Scenario getScenario() {
		return scenario;
	}

	public PageContext getPageContext() {
		return pageContext;
	}

	public AbstractPageInstance(Scenario scenario, PageContext pageContext, MapPosition position)
	{
		this.scenario = scenario;
		this.pageContext = pageContext;
		this.position = position;
	}
	
	protected void processAdjustmentData(RestrictedJson<AdjustmentDataRestriction> adjustmentData) throws Exception
	{
		if (adjustmentData == null)
			return;
		
		JsonEntityArray<RestrictedJson<PositionAdjustmentRestriction>> positionAdjustmentArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.POSITION_ADJUSTMENTS, PositionAdjustmentRestriction.class);
		JsonEntityArray<RestrictedJson<ElementAdjustmentRestriction>> elementAdjustmentArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.ELEMENT_ADJUSTMENTS, ElementAdjustmentRestriction.class);
		JsonEntityArray<RestrictedJson<CounterAdjustmentRestriction>> counterAdjustmentArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.COUNTER_ADJUSTMENTS, CounterAdjustmentRestriction.class);
		JsonEntityArray<RestrictedJson<CounterInitialisationRestriction>> counterInitialisationArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.COUNTER_INITIALISATIONS, CounterInitialisationRestriction.class);
		
		this.makeElementAdjustments(elementAdjustmentArray);
		this.makePositionAdjustments(positionAdjustmentArray);
		this.initialiseCounters(counterInitialisationArray);
		this.makeCounterAdjustments(counterAdjustmentArray);	
	}
	
	private Integer assessReference(String numberReference) throws Exception
	{
		Matcher numberRefMatcher = numberReferencePattern.matcher(numberReference);
		
		if (!numberRefMatcher.find())
		{
			throw new Exception("No valid number reference.");
		}
		
		String elementType = numberRefMatcher.group(1);
		String elementQualityName = numberRefMatcher.group(2);
		
		Element element = this.scenario.getElement(elementType);
		ElementInstance elementInstance = this.getSelectedElementInstance(element);
		Integer numberValue = elementInstance.getNumberValueByName(elementQualityName);
		
		if (numberValue == null)
		{
			throw new Exception("No valid number reference.");
		}
		
		return numberValue;
	}
	
	private Integer assessSumComponent(RestrictedJson<SumComponentRestriction> sumComponentData, Integer value) throws Exception
	{
		Integer adjustmentValue = sumComponentData.getNumber(SumComponentRestriction.NUMBER_VALUE);
		if (adjustmentValue == null)
		{
			String numberReferenceString = sumComponentData.getString(SumComponentRestriction.NUMBER_REFERENCE);
			if (numberReferenceString == null)
			{
				throw new Exception("No number available for sum component.");
			}
			adjustmentValue = this.assessReference(numberReferenceString);
		}

		String sumSign = sumComponentData.getString(SumComponentRestriction.SUM_SIGN);
		value = PageInstance.performSum(value, sumSign, adjustmentValue);
		return value;
	}
	
	protected ElementInstance getSelectedElementInstance(Element element)
	{
		if (element == null)
			return null;
		else if (element.getUnique())
			return element.getUniqueInstance();
		else
			return this.pageContext.getElementInstance(element);
	}
	
	protected ElementGroup getSelectedElementGroup()
	{
		return this.pageContext.getSelectedElementGroup();
	}
	
	protected ElementGroup setupElementGroup(String positionCounterName)
	{
		MapPosition mapPosition = this.scenario.getMapPositionFromPositionCounter(positionCounterName);
		ArrayList<ElementInstance> elementInstances = mapPosition.getElementInstances();
		ElementGroup elementGroup = new ElementGroup(elementInstances);
		return elementGroup;
	}
	
	private Integer assessSum(String sumName) throws Exception
	{
		RestrictedJson<SumRestriction> sumData = this.scenario.getSum(sumName);
		Integer value = sumData.getNumber(SumRestriction.NUMBER_VALUE);
		if (value == null)
		{
			value = this.assessReference(sumData.getString(SumRestriction.NUMBER_REFERENCE));
		}
		if (value == null)
		{
			throw new Exception("No number available for sum.");
		}
		
		JsonEntityArray<RestrictedJson<SumComponentRestriction>> sumComponents = 
				sumData.getRestrictedJsonArray(SumRestriction.SUM_COMPONENTS, SumComponentRestriction.class);
		
		for (int i = 0; i < sumComponents.getLength(); i++)
		{
			RestrictedJson<SumComponentRestriction> sumComponentData = sumComponents.getMemberAt(i);
			value = this.assessSumComponent(sumComponentData, value);
		}
		
		return value;
	}
	
	protected void initialiseCounters(JsonEntityArray<RestrictedJson<CounterInitialisationRestriction>> counterInitialisationArray) throws Exception
	{
		if (counterInitialisationArray == null)
			return;
		for (int i = 0; i < counterInitialisationArray.getLength(); i++)
		{
			RestrictedJson<CounterInitialisationRestriction> initialisation = counterInitialisationArray.getMemberAt(i);
			String mapName = initialisation.getString(CounterInitialisationRestriction.MAP_NAME);
			Map map = this.scenario.getMapByName(mapName);
			String counterName = initialisation.getString(CounterInitialisationRestriction.COUNTER_NAME);
			String counterPrimaryTypeName = initialisation.getString(CounterInitialisationRestriction.COUNTER_PRIMARY_TYPE);
			CounterPrimaryType counterPrimaryType = CounterPrimaryType.valueOf(counterPrimaryTypeName.toUpperCase());
			String counterSecondaryTypeName = initialisation.getString(CounterInitialisationRestriction.COUNTER_SECONDARY_TYPE);
			CounterSecondaryType counterSecondaryType = CounterSecondaryType.valueOf(counterSecondaryTypeName.toUpperCase());
			
			switch(counterPrimaryType)
			{
			case POSITION:
			default:
				this.scenario.addPositionCounter(map, counterName, counterSecondaryType);
			}		
		}
	}
	
	protected void makeCounterAdjustments(JsonEntityArray<RestrictedJson<CounterAdjustmentRestriction>> counterAdjustmentArray) throws Exception
	{
		if (counterAdjustmentArray == null)
			return;
		
		for (int i = 0; i < counterAdjustmentArray.getLength(); i++)
		{
			RestrictedJson<CounterAdjustmentRestriction> adjustment = counterAdjustmentArray.getMemberAt(i);
			String counterName = adjustment.getString(CounterAdjustmentRestriction.COUNTER_NAME);
			String counterAdjustmentTypeName = adjustment.getString(CounterAdjustmentRestriction.COUNTER_ADJUSTMENT_TYPE);
			CounterAdjustmentType counterAdjustmentType = CounterAdjustmentType.valueOf(counterAdjustmentTypeName.toUpperCase());
			
			switch(counterAdjustmentType)
			{
			case INCREMENT:
				this.scenario.incrementPositionCounter(counterName);
				break;
			default:
			}
		}
	}
	
	protected void makePositionAdjustments(JsonEntityArray<RestrictedJson<PositionAdjustmentRestriction>> positionAdjustmentArray) throws Exception
	{
		if (positionAdjustmentArray == null)
			return;
		
		for (int i = 0; i < positionAdjustmentArray.getLength(); i++)
		{
			RestrictedJson<PositionAdjustmentRestriction> adjustment = positionAdjustmentArray.getMemberAt(i);
			String patString = adjustment.getString(PositionAdjustmentRestriction.ADJUSTMENT_TYPE);
			PositionAdjustmentType pat = PositionAdjustmentType.valueOf(patString.toUpperCase());
			String mapName = adjustment.getString(PositionAdjustmentRestriction.MAP_NAME);
			String elementType = adjustment.getString(PositionAdjustmentRestriction.ELEMENT_NAME);
			Element element = this.scenario.getElement(elementType);
			Map map = this.scenario.getMapByName(mapName);
			switch(pat)
			{
				case DIRECT:
					this.directMovement(map, element);
					break;
				case ROUTE:
					this.routeMovement(map, element);
					break;
			}
		}
	}
	
	protected void directMovement(Map map, Element element) throws Exception
	{
		ElementInstance elementInstance = this.getSelectedElementInstance(element);
		if (this.position != null)
			this.moveElementInstance(map, elementInstance, position);
	}
	
	protected void routeMovement(Map map, Element element) throws Exception
	{
		for (ElementInstance elementInstance : element.getInstances())
		{
			if (elementInstance.getRoute(map) != null)
			{	
				MapPosition position = elementInstance.incrementRoutePos(map);
				if (position != elementInstance.getMapPosition(map))
					this.moveElementInstance(map, elementInstance, position);
			}
		}
	}
	
	private void moveElementInstance(Map map, ElementInstance elementInstance, MapPosition newPosition) throws Exception
	{
		MapPosition oldPosition = elementInstance.getMapPosition(map);
		elementInstance.setMapPosition(map, newPosition);
		map.addChangeInPosition(elementInstance, oldPosition, newPosition);
	}
	
	protected ArrayList<Integer> makeElementAdjustments(JsonEntityArray<RestrictedJson<ElementAdjustmentRestriction>> elementAdjustmentArray) throws Exception
	{
		ArrayList<Integer> adjustmentValues = new ArrayList<Integer>();
		
		if (elementAdjustmentArray == null)
			return adjustmentValues;
		
		for (int i = 0; i < elementAdjustmentArray.size(); i++)
		{
			RestrictedJson<ElementAdjustmentRestriction> elementAdjustmentData = elementAdjustmentArray.getMemberAt(i);
			String typeString = elementAdjustmentData.getString(ElementAdjustmentRestriction.TYPE);
			ElementAdjustmentType elementAdjustmentType = ElementAdjustmentType.stringToType(typeString);
			String elementType = elementAdjustmentData.getString(ElementAdjustmentRestriction.ELEMENT_NAME);
			String elementNumberName = elementAdjustmentData.getString(ElementAdjustmentRestriction.ELEMENT_QUALITY);			
			Integer value = elementAdjustmentData.getNumber(ElementAdjustmentRestriction.NUMBER_VALUE);
			String sumSign = elementAdjustmentData.getString(ElementAdjustmentRestriction.SUM_SIGN);
			if (value == null)
			{
				value = this.assessSum(elementAdjustmentData.getString(ElementAdjustmentRestriction.SUM_NAME));
			}
			if (value == null)
			{
				throw new Exception("No number available for element adjustment.");
			}
			adjustmentValues.add(value);
			Element element = this.scenario.getElement(elementType);
			
			switch(elementAdjustmentType)
			{
				case CONNECTED:
					String connectionName = elementAdjustmentData.getString(ElementAdjustmentRestriction.CONNECTION_NAME);
					ConnectionSet connectionSet = this.scenario.getConnectionSet(connectionName);
					ElementInstance elementInstance = this.getSelectedElementInstance(element);
					ElementInstance connectedInstance = connectionSet.get(elementInstance);
					connectedInstance.adjustNumber(elementNumberName, sumSign, value);
					break;
				case EACH:
					for (ElementInstance instance : element.getInstances())
					{
						instance.adjustNumber(elementNumberName, sumSign, value);
					}
					break;
				case SELECTED:
					ElementInstance selectedInstance = this.getSelectedElementInstance(element);
					selectedInstance.adjustNumber(elementNumberName, sumSign, value);
					break;
			}
		}
		
		return adjustmentValues;
	}
}
