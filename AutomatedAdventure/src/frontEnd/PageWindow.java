package frontEnd;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import backend.Scenario;
import backend.pages.PageContext;
import backend.pages.PageInstance;

public class PageWindow extends MyWindow
{
	JTextArea textArea;
	ChoiceBox choiceBox;
	
	public PageWindow(Scenario scenario)
	{
		super();
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		this.choiceBox = new ChoiceBox();
		JPanel innerPanel = new JPanel();
		this.setLayout(new GridBagLayout());
		innerPanel.add(this.textArea);
		innerPanel.setLayout(new GridLayout(1, 1));
		this.add(innerPanel, this.getConstraints(1, 1, 1, 1));
		this.add(this.choiceBox, this.getConstraints(1, 2, 1, 0));
	}
	
	private GridBagConstraints getConstraints(int gridx, int gridy, int weightx, int weighty)
	{
		GridBagConstraints gdc = new GridBagConstraints();
		gdc.gridx = gridx;
		gdc.gridy = gridy;
		gdc.weightx = weightx;
		gdc.weighty = weighty;	
		gdc.fill = GridBagConstraints.BOTH;
		return gdc;
	}
	
	public void update(PageInstance pageInstance) throws Exception
	{
		String redirectPage = pageInstance.getRedirect();
		
		if (redirectPage == null)
		{
			redirectPage = pageInstance.getRandomRedirect();
		}
		
		if (redirectPage != null)
		{
			PageContext pageContext = pageInstance.getPageContext();
			Scenario scenario = pageInstance.getScenario();
			String pageTemplate = scenario.getPageTemplate(redirectPage);
			PageInstance newInstance = new PageInstance(scenario, pageContext, pageTemplate);
			this.update(newInstance);
		}
		else
		{
			this.textArea.setText(pageInstance.getText());
			this.choiceBox.updateChoiceBox(pageInstance);
		}
	}
}
