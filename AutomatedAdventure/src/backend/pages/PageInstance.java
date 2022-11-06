package backend.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.Element;
import backend.Element.ElementInstance;
import backend.ElementGroup;
import backend.Map;
import backend.Map.MapPosition;
import backend.NumberRange;
import backend.Scenario;
import backend.component.ConnectionSet;
import json.JsonEntityArray;
import json.RestrictedJson;
import json.restrictions.AdjustmentDataRestriction;
import json.restrictions.ChoiceRestriction;
import json.restrictions.ContextConditionRestriction;
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
import main.Main;
import main.Pages;

public class PageInstance extends AbstractPageInstance
{
	private static String BACK = "Back";
	
	RestrictedJson<PageRestriction> pageJson;
	HashMap<String, ElementChoice> choiceMap = new HashMap<String, ElementChoice>();
	ArrayList<String> choiceList = new ArrayList<String>();
	ElementChoiceType elementChoiceType;
	
	public ElementChoiceType getElementChoiceType() {
		return elementChoiceType;
	}

	public ArrayList<String> getChoiceList() {
		return choiceList;
	}

	public HashMap<String, ElementChoice> getChoiceMap() {
		return choiceMap;
	}
	
	public RestrictedJson<PageRestriction> getPageJson() {
		return pageJson;
	}

	public PageInstance(Scenario scenario, PageContext pageContext, RestrictedJson<PageRestriction> pageJson, MapPosition position)
	{
		super(scenario, pageContext, position);
		this.pageJson = pageJson;
	}

	public String parseContents() throws Exception
	{
		RestrictedJson<AdjustmentDataRestriction> adjustmentData = 
				this.pageJson.getRestrictedJson(PageRestriction.ADJUSTMENT_DATA, AdjustmentDataRestriction.class);
		ArrayList<Integer> adjustments = null;
		
		this.processAdjustmentData(adjustmentData);
		this.setupChoices();
		
		String adjustedText = this.checkPatterns(this.pageJson.getString(PageRestriction.VALUE), adjustments);
		return adjustedText;
	}
	
	private boolean checkContextCondition(JsonEntityArray<RestrictedJson<ContextConditionRestriction>> contextConditionDataArray)
			throws Exception
	{
		boolean check = true;
		
		for (int i = 0; i < contextConditionDataArray.getLength(); i++)
		{
			RestrictedJson<ContextConditionRestriction> contextConditionData = contextConditionDataArray.getMemberAt(i);
			String elementName = contextConditionData.getString(ContextConditionRestriction.ELEMENT_NAME);
			Element element = this.scenario.getElement(elementName);
			ElementInstance elementInstance = this.getSelectedElementInstance(element);
			if (!ContextConditionRestriction.checkCondition(this.scenario, contextConditionData, elementInstance, this.getSelectedElementGroup()))
			{
				check = false;
				break;
			}
		}
		
		return check;
	}
	
	private void setupChoices() throws Exception
	{
		JsonEntityArray<RestrictedJson<ChoiceRestriction>> choiceDataArray = 
				this.pageJson.getRestrictedJsonArray(PageRestriction.CHOICES, ChoiceRestriction.class);
		
		if (choiceDataArray == null)
		{
			String returnToString = this.pageJson.getString(PageRestriction.RETURN_TO);
			if (returnToString != null)
			{
				this.setupReturnChoice(returnToString);
			}
		}
		else
		{
			for (int i = 0; i < choiceDataArray.size(); i++)
			{
				this.setupChoice(choiceDataArray.getMemberAt(i));
			}
		}
	}
	
	private void setupReturnChoice(String returnToString) throws Exception
	{
		ReturnChoiceType returnChoiceType = ReturnChoiceType.fromString(returnToString);	
		
		switch(returnChoiceType)
		{
			case CONTEXT_TOP:
				this.setupContextTopChoice();
			break;
		}
	}
	
	private void setupContextTopChoice()
	{
		ElementChoice elementChoice = new ElementChoice();
		elementChoice.keyword = this.pageContext.getTopPage();
		elementChoice.context = new PageContext(elementChoice.keyword);
		this.addChoice(BACK, elementChoice);
	}
	
