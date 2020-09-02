package frontEnd;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import backend.pages.ElementChoice;

public class ChoiceItem extends JMenuItem
{
	ElementChoice elementChoice;
	
	public ElementChoice getElementChoice() {
		return elementChoice;
	}

	public ChoiceItem(String text, ElementChoice elementChoice)
	{
		super(text);
		this.elementChoice = elementChoice;
	}
}
