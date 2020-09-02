package frontEnd;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import backend.pages.ElementChoice;
import backend.pages.PageInstance;
import main.Pages;

public class ChoiceBox extends JList<ChoiceEntry> implements ListSelectionListener
{
	private DefaultListModel<ChoiceEntry> model = new DefaultListModel<ChoiceEntry>();
	
	public ChoiceBox()
	{
		super();
		this.setModel(this.model);
		this.setCellRenderer(new ChoiceBoxRenderer());
		this.addListSelectionListener(this);
	}
	
	public void valueChanged(ListSelectionEvent lse)
	{		
		ChoiceEntry choiceEntry = this.getSelectedValue();
		if (choiceEntry != null)
		{
			ElementChoice elementChoice = choiceEntry.elementChoice;
			try
			{
				Pages.getScenario().loadPage(elementChoice);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void updateChoiceBox(PageInstance pageInstance)
	{
		this.model.removeAllElements();
		HashMap<String, ElementChoice> choiceMap = pageInstance.getChoiceMap();
		for (String key : pageInstance.getChoiceList())
		{
			ChoiceEntry choiceEntry = new ChoiceEntry(key, choiceMap.get(key));
			this.model.addElement(choiceEntry);	
		}
		this.setEnabled(true);
	}
	
	public void clear()
	{
		this.model.removeAllElements();
		this.setEnabled(false);
	}
	
	private class ChoiceBoxRenderer implements ListCellRenderer
	{

		@Override
		public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4)
		{	
			Font font = new Font("arial", 1, 20);
			if (arg1 == null)
			{
				JLabel label = new JLabel("");
				label.setFont(font);
				return label;
			}
			
			ChoiceEntry choiceEntry = (ChoiceEntry) arg1;
			JLabel label = new JLabel(choiceEntry.value);
			label.setFont(font);
			return label;
		}
		
	}
}


