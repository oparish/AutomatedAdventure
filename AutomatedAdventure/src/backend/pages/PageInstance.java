package backend.pages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageInstance
{
	private static final Pattern mainPattern = Pattern.compile("<head>(.*)</head><body>(.*)</body>");
	String pageTemplate;
	
	public PageInstance(String pageTemplate)
	{
		this.pageTemplate = pageTemplate;
	}
	
	public String getText()
	{
		Matcher matcher = mainPattern.matcher(this.pageTemplate);
		matcher.find();
		String headerText = matcher.group(1);
		String bodyText = matcher.group(2);
		return bodyText;
	}
}
