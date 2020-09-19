package frontEnd;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import backend.Element;
import backend.Element.ElementInstance;
import backend.Map;
import backend.Map.MapPosition;
import backend.pages.ElementChoice;
import backend.pages.PageInstance;
import json.RestrictedJson;
import json.restrictions.ImageRestriction;
import json.restrictions.MapRestriction;
import main.Pages;

public class MapPanel extends JPanel implements ActionListener
{
	ArrayList<MapButton> mapButtons;
	HashMap<ElementInstance, HashMap<String, ElementChoice>> elementMap;
	Map map;
	JPanel innerPanel = new JPanel();
	
	public MapPanel(Map map)
	{
		super();
		this.map = map;
		this.innerPanel.setLayout(new GridLayout(this.map.getWidth(), this.map.getHeight()));
		this.add(this.innerPanel);
		this.paintMap();
	}
	
	private void paintMap()
	{	
		HashMap<Integer, HashMap<Integer, ElementInstance>> instanceMap = new HashMap<Integer, HashMap<Integer, ElementInstance>>();
		this.mapButtons = new ArrayList<MapButton>();
		
		ArrayList<Element> elements = this.map.getElements();
		for (Element element : elements)
		{
			for (ElementInstance elementInstance : element.getInstances())
			{
				MapPosition mapPosition = elementInstance.getMapPosition(this.map);
				HashMap<Integer, ElementInstance> innerMap;
				if (instanceMap.containsKey(mapPosition.x))
				{
					innerMap = instanceMap.get(mapPosition.x);
				}
				else
				{
					innerMap = new HashMap<Integer, ElementInstance>();
					instanceMap.put(mapPosition.x, innerMap);
				}
				innerMap.put(mapPosition.y, elementInstance);
			}
		}
		
		RestrictedJson<MapRestriction> mapData = this.map.getMapData();
		RestrictedJson<ImageRestriction> blankImageData = mapData.getRestrictedJson(MapRestriction.IMAGE, ImageRestriction.class);
		String blankImageName = blankImageData.getString(ImageRestriction.FILENAME);
		
		for (int i = 0; i < this.map.getWidth(); i++)
		{
			for (int j = 0; j < this.map.getHeight(); j++)
			{
				if (instanceMap.containsKey(i))
				{
					HashMap<Integer, ElementInstance> innerMap = instanceMap.get(i);
					if (innerMap.containsKey(j))
					{
						ElementInstance elementInstance = innerMap.get(j);
						this.createButton(i, j, elementInstance);
						continue;
					}
				}
				this.createLabel(i, j, blankImageName);
			}
		}
	}
	
	private void createLabel(int x, int y, String imagePath)
	{
		ImageIcon imageIcon = new ImageIcon(imagePath);
		JLabel jLabel = new JLabel(imageIcon);
		this.innerPanel.add(jLabel);
	}
	
	private void createButton(int x, int y, ElementInstance elementInstance)
	{
		Element element = elementInstance.getElement();
		RestrictedJson<ImageRestriction> imageData = element.getMapImageData(this.map);
		String fileName = imageData.getString(ImageRestriction.FILENAME);
		ImageIcon imageIcon = new ImageIcon(fileName);
		MapButton mapButton = new MapButton(imageIcon, elementInstance);
		mapButton.setMargin(new Insets(-4, -4, -4, -4));
		mapButton.addActionListener(this);
		this.innerPanel.add(mapButton);
		this.mapButtons.add(mapButton);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof MapButton)
		{

			MapButton button = (MapButton) e.getSource();
			HashMap<String, ElementChoice> choices = this.elementMap.get(button.elementInstance);
			if (choices == null || choices.size() == 0)
			{
				return;
			}
			JPopupMenu popupMenu = new JPopupMenu();
			
			ArrayList<String> sortedList = new ArrayList<String>();
			for (String key : choices.keySet())
			{
				sortedList.add(key);
			}
			
			Collections.sort(sortedList);
			
			for (String key : sortedList)
			{
				ElementChoice choice = choices.get(key);
				ChoiceItem choiceItem = new ChoiceItem(key, choice);
				choiceItem.addActionListener(this);
				popupMenu.add(choiceItem);
			}	
			popupMenu.show(button, this.map.getTileSize(), 0);
		}
		else if (e.getSource() instanceof ChoiceItem)
		{
			ChoiceItem choiceItem = (ChoiceItem) e.getSource();
			ElementChoice elementChoice = choiceItem.getElementChoice();
			try
			{
				Pages.getScenario().loadPage(elementChoice);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}

	}
	
	public void update(PageInstance pageInstance)
	{
		this.innerPanel.removeAll();
		this.paintMap();
		this.elementMap = new HashMap<ElementInstance, HashMap<String, ElementChoice>>();
		HashMap<String, ElementChoice> choiceMap = pageInstance.getChoiceMap();
		for (Entry<String, ElementChoice> entry : choiceMap.entrySet())
		{
			ElementChoice elementChoice = entry.getValue();
			HashMap<String, ElementChoice> choices;
			if (this.elementMap.containsKey(elementChoice.elementInstance))
			{
				choices = this.elementMap.get(elementChoice.elementInstance);
			}
			else
			{
				choices = new HashMap<String, ElementChoice>();
				this.elementMap.put(elementChoice.elementInstance, choices);
			}		
			choices.put(entry.getKey(), elementChoice);			
		}
	}
	
	public void setEnabled(boolean value)
	{
		super.setEnabled(value);
		for (MapButton mapButton : mapButtons)
		{
			mapButton.setEnabled(value);
		}
	}
	
	private class MapButton extends JButton
	{
		public ElementInstance elementInstance;
		
		public MapButton(ImageIcon imageIcon, ElementInstance elementInstance)
		{
			super(imageIcon);
			this.setDisabledIcon(imageIcon);
			this.elementInstance = elementInstance;
		}
	}

}
