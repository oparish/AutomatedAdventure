package frontEnd;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import backend.Map;
import backend.Scenario;
import backend.pages.PageContext;
import backend.pages.PageInstance;
import json.JsonEntityMap;
import json.RestrictedJson;
import json.restrictions.PageRestriction;
import json.restrictions.PanelRestriction;
import main.Main;
import main.Pages;

public class PageWindow extends MyWindow
{	
	HashMap<String, PagePanel> panelMap = new HashMap<String, PagePanel>();
	
	public PageWindow(Scenario scenario, JsonEntityMap<RestrictedJson<PanelRestriction>> panelMap) throws Exception
	{
		super();

		MetalLookAndFeel.setCurrentTheme(new WoodTheme());
		try
		{
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		}
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}

		SwingUtilities.updateComponentTreeUI(this);
		
		this.setLayout(new GridBagLayout());
		
		for(Entry<String, RestrictedJson<PanelRestriction>> entry : panelMap.getEntityMap().entrySet())
		{
			PagePanel pagePanel;
			RestrictedJson<PanelRestriction> panelJson = entry.getValue();
			
			String mapName = panelJson.getString(PanelRestriction.MAP_NAME);
			
			if (mapName != null)
			{
				Map map = scenario.getMapByName(mapName);
				pagePanel = new PageWithMapPanel(map);
			}
			else
			{
				pagePanel = new PageWithChoicesPanel();
			}
			
			int x = panelJson.getNumber(PanelRestriction.X);
			int y = panelJson.getNumber(PanelRestriction.Y);
			int width = panelJson.getNumber(PanelRestriction.WIDTH);
			int height = panelJson.getNumber(PanelRestriction.HEIGHT);
			this.add(pagePanel, Main.setupConstraints(x, y, width, height));
			this.panelMap.put(entry.getKey(), pagePanel);
		}
	}
	
	public void update(PageInstance pageInstance) throws Exception
	{
		RestrictedJson<PageRestriction> panelJson = pageInstance.getPageJson();
		String panelName = panelJson.getString(PageRestriction.PANEL_NAME);
		String secondaryPanelName = panelJson.getString(PageRestriction.SECONDARY_PANEL_NAME);
		for(Entry<String, PagePanel> entry : this.panelMap.entrySet())
		{
			PagePanel pagePanel = entry.getValue();
			String key = entry.getKey();
			if (panelName.equals(key))
			{
				pagePanel.update(pageInstance);
			}
			else if (secondaryPanelName != null && secondaryPanelName.equals(key))
			{
				pagePanel.secondaryUpdate(pageInstance);
			}
			else
			{
				pagePanel.clear();
			}
		}
	}
}
