package frontEnd;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import backend.Scenario;
import backend.pages.PageContext;
import backend.pages.PageInstance;
import json.JsonEntityMap;
import json.RestrictedJson;
import json.restrictions.PageRestriction;
import json.restrictions.PanelRestriction;

public class PageWindow extends MyWindow
{
	HashMap<String, PagePanel> panelMap = new HashMap<String, PagePanel>();
	
	public PageWindow(JsonEntityMap<RestrictedJson<PanelRestriction>> panelMap)
	{
		super();
		this.setLayout(new GridBagLayout());
		
		for(Entry<String, RestrictedJson<PanelRestriction>> entry : panelMap.getEntityMap().entrySet())
		{
			RestrictedJson<PanelRestriction> panelJson = entry.getValue();
			PagePanel pagePanel = new PagePanel();
			int x = panelJson.getNumber(PanelRestriction.X);
			int y = panelJson.getNumber(PanelRestriction.Y);
			int width = panelJson.getNumber(PanelRestriction.WIDTH);
			int height = panelJson.getNumber(PanelRestriction.HEIGHT);
			this.add(pagePanel, this.setupConstraints(x, y, width, height));
			this.panelMap.put(entry.getKey(), pagePanel);
		}
	}
	
	public void update(PageInstance pageInstance) throws Exception
	{
		RestrictedJson<PageRestriction> panelJson = pageInstance.getPageJson();
		String panelName = panelJson.getString(PageRestriction.PANEL_NAME);
		for(Entry<String, PagePanel> entry : this.panelMap.entrySet())
		{
			if (panelName.equals(entry.getKey()))
			{
				entry.getValue().update(pageInstance);
			}
			else
			{
				entry.getValue().clear();
			}
		}
	}
	
	private GridBagConstraints setupConstraints(int x, int y, int width, int height)
	{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = width;
		gridBagConstraints.weighty = height;
		return gridBagConstraints;
	}
}