	private void setupElementChoice(RestrictedJson<ElementChoiceRestriction> elementChoiceData, String keyword, String first, 
			boolean withContext, ElementGroup elementGroup) throws Exception
	{
		String elementName = elementChoiceData.getString(ElementChoiceRestriction.ELEMENT_NAME);
		String elementQualityName = elementChoiceData.getString(ElementChoiceRestriction.ELEMENT_QUALITY);
		String second = elementChoiceData.getString(ElementChoiceRestriction.SECOND);
		String type = elementChoiceData.getString(ElementChoiceRestriction.TYPE);
		String rangeAttribute = elementChoiceData.getString(ElementChoiceRestriction.RANGE_ATTRIBUTE);
		
		Element element = this.scenario.getElement(elementName);
		
		JsonEntityArray<RestrictedJson<ElementConditionRestriction>> elementConditionArray = 
				elementChoiceData.getRestrictedJsonArray(ElementChoiceRestriction.ELEMENT_CONDITIONS, ElementConditionRestriction.class);
		if (elementConditionArray != null)
		{
			for (ElementInstance elementInstance : element.getInstances())
			{
				boolean check = this.checkElementConditions(elementConditionArray, elementInstance);
				if (!check)
					this.makeElementChoice(elementInstance, keyword, withContext, elementQualityName, first, second, type, rangeAttribute, 
							elementGroup);
			}
		}
		else
		{
			for (ElementInstance elementInstance : element.getInstances())
			{
				this.makeElementChoice(elementInstance, keyword, withContext, elementQualityName, first, second, type, rangeAttribute, 
						elementGroup);
			}
		}
	}
	
	private void setupChoice(RestrictedJson<ChoiceRestriction> choiceData) throws Exception
	{	
		boolean withContext = choiceData.getBoolean(ChoiceRestriction.WITH_CONTEXT);
		
		JsonEntityArray<RestrictedJson<ContextConditionRestriction>> contextConditionDataArray = choiceData.getRestrictedJsonArray(ChoiceRestriction.CONTEXT_CONDITIONS, ContextConditionRestriction.class);
		if (contextConditionDataArray != null && !this.checkContextCondition(contextConditionDataArray))
			return;
		
		RestrictedJson<GroupChoiceRestriction> groupChoiceRestriction = choiceData.getRestrictedJson(ChoiceRestriction.GROUP_CHOICE, 
				GroupChoiceRestriction.class);
		ElementGroup elementGroup = this.setupElementGroup(groupChoiceRestriction);
		String keyword = choiceData.getString(ChoiceRestriction.VALUE);
		String first = choiceData.getString(ChoiceRestriction.FIRST);
		RestrictedJson<ElementChoiceRestriction> elementChoiceData = choiceData.getRestrictedJson(ChoiceRestriction.ELEMENT_CHOICE, 
				ElementChoiceRestriction.class);
		
		if (elementChoiceData != null)
		{
			this.setupElementChoice(elementChoiceData, keyword, first, withContext, elementGroup);			
		}
		else
		{
			ElementChoice elementChoice = new ElementChoice();
			elementChoice.keyword = keyword;
			elementChoice.elementGroup = elementGroup;
			if (withContext)
				elementChoice.context = this.pageContext;
			this.addChoice(first, elementChoice);
		}
	}
	
	private String checkForSelectedElementPattern(String bodyText)
	{
		String adjustedText = bodyText;
		Matcher elementMatcher = selectedElementPattern.matcher(adjustedText);
		while(elementMatcher.find())
		{
			String elementType = elementMatcher.group(1);
			String elementQualityType = elementMatcher.group(2);
			Element element = this.scenario.getElement(elementType);
			ElementInstance elementInstance = this.getSelectedElementInstance(element);
			String elementQuality = elementInstance.getStringValue(elementQualityType);
			adjustedText = elementMatcher.replaceFirst(elementQuality);
			elementMatcher = selectedElementPattern.matcher(adjustedText);
		}
		return adjustedText;
	}
	
