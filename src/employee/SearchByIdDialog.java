
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

public class SearchByIdDialog extends SearchDialog{

	public SearchByIdDialog(EmployeeDetails parent) {
		super(parent);
	}

	@Override
	public void addLabelAndListener(){
		searchPanel.add(new JLabel("Search by ID"));
		textPanel.add(searchLabel = new JLabel("Enter ID:"));
		//System.out.println("Calling code in new constructor");

		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Double.parseDouble(searchField.getText());
					parent.searchByIdField.setText(searchField.getText());
					parent.search(parent.SEARCH_ID);

					//parent.searchEmployeeById();
					dispose();
				}
				catch (NumberFormatException num) {
					searchField.setBackground(new Color(255, 150, 150));
					JOptionPane.showMessageDialog(null, "Wrong ID format!");
				}
			}
		});
	}
}
