package backend.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.Element;
import backend.Element.ElementInstance;
import backend.Scenario;
import backend.component.ConnectionSet;

public class PageInstance
{
	private static final Pattern mainPattern = Pattern.compile("<head>([\\s\\S]*)</head><body>([\\s\\S]*)</body>");
	private static final Pattern choicePattern = Pattern.compile("choice:([\\s\\S]*):([\\s\\S]*)");
	private static final Pattern elementChoicePattern = Pattern.compile("elementChoice:([\\s\\S]*):([\\s\\S]*):([\\s\\S]*)");
	private static final Pattern elementHeadPattern = Pattern.compile("element:(.*):(\\d+)");
	private static final Pattern connectionHeadPattern = Pattern.compile("connectionList:(.*):(\\d+)");
	Scenario scenario;
	String pageTemplate;
	PageContext pageContext;
	HashMap<String, ElementChoice> choiceMap = new HashMap<String, ElementChoice>();
	HashMap<String, ConnectionSet> connectionMap = new HashMap<String, ConnectionSet>();
	
	public HashMap<String, ElementChoice> getChoiceMap() {
		return choiceMap;
	}

	public PageInstance(Scenario scenario, PageContext pageContext, String pageTemplate)
	{
		this.scenario = scenario;
		this.pageTemplate = pageTemplate;
		this.pageContext = pageContext;
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
	
	private String checkPatterns(String bodyText)
	{
		return bodyText;
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
			if (this.checkForChoice(line))
				continue;
			if (this.checkForElementChoice(line))
				continue;
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
			Element element = this.scenario.getElement(elementName);
			
			for (ElementInstance elementInstance : element.getInstances())
			{
				ElementChoice elementChoice = new ElementChoice();
				elementChoice.keyword = keyword;
				elementChoice.elementInstance = elementInstance;
				this.choiceMap.put(elementInstance.getValueByName(elementNamingQuality), elementChoice);
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkForConnection(String line) throws Exception
	{
		Matcher matcher = connectionHeadPattern.matcher(line);
		if (matcher.find())
		{	
			String connectionName = matcher.group(1);
			int connectionNumber = Integer.valueOf(matcher.group(2));
			ConnectionSet connectionSet = this.connectionMap.get(connectionName);
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
