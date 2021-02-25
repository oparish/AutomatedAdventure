package frontEnd;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

import backend.Element;
import backend.Element.ElementInstance;
import backend.Map;
import backend.Map.MapPosition;
import backend.MapElementType;
import backend.Scenario;
import backend.component.ConnectionSet;
import backend.pages.ElementChoice;
import backend.pages.PageInstance;
import json.JsonEntityArray;
import json.RestrictedJson;
import json.restrictions.ContextConditionRestriction;
import json.restrictions.ImageRestriction;
import json.restrictions.MapRestriction;
import json.restrictions.TooltipComponentRestriction;
import json.restrictions.TooltipRestriction;
import main.Main;
import main.Pages;

public class MapPanel extends JPanel implements ActionListener
{
	private static final Pattern tooltipElementPattern = Pattern.compile("<element:(.*)>");
	private static final Pattern tooltipConnectionPattern = Pattern.compile("<connection:([^<>]*):([^<>]*)>");
	
	ArrayList<MapButton> mapButtons;
	HashMap<ElementInstance, HashMap<String, ElementChoice>> elementMap;
	Map map;
	JPanel innerPanel = new JPanel();
	
	public MapPanel(Map map) throws Exception
	{
		super();
		this.map = map;
		this.innerPanel.setLayout(new GridLayout(this.map.getWidth(), this.map.getHeight()));
		this.add(this.innerPanel);
		this.paintMap();
	}
	
