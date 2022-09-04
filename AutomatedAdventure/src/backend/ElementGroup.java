package backend;

import java.util.ArrayList;

import backend.Element.ElementInstance;

public class ElementGroup
{
	ArrayList<ElementInstance> elementInstances;
	
	public ArrayList<ElementInstance> getElementInstances() {
		return elementInstances;
	}

	public ElementGroup(ArrayList<ElementInstance> elementInstances)
	{
		this.elementInstances = elementInstances;
	}
}
