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
import backend.Route;
import backend.RouteType;
import backend.Scenario;
import backend.component.ConnectionSet;
import backend.pages.ElementChoice;
import backend.pages.ElementChoiceType;
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
	private static final String CANCEL = "Cancel";
	private static final Pattern tooltipElementPattern = Pattern.compile("<element:(.*)>");
	private static final Pattern tooltipConnectionPattern = Pattern.compile("<connection:([^<>]*):([^<>]*)>");
	
	ArrayList<LocationButton> locationButtons;
	HashMap<ElementInstance, HashMap<String, ElementChoice>> elementMap;
	Map map;
	MapMode mode = MapMode.LOCATION;
	MapPosition selectedPosition;
	ElementChoice selectedChoice;
	Route selectedRoute;
	JPanel innerPanel = new JPanel();
	JButton cancelButton;
	
	public MapPanel(Map map) throws Exception
	{
		super();
		this.map = map;
		this.innerPanel.setLayout(new GridLayout(this.map.getWidth(), this.map.getHeight()));
		this.add(this.innerPanel);
		this.addCancelButton();
		this.paintMap();
	}
	
	private void addCancelButton()
	{
		this.cancelButton = new JButton(CANCEL);
		this.add(this.cancelButton);
		this.cancelButton.addActionListener(this);
		this.cancelButton.setEnabled(false);
	}
	
	private void populateInstanceMap(HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ElementInstance>>> instanceMap) throws Exception
	{
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
	}
	
	private void paintMap() throws Exception
	{	
		HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ElementInstance>>> instanceMap = new HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ElementInstance>>>();
		this.populateInstanceMap(instanceMap);
		this.paintLocationAndMapButtons(instanceMap);
	}
	
	private void paintLocationAndMapButtons(HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ElementInstance>>> instanceMap) throws Exception
	{
		this.locationButtons = new ArrayList<LocationButton>();	
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
						this.createLocationButton(i, j, dataMap);
						continue;
					}
				}
				this.createEmptyLocationButton(i, j, blankImageName);
			}
		}
	}
	
	private void createEmptyLocationButton(int x, int y, String imagePath) throws Exception
	{
		ImageIcon imageIcon = Main.loadImageIcon(imagePath);
		ImageIcon disabledImageIcon = Main.loadDisabledImageIcon(imagePath);
		LocationButton locationButton = new LocationButton(imageIcon, disabledImageIcon, this.map.getMapPosition(x, y), new ArrayList<ElementInstance>());
		this.innerPanel.add(locationButton);
		this.locationButtons.add(locationButton);
		locationButton.addActionListener(this);
	}
	
	private void repaintEmptyLocationButton(LocationButton locationButton, String blankImageName) throws Exception
	{
		ImageIcon blankImageIcon = Main.loadImageIcon(blankImageName);
		locationButton.setToolTipText(null);
		locationButton.setElementInstances(new ArrayList<ElementInstance>());
		locationButton.setIcon(blankImageIcon);
	}
	
	private void repaintLocationButton(LocationButton locationButton, HashMap<MapElementType, ElementInstance> dataMap) throws Exception
	{
		MapPosition position = locationButton.getPosition();
		LocationButtonData locationButtonData = this.createLocationButtonData(position.x, position.y, dataMap);
		locationButton.setElementInstances(locationButtonData.elementInstances);
		locationButton.setIcon(locationButtonData.imageIcon);
		locationButton.setToolTipText(locationButtonData.tooltipText);
	}
	
	private void createLocationButton(int x, int y, HashMap<MapElementType, ElementInstance> dataMap) throws Exception
	{	
		LocationButtonData locationButtonData = this.createLocationButtonData(x, y, dataMap);	
		LocationButton locationButton;
		
		if (locationButtonData.tooltipText.length() != 0)
		{
			locationButton = new LocationButton(locationButtonData.imageIcon, locationButtonData.disabledImageIcon, this.map.getMapPosition(x, y), 
					locationButtonData.elementInstances, locationButtonData.tooltipText);
		}
		else
		{
			locationButton = new LocationButton(locationButtonData.imageIcon, locationButtonData.disabledImageIcon, this.map.getMapPosition(x, y), 
					locationButtonData.elementInstances);
		}
		
		locationButton.addActionListener(this);
		this.innerPanel.add(locationButton);
		this.locationButtons.add(locationButton);
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
	
	private void performLocationButtonAction(LocationButton button)
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
			ChoiceItem choiceItem = new ChoiceItem(key, button.getPosition(), choice);
			choiceItem.addActionListener(this);
			popupMenu.add(choiceItem);
		}	
		popupMenu.show(button, this.map.getTileSize(), 0);
	}
	
	private void performMenuAction(ElementChoice elementChoice) throws Exception
	{
		Pages.getScenario().loadPage(elementChoice);
	}
	
	private void performMenuRangeAction(ChoiceItem choiceItem, ElementChoice elementChoice) throws Exception
	{
		this.mode = MapMode.LOCATION_RANGE;
		this.selectedPosition = choiceItem.getPosition();
		this.selectedChoice = choiceItem.getElementChoice();
		this.disableButtonsOutsideRange();
		this.cancelButton.setEnabled(true);
	}
	
	private void performRouteSelectionAction(ChoiceItem choiceItem, ElementChoice elementChoice, MapMode mapMode)
	{
		this.mode = mapMode;
		this.selectedPosition = choiceItem.getPosition();
		this.selectedChoice = choiceItem.getElementChoice();
		this.disableButtonsOutsideRange();
		this.cancelButton.setEnabled(true);
		elementChoice.elementInstance.clearRoute(this.map);
	}
	
	private void performChoiceItemAction(ChoiceItem choiceItem) throws Exception
	{
		ElementChoice elementChoice = choiceItem.getElementChoice();
		
		switch(elementChoice.elementChoiceType)
		{
			case ROUTE_SELECTION_WAIT:
				this.performRouteSelectionAction(choiceItem, elementChoice, MapMode.ROUTE_SELECTION_WAIT);
				break;
			case ROUTE_SELECTION_RETURN:
				this.performRouteSelectionAction(choiceItem, elementChoice, MapMode.ROUTE_SELECTION_RETURN);
				break;
			case MENU_RANGE:
				this.performMenuRangeAction(choiceItem, elementChoice);
				break;
			case MENU:
				this.performMenuAction(elementChoice);
				break;
		}
	}
	
	private void disableButtonsOutsideRange()
	{
		for (LocationButton locationButton : this.locationButtons)
		{
			MapPosition position = locationButton.getPosition();
			if (!this.checkInRange(position))
			{
				locationButton.setEnabled(false);
			}
		}
	}
	
	private void enableAllButtons()
	{
		for (LocationButton locationButton : this.locationButtons)
		{
			locationButton.setEnabled(true);
		}
	}
	
	private void endAction()
	{
		if (this.selectedRoute != null)
		{
			this.selectedChoice.elementInstance.setRoute(this.map, this.selectedRoute);
			this.selectedRoute = null;
		}
		
		this.mode = MapMode.LOCATION;
		this.selectedPosition = null;
		this.cancelButton.setEnabled(false);
		this.enableAllButtons();
	}
	
	private void cancelAction()
	{
		this.selectedRoute = null;		
		this.mode = MapMode.LOCATION;
		this.selectedPosition = null;
		this.cancelButton.setEnabled(false);
		this.enableAllButtons();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.cancelButton)
		{
			this.cancelAction();
			return;
		}
		try
		{
			switch (this.mode)
			{
			case LOCATION:
				this.performLocationAction(e);
				break;
			case LOCATION_RANGE:
				this.performLocationRangeAction(e);
				break;
			case ROUTE_SELECTION_WAIT:
				this.performStepSelectionAction(e, RouteType.WAIT);
				break;
			case ROUTE_SELECTION_RETURN:
				this.performStepSelectionAction(e, RouteType.REVERSE);
				break;
			case DISABLED:
				break;
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
	
	private void performStepSelectionAction(ActionEvent e, RouteType routeType) throws Exception
	{
		if (e.getSource() instanceof LocationButton)
		{
			LocationButton locationButton = (LocationButton) e.getSource();
			this.addRouteStep(locationButton, routeType);
		}
	}
	
	private void addRouteStep(LocationButton locationButton, RouteType routeType) throws Exception
	{
		MapPosition position = locationButton.getPosition();
		this.addRouteStep(routeType, position);
		
		if (this.checkForLocation(locationButton))
		{
			this.endAction();
			Pages.getScenario().loadPage(this.selectedChoice);
		}
		else
		{
			this.selectedPosition = position;
			this.enableAllButtons();
			this.disableButtonsOutsideRange();
		}
	}
	
	public void addRouteStep(RouteType routeType, MapPosition position)
	{
		if (this.selectedRoute == null)
		{
			this.selectedRoute = this.makeNewRoute(routeType, position);
		}
		this.selectedRoute.addRoutePosition(position);
	}
	
	private Route makeNewRoute(RouteType routeType, MapPosition position)
	{
		Route route = new Route(routeType);
		route.addRoutePosition(position);
		return route;
	}
	
	private boolean checkForLocation(LocationButton locationButton)
	{
		for (ElementInstance elementInstance : locationButton.elementInstances)
		{
			if (elementInstance.getElement().getMapElementType(this.map) == MapElementType.LOCATION)
				return true;
		}
		return false;
	}
	
	private void performLocationAction(ActionEvent e) throws Exception
	{
		if (e.getSource() instanceof LocationButton)
		{
			this.performLocationButtonAction((LocationButton) e.getSource());
		}
		else if (e.getSource() instanceof ChoiceItem)
		{
			this.performChoiceItemAction((ChoiceItem) e.getSource());
		}
	}
	
	private void performLocationRangeAction(ActionEvent e) throws Exception
	{
		if (e.getSource() instanceof MapButton)
		{
			this.selectInRange((MapButton) e.getSource());
		}
	}
	
	private void selectInRange(MapButton mapButton) throws Exception
	{
		this.endAction();
		Pages.getScenario().loadPage(this.selectedChoice, mapButton.getPosition());
	}
	
	private boolean checkInRange(MapPosition position)
	{
		Integer xDiff = (this.selectedPosition.x - position.x);
		if (xDiff < 0)
		{
			xDiff = xDiff * -1;
		}
		Integer yDiff = (this.selectedPosition.y - position.y);
		if (yDiff < 0)
		{
			yDiff = yDiff * -1;
		}
		int rangeLimit = this.selectedChoice.elementInstance.getNumberValueByName(this.selectedChoice.rangeAttribute);
		if ((xDiff + yDiff) <= rangeLimit)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private LocationButtonData createLocationButtonData(int x, int y, HashMap<MapElementType, ElementInstance> dataMap) throws Exception
	{
		LocationButtonData locationButtonData = new LocationButtonData();	
		locationButtonData.elementInstances = new ArrayList<ElementInstance>();
		ElementInstance locationInstance = null;
		ElementInstance characterInstance = null;
		String locationFileName = null;
		String characterFileName = null;
		locationButtonData.tooltipText = "";
		
		if (dataMap.containsKey(MapElementType.LOCATION))
		{
			locationInstance = dataMap.get(MapElementType.LOCATION);
			Element locationElement = locationInstance.getElement();
			RestrictedJson<ImageRestriction> locationImageData = locationElement.getMapImageData(this.map);
			locationFileName = locationImageData.getString(ImageRestriction.FILENAME);
			RestrictedJson<TooltipRestriction> tooltipData = locationElement.getTooltip(map);
			String locationTooltip = this.createTooltipText(tooltipData, locationInstance);
			locationButtonData.tooltipText += this.assessTooltipText(locationInstance, locationTooltip);
			locationButtonData.elementInstances.add(locationInstance);
		}
		
		if (dataMap.containsKey(MapElementType.CHARACTER))
		{
			characterInstance = dataMap.get(MapElementType.CHARACTER);
			Element characterElement = characterInstance.getElement();
			RestrictedJson<ImageRestriction> pcImageData = characterElement.getMapImageData(this.map);
			characterFileName = pcImageData.getString(ImageRestriction.FILENAME);
			RestrictedJson<TooltipRestriction> tooltipData = characterElement.getTooltip(map);
			String characterTooltip = this.createTooltipText(tooltipData, characterInstance);
			locationButtonData.tooltipText += this.assessTooltipText(characterInstance, characterTooltip);
			locationButtonData.elementInstances.add(characterInstance);
		}
		
		if (locationInstance != null && characterInstance != null)
		{
			locationButtonData.imageIcon = Main.loadCombinedImageIcon(locationFileName, characterFileName);
			locationButtonData.disabledImageIcon = Main.loadDisableCombinedImageIcon(locationFileName, characterFileName);
		}
		else if (locationInstance != null)
		{
			locationButtonData.imageIcon = Main.loadImageIcon(locationFileName);
			locationButtonData.disabledImageIcon = Main.loadDisabledImageIcon(locationFileName);
		}
		else
		{
			RestrictedJson<ImageRestriction> imageRestriction = this.map.getMapData().getRestrictedJson((MapRestriction.IMAGE), ImageRestriction.class);
			String baseFileName = imageRestriction.getString(ImageRestriction.FILENAME);
			locationButtonData.imageIcon = Main.loadCombinedImageIcon(baseFileName, characterFileName);
			locationButtonData.disabledImageIcon = Main.loadDisableCombinedImageIcon(baseFileName, characterFileName);
		}
		return locationButtonData;
	}
	
	private void repaintMap() throws Exception
	{		
		HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ElementInstance>>> instanceMap = 
				new HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ElementInstance>>>();
		RestrictedJson<MapRestriction> mapData = this.map.getMapData();
		RestrictedJson<ImageRestriction> blankImageData = mapData.getRestrictedJson(MapRestriction.IMAGE, ImageRestriction.class);
		String blankImageName = blankImageData.getString(ImageRestriction.FILENAME);
		this.populateInstanceMap(instanceMap);
		for (LocationButton locationButton : this.locationButtons)
		{
			MapPosition position = locationButton.getPosition();
			HashMap<Integer, HashMap<MapElementType, ElementInstance>> innerMap = instanceMap.get(position.x);
			if (innerMap == null)
			{
				this.repaintEmptyLocationButton(locationButton, blankImageName);
			}
			else
			{
				HashMap<MapElementType, ElementInstance> dataMap = innerMap.get(position.y);
				if (dataMap != null)
				{
					this.repaintLocationButton(locationButton, dataMap);
				}
				else
				{
					this.repaintEmptyLocationButton(locationButton, blankImageName);
				}		
			}		
		}
	}
	
	public void update(PageInstance pageInstance) throws Exception
	{
		this.repaintMap();
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
		for (LocationButton mapButton : locationButtons)
		{
			mapButton.setEnabled(value);
		}
	}
	
	private enum MapMode
	{
		DISABLED, LOCATION, LOCATION_RANGE, ROUTE_SELECTION_WAIT, ROUTE_SELECTION_RETURN;
	}
	
	private class MapButton extends JButton
	{
		private MapPosition position;
		
		public MapButton(ImageIcon imageIcon, ImageIcon disabledImageIcon, MapPosition position)
		{
			super(imageIcon);
			this.setDisabledIcon(disabledImageIcon);
			this.setMargin(new Insets(-4, -4, -4, -4));
			this.position = position;
		}
		
		public MapPosition getPosition()
		{
			return this.position;
		}
	}
	
	private class LocationButtonData
	{
		public ArrayList<ElementInstance> elementInstances;
		public ImageIcon imageIcon;
		public ImageIcon disabledImageIcon;
		public String tooltipText;
	}
	
	@SuppressWarnings("serial")
	private class LocationButton extends MapButton
	{
		public ArrayList<ElementInstance> elementInstances;
		
		public LocationButton(ImageIcon imageIcon, ImageIcon disabledImageIcon, MapPosition position, ArrayList<ElementInstance> elementInstances, String tooltipText)
		{
			super(imageIcon, disabledImageIcon, position);
			this.elementInstances = elementInstances;
			if (tooltipText != null)
				this.updateTooltip(tooltipText);
		}
		
		public LocationButton(ImageIcon imageIcon, ImageIcon disabledImageIcon, MapPosition position, ArrayList<ElementInstance> elementInstances)
		{
			this(imageIcon, disabledImageIcon, position, elementInstances, null);
		}
		
		public void updateTooltip(String tooltipText)
		{
			this.setToolTipText(tooltipText);
		}
		
		public JToolTip createToolTip()
		{
			return new MapToolTip();
		}
		
		public void setElementInstances(ArrayList<ElementInstance> elementInstances)
		{
			this.elementInstances = elementInstances;
		}
	}
	
	@SuppressWarnings("serial")
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