	private void paintMap() throws Exception
	{	
		HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ElementInstance>>> instanceMap = new HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ElementInstance>>>();
		this.mapButtons = new ArrayList<MapButton>();
		
		ArrayList<Element> elements = this.map.getElements();
		for (Element element : elements)
		{
			for (ElementInstance elementInstance : element.getInstances())
			{
				MapPosition mapPosition = elementInstance.getMapPosition(this.map);
				HashMap<Integer, HashMap<MapElementType, ElementInstance>> innerMap;
				if (instanceMap.containsKey(mapPosition.x))
				{
					innerMap = instanceMap.get(mapPosition.x);
				}
				else
				{
					innerMap = new HashMap<Integer, HashMap<MapElementType, ElementInstance>>();
					instanceMap.put(mapPosition.x, innerMap);
				}		

				HashMap<MapElementType, ElementInstance> dataMap;	
				
				if (innerMap.containsKey(mapPosition.y))
				{
					dataMap = innerMap.get(mapPosition.y);
				}
				else
				{
					dataMap = new HashMap<MapElementType, ElementInstance>();
					innerMap.put(mapPosition.y, dataMap);
				}
				
				MapElementType mapElementType = elementInstance.getElement().getMapElementType(this.map);
				if (dataMap.containsKey(mapElementType))
					throw new Exception("More than one map element of the same layer in the same place: " + mapPosition.x + ", " 
							+ mapPosition.y);
				dataMap.put(mapElementType, elementInstance);
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
					HashMap<Integer, HashMap<MapElementType, ElementInstance>> innerMap = instanceMap.get(i);
					if (innerMap.containsKey(j))
					{
						HashMap<MapElementType, ElementInstance> dataMap = innerMap.get(j);
						this.createButton(i, j, dataMap);
						continue;
					}
				}
				this.createLabel(i, j, blankImageName);
			}
		}
	}
	
	private void createLabel(int x, int y, String imagePath)
	{
		ImageIcon imageIcon = Main.loadImageIcon(imagePath);
		JLabel jLabel = new JLabel(imageIcon);
		this.innerPanel.add(jLabel);
	}
	
	private void createButton(int x, int y, HashMap<MapElementType, ElementInstance> dataMap) throws Exception
	{
		ImageIcon imageIcon = null;
		
		ArrayList<ElementInstance> elementInstances = new ArrayList<ElementInstance>();
		ElementInstance locationInstance = null;
		ElementInstance pcInstance = null;
		String locationFileName = null;
		String pcFileName = null;
		String tooltipText = "";
		
		if (dataMap.containsKey(MapElementType.LOCATION))
		{
			locationInstance = dataMap.get(MapElementType.LOCATION);
			Element locationElement = locationInstance.getElement();
			RestrictedJson<ImageRestriction> locationImageData = locationElement.getMapImageData(this.map);
			locationFileName = locationImageData.getString(ImageRestriction.FILENAME);
			RestrictedJson<TooltipRestriction> tooltipData = locationElement.getTooltip(map);
			String locationTooltip = this.createTooltipText(tooltipData, locationInstance);
			tooltipText += this.assessTooltipText(locationInstance, locationTooltip);
			elementInstances.add(locationInstance);
		}
		
		if (dataMap.containsKey(MapElementType.PC))
		{
			pcInstance = dataMap.get(MapElementType.PC);
			Element pcElement = pcInstance.getElement();
			RestrictedJson<ImageRestriction> pcImageData = pcElement.getMapImageData(this.map);
			pcFileName = pcImageData.getString(ImageRestriction.FILENAME);
			RestrictedJson<TooltipRestriction> tooltipData = pcElement.getTooltip(map);
			String pcTooltip = this.createTooltipText(tooltipData, pcInstance);
			tooltipText += this.assessTooltipText(pcInstance, pcTooltip);
			elementInstances.add(pcInstance);
		}
		
		if (locationInstance != null && pcInstance != null)
		{
			imageIcon = Main.loadCombinedImageIcon(locationFileName, pcFileName);
		}
		else if (locationInstance != null)
		{
			imageIcon = Main.loadImageIcon(locationFileName);
		}
		else
		{
			RestrictedJson<ImageRestriction> imageRestriction = this.map.getMapData().getRestrictedJson((MapRestriction.IMAGE), ImageRestriction.class);
			String baseFileName = imageRestriction.getString(ImageRestriction.FILENAME);
			imageIcon = Main.loadCombinedImageIcon(baseFileName, pcFileName);
		}
		
		MapButton mapButton;
		
		if (tooltipText.length() != 0)
		{
			mapButton = new MapButton(imageIcon, elementInstances, tooltipText);
		}
		else
		{
			mapButton = new MapButton(imageIcon, elementInstances);
		}
		
		mapButton.setMargin(new Insets(-4, -4, -4, -4));
		mapButton.addActionListener(this);
		this.innerPanel.add(mapButton);
		this.mapButtons.add(mapButton);
	}
	
	private String createTooltipText(RestrictedJson<TooltipRestriction> tooltipData, ElementInstance elementInstance) throws Exception
	{
		String tooltipText = null;
		JsonEntityArray<RestrictedJson<TooltipComponentRestriction>> components = 
				tooltipData.getRestrictedJsonArray(TooltipRestriction.TOOLTIP_COMPONENTS, TooltipComponentRestriction.class);
		for (int i = 0; i < components.getLength(); i++)
		{
			RestrictedJson<TooltipComponentRestriction> tooltipComponentData = components.getMemberAt(i);
			JsonEntityArray<RestrictedJson<ContextConditionRestriction>> contextConditionDataArray = 
					tooltipComponentData.getRestrictedJsonArray(TooltipComponentRestriction.CONTEXT_CONDITIONS, ContextConditionRestriction.class);

			if (contextConditionDataArray == null || ContextConditionRestriction.checkCondition(this.map.getScenario(), contextConditionDataArray, elementInstance))
			{
				tooltipText = tooltipComponentData.getString(TooltipComponentRestriction.TOOLTIP_TEXT);
				break;
			}
		}
		return tooltipText;
	}

	private String assessTooltipText(ElementInstance elementInstance, String tooltipText) throws Exception
	{
		Matcher tooltipMatcher = MapPanel.tooltipElementPattern.matcher(tooltipText);
		while(tooltipMatcher.find())
		{
			String elementQualityName = tooltipMatcher.group(1);
			String stringValue = elementInstance.getValue(elementQualityName);
			tooltipText = tooltipMatcher.replaceFirst(stringValue);
			tooltipMatcher = MapPanel.tooltipElementPattern.matcher(tooltipText);
		}
		
		Scenario scenario = map.getScenario();
		Matcher tooltipconnectionMatcher = MapPanel.tooltipConnectionPattern.matcher(tooltipText);
		while(tooltipconnectionMatcher.find())
		{
			String connectionTypeName = tooltipconnectionMatcher.group(1);
			String elementQualityName = tooltipconnectionMatcher.group(2);
			ConnectionSet connectionSet = scenario.getConnectionSet(connectionTypeName);
			ElementInstance connectedInstance = connectionSet.get(elementInstance);		
			String stringValue = connectedInstance.getValue(elementQualityName);
			tooltipText = tooltipconnectionMatcher.replaceFirst(stringValue);
			tooltipconnectionMatcher = MapPanel.tooltipConnectionPattern.matcher(tooltipText);
		}
		
		return tooltipText;
	}
	
	private void performMapButtonAction(MapButton button)
	{
		JPopupMenu popupMenu = new JPopupMenu();
		HashMap<String, ElementChoice> combinedChoices = new HashMap<String, ElementChoice>();
		for (ElementInstance elementInstance : button.elementInstances)
		{
			HashMap<String, ElementChoice> choices = this.elementMap.get(elementInstance);
			if (choices == null || choices.size() == 0)
			{
				continue;
			}
			for (Entry<String, ElementChoice> entry : choices.entrySet())
			{
				combinedChoices.put(entry.getKey(), entry.getValue());
			}
		}
		
		ArrayList<String> sortedList = new ArrayList<String>();
		for (String key : combinedChoices.keySet())
		{
			sortedList.add(key);
		}
		
		Collections.sort(sortedList);
		
		for (String key : sortedList)
		{
			ElementChoice choice = combinedChoices.get(key);
			ChoiceItem choiceItem = new ChoiceItem(key, choice);
			choiceItem.addActionListener(this);
			popupMenu.add(choiceItem);
		}	
		popupMenu.show(button, this.map.getTileSize(), 0);
	}
	
	private void performChoiceItemAction(ChoiceItem choiceItem)
	{
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
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof MapButton)
		{
			this.performMapButtonAction((MapButton) e.getSource());
		}
		else if (e.getSource() instanceof ChoiceItem)
		{
			this.performChoiceItemAction((ChoiceItem) e.getSource());
		}
	}
	
	public void update(PageInstance pageInstance) throws Exception
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
		public ArrayList<ElementInstance> elementInstances;
		
		public MapButton(ImageIcon imageIcon, ArrayList<ElementInstance> elementInstances, String tooltipText)
		{
			super(imageIcon);
			this.setDisabledIcon(imageIcon);
			this.elementInstances = elementInstances;
			if (tooltipText != null)
				this.updateTooltip(tooltipText);
		}
		
		public MapButton(ImageIcon imageIcon, ArrayList<ElementInstance> elementInstances)
		{
			this(imageIcon, elementInstances, null);
		}
		
		public JToolTip createToolTip()
		{
			return new MapToolTip();
		}
		
		public void updateTooltip(String tooltipText)
		{
			this.setToolTipText(tooltipText);
		}
	}
	
	private class MapToolTip extends JToolTip
	{
		public MapToolTip()
		{
			super();
			this.setUI(new MapToolTipUI());
		}
	}
	
	private class MapToolTipUI extends MetalToolTipUI
	{
		private String[] strs;
		
		public void paint(Graphics g, JComponent c)
		{
			FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());
			Dimension size = c.getSize();
			g.setColor(c.getBackground());
			g.fillRect(0, 0, size.width, size.height);
			g.setColor(c.getForeground());
			if (strs != null)
			{
				for (int i = 0; i < strs.length; i++)
				{
					g.drawString(strs[i], 3, (metrics.getHeight()) * (i + 1));
				}
			}
		}

		public Dimension getPreferredSize(JComponent c)
		{
			FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(c.getFont());
		    String tipText = ((JToolTip) c).getTipText();
		    if (tipText == null)
		    {
		    	tipText = "";
		    }
		    BufferedReader br = new BufferedReader(new StringReader(tipText));
		    String line;
		    int maxWidth = 0;
		    Vector v = new Vector();
		    try
		    {
		    	while ((line = br.readLine()) != null)
		    	{
		    		int width = SwingUtilities.computeStringWidth(metrics, line);
		    		maxWidth = (maxWidth < width) ? width : maxWidth;
		    		v.addElement(line);
		    	}
		    }
		    catch (IOException ex)
		    {
		    	ex.printStackTrace();
		    }
		    
		    int lines = v.size();
		    if (lines < 1)
		    {
		      strs = null;
		      lines = 1;
		    }
		    else
		    {
		      strs = new String[lines];
		      int i = 0;
		      for (Enumeration e = v.elements(); e.hasMoreElements(); i++)
		      {
		        strs[i] = (String) e.nextElement();
		      }
		    }
			int height = metrics.getHeight() * lines;
			return new Dimension(maxWidth + 6, height + 4);
		}
	}
}
