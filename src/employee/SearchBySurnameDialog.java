
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

public class SearchBySurnameDialog extends SearchDialog{

	public SearchBySurnameDialog(EmployeeDetails parent) {
		super(parent);
	}

	@Override
	public void addLabelAndListener(){
		searchPanel.add(new JLabel("Search by Surname"));
		textPanel.add(searchLabel = new JLabel("Enter Surname:"));

		search.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.searchBySurnameField.setText(searchField.getText());
				parent.search(parent.SEARCH_SURNAME);
				//parent.searchEmployeeBySurname();
				dispose();
			}
		});
	}
}
