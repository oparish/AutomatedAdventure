package backend.pages;

import java.util.ArrayList;

import backend.ElementGroup;
import backend.Element.ElementInstance;

public class GroupCounter implements Counter
{
	CounterSecondaryType counterSecondaryType;
	private ElementGroup elementGroup;
	int i = 0;
	
	public GroupCounter(CounterSecondaryType counterSecondaryType, ElementGroup elementGroup)
	{
		this.counterSecondaryType = counterSecondaryType;
		this.elementGroup = elementGroup;
	}
	
	@Override
	public void increment()
	{
		this.i++;
	}

	@Override
	public boolean isFinished()
	{
		if (i >= elementGroup.getLength())
			return true;
		else
			return false;
	}
	
	public ElementInstance getSelectedElement()
	{
		return this.elementGroup.getElementInstance(this.i);
	}

}
