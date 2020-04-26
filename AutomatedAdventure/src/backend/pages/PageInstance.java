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
import main.Main;

public class PageInstance
{
	private static final Pattern mainPattern = Pattern.compile("<head>([\\s\\S]*)</head><body>([\\s\\S]*)</body>");
	private static final Pattern selectedElementPattern = Pattern.compile("<selectedElement:([^<>]*):([^<>]*)>");
	private static final Pattern connectionToSelectedElementPattern = Pattern.compile("<connectionToSelectedElement:([^<>]*):([^<>]*):([^<>]*)>");
	
	private static final Pattern redirectOuterPattern = Pattern.compile("((?:<redirect:(?:.*)))+<else:([^<>]*)>");
	private static final Pattern redirectInnerPattern = Pattern.compile("<redirect:([^<>]*):([^<>]*):([^<>]*):([<>]?=?):(-?\\d+)>");
	private static final Pattern randomRedirectOuterPattern = Pattern.compile("(<randomRedirect:([^<>]*):(\\d+)>)+");
	private static final Pattern randomRedirectInnerPattern = Pattern.compile("<randomRedirect:([^<>]*):(\\d+)>");
	
	private static final Pattern choicePattern = Pattern.compile("choice:([\\s\\S]*):([\\s\\S]*)");
	private static final Pattern conditionalChoicePattern = Pattern.compile("choice:([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):([<>!]?=?):(-?\\d+)");
	private static final Pattern elementChoicePattern = Pattern.compile("elementChoice:([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):([\\s\\S]*)");
	private static final Pattern conditionalElementChoicePattern = Pattern.compile("elementChoice:([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):([\\s\\S]*):([<>!]?=?):(-?\\d+)");
	private static final Pattern elementHeadPattern = Pattern.compile("element:(.*):(\\d+)");
	private static final Pattern connectionHeadPattern = Pattern.compile("connectionList:(.*):(\\d+)");
	private static final Pattern eachElementAdjustPattern = Pattern.compile("eachElementAdjust:([\\s\\S]*):([\\s\\S]*):(-?\\d+)");
	private static final Pattern adjustSelectedElementPattern = Pattern.compile("selectedElementAdjust:([\\s\\S]*):([\\s\\S]*):(-?\\d+)");
	
	Scenario scenario;
	String pageTemplate;
	PageContext pageContext;
	HashMap<String, ElementChoice> choiceMap = new HashMap<String, ElementChoice>();
	
	public HashMap<String, ElementChoice> getChoiceMap() {
		return choiceMap;
	}

