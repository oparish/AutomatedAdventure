package backend.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.Element;
import backend.Element.ElementInstance;
import backend.NumberRange;
import backend.Scenario;
import backend.component.ConnectionSet;
import json.JsonEntityArray;
import json.RestrictedJson;
import json.restrictions.ChoiceRestriction;
import json.restrictions.ContextConditionRestriction;
import json.restrictions.ElementAdjustmentRestriction;
import json.restrictions.ElementAdjustmentType;
import json.restrictions.ElementChoiceRestriction;
import json.restrictions.ElementConditionRestriction;
import json.restrictions.MakeConnectionRestriction;
import json.restrictions.MakeElementRestriction;
import json.restrictions.PageRestriction;
import json.restrictions.SumComponentRestriction;
import json.restrictions.SumRestriction;
import main.Main;
import main.Pages;

public class PageInstance
{
	private static final Pattern selectedElementPattern = Pattern.compile("<selectedElement:([^<>]*):([^<>]*)>");
	private static final Pattern connectionToSelectedElementPattern = Pattern.compile("<connectionToSelectedElement:([^<>]*):([^<>]*):([^<>]*)>");
	private static final Pattern connectionToRepeatedElementPattern = Pattern.compile("<connectionToRepeatedElement:([^<>]*):([^<>]*)>");
	private static final Pattern repeatForElementPattern = Pattern.compile("<repeatForElement:([^<>]*)>([\\s\\S]*)</repeatForElement>");
	private static final Pattern conditionalRepeatForElementPattern = Pattern.compile("<conditionalRepeatForElement:([^<>]*):([^<>]*):([<>!]?=?):(-?\\d+)>([\\s\\S]*)</conditionalRepeatForElement>");
	private static final Pattern repeatedElementPattern = Pattern.compile("<repeatedElement:([^<>]*)>");
	private static final Pattern numberAdjustmentPattern = Pattern.compile("<numberAdjustment:([0-9]*)>");
	private static final Pattern numberReferencePattern = Pattern.compile("^(.*):(.*)$");
	
	Scenario scenario;
	RestrictedJson<PageRestriction> pageJson;
	public RestrictedJson<PageRestriction> getPageJson() {
		return pageJson;
	}

	PageContext pageContext;
	HashMap<String, ElementChoice> choiceMap = new HashMap<String, ElementChoice>();
	ArrayList<String> choiceList = new ArrayList<String>();
	
	public ArrayList<String> getChoiceList() {
		return choiceList;
	}

	public HashMap<String, ElementChoice> getChoiceMap() {
		return choiceMap;
	}

	public PageInstance(Scenario scenario, PageContext pageContext, RestrictedJson<PageRestriction> pageJson)
	{
		this.scenario = scenario;
		this.pageJson = pageJson;
		this.pageContext = pageContext;
	}
	
	public Scenario getScenario() {
		return scenario;
	}

	public PageContext getPageContext() {
		return pageContext;
	}

	public String getText() throws Exception
	{
		this.makeElements();
		this.makeConnections();
		ArrayList<Integer> adjustments = this.makeElementAdjustments();
		this.setupChoices();
		
		String adjustedText = this.checkPatterns(this.pageJson.getString(PageRestriction.VALUE), adjustments);
		return adjustedText;
	}
	
	private boolean checkContextCondition(RestrictedJson<ContextConditionRestriction> contextConditionData) throws Exception
	{
		String elementName = contextConditionData.getString(ContextConditionRestriction.ELEMENT_NAME);
		Element element = this.scenario.getElement(elementName);
		ElementInstance elementInstance = this.getSelectedElementInstance(element);
		return ContextConditionRestriction.checkCondition(this.scenario, contextConditionData, elementInstance);
	}
	
	private void setupChoices() throws Exception
	{
		JsonEntityArray<RestrictedJson<ChoiceRestriction>> choiceDataArray = 
				this.pageJson.getRestrictedJsonArray(PageRestriction.CHOICES, ChoiceRestriction.class);
		
		if (choiceDataArray == null)
			return;
		
		for (int i = 0; i < choiceDataArray.size(); i++)
		{
			this.setupChoice(choiceDataArray.getMemberAt(i));
		}
	}
	
