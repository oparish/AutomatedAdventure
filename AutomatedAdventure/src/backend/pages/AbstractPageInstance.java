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
import json.restrictions.ElementChoiceRestriction;
import json.restrictions.ElementConditionRestriction;
import json.restrictions.GroupChoiceRestriction;
import json.restrictions.InstanceDetailsRestriction;
import json.restrictions.MakeConnectionRestriction;
import json.restrictions.MakeElementRestriction;
import json.restrictions.PageRestriction;
import json.restrictions.PositionAdjustmentRestriction;
import json.restrictions.PositionAdjustmentType;
import json.restrictions.RemoveElementRestriction;
import json.restrictions.SumComponentRestriction;
import json.restrictions.SumRestriction;
import main.Pages;

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
	protected static final Pattern reportReferencePattern = Pattern.compile("<report:([^<>]*):([^<>]*)>([^<>]*)</report>");
	
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
	
	private void removeElements(JsonEntityArray<RestrictedJson<RemoveElementRestriction>> removeElementArray) throws Exception
	{	
		if (removeElementArray == null)
			return;
		
		Report report = this.pageContext.getReport();
		for (int i = 0; i < removeElementArray.size(); i++)
		{
			RestrictedJson<RemoveElementRestriction> removeElementData = removeElementArray.getMemberAt(i);
			String elementName = removeElementData.getString(RemoveElementRestriction.ELEMENT_NAME);
			Element element = this.scenario.getElement(elementName);
			ElementInstance elementInstance = this.pageContext.getElementInstance(element);
			JsonEntityArray<RestrictedJson<ElementConditionRestriction>> elementConditionArray = 
					removeElementData.getRestrictedJsonArray(RemoveElementRestriction.ELEMENT_CONDITIONS, ElementConditionRestriction.class);
			boolean result = this.checkElementConditions(elementConditionArray, elementInstance);
			if (result)
			{
				report.addRemovedElement(elementInstance);
				element.removeInstance(elementInstance);
			}
		}
	}
	
	private void makeElements(JsonEntityArray<RestrictedJson<MakeElementRestriction>> makeElementArray) throws Exception
	{		
		if (makeElementArray == null)
			return;
		
		Report report = this.pageContext.getReport();
		for (int i = 0; i < makeElementArray.size(); i++)
		{
			RestrictedJson<MakeElementRestriction> makeElementData = makeElementArray.getMemberAt(i);
			String elementName = makeElementData.getString(MakeElementRestriction.ELEMENT_NAME);
			Element element = this.scenario.getElement(elementName);
			Integer numberValue = makeElementData.getNumber(MakeElementRestriction.NUMBER_VALUE);
			String uniqueName = makeElementData.getString(MakeElementRestriction.UNIQUE_NAME);
			
			if (numberValue != null)
			{
				ArrayList<Element.ElementInstance> instances = element.makeInstances(numberValue);
				for (ElementInstance instance : instances)
				{
					report.addMadeElement(instance);
				}
			}
			else
			{
				RestrictedJson<InstanceDetailsRestriction> instanceDetailsData = 
						makeElementData.getRestrictedJson(MakeElementRestriction.INSTANCE_DETAILS, InstanceDetailsRestriction.class);
				ElementInstance instance = element.makeInstance(instanceDetailsData, uniqueName);
				report.addMadeElement(instance);
			}
		}
	}
	
	protected void processAdjustmentData(RestrictedJson<AdjustmentDataRestriction> adjustmentData) throws Exception
	{
		if (adjustmentData == null)
			return;
		
		JsonEntityArray<RestrictedJson<MakeConnectionRestriction>> makeConnectionArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.MAKE_CONNECTIONS, MakeConnectionRestriction.class);
		JsonEntityArray<RestrictedJson<MakeElementRestriction>> makeElementArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.MAKE_ELEMENTS, MakeElementRestriction.class);
		JsonEntityArray<RestrictedJson<PositionAdjustmentRestriction>> positionAdjustmentArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.POSITION_ADJUSTMENTS, PositionAdjustmentRestriction.class);
		JsonEntityArray<RestrictedJson<ElementAdjustmentRestriction>> elementAdjustmentArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.ELEMENT_ADJUSTMENTS, ElementAdjustmentRestriction.class);
		JsonEntityArray<RestrictedJson<CounterAdjustmentRestriction>> counterAdjustmentArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.COUNTER_ADJUSTMENTS, CounterAdjustmentRestriction.class);
		JsonEntityArray<RestrictedJson<CounterInitialisationRestriction>> counterInitialisationArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.COUNTER_INITIALISATIONS, CounterInitialisationRestriction.class);
		JsonEntityArray<RestrictedJson<RemoveElementRestriction>> removeElementArray = 
				adjustmentData.getRestrictedJsonArray(AdjustmentDataRestriction.REMOVE_ELEMENTS, RemoveElementRestriction.class);
		
		this.makeConnections(makeConnectionArray);
		this.makeElements(makeElementArray);
		this.makeElementAdjustments(elementAdjustmentArray);
		this.makePositionAdjustments(positionAdjustmentArray);
		this.initialiseCounters(counterInitialisationArray);
		this.makeCounterAdjustments(counterAdjustmentArray);
		this.removeElements(removeElementArray);
	}
	
	private void makeConnections(JsonEntityArray<RestrictedJson<MakeConnectionRestriction>> makeConnectionArray) throws Exception
	{		
		if (makeConnectionArray == null)
			return;
			
		for (int i = 0; i < makeConnectionArray.size(); i++)
		{
			RestrictedJson<MakeConnectionRestriction> makeConnectionData = makeConnectionArray.getMemberAt(i);
			int connectionNumber = makeConnectionData.getNumber(MakeConnectionRestriction.NUMBER_VALUE);
			String connectionName = makeConnectionData.getString(MakeConnectionRestriction.NAME);
			ConnectionSet connectionSet = this.scenario.getConnectionSet(connectionName);
			connectionSet.makeUniqueConnections(connectionNumber);
		}
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
	
	protected ElementGroup setupElementGroup(String positionCounterName) throws Exception
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
		
		if (sumComponents != null)
		{
			for (int i = 0; i < sumComponents.getLength(); i++)
			{
				RestrictedJson<SumComponentRestriction> sumComponentData = sumComponents.getMemberAt(i);
				value = this.assessSumComponent(sumComponentData, value);
			}
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
			case GROUP:
				this.scenario.addGroupCounter(counterName, counterSecondaryType, this.pageContext.getSelectedElementGroup());
				break;
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
				this.scenario.incrementCounter(counterName);
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
	
	protected boolean checkElementConditions(JsonEntityArray<RestrictedJson<ElementConditionRestriction>> elementConditionArray, 
			ElementInstance elementInstance) throws Exception
	{
		if (elementConditionArray == null)
			return true;
		
		for (int i = 0; i < elementConditionArray.getLength(); i++)
		{
			RestrictedJson<ElementConditionRestriction> elementConditionData = elementConditionArray.getMemberAt(i);
			String elementQualityText = elementConditionData.getString(ElementConditionRestriction.ELEMENT_QUALITY);
			String comparatorText = elementConditionData.getString(ElementConditionRestriction.TYPE);
			Integer value = elementConditionData.getNumber(ElementConditionRestriction.NUMBER_VALUE);		
			String qualityValue = elementConditionData.getString(ElementConditionRestriction.QUALITY_NAME);
			
			if (value == null)
			{
				if (!Pages.checkStringComparison(elementInstance, elementQualityText, qualityValue))
				{
					return false;
				}
			}
			else
			{
				if (!Pages.checkNumberComparison(elementInstance, comparatorText, elementQualityText, value))
				{
					return false;
				}
			}
		}
		return true;
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
			JsonEntityArray<RestrictedJson<ElementConditionRestriction>> elementConditionArray = 
					elementAdjustmentData.getRestrictedJsonArray(ElementAdjustmentRestriction.ELEMENT_CONDITIONS, ElementConditionRestriction.class);
			
			switch(elementAdjustmentType)
			{
				case GROUP:
					String counterName = elementAdjustmentData.getString(ElementAdjustmentRestriction.COUNTER_NAME);
					ElementInstance groupInstance = this.scenario.getElementInstanceFromGroupCounter(counterName);
					if (this.checkElementConditions(elementConditionArray, groupInstance))
						groupInstance.adjustNumber(elementNumberName, sumSign, value);
					break;
				case CONNECTED:
					String connectionName = elementAdjustmentData.getString(ElementAdjustmentRestriction.CONNECTION_NAME);
					ConnectionSet connectionSet = this.scenario.getConnectionSet(connectionName);
					ElementInstance elementInstance = this.getSelectedElementInstance(element);
					ElementInstance connectedInstance = connectionSet.get(elementInstance);
					if (this.checkElementConditions(elementConditionArray, connectedInstance))
						connectedInstance.adjustNumber(elementNumberName, sumSign, value);
					break;
				case EACH:
					for (ElementInstance instance : element.getInstances())
					{
						if (this.checkElementConditions(elementConditionArray, instance))
							instance.adjustNumber(elementNumberName, sumSign, value);
					}
					break;
				case SELECTED:
					ElementInstance selectedInstance = this.getSelectedElementInstance(element);
					if (this.checkElementConditions(elementConditionArray, selectedInstance))
						selectedInstance.adjustNumber(elementNumberName, sumSign, value);
					break;
			}
		}
		
		return adjustmentValues;
	}
}
