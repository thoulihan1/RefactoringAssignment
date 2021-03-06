package employee;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public abstract class SearchDialog extends JDialog{
	EmployeeDetails parent;
	JButton search, cancel;
	JTextField searchField;
	
	JPanel searchPanel;
	JPanel textPanel;
	JPanel buttonPanel;
	JLabel searchLabel;

	public SearchDialog(EmployeeDetails parent) {
		setModal(true);
		this.parent = parent;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(searchPane());
		setContentPane(scrollPane);


		getRootPane().setDefaultButton(search);
		
		setSize(500, 190);
		setLocation(350, 250);
		setVisible(true);

	}

	public Container searchPane() {
		searchPanel = new JPanel(new GridLayout(3,1));
		textPanel = new JPanel();
		buttonPanel = new JPanel();
		textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		buttonPanel.add(search = new JButton("Search"));
		search.requestFocus();

		addLabelAndListener();

		textPanel.add(searchField = new JTextField(20));
		searchField.setFont(this.parent.font1);
		searchField.setDocument(new JTextFieldLimit(20));

		buttonPanel.add(cancel = new JButton("Cancel"));

		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		searchPanel.add(textPanel);
		searchPanel.add(buttonPanel);

		return searchPanel;
	}

	public abstract void addLabelAndListener();
}











