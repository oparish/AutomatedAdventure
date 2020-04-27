package frontEnd;

import java.awt.Component;
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
			Pages.loadPage(choiceEntry.elementChoice);
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
	}
	
	private class ChoiceBoxRenderer implements ListCellRenderer
	{

		@Override
		public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4)
		{	
			if (arg1 == null)
				return new JLabel("");
			
			ChoiceEntry choiceEntry = (ChoiceEntry) arg1;
			return new JLabel(choiceEntry.value);
		}
		
	}
}


