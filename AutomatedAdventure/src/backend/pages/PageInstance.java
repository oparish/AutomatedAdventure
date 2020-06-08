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
import json.JsonEntityMap;
import json.RestrictedJson;
import json.restrictions.ChoiceRestriction;
import json.restrictions.ConditionRestriction;
import json.restrictions.ContextConditionRestriction;
import json.restrictions.ElementChoiceRestriction;
import json.restrictions.ElementConditionRestriction;
import json.restrictions.MakeConnectionRestriction;
import json.restrictions.MakeElementRestriction;
import json.restrictions.PageRestriction;
import main.Main;

public class PageInstance
{
	private static final Pattern mainPattern = Pattern.compile("<head>([\\s\\S]*)</head><body>([\\s\\S]*)</body>");
	private static final Pattern selectedElementPattern = Pattern.compile("<selectedElement:([^<>]*):([^<>]*)>");
	private static final Pattern connectionToSelectedElementPattern = Pattern.compile("<connectionToSelectedElement:([^<>]*):([^<>]*):([^<>]*)>");
	private static final Pattern connectionToRepeatedElementPattern = Pattern.compile("<connectionToRepeatedElement:([^<>]*):([^<>]*)>");
	private static final Pattern repeatForElementPattern = Pattern.compile("<repeatForElement:([^<>]*)>([\\s\\S]*)</repeatForElement>");
	private static final Pattern conditionalRepeatForElementPattern = Pattern.compile("<conditionalRepeatForElement:([^<>]*):([^<>]*):([<>!]?=?):(-?\\d+)>([\\s\\S]*)</conditionalRepeatForElement>");
	private static final Pattern repeatedElementPattern = Pattern.compile("<repeatedElement:([^<>]*)>");
	
	private static final Pattern redirectOuterPattern = Pattern.compile("((?:<redirect:(?:.*)))+<else:([^<>]*)>");
	private static final Pattern redirectInnerPattern = Pattern.compile("<redirect:([^<>]*):([^<>]*):([^<>]*):([<>]?=?):(-?\\d+)>");
	private static final Pattern randomRedirectOuterPattern = Pattern.compile("(<randomRedirect:([^<>]*):(\\d+)>)+");
	private static final Pattern randomRedirectInnerPattern = Pattern.compile("<randomRedirect:([^<>]*):(\\d+)>");
	
	private static final Pattern eachElementAdjustPattern = Pattern.compile("eachElementAdjust:([\\s\\S]*):([\\s\\S]*):(-?\\d+)");
	private static final Pattern adjustSelectedElementPattern = Pattern.compile("selectedElementAdjust:([\\s\\S]*):([\\s\\S]*):(-?\\d+)");
	private static final Pattern adjustConnectedElementPattern = Pattern.compile("connectedElementAdjust:([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):(-?\\d+)");
	
	Scenario scenario;
	RestrictedJson<PageRestriction> pageJson;
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
		this.setupChoices();
		this.makeElements();
		this.makeConnections();
		
