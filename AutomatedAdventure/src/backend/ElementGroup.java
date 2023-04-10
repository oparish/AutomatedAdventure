package backend;

import java.util.ArrayList;

import backend.Element.ElementInstance;

public class ElementGroup
{
	ArrayList<ElementInstance> elementInstances;
	
	public ArrayList<ElementInstance> getElementInstances() {
		return elementInstances;
	}

	public int getLength()
	{
		return this.elementInstances.size();
	}
	
	public ElementInstance getElementInstance(int i)
	{
		return this.elementInstances.get(i);
	}
	
	public ElementGroup(ArrayList<ElementInstance> elementInstances)
	{
		this.elementInstances = elementInstances;
	}
	
	public void addInstances(ArrayList<ElementInstance> elementInstances)
	{
		this.elementInstances.addAll(elementInstances);
	}
}
