package frontEnd;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class ControlPanel extends JPanel 
{
	private static final String CONFIRM = "Confirm";
	JComboBox<String> optionBox = new JComboBox<String>();
	JButton confirmButton = new JButton(CONFIRM);
	
	public ControlPanel()
	{
		super();
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints optionBoxConstraints = new GridBagConstraints();
		optionBoxConstraints.gridx = 0;
		optionBoxConstraints.gridy = 0;
		optionBoxConstraints.weightx = 1;
		optionBoxConstraints.weighty = 1;
		optionBoxConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.optionBox, optionBoxConstraints);
		
		GridBagConstraints confirmButtonConstraints = new GridBagConstraints();
		confirmButtonConstraints.gridx = 1;
		confirmButtonConstraints.gridy = 0;
		confirmButtonConstraints.weightx = 1;
		confirmButtonConstraints.weighty = 1;
		this.add(this.confirmButton, confirmButtonConstraints);
	}
}
