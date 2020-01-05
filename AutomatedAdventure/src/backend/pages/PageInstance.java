package backend.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.Element;
import backend.Element.ElementInstance;
import backend.Scenario;

public class PageInstance
{
	private static final String RANDOM = "random";
	private static final String ALL = "all";
	private static final Pattern mainPattern = Pattern.compile("<head>([\\s\\S]*)</head><body>([\\s\\S]*)</body>");
	private static final Pattern choicePattern = Pattern.compile("choice:([\\s\\S]*):([\\s\\S]*)");
	private static final Pattern elementHeadPattern = Pattern.compile("element:(.*):(.*):(.*)");
	private static final Pattern elementListHeadPattern = Pattern.compile("elementList:(.*):(.*):(.*)");
	private static final Pattern elementBodyPattern = Pattern.compile("<element:([^<>]*):([^<>]*)>");
	private static final Pattern elementItemPattern = Pattern.compile("<elementItem:([^<>]*)>");
	private static final Pattern loopThroughListPattern = Pattern.compile("<loopThroughList:([^<>]*)>([\\s\\S]*)</loopThroughList>");
	Scenario scenario;
	String pageTemplate;
	HashMap<String, ElementInstance> elementMap = new HashMap<String, ElementInstance>();
	HashMap<String, ArrayList<ElementInstance>> elementListMap = new HashMap<String, ArrayList<ElementInstance>>();
	HashMap<String, String> choiceMap = new HashMap<String, String>();
	
	public HashMap<String, String> getChoiceMap() {
		return choiceMap;
	}

	public PageInstance(Scenario scenario, String pageTemplate)
	{
		this.scenario = scenario;
		this.pageTemplate = pageTemplate;
	}
	
	public String getText()
	{
		Matcher matcher = mainPattern.matcher(this.pageTemplate);
		matcher.find();
		String headerText = matcher.group(1);
		String bodyText = matcher.group(2);
		this.assessHead(headerText);
		String adjustedText = this.checkPatterns(bodyText);
		return adjustedText;
	}
	
	private String checkPatterns(String bodyText)
	{
		String adjustedText = bodyText;
		
		Matcher elementMatcher = elementBodyPattern.matcher(adjustedText);
		while(elementMatcher.find())
		{
			String elementName = elementMatcher.group(1);
			String elementOption = elementMatcher.group(2);
			ElementInstance elementInstance = this.elementMap.get(elementName);
			String replaceText = elementInstance.getValueByName(elementOption);
			adjustedText = elementMatcher.replaceFirst(replaceText);
			elementMatcher = elementBodyPattern.matcher(adjustedText);
		}
		
		Matcher loopMatcher = loopThroughListPattern.matcher(adjustedText);
		while(loopMatcher.find())
		{
			String elementListName = loopMatcher.group(1);
			String loopBody = loopMatcher.group(2);
			
			ArrayList<ElementInstance> elementList = this.elementListMap.get(elementListName);
			StringBuffer stringBuffer = new StringBuffer();		
			
			for (ElementInstance elementInstance : elementList)
			{
				String adjustedBody = new String(loopBody);
				Matcher itemMatcher = elementItemPattern.matcher(adjustedBody);
				while (itemMatcher.find())
				{
					String elementItemName = itemMatcher.group(1);
					String replaceItemText = elementInstance.getValueByName(elementItemName);
					adjustedBody = itemMatcher.replaceFirst(replaceItemText);
					itemMatcher = elementItemPattern.matcher(adjustedBody);
				}
				stringBuffer.append(adjustedBody);
			}
			
			adjustedText = loopMatcher.replaceFirst(stringBuffer.toString());		
			loopMatcher = loopThroughListPattern.matcher(adjustedText);
		}
		
		return adjustedText;
	}
	
	
	
	private void assessHead(String headerText)
	{
		String[] lines = headerText.split("\r\n");
		for (String line : lines)
		{
			if (this.checkForElement(line))
				continue;
			if (this.checkForElementList(line))
				continue;
			if (this.checkForChoice(line))
				continue;
		}
	}
	
	private boolean checkForElementList(String line)
	{
		Matcher matcher = elementListHeadPattern.matcher(line);
		if (matcher.find())
		{	
			String elementListName = matcher.group(1);
			String elementListType = matcher.group(2);
			String elementListDefinition = matcher.group(3);
			if (elementListDefinition.equals(ALL))
			{
				Element element = this.scenario.getElement(elementListType);			
				this.elementListMap.put(elementListName, element.getInstances());
			}	
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkForElement(String line)
	{
		Matcher matcher = elementHeadPattern.matcher(line);
		if (matcher.find())
		{	
			String elementName = matcher.group(1);
			String elementType = matcher.group(2);
			String elementDefinition = matcher.group(3);
			if (elementDefinition.equals(RANDOM))
			{
				ElementInstance elementInstance = this.scenario.getRandomElementInstance(elementType);
				this.elementMap.put(elementName, elementInstance);
			}	
			return true;
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
			String choiceText = matcher.group(2);
			this.choiceMap.put(choiceName, choiceText);
			return true;
		}
		else
		{
			return false;
		}
	}
}
