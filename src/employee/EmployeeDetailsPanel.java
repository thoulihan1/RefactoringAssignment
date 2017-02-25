package employee;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetailsPanel{
	
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	String[] fullTime = { "", "Yes", "No" };
	String[] gender = { "", "M", "F" };
	String[] department = { "", "Administration", "Production", "Transport", "Management" };
	
	JPanel buttonPanel;
	JTextField field;
	
	public JPanel detailsPanel(){
		JPanel empDetails = new JPanel(new MigLayout());
		buttonPanel = new JPanel();

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), "growx, pushx");
		empDetails.add(idField = new JTextField(20), "growx, pushx, wrap");
		idField.setEditable(false);

		empDetails.add(new JLabel("PPS Number:"), "growx, pushx");
		empDetails.add(ppsField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Surname:"), "growx, pushx");
		empDetails.add(surnameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("First Name:"), "growx, pushx");
		empDetails.add(firstNameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Gender:"), "growx, pushx");
		empDetails.add(genderCombo = new JComboBox<String>(gender), "growx, pushx, wrap");

		empDetails.add(new JLabel("Department:"), "growx, pushx");
		empDetails.add(departmentCombo = new JComboBox<String>(department), "growx, pushx, wrap");

		empDetails.add(new JLabel("Salary:"), "growx, pushx");
		empDetails.add(salaryField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Full Time:"), "growx, pushx");
		empDetails.add(fullTimeCombo = new JComboBox<String>(fullTime), "growx, pushx, wrap");
		
		return empDetails;
	}

	public JComboBox<String> getGenderCombo() {
		return genderCombo;
	}

	public JComboBox<String> getDepartmentCombo() {
		return departmentCombo;
	}

	public JComboBox<String> getFullTimeCombo() {
		return fullTimeCombo;
	}

	public JTextField getIdField() {
		return idField;
	}

	public JTextField getPpsField() {
		return ppsField;
	}

	public JTextField getSurnameField() {
		return surnameField;
	}

	public JTextField getFirstNameField() {
		return firstNameField;
	}

	public JTextField getSalaryField() {
		return salaryField;
	}

	public String[] getFullTime() {
		return fullTime;
	}

	public String[] getGender() {
		return gender;
	}

	public String[] getDepartment() {
		return department;
	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	public JTextField getField() {
		return field;
	}
	
	
	
	

}
