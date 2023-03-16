package frontEnd;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import backend.Map.MapPosition;
import backend.pages.ElementChoice;

@SuppressWarnings("serial")
public class ChoiceItem extends JMenuItem implements Positioned
{
	private MapPosition position;
	private ElementChoice elementChoice;
	
	public ElementChoice getElementChoice() {
		return this.elementChoice;
	}
	
	public MapPosition getPosition() {
		return this.position;
	}

	public ChoiceItem(String text, MapPosition position, ElementChoice elementChoice)
	{
		super(text);
		this.position = position;
		this.elementChoice = elementChoice;
	}
}