	private String checkForSelectedPositionPattern(String bodyText)
	{
		String adjustedText = bodyText;
		Matcher elementMatcher = selectedPositionPattern.matcher(adjustedText);
		while(elementMatcher.find())
		{
			String posCoOrd = elementMatcher.group(1);
			int number;
			if (posCoOrd.equals("x"))
			{
				number = this.position.x;
			}
			else
			{
				number = this.position.y;
			}
			adjustedText = elementMatcher.replaceFirst(String.valueOf(number));
			elementMatcher = selectedPositionPattern.matcher(adjustedText);
		}
		return adjustedText;
	}
	
	private String checkForConnectionToSelectedElementPattern(String bodyText) throws Exception
	{
		String adjustedText = bodyText;
		Matcher connectionMatcher = connectionToSelectedElementPattern.matcher(adjustedText);
		while(connectionMatcher.find())
		{
			String elementType = connectionMatcher.group(1);
			String connectionType = connectionMatcher.group(2);
			String elementQualityType = connectionMatcher.group(3);
			ConnectionSet connectionSet = this.scenario.getConnectionSet(connectionType);
			Element element = this.scenario.getElement(elementType);
			ElementInstance firstInstance = this.getSelectedElementInstance(element);
			ElementInstance connectedInstance = connectionSet.get(firstInstance);
			String elementQuality = connectedInstance.getStringValue(elementQualityType);
			adjustedText = connectionMatcher.replaceFirst(elementQuality);
			connectionMatcher = connectionToSelectedElementPattern.matcher(adjustedText);
		}
		return adjustedText;
	}
	
	private String checkForRepeatForElementPattern(String bodyText) throws Exception
	{
		String adjustedText = bodyText;
		Matcher repeatMatcher = repeatForElementPattern.matcher(adjustedText);
		while (repeatMatcher.find())
		{
			String elementTypeName = repeatMatcher.group(1);
			String text = repeatMatcher.group(2);
			
			StringBuffer stringBuffer = new StringBuffer();
			
			Element element = this.scenario.getElement(elementTypeName);
			for (ElementInstance elementInstance : element.getInstances())
			{
				this.processRepeatedElementInstance(text, stringBuffer, elementInstance);
			}
			
			adjustedText = repeatMatcher.replaceFirst(stringBuffer.toString());
			repeatMatcher = repeatForElementPattern.matcher(adjustedText);
		}
		return adjustedText;
	}
	
	private String checkForConditionalRepeatForElementPattern(String bodyText) throws Exception
	{
		String adjustedText = bodyText;
		Matcher repeatMatcher = conditionalRepeatForElementPattern.matcher(adjustedText);
		while (repeatMatcher.find())
		{
			String elementName = repeatMatcher.group(1);
			String elementNumberName = repeatMatcher.group(2);
			String comparatorText = repeatMatcher.group(3);
			int number = Integer.valueOf(repeatMatcher.group(4));
			String text = repeatMatcher.group(5);
			
			StringBuffer stringBuffer = new StringBuffer();		
			Element element = this.scenario.getElement(elementName);
			for (ElementInstance elementInstance : element.getInstances())
			{
				if (Pages.checkNumberComparison(elementInstance, comparatorText, elementNumberName, number))
					this.processRepeatedElementInstance(text, stringBuffer, elementInstance);
			}		
			adjustedText = repeatMatcher.replaceFirst(stringBuffer.toString());
			repeatMatcher = conditionalRepeatForElementPattern.matcher(adjustedText);
		}
		return adjustedText;
	}

	private void processRepeatedElementInstance(String text, StringBuffer stringBuffer, ElementInstance elementInstance)
			throws Exception
	{
		String innerText = text;
		Matcher elementMatcher = repeatedElementPattern.matcher(innerText);
		while (elementMatcher.find())
		{
			String elementQuality = elementMatcher.group(1);
			innerText = elementMatcher.replaceFirst(elementInstance.getStringValue(elementQuality));
			elementMatcher = repeatedElementPattern.matcher(innerText);
		}
		
		Matcher connectionMatcher = connectionToRepeatedElementPattern.matcher(innerText);
		while (connectionMatcher.find())
		{
			String connectionName = connectionMatcher.group(1);
			String elementQuality = connectionMatcher.group(2);
			ConnectionSet connectionSet = this.scenario.getConnectionSet(connectionName);
			ElementInstance connectedInstance = connectionSet.get(elementInstance);
			innerText = connectionMatcher.replaceFirst(connectedInstance.getStringValue(elementQuality));
			connectionMatcher = connectionToRepeatedElementPattern.matcher(innerText);
		}
		stringBuffer.append(innerText);
	}
	
