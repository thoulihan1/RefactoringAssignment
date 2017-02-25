package employee;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class SearchDialog extends JDialog{
	EmployeeDetails parent;
	JButton search, cancel;
	JTextField searchField;
	
	JPanel searchPanel;
	JPanel textPanel;
	JPanel buttonPanel;
	JLabel searchLabel;
	int ID = 1;
	int SURNAME = 2;
	

	public SearchDialog(EmployeeDetails parent, int mode) {
		//setTitle("Search by Surname");
		setModal(true);
		this.parent = parent;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(searchPane(mode));
		setContentPane(scrollPane);

		getRootPane().setDefaultButton(search);
		
		setSize(500, 190);
		setLocation(350, 250);
		setVisible(true);
	}
	
	public Container searchPane(int mode) {
		searchPanel = new JPanel(new GridLayout(3,1));
		textPanel = new JPanel();
		buttonPanel = new JPanel();
		textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		buttonPanel.add(search = new JButton("Search"));
		search.requestFocus();
		
		setButtonListeners(mode);

		searchLabel.setFont(this.parent.font1);
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
	
	public void setButtonListeners(int mode){
		if(mode==1){
			searchPanel.add(new JLabel("Search by ID"));
			textPanel.add(searchLabel = new JLabel("Enter ID:"));
			
			search.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Double.parseDouble(searchField.getText());
						parent.searchByIdField.setText(searchField.getText());
						parent.searchEmployeeById();
						dispose();
					}
					catch (NumberFormatException num) {
						searchField.setBackground(new Color(255, 150, 150));
						JOptionPane.showMessageDialog(null, "Wrong ID format!");
					}		
				}	
			});
			
		} else if(mode==2){
			searchPanel.add(new JLabel("Search by Surname"));
			textPanel.add(searchLabel = new JLabel("Enter Surname:"));
			
			search.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					parent.searchBySurnameField.setText(searchField.getText());
					parent.searchEmployeeBySurname();
					dispose();
				}
				
			});
			this.parent.searchBySurnameField.setText(searchField.getText());
			this.parent.searchEmployeeBySurname();
			dispose();
		}
	}
}











