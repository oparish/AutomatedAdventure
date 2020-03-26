package frontEnd;

import backend.pages.ElementChoice;

public class ChoiceEntry
{
	public ElementChoice elementChoice;
	public String value;
	
	public ChoiceEntry(String value, ElementChoice elementChoice)
	{
		this.elementChoice = elementChoice;
		this.value = value;
	}
}
