package frontEnd;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import backend.pages.ElementChoice;

@SuppressWarnings("serial")
public class ChoiceItem extends JMenuItem
{
	private Position position;
	private ElementChoice elementChoice;
	
	public ElementChoice getElementChoice() {
		return this.elementChoice;
	}
	
	public Position getPosition() {
		return this.position;
	}

	public ChoiceItem(String text, Position position, ElementChoice elementChoice)
	{
		super(text);
		this.position = position;
		this.elementChoice = elementChoice;
	}
}
