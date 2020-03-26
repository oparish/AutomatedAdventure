package backend.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import backend.Element;
import backend.Element.ElementInstance;
import main.Main;

public class ConnectionSet
{
	private HashMap<ElementInstance, ElementInstance> contents = new HashMap<ElementInstance, ElementInstance>();
	private Element keyElement;
	private Element valueElement;
	
	public Element getKeyElement() {
		return keyElement;
	}

	public Element getValueElement() {
		return valueElement;
	}

	public ConnectionSet(Element keyElement, Element valueElement) throws Exception
	{
		if (keyElement == valueElement)
			new Exception("Can't create a connection set with the same element twice.");
		
		this.keyElement = keyElement;
		this.valueElement = valueElement;
		
		ArrayList<ElementInstance> valueInstances = (ArrayList<ElementInstance>) valueElement.getInstances().clone();
		
		for (ElementInstance keyInstance : keyElement.getInstances())
		{
			if (valueInstances.size() == 0)
				throw new Exception("Not enough values to connect to.");
			
			int randomInt = Main.getRndm(valueInstances.size());
			ElementInstance valueInstance = valueInstances.get(randomInt);
			this.makeConnection(keyInstance, valueInstance);
			valueInstances.remove(valueInstance);
		}
	}
	
	public void makeUniqueConnections(int number) throws Exception
	{
		ArrayList<ElementInstance> keyElementList = (ArrayList<ElementInstance>) this.keyElement.getInstances().clone();
		ArrayList<ElementInstance> valueElementList = (ArrayList<ElementInstance>) this.valueElement.getInstances().clone();
		
		for (ElementInstance keyElement : keyElementList)
		{
			if (this.contents.keySet().contains(keyElement))
			{
				keyElementList.remove(keyElement);
			}
		}
		
		for (ElementInstance valueElement : valueElementList)
		{
			if (this.contents.values().contains(valueElement))
			{
				valueElementList.remove(valueElement);
			}
		}
		
		if (number > keyElementList.size() || number > valueElementList.size())
		{
			throw new Exception("Too many connections for not enough elements.");
		}
		
		for (int i = 0; i < number; i++)
		{
			ElementInstance keyElementInstance = keyElementList.get(Main.getRndm(keyElementList.size()));
			ElementInstance valueElementInstance = valueElementList.get(Main.getRndm(valueElementList.size()));
			keyElementList.remove(keyElementInstance);
			valueElementList.remove(valueElementInstance);
			this.makeConnection(keyElementInstance, valueElementInstance);
		}
	}
	
	public void makeConnection(ElementInstance firstInstance, ElementInstance secondInstance) throws Exception
	{
		Element firstElement = firstInstance.getElement();
		Element secondElement = secondInstance.getElement();
		if ((firstElement != this.keyElement && firstElement != this.valueElement) || 
		(secondElement != this.keyElement && secondElement != this.valueElement)  ||
		(firstElement == secondElement))
			throw new Exception("Invalid elements for this connection set.");
		if (firstElement == this.keyElement)
			this.innerMakeConnection(firstInstance, secondInstance);
		else
			this.innerMakeConnection(secondInstance, firstInstance);
	}
	
	private void innerMakeConnection(ElementInstance keyInstance, ElementInstance valueInstance)
	{
		for (Entry<ElementInstance, ElementInstance> entry : this.contents.entrySet())
		{
			if (entry.getValue() == valueInstance)
			{
				this.contents.put(entry.getKey(), null);
				break;
			}
		}
		this.contents.put(keyInstance, valueInstance);
	}
	
	public ElementInstance getByValue(ElementInstance valueInstance) throws Exception
	{
		if (valueInstance.getElement() != this.valueElement)
			throw new Exception("Wrong connection value.");
			
		for (Entry<ElementInstance, ElementInstance> entry : this.contents.entrySet())
		{
			if (entry.getValue() == valueInstance)
			{
				return entry.getKey();
			}
		}
		return null;
	}
	
	public ElementInstance getByKey(ElementInstance keyInstance) throws Exception
	{
		if (keyInstance.getElement() != this.keyElement)
			throw new Exception("Wrong key value.");
		return this.contents.get(keyInstance);
	}
}
