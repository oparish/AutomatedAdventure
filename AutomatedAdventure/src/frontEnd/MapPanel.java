package frontEnd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.metal.MetalToolTipUI;

import backend.Element;
import backend.Element.ElementInstance;
import backend.Faction;
import backend.ImageData;
import backend.ImageDataKey;
import backend.Map;
import backend.Map.ChangeInPosition;
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
	private static final int TILE_LENGTH = 40;
	private static final int STEPS_PER_MOVE = 10;
	private static final int TIME_PER_MOVE = 400;
	
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
	JLayeredPane layeredPanel = new JLayeredPane();
	JPanel innerPanel = new JPanel();
	GlassPanel upperPanel = new GlassPanel();
	JButton cancelButton;
	Timer animationTimer = new Timer(TIME_PER_MOVE/STEPS_PER_MOVE, this);
	int animationCounter = 0;
	
	HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>> movingMap;
	
	public MapPanel(Map map) throws Exception
	{
		super();
		this.map = map;

		this.innerPanel.setLayout(new GridLayout(this.map.getWidth(), this.map.getHeight()));
		int mapWidth = this.map.getWidth() * TILE_LENGTH;
		int mapHeight = this.map.getHeight() * TILE_LENGTH;
		this.setLayout(new GridBagLayout());
		this.layeredPanel.setBounds(0, 0, mapWidth, mapHeight);
		this.upperPanel.setOpaque(false);
		this.innerPanel.setBounds(0, 0, mapWidth, mapHeight);
		this.upperPanel.setBounds(0, 0, mapWidth, mapHeight);
		this.layeredPanel.add(this.innerPanel, new Integer(0), 0);
		this.layeredPanel.add(this.upperPanel, new Integer(1), 0);

		this.addCancelButton();
		
		GridBagConstraints layeredConstraints = new GridBagConstraints();
		layeredConstraints.gridx = 0;
		layeredConstraints.gridy = 0;
		layeredConstraints.weightx = 1;
		layeredConstraints.weighty = 1;
		layeredConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.layeredPanel, layeredConstraints);
		this.paintMap();
	}
	
	private void addCancelButton()
	{
		this.cancelButton = new JButton(CANCEL);
		GridBagConstraints cancelConstraints = new GridBagConstraints();
		cancelConstraints.gridx = 1;
		cancelConstraints.gridy = 0;
		cancelConstraints.weightx = 1;
		cancelConstraints.weighty = 1;
		this.add(this.cancelButton, cancelConstraints);
		this.cancelButton.addActionListener(this);
		this.cancelButton.setEnabled(false);
	}
	
	private void populateInstanceMap(HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>> instanceMap, boolean excludeChanging) throws Exception
	{
		HashMap<ElementInstance, ChangeInPosition> changePositionMap = this.map.getChangeInPositionMap();
		ArrayList<Element> elements = this.map.getElements();
		for (Element element : elements)
		{
			for (ElementInstance elementInstance : element.getInstances())
			{
				if (excludeChanging && changePositionMap.containsKey(elementInstance))
				{
					continue;
				}
				
				MapPosition mapPosition = elementInstance.getMapPosition(this.map);
				HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>> innerMap;
				if (instanceMap.containsKey(mapPosition.x))
				{
					innerMap = instanceMap.get(mapPosition.x);
				}
				else
				{
					innerMap = new HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>();
					instanceMap.put(mapPosition.x, innerMap);
				}		
		
				HashMap<MapElementType, ArrayList<ElementInstance>> dataMap;	
				
				if (innerMap.containsKey(mapPosition.y))
				{
					dataMap = innerMap.get(mapPosition.y);
				}
				else
				{
					dataMap = new HashMap<MapElementType, ArrayList<ElementInstance>>();
					innerMap.put(mapPosition.y, dataMap);
				}
				
				MapElementType mapElementType = elementInstance.getElement().getMapElementType(this.map);
				ArrayList<ElementInstance> elementInstances;
				if (dataMap.containsKey(mapElementType))
				{
					elementInstances = dataMap.get(mapElementType);
					elementInstances.add(elementInstance);
				}
				else
				{
					elementInstances = new ArrayList<ElementInstance>();
					elementInstances.add(elementInstance);
					dataMap.put(mapElementType, elementInstances);
				}
				
			}
		}
	}
	
	private void paintMap() throws Exception
	{	
		HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>> instanceMap = new HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>>();
		this.populateInstanceMap(instanceMap, false);
		this.paintLocationAndMapButtons(instanceMap);
	}
	
	private void paintLocationAndMapButtons(HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>> instanceMap) throws Exception
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
					HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>> innerMap = instanceMap.get(i);
					if (innerMap.containsKey(j))
					{
						HashMap<MapElementType, ArrayList<ElementInstance>> dataMap = innerMap.get(j);
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
	
	private void repaintLocationButton(LocationButton locationButton, HashMap<MapElementType, ArrayList<ElementInstance>> dataMap) throws Exception
	{
		MapPosition position = locationButton.getPosition();
		LocationButtonData locationButtonData = this.createLocationButtonData(position.x, position.y, dataMap);
		locationButton.setElementInstances(locationButtonData.elementInstances);
		locationButton.setIcon(locationButtonData.imageIcon);
		locationButton.setToolTipText(locationButtonData.tooltipText);
	}
	
	private void createLocationButton(int x, int y, HashMap<MapElementType, ArrayList<ElementInstance>> dataMap) throws Exception
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

			if (contextConditionDataArray == null || ContextConditionRestriction.checkCondition(this.map.getScenario(), contextConditionDataArray, elementInstance, null))
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
			if (elementInstance.getFaction(this.map) == Faction.COMPUTER)
			{
				return;
			}
		}
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
		this.selectedChoice = choiceItem.getElementChoice();
		this.disableButtonsOutsideRange();
		this.cancelButton.setEnabled(true);
	}
	
	private void performRouteSelectionAction(ChoiceItem choiceItem, ElementChoice elementChoice, MapMode mapMode)
	{
		this.mode = mapMode;
		this.selectedChoice = choiceItem.getElementChoice();
		this.disableButtonsOutsideRange();
		this.cancelButton.setEnabled(true);
		elementChoice.elementInstance.clearRoute(this.map);
		if (this.mode == MapMode.ROUTE_SELECTION_WAIT)
			this.addRouteStep(RouteType.WAIT, this.selectedPosition);
		else
			this.addRouteStep(RouteType.REVERSE, this.selectedPosition);
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
		this.mode = MapMode.LOCATION;
		this.selectedPosition = null;
		this.selectedChoice = null;
		this.cancelButton.setEnabled(false);
		this.enableAllButtons();
	}
	
	private void cancelAction()
	{
		this.selectedRoute = null;		
		this.endAction();
	}
	
	private void completeAction()
	{
		if (this.selectedRoute != null)
		{
			this.selectedChoice.elementInstance.setRoute(this.map, this.selectedRoute);
			this.selectedRoute = null;
		}
		this.endAction();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof Timer)
		{
			try
			{
				if (this.animationCounter == STEPS_PER_MOVE)
					this.finishRepaintingMap();
				else
					this.continueRepaintingMap();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}	
		else if (e.getSource() == this.cancelButton)
		{
			this.cancelAction();
		}
		else
		{
			if (e.getSource() instanceof Positioned)
			{
				Positioned positioned = (Positioned) e.getSource();
				this.selectedPosition = positioned.getPosition();
				this.map.setSelectedPosition(selectedPosition);
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
			ElementChoice choice = this.selectedChoice;
			this.completeAction();
			Pages.getScenario().loadPage(choice);
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
		ElementChoice choice = this.selectedChoice;
		this.completeAction();
		Pages.getScenario().loadPage(choice, mapButton.getPosition());
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
	
	private void populateLocationButtonData(LocationButtonData locationButtonData, MapElementType mapElementType, ArrayList<ElementInstance> instances) throws Exception
	{	
		ImageDataKey[] keyArray;
		switch (mapElementType)
		{
		case CHARACTER:
			int instancesCount = instances.size();
			if (instancesCount > 2)
				keyArray = new ImageDataKey[]{ImageDataKey.LEFT_CHARACTER, ImageDataKey.RIGHT_CHARACTER, ImageDataKey.CENTRE_CHARACTER};
			else if (instancesCount == 2)
				keyArray = new ImageDataKey[]{ImageDataKey.LEFT_CHARACTER, ImageDataKey.RIGHT_CHARACTER};
			else if (instancesCount == 1)
				keyArray = new ImageDataKey[]{ImageDataKey.CENTRE_CHARACTER};
			else
				keyArray = new ImageDataKey[]{};
			break;
		case EFFECT:
			keyArray = new ImageDataKey[]{ImageDataKey.EFFECT};
			break;
		case LOCATION:
			keyArray = new ImageDataKey[]{ImageDataKey.BACKGROUND};
			break;
		default:
			throw new Exception("Unrecognised map element type.");
		}

		int i = 0;
		for (ImageDataKey imageDatakey : keyArray)
		{
			ElementInstance instance = instances.get(i);
			Element locationElement = instance.getElement();
			Faction faction = instance.getFaction(this.map);
			RestrictedJson<ImageRestriction> locationImageData = locationElement.getMapImageData(this.map, faction);
			String fileName = locationImageData.getString(ImageRestriction.FILENAME);	
			locationButtonData.addFilename(imageDatakey, fileName);		
			i++;
		}
		
		for (ElementInstance locationInstance : instances)
		{
			Element locationElement = locationInstance.getElement();
			RestrictedJson<TooltipRestriction> tooltipData = locationElement.getTooltip(map);
			String locationTooltip = this.createTooltipText(tooltipData, locationInstance);
			locationButtonData.tooltipText += this.assessTooltipText(locationInstance, locationTooltip);
			locationButtonData.elementInstances.add(locationInstance);
		}
	}
	
	private LocationButtonData createLocationButtonData(int x, int y, HashMap<MapElementType, ArrayList<ElementInstance>> dataMap) throws Exception
	{
		LocationButtonData locationButtonData = new LocationButtonData();	
		locationButtonData.elementInstances = new ArrayList<ElementInstance>();
		locationButtonData.tooltipText = "";
		
		for (MapElementType mapElementType : MapElementType.values())
		{
			ArrayList<ElementInstance> instances = dataMap.get(mapElementType);
			if (instances != null)
				this.populateLocationButtonData(locationButtonData, mapElementType, instances);	
		}
		
		ArrayList<ElementInstance> locationInstances = dataMap.get(MapElementType.LOCATION);
		
		if (locationInstances == null)
		{
			RestrictedJson<ImageRestriction> imageRestriction = this.map.getMapData().getRestrictedJson((MapRestriction.IMAGE), ImageRestriction.class);
			String baseFileName = imageRestriction.getString(ImageRestriction.FILENAME);
			locationButtonData.imageData.put(ImageDataKey.BACKGROUND, baseFileName);
		}
		
		locationButtonData.imageIcon = Main.loadCombinedImageIcon(locationButtonData.imageData);
		locationButtonData.disabledImageIcon = Main.loadDisableCombinedImageIcon(locationButtonData.imageData);

		return locationButtonData;
	}
	
	public void repaintMap() throws Exception
	{		
		this.movingMap = new HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>>();
		this.populateInstanceMap(this.movingMap, false);
		if (this.map.getChangeInPositionMap().size() != 0)
		{
			this.startRepaintingMap();
			this.mode = MapMode.DISABLED;
			this.animationCounter = 1;
			this.animationTimer.start();
		}
		else
		{
			this.finishRepaintingMap();
		}
	}
	
	private void startRepaintingMap() throws Exception
	{
		HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>> instanceMap = new HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>>();
		this.populateInstanceMap(instanceMap, true);
		HashMap<ElementInstance, ChangeInPosition> changeInPositionMap = this.map.getChangeInPositionMap();
		for (LocationButton locationButton : this.locationButtons)
		{
			MapPosition mapPosition = locationButton.getPosition();
			for (ChangeInPosition changeInPosition : changeInPositionMap.values())
			{
				if (changeInPosition.oldPosition.x == mapPosition.x && changeInPosition.oldPosition.y == mapPosition.y)
				{
					this.repaintLocationButtonAtPosition(instanceMap, locationButton, mapPosition);
					break;
				}
			}
		}
	}
	
	private void repaintLocationButtonAtPosition(HashMap<Integer, HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>>> instanceMap, LocationButton locationButton, MapPosition mapPosition) throws Exception
	{
		RestrictedJson<MapRestriction> mapData = this.map.getMapData();
		RestrictedJson<ImageRestriction> blankImageData = mapData.getRestrictedJson(MapRestriction.IMAGE, ImageRestriction.class);
		String blankImageName = blankImageData.getString(ImageRestriction.FILENAME);
		HashMap<Integer, HashMap<MapElementType, ArrayList<ElementInstance>>> innerMap = instanceMap.get(mapPosition.x);
		if (innerMap == null)
		{
			this.repaintEmptyLocationButton(locationButton, blankImageName);
		}
		else
		{
			HashMap<MapElementType, ArrayList<ElementInstance>> dataMap = innerMap.get(mapPosition.y);
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
	
	private void continueRepaintingMap() throws Exception
	{		
		HashMap<ElementInstance, ChangeInPosition> changeInPositionMap = this.map.getChangeInPositionMap();
		if (changeInPositionMap != null)
		{
			for (Entry<ElementInstance, ChangeInPosition> changeInPositionEntry : changeInPositionMap.entrySet())
			{
				ElementInstance elementInstance = changeInPositionEntry.getKey();
				ChangeInPosition changeInPosition = changeInPositionEntry.getValue();
				Faction faction = elementInstance.getFaction(this.map);
				RestrictedJson<ImageRestriction> mapImageData = elementInstance.getElement().getMapImageData(this.map, faction);
				String instanceFileName = mapImageData.getString(ImageRestriction.FILENAME);
				BufferedImage instanceImage = Main.loadImageFromFile(instanceFileName);
				MapPosition oldPosition = changeInPosition.oldPosition;
				MapPosition newPosition = changeInPosition.newPosition;
				LocationButton oldButton = null;
				LocationButton newButton = null;
				for (LocationButton locationButton : this.locationButtons)
				{		
					if (oldPosition == locationButton.getPosition())
					{
						oldButton = locationButton;
					}
					else if (newPosition == locationButton.getPosition())
					{
						newButton = locationButton;
					}
				}
				int oldX = oldButton.getX();
				int oldY = oldButton.getY();
				int newX = newButton.getX();
				int newY = newButton.getY();
				this.upperPanel.addGlassImage(instanceImage, oldX + (newX - oldX)/STEPS_PER_MOVE*this.animationCounter, oldY + (newY - oldY)/STEPS_PER_MOVE*this.animationCounter);
			}
		}
		
		this.upperPanel.repaint();
		
		this.animationCounter++;
	}
	
	private void finishRepaintingMap() throws Exception
	{
		this.animationTimer.stop();
		this.upperPanel.repaint();
		for (LocationButton locationButton : this.locationButtons)
		{		
			MapPosition position = locationButton.getPosition();
			this.repaintLocationButtonAtPosition(this.movingMap, locationButton, position);
		}
		this.map.completeMove();
		this.mode = MapMode.LOCATION;
		this.setEnabled(true);
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
	
	private class MapButton extends JButton implements Positioned
	{
		private MapPosition position;
		
		public MapButton(ImageIcon imageIcon, ImageIcon disabledImageIcon, MapPosition position) throws Exception
		{
			super(imageIcon);
			this.setDisabledIcon(disabledImageIcon);
			this.setMargin(new Insets(-4, -4, -4, -4));
			this.position = position;
			if (position == null)
				throw new Exception("A MapButton should never be at a NULL position.");
		}
		
		public MapPosition getPosition()
		{
			return this.position;
		}
	}
	
	private class LocationButtonData
	{
		public ImageData imageData = new ImageData();
		public ArrayList<ElementInstance> elementInstances;
		public ImageIcon imageIcon;
		public ImageIcon disabledImageIcon;
		public String tooltipText;
		
		public void addFilename(ImageDataKey key, String value)
		{
			this.imageData.put(key, value);
		}
	}
	
	@SuppressWarnings("serial")
	private class LocationButton extends MapButton
	{
		public ArrayList<ElementInstance> elementInstances;
		
		public LocationButton(ImageIcon imageIcon, ImageIcon disabledImageIcon, MapPosition position, ArrayList<ElementInstance> elementInstances, String tooltipText) throws Exception
		{
			super(imageIcon, disabledImageIcon, position);
			this.elementInstances = elementInstances;
			if (tooltipText != null)
				this.updateTooltip(tooltipText);
		}
		
		public LocationButton(ImageIcon imageIcon, ImageIcon disabledImageIcon, MapPosition position, ArrayList<ElementInstance> elementInstances) throws Exception
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
