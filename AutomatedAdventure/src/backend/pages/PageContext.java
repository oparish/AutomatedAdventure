package backend.pages;

import java.util.HashMap;

import backend.Element;
import backend.Element.ElementInstance;

public class PageContext
{
	private HashMap<Element, ElementInstance> elementInstanceMap = new HashMap<Element, ElementInstance>();
	private String topPage;
	
	public PageContext(String topPage)
	{
		this.topPage = topPage;
	}
	
	public ElementInstance getElementInstance(Element element)
	{
		return this.elementInstanceMap.get(element);
	}
	
	public void addElementInstance(ElementInstance elementInstance)
	{
		this.elementInstanceMap.put(elementInstance.getElement(), elementInstance);
	}
	
	public String getTopPage()
	{
		return this.topPage;
	}
}
