package employee;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Thomas on 2/27/17.
 */
public class AddRecordDialog extends JDialog{

	EmpDetailsPanel addRecordDialog;

	public AddRecordDialog(EmployeeDetails parent){


		addRecordDialog = new EmpDetailsPanel(parent);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


		JScrollPane scrollPane = new JScrollPane(addRecordDialog.detailsPanel());
		addRecordDialog.setEnabled(true);
		addRecordDialog.getIdField().setText(Integer.toString(addRecordDialog.empDetails.getNextFreeId()));
		setContentPane(scrollPane);

		getRootPane().setDefaultButton(addRecordDialog.getSaveChange());

		setSize(500, 370);
		setLocation(350, 250);
		setVisible(true);


		addRecordDialog.getSaveChange().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(addRecordDialog.checkInput()){
					addRecord();
					dispose();
				}
			}
		});

		addRecordDialog.getCancelChange().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(addRecordDialog.checkInput()){
					dispose();
				}
			}
		});
	}

	public void addRecord() {
		System.out.println("Adding record");
		boolean fullTime = false;
		Employee theEmployee;

		if (((String) addRecordDialog.getFullTimeCombo().getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;
		// create new Employee record with details from text fields
		theEmployee = new Employee(Integer.parseInt(addRecordDialog.getIdField().getText()),  addRecordDialog.getPpsField().getText().toUpperCase(),  addRecordDialog.getSurnameField().getText().toUpperCase(),
				addRecordDialog.getFirstNameField().getText().toUpperCase(),  addRecordDialog.getGenderCombo().getSelectedItem().toString().charAt(0),
				addRecordDialog.getDepartmentCombo().getSelectedItem().toString(), Double.parseDouble(addRecordDialog.getSalaryField().getText()), fullTime);
		this.addRecordDialog.empDetails.currentEmployee = theEmployee;
		System.out.println("Created Employee: " + theEmployee.getPps());
		System.out.println("Current Employee:" + this.addRecordDialog.empDetails.currentEmployee.getPps());
		this.addRecordDialog.empDetails.addRecord(theEmployee);
		this.addRecordDialog.empDetails.getDetailsPanel().displayRecords(theEmployee);
	}
}