	private String checkPatterns(String bodyText, ArrayList<Integer> adjustments) throws Exception
	{		
		String adjustedText = this.checkForSelectedElementPattern(bodyText);
		adjustedText = this.checkForSelectedPositionPattern(adjustedText);
		adjustedText = this.checkForConnectionToSelectedElementPattern(adjustedText);
		adjustedText = this.checkForRepeatForElementPattern(adjustedText);
		adjustedText = this.checkForConditionalRepeatForElementPattern(adjustedText);
		adjustedText = this.checkForElementAdjustmentText(adjustedText, adjustments);
		return adjustedText;
	}
	
	private String checkForElementAdjustmentText(String bodyText, ArrayList<Integer> adjustmentValues)
	{
		String adjustedText = bodyText;
		Matcher adjustmentMatcher = numberAdjustmentPattern.matcher(adjustedText);
		while (adjustmentMatcher.find())
		{
			String numberString = adjustmentMatcher.group(1);
			int number = Integer.valueOf(numberString);
			String adjustmentValueString = String.valueOf(adjustmentValues.get(number)); 
			adjustedText = adjustmentMatcher.replaceFirst(adjustmentValueString);
			adjustmentMatcher = numberAdjustmentPattern.matcher(adjustedText);
		}		
		return adjustedText;
	}
	
	public static Integer performSum(Integer value, String sumSign, Integer adjustmentValue) throws Exception
	{
		if (sumSign.equals("+"))
		{
			value = value + adjustmentValue;
		}
		else if (sumSign.equals("-"))
		{
			value = value - adjustmentValue;
		}
		else if (sumSign.equals("*"))
		{
			value = value * adjustmentValue;
		}
		else if (sumSign.equals("/"))
		{
			value = value / adjustmentValue;
		}
		else
		{
			throw new Exception("No sign available for sum component.");
		}
		return value;
	}
	
	private void makeElementChoice(ElementInstance elementInstance, String keyword, boolean withContext, String elementNamingQuality, 
			String startString, String endString, String typeString, String rangeAttributeString, ElementGroup elementGroup) throws Exception
	{
		ElementChoiceType elementChoiceType = ElementChoiceType.getByName(typeString);
		
		ElementChoice elementChoice = new ElementChoice();
		elementChoice.keyword = keyword;
		elementChoice.elementInstance = elementInstance;
		elementChoice.elementChoiceType = elementChoiceType;
		elementChoice.rangeAttribute = rangeAttributeString;
		if (withContext)
			elementChoice.context = this.getPageContext();
		String qualityString = elementInstance.getDetailValueByName(elementNamingQuality);
		String keyString;
		if (elementInstance.getElement().getUnique())
		{
			keyString = startString;
		}
		else
		{
			keyString = startString + qualityString + endString;
		}		
		this.addChoice(keyString, elementChoice);
	}
	
	private ElementGroup setupElementGroup(RestrictedJson<GroupChoiceRestriction> groupChoiceRestrictionData) throws Exception
	{
		if (groupChoiceRestrictionData == null)
			return null;
		String positionCounterName = groupChoiceRestrictionData.getString(GroupChoiceRestriction.POSITION_COUNTER_NAME);
		return this.setupElementGroup(positionCounterName);
	}
	
	private void addChoice(String choiceName, ElementChoice elementChoice)
	{
		this.choiceMap.put(choiceName, elementChoice);
		this.choiceList.add(choiceName);
	}
	
	private enum ReturnChoiceType
	{
		CONTEXT_TOP("contextTop");
		
		private String name;
		
		private ReturnChoiceType(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public static ReturnChoiceType fromString(String name) throws Exception
		{
			for (ReturnChoiceType returnChoiceType : ReturnChoiceType.values())
			{
				if (returnChoiceType.getName().equals(name))
					return returnChoiceType;
			}
			throw new Exception("Unrecognised return choice type: " + name);
		}
	}
}