		Matcher matcher = mainPattern.matcher(this.pageJson.getString(PageRestriction.VALUE));
		matcher.find();
		String headerText = matcher.group(1);
		String bodyText = matcher.group(2);
		this.assessHead(headerText);
		String adjustedText = this.checkPatterns(bodyText);
		return adjustedText;
	}
	
	private boolean checkContextCondition(RestrictedJson<ContextConditionRestriction> contextConditionData) throws Exception
	{
		String comparatorText = contextConditionData.getString(ContextConditionRestriction.TYPE);
		int value = contextConditionData.getNumber(ContextConditionRestriction.NUMBER_VALUE);
		String elementQualityText = contextConditionData.getString(ContextConditionRestriction.ELEMENT_QUALITY);
		String elementName = contextConditionData.getString(ContextConditionRestriction.ELEMENT_NAME);
		Element element = this.scenario.getElement(elementName);
		ElementInstance elementInstance = this.getSelectedElementInstance(element);
		return this.checkComparison(elementInstance, comparatorText, elementQualityText, value);
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
					if (this.checkComparison(elementInstance, comparatorText, elementQualityText, value))
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
	
	public String getRandomRedirect() throws Exception
	{
		String pageText = this.pageJson.getString(PageRestriction.VALUE);
		Matcher outerMatcher = randomRedirectOuterPattern.matcher(pageText);
		if (outerMatcher.find())
		{		
			Matcher innerMatcher = randomRedirectInnerPattern.matcher(pageText);
			
			HashMap<String, NumberRange> pages = new HashMap<String, NumberRange>();
			int total = 0;
			
			while(innerMatcher.find())
			{
				String pageName = innerMatcher.group(1);
				String numberString = innerMatcher.group(2);
				int number = Integer.valueOf(numberString);
				pages.put(pageName, new NumberRange(total, total + number));
				total += number;
			}
			
			int result = Main.getRndm(total) + 1;
			
			for (Entry<String, NumberRange> entry: pages.entrySet())
			{
				NumberRange numberRange = entry.getValue();
				if (numberRange.check(result))
				{
					return entry.getKey();
				}
			}
			
			return null;
		}
		else
		{
			return null;
		}
	}
	
	public String getRedirect() throws Exception
	{
		String pageText = this.pageJson.getString(PageRestriction.VALUE);
		Matcher outerMatcher = redirectOuterPattern.matcher(pageText);
		if (outerMatcher.find())
		{
			String redirects = outerMatcher.group(1);			
			Matcher innerMatcher = redirectInnerPattern.matcher(redirects);
			
			while(innerMatcher.find())
			{
				String pageName = innerMatcher.group(1);
				String elementType = innerMatcher.group(2);
				String elementNumberName = innerMatcher.group(3);
				String comparatorText = innerMatcher.group(4);
				int value = Integer.valueOf(innerMatcher.group(5));
				
				Element element = this.scenario.getElement(elementType);
				if (this.checkComparison(this.getSelectedElementInstance(element), comparatorText, elementNumberName, value))
					return pageName;
			}
			
			return outerMatcher.group(2);
		}
		else
		{
			return null;
		}
	}
	
	private boolean checkComparison(ElementInstance elementInstance, String comparatorText, String elementNumberName, int value) throws Exception
	{
		Comparator comparator = Comparator.fromText(comparatorText);		
		int elementNumber = elementInstance.getNumberValueByName(elementNumberName);
		return this.checkComparison(comparator, elementNumber, value);
	}
	
	private boolean checkComparison(Comparator comparator, int elementNumber, int value) throws Exception
	{
		switch(comparator)
		{
			case GREATER_THAN:
				return (elementNumber > value);
			case GREATER_THAN_OR_EQUAL:
				return (elementNumber >= value);
			case LESS_THAN:
				return (elementNumber < value);
			case LESS_THAN_OR_EQUAL:
				return (elementNumber <= value);
			case EQUAL:
				return (elementNumber == value);
			case NOT_EQUAL:
				return (elementNumber != value);
		}
		throw new Exception("Unrecognised comparator type.");
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
				if (this.checkComparison(elementInstance, comparatorText, elementNumberName, number))
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
	
	private String checkPatterns(String bodyText) throws Exception
	{		
		String adjustedText = this.checkForSelectedElementPattern(bodyText);
		adjustedText = this.checkForConnectionToSelectedElementPattern(adjustedText);
		adjustedText = this.checkForRepeatForElementPattern(adjustedText);
		adjustedText = this.checkForConditionalRepeatForElementPattern(adjustedText);
		return adjustedText;
	}
	
	private void assessHead(String headerText) throws Exception
	{
		
		String[] lines = headerText.split("\r\n");
		
		for (String line : lines)
		{
			if (this.checkForEachElementAdjust(line))
				continue;
			if (this.checkForSelectedElementAdjust(line))
				continue;
			if (this.checkForConnectedlementAdjust(line))
				continue;
		}
	}

	private boolean checkForSelectedElementAdjust(String line)
	{
		Matcher matcher = adjustSelectedElementPattern.matcher(line);
		if (matcher.find())
		{
			String elementType = matcher.group(1);
			String elementNumberName = matcher.group(2);
			String valueText = matcher.group(3);
			Element element = this.scenario.getElement(elementType);
			int value = Integer.valueOf(valueText);
			ElementInstance elementInstance = this.getSelectedElementInstance(element);
			elementInstance.adjustNumber(elementNumberName, value);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkForConnectedlementAdjust(String line) throws Exception
	{
		Matcher matcher = adjustConnectedElementPattern.matcher(line);
		if (matcher.find())
		{
			String elementType = matcher.group(1);
			String connectionType = matcher.group(2);
			String elementNumberName = matcher.group(3);
			String valueText = matcher.group(4);
			Element element = this.scenario.getElement(elementType);
			ConnectionSet connectionSet = this.scenario.getConnectionSet(connectionType);
			ElementInstance selectedInstance = this.getSelectedElementInstance(element);
			ElementInstance connectedInstance = connectionSet.get(selectedInstance);		
			int value = Integer.valueOf(valueText);
			connectedInstance.adjustNumber(elementNumberName, value);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkForEachElementAdjust(String line)
	{
		Matcher matcher = eachElementAdjustPattern.matcher(line);
		if (matcher.find())
		{
			String elementType = matcher.group(1);
			String elementNumberType = matcher.group(2);
			int value = Integer.valueOf(matcher.group(3));
			Element element = this.scenario.getElement(elementType);
			for (ElementInstance instance : element.getInstances())
			{
				instance.adjustNumber(elementNumberType, value);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void makeElementChoice(ElementInstance elementInstance, String keyword, boolean withContext, String elementNamingQuality, String startString, String endString)
	{
		ElementChoice elementChoice = new ElementChoice();
		elementChoice.keyword = keyword;
		elementChoice.elementInstance = elementInstance;
		if (withContext)
			elementChoice.context = this.getPageContext();
		String qualityString = elementInstance.getDetailValueByName(elementNamingQuality);
		String keyString = startString + qualityString + endString;
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