	public PageInstance(Scenario scenario, PageContext pageContext, String pageTemplate)
	{
		this.scenario = scenario;
		this.pageTemplate = pageTemplate;
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
		Matcher matcher = mainPattern.matcher(this.pageTemplate);
		matcher.find();
		String headerText = matcher.group(1);
		String bodyText = matcher.group(2);
		this.assessHead(headerText);
		String adjustedText = this.checkPatterns(bodyText);
		return adjustedText;
	}
	public String getRandomRedirect() throws Exception
	{
		Matcher outerMatcher = randomRedirectOuterPattern.matcher(this.pageTemplate);
		if (outerMatcher.find())
		{		
			Matcher innerMatcher = randomRedirectInnerPattern.matcher(this.pageTemplate);
			
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
		Matcher outerMatcher = redirectOuterPattern.matcher(this.pageTemplate);
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
				String valueText = innerMatcher.group(5);
				
				Element element = this.scenario.getElement(elementType);
				if (this.checkComparison(this.pageContext.getElementInstance(element), comparatorText, elementNumberName, valueText))
					return pageName;
			}
			
			return outerMatcher.group(2);
		}
		else
		{
			return null;
		}
	}
	
	private boolean checkComparison(ElementInstance elementInstance, String comparatorText, String elementNumberName, String valueText) throws Exception
	{
		Comparator comparator = Comparator.fromText(comparatorText);
		int value = Integer.valueOf(valueText);
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
	
	private String checkPatterns(String bodyText) throws Exception
	{		
		String adjustedText = bodyText;
		Matcher elementMatcher = selectedElementPattern.matcher(adjustedText);
		while(elementMatcher.find())
		{
			String elementType = elementMatcher.group(1);
			String elementQualityType = elementMatcher.group(2);
			Element element = this.scenario.getElement(elementType);
			ElementInstance elementInstance = this.pageContext.getElementInstance(element);
			String elementQuality = elementInstance.getStringValue(elementQualityType);
			adjustedText = elementMatcher.replaceFirst(elementQuality);
			elementMatcher = selectedElementPattern.matcher(adjustedText);
		}
		
		Matcher connectionMatcher = connectionToSelectedElementPattern.matcher(adjustedText);
		while(connectionMatcher.find())
		{
			String elementType = connectionMatcher.group(1);
			String connectionType = connectionMatcher.group(2);
			String elementQualityType = connectionMatcher.group(3);
			ConnectionSet connectionSet = this.scenario.getConnectionSet(connectionType);
			Element element = this.scenario.getElement(elementType);
			ElementInstance firstInstance = this.pageContext.getElementInstance(element);
			ElementInstance connectedInstance = connectionSet.get(firstInstance);
			String elementQuality = connectedInstance.getStringValue(elementQualityType);
			adjustedText = connectionMatcher.replaceFirst(elementQuality);
			connectionMatcher = selectedElementPattern.matcher(adjustedText);
		}
		
		return adjustedText;
	}
	
	private void assessHead(String headerText) throws Exception
	{
		String[] lines = headerText.split("\r\n");
		
		for (String line : lines)
		{
			if (this.checkForElement(line))
				continue;
			if (this.checkForConnection(line))
				continue;
			if (this.checkForConditionalChoice(line))
				continue;
			if (this.checkForChoice(line))
				continue;
			if (this.checkForConditionalElementChoice(line))
				continue;
			if (this.checkForElementChoice(line))
				continue;
			if (this.checkForEachElementAdjust(line))
				continue;
			if (this.checkForSelectedElementAdjust(line))
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
			
			ElementInstance elementInstance;
			if (element.getUnique())
				elementInstance = element.getUniqueInstance();
			else
				elementInstance = this.pageContext.getElementInstance(element);
			
			elementInstance.adjustNumber(elementNumberName, value);
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
	
	private boolean checkForElementChoice(String line)
	{
		Matcher matcher = elementChoicePattern.matcher(line);
		if (matcher.find())
		{	
			String keyword = matcher.group(1);
			String elementName = matcher.group(2);
			String elementNamingQuality = matcher.group(3);
			String startString = matcher.group(4);
			String endString = matcher.group(5);
			
			Element element = this.scenario.getElement(elementName);
			
			for(ElementInstance elementInstance : element.getInstances())
			{
				this.makeElementChoice(elementInstance, keyword, elementNamingQuality, startString, endString);
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkForConditionalElementChoice(String line) throws Exception
	{
		Matcher matcher = conditionalElementChoicePattern.matcher(line);
		if (matcher.find())
		{	
			String keyword = matcher.group(1);
			String elementName = matcher.group(2);
			String elementNamingQuality = matcher.group(3);
			String elementNumberName = matcher.group(4);
			String startString = matcher.group(5);
			String endString = matcher.group(6);
			String comparatorText = matcher.group(7);
			String valueText = matcher.group(8);
			
			Element element = this.scenario.getElement(elementName);
			
			for (ElementInstance elementInstance : element.getInstances())
			{
				if (this.checkComparison(elementInstance, comparatorText, elementNumberName, valueText))
					this.makeElementChoice(elementInstance, keyword, elementNamingQuality, startString, endString);
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void makeElementChoice(ElementInstance elementInstance, String keyword, String elementNamingQuality, String startString, String endString)
	{
		ElementChoice elementChoice = new ElementChoice();
		elementChoice.keyword = keyword;
		elementChoice.elementInstance = elementInstance;
		String qualityString = elementInstance.getDetailValueByName(elementNamingQuality);
		String keyString = startString + qualityString + endString;
		this.choiceMap.put(keyString, elementChoice);
	}
	
	private boolean checkForConnection(String line) throws Exception
	{
		Matcher matcher = connectionHeadPattern.matcher(line);
		if (matcher.find())
		{	
			String connectionName = matcher.group(1);
			int connectionNumber = Integer.valueOf(matcher.group(2));
			ConnectionSet connectionSet = this.scenario.getConnectionSet(connectionName);
			connectionSet.makeUniqueConnections(connectionNumber);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkForElement(String line) throws Exception
	{
		Matcher matcher = elementHeadPattern.matcher(line);
		if (matcher.find())
		{	
			String elementName = matcher.group(1);
			int elementNumber = Integer.valueOf(matcher.group(2));
			Element element = this.scenario.getElement(elementName);
			element.makeInstances(elementNumber);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkForConditionalChoice(String line) throws Exception
	{
		Matcher matcher = conditionalChoicePattern.matcher(line);
		if (matcher.find())
		{	
			String choiceName = matcher.group(1);
			String keyword = matcher.group(2);
			String elementType = matcher.group(3);
			String elementNumberName = matcher.group(4);
			String comparatorText = matcher.group(5);
			String numberString = matcher.group(6);
			
			Element element = this.scenario.getElement(elementType);	
			ElementInstance elementInstance = this.pageContext.getElementInstance(element);
			ElementChoice elementChoice = new ElementChoice();
			elementChoice.keyword = keyword;
			
			if (this.checkComparison(elementInstance, comparatorText, elementNumberName, numberString))
			{
				this.choiceMap.put(choiceName, elementChoice);
				return true;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkForChoice(String line)
	{
		Matcher matcher = choicePattern.matcher(line);
		if (matcher.find())
		{	
			String choiceName = matcher.group(1);
			String keyword = matcher.group(2);
			ElementChoice elementChoice = new ElementChoice();
			elementChoice.keyword = keyword;
			this.choiceMap.put(choiceName, elementChoice);
			return true;
		}
		else
		{
			return false;
		}
	}
}
