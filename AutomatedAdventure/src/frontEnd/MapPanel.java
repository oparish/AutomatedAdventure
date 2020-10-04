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
	private static final Pattern tooltipTextPattern = Pattern.compile("<element:(.*)>");
	
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
	
	private void createButton(int x, int y, ElementInstance elementInstance) throws Exception
	{
		Element element = elementInstance.getElement();
		RestrictedJson<ImageRestriction> imageData = element.getMapImageData(this.map);
		String fileName = imageData.getString(ImageRestriction.FILENAME);
		ImageIcon imageIcon = new ImageIcon(fileName);
		RestrictedJson<TooltipRestriction> tooltipData = element.getTooltip(this.map);
		JsonEntityArray<RestrictedJson<TooltipComponentRestriction>> components = 
				tooltipData.getRestrictedJsonArray(TooltipRestriction.TOOLTIP_COMPONENTS, TooltipComponentRestriction.class);
		
		String tooltipText = null;
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
		
		MapButton mapButton;
		
		if (tooltipText != null)
		{
			String adjustedTooltipText = this.assessTooltipText(elementInstance, tooltipText);
			mapButton = new MapButton(imageIcon, elementInstance, adjustedTooltipText);
		}
		else
		{
			mapButton = new MapButton(imageIcon, elementInstance);
		}
		
		mapButton.setMargin(new Insets(-4, -4, -4, -4));
		mapButton.addActionListener(this);
		this.innerPanel.add(mapButton);
		this.mapButtons.add(mapButton);
	}

	private String assessTooltipText(ElementInstance elementInstance, String tooltipText)
	{
		Matcher tooltipMatcher = MapPanel.tooltipTextPattern.matcher(tooltipText);
		while(tooltipMatcher.find())
		{
			String elementQualityName = tooltipMatcher.group(1);
			String stringValue = elementInstance.getValue(elementQualityName);
			tooltipText = tooltipMatcher.replaceFirst(stringValue);
			tooltipMatcher = MapPanel.tooltipTextPattern.matcher(tooltipText);
		}
		return tooltipText;
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
		public ElementInstance elementInstance;
		
		public MapButton(ImageIcon imageIcon, ElementInstance elementInstance, String tooltipText)
		{
			super(imageIcon);
			this.setDisabledIcon(imageIcon);
			this.elementInstance = elementInstance;
			if (tooltipText != null)
				this.updateTooltip(tooltipText);
		}
		
		public MapButton(ImageIcon imageIcon, ElementInstance elementInstance)
		{
			this(imageIcon, elementInstance, null);
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