	private void setupChoice(RestrictedJson<ChoiceRestriction> choiceData) throws Exception
	{	
		boolean withContext = choiceData.getBoolean(ChoiceRestriction.WITH_CONTEXT);
		
		RestrictedJson<ContextConditionRestriction> contextConditionData = choiceData.getRestrictedJson(ChoiceRestriction.CONTEXT_CONDITION, ContextConditionRestriction.class);
		if (contextConditionData != null && !this.checkContextCondition(contextConditionData))
			return;
		
		String keyword = choiceData.getString(ChoiceRestriction.VALUE);
		String first = choiceData.getString(ChoiceRestriction.FIRST);
		RestrictedJson<ElementChoiceRestriction> elementChoiceData = choiceData.getRestrictedJson(ChoiceRestriction.ELEMENT_CHOICE, ElementChoiceRestriction.class);
		
		if (elementChoiceData != null)
		{
			String elementName = elementChoiceData.getString(ElementChoiceRestriction.ELEMENT_NAME);
			String elementQualityName = elementChoiceData.getString(ElementChoiceRestriction.ELEMENT_QUALITY);
			String second = elementChoiceData.getString(ElementChoiceRestriction.SECOND);
			
			Element element = this.scenario.getElement(elementName);
			
			RestrictedJson<ElementConditionRestriction> elementConditionData = elementChoiceData.getRestrictedJson(
					ElementChoiceRestriction.ELEMENT_CONDITION, ElementConditionRestriction.class);
			
			if (elementConditionData != null)
			{
				String elementQualityText = elementConditionData.getString(ElementConditionRestriction.ELEMENT_QUALITY);
				String comparatorText = elementConditionData.getString(ElementConditionRestriction.TYPE);
				int value = elementConditionData.getNumber(ElementConditionRestriction.NUMBER_VALUE);		
			
				for (ElementInstance elementInstance : element.getInstances())
				{
					if (Pages.checkComparison(elementInstance, comparatorText, elementQualityText, value))
						this.makeElementChoice(elementInstance, keyword, withContext, elementQualityName, first, second);
				}	
			}
			else
			{
				for (ElementInstance elementInstance : element.getInstances())
				{
					this.makeElementChoice(elementInstance, keyword, withContext, elementQualityName, first, second);
				}
			}	
					
		}
		else
		{
			ElementChoice elementChoice = new ElementChoice();
			elementChoice.keyword = keyword;
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
	
	private ElementInstance getSelectedElementInstance(Element element)
	{
		if (element.getUnique())
			return element.getUniqueInstance();
		else
			return this.pageContext.getElementInstance(element);
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
				if (Pages.checkComparison(elementInstance, comparatorText, elementNumberName, number))
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
			adjustedText = adjustmentMatcher.replaceAll(adjustmentValueString);
		}		
		return adjustedText;
	}
	
	private ArrayList<Integer> makeElementAdjustments() throws Exception
	{
		ArrayList<Integer> adjustmentValues = new ArrayList<Integer>();
		JsonEntityArray<RestrictedJson<ElementAdjustmentRestriction>> elementAdjustmentArray = 
				this.pageJson.getRestrictedJsonArray(PageRestriction.ELEMENT_ADJUSTMENTS, ElementAdjustmentRestriction.class);
		
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
					connectedInstance.adjustNumber(elementNumberName, value);
					break;
				case EACH:
					for (ElementInstance instance : element.getInstances())
					{
						instance.adjustNumber(elementNumberName, value);
					}
					break;
				case SELECTED:
					ElementInstance selectedInstance = this.getSelectedElementInstance(element);
					selectedInstance.adjustNumber(elementNumberName, value);
					break;
			}
		}
		
		return adjustmentValues;
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
	
	private void makeElementChoice(ElementInstance elementInstance, String keyword, boolean withContext, String elementNamingQuality, String startString, String endString)
	{
		ElementChoice elementChoice = new ElementChoice();
		elementChoice.keyword = keyword;
		elementChoice.elementInstance = elementInstance;
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
	
	private void makeConnections() throws Exception
	{
		JsonEntityArray<RestrictedJson<MakeConnectionRestriction>> makeConnectionArray = 
				this.pageJson.getRestrictedJsonArray(PageRestriction.MAKE_CONNECTIONS, MakeConnectionRestriction.class);
		
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
	
	private void makeElements() throws Exception
	{
		JsonEntityArray<RestrictedJson<MakeElementRestriction>> makeElementArray = 
				this.pageJson.getRestrictedJsonArray(PageRestriction.MAKE_ELEMENTS, MakeElementRestriction.class);
		
		if (makeElementArray == null)
			return;
		
		for (int i = 0; i < makeElementArray.size(); i++)
		{
			RestrictedJson<MakeElementRestriction> makeElementData = makeElementArray.getMemberAt(i);
			String elementName = makeElementData.getString(MakeElementRestriction.ELEMENT_NAME);
			int numberValue = makeElementData.getNumber(MakeElementRestriction.NUMBER_VALUE);
			Element element = this.scenario.getElement(elementName);
			element.makeInstances(numberValue);
		}
	}
	
	private void addChoice(String choiceName, ElementChoice elementChoice)
	{
		this.choiceMap.put(choiceName, elementChoice);
		this.choiceList.add(choiceName);
	}
}
