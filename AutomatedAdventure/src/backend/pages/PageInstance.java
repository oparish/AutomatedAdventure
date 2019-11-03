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
	private static final Pattern mainPattern = Pattern.compile("<head>([\\s\\S]*)</head><body>(.*)</body>");
	private static final Pattern elementHeadPattern = Pattern.compile("element:(.*):(.*):(.*)");
	private static final Pattern elementListHeadPattern = Pattern.compile("elementList:(.*):(.*):(.*)");
	private static final Pattern elementBodyPattern = Pattern.compile("<element:([^<>]*):([^<>]*)>");
	Scenario scenario;
	String pageTemplate;
	HashMap<String, ElementInstance> elementMap = new HashMap<String, ElementInstance>();
	HashMap<String, ArrayList<ElementInstance>> elementListMap = new HashMap<String, ArrayList<ElementInstance>>();
	
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
		
		Matcher matcher = elementBodyPattern.matcher(adjustedText);
		while(matcher.find())
		{
			String elementName = matcher.group(1);
			String elementOption = matcher.group(2);
			ElementInstance elementInstance = this.elementMap.get(elementName);
			String replaceText = elementInstance.getValueByName(elementOption);
			adjustedText = matcher.replaceFirst(replaceText);
			matcher = elementBodyPattern.matcher(adjustedText);
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
}
