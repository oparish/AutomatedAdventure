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
		Pages.loadPage(choiceEntry.value);
	}
	
	public void updateChoiceBox(HashMap<String, String> choiceMap)
	{
		this.removeAll();
		for (Entry<String, String> entry : choiceMap.entrySet())
		{
			ChoiceEntry choiceEntry = new ChoiceEntry(entry.getKey(), entry.getValue());
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
			return new JLabel(choiceEntry.text);
		}
		
	}
}


