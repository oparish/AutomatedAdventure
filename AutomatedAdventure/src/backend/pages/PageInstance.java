package backend.pages;

public class PageInstance
{
	String pageTemplate;
	
	public PageInstance(String pageTemplate)
	{
		this.pageTemplate = pageTemplate;
	}
	
	public String getText()
	{
		return this.pageTemplate;
	}
}
