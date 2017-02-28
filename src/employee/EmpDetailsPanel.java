package employee;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

/**
 * Created by Thomas on 2/27/17.
 */
public class EmpDetailsPanel implements DocumentListener, ItemListener {

    private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
    private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
    private static EmployeeDetails frame = new EmployeeDetails();
    private RandomFile application = new RandomFile();
    private long currentByteStart = 0;
    private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
    private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
    boolean change = false;

    Employee currentEmployee;

    String[] gender = { "", "M", "F" };

    String[] department = { "", "Administration", "Production", "Transport", "Management" };

    String[] fullTime = { "", "Yes", "No" };


    private JButton saveChange, cancelChange;

    EmployeeDetails empDetails;

    public EmpDetailsPanel(EmployeeDetails empDetails){
        this.empDetails = empDetails;
    }

    public JPanel detailsPanel(){
        JPanel empDetails = new JPanel(new MigLayout());
        JPanel buttonPanel = new JPanel();
        JTextField field;

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

        saveChange = new JButton("Save");

        buttonPanel.add(saveChange);
        saveChange.setVisible(false);
        saveChange.setToolTipText("Save changes");
        saveChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("saveChange clicked");
                if (checkInput() && !checkForChanges())
                    ;
            }
        });
        cancelChange = new JButton("Cancel");

        buttonPanel.add(cancelChange);
        cancelChange.setVisible(false);
        cancelChange.setToolTipText("Cancel edit");
        cancelChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("cancelChange clicked, calling cancelChange()");
                cancelChange();

            }
        });
        empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

        // loop through panel components and add listeners and format
        for (int i = 0; i < empDetails.getComponentCount(); i++) {

            if (empDetails.getComponent(i) instanceof JTextField) {
                field = (JTextField) empDetails.getComponent(i);
                setUpField(field);
            }
            else if (empDetails.getComponent(i) instanceof JComboBox) {
                JComboBox<String> combo = (JComboBox<String>) empDetails.getComponent(i);
                setUpJComboBox(combo);
            }
        }
        return empDetails;
    }

    private void cancelChange() {
        setEnabled(false);
        displayRecords(empDetails.getCurrentEmployee());
    }

    public void setUpField(JTextField field){
        field.setEditable(false);

        if (field == ppsField)
            field.setDocument(new JTextFieldLimit(9));
        else
            field.setDocument(new JTextFieldLimit(20));

        field.getDocument().addDocumentListener(this);
    }

    public void setUpJComboBox(JComboBox<String> combo){
        combo.setBackground(Color.WHITE);
        combo.setEnabled(false);
        ((JComboBox<String>) combo).addItemListener(this);
        ((JComboBox<String>) combo).setRenderer(new DefaultListCellRenderer() {

            public void paint(Graphics g) {
                setForeground(new Color(65, 65, 65));
                super.paint(g);
            }// end paint
        });
    }

    public boolean checkInput() {
        boolean valid = true;
        // if any of inputs are in wrong format, colour text field and display
        // message
        if (ppsField.isEditable() && ppsField.getText().trim().isEmpty()) {
            ppsField.setBackground(new Color(255, 150, 150));
            valid = false;
        } // end if
        if (ppsField.isEditable() && correctPps(ppsField.getText().trim(), currentByteStart)) {
            ppsField.setBackground(new Color(255, 150, 150));
            valid = false;
        } // end if
        if (surnameField.isEditable() && surnameField.getText().trim().isEmpty()) {
            surnameField.setBackground(new Color(255, 150, 150));
            valid = false;
        } // end if
        if (firstNameField.isEditable() && firstNameField.getText().trim().isEmpty()) {
            firstNameField.setBackground(new Color(255, 150, 150));
            valid = false;
        } // end if
        if (genderCombo.getSelectedIndex() == 0 && genderCombo.isEnabled()) {
            genderCombo.setBackground(new Color(255, 150, 150));
            valid = false;
        } // end if
        if (departmentCombo.getSelectedIndex() == 0 && departmentCombo.isEnabled()) {
            departmentCombo.setBackground(new Color(255, 150, 150));
            valid = false;
        } // end if
        try {// try to get values from text field
            Double.parseDouble(salaryField.getText());
            // check if salary is greater than 0
            if (Double.parseDouble(salaryField.getText()) < 0) {
                salaryField.setBackground(new Color(255, 150, 150));
                valid = false;
            } // end if
        } // end try
        catch (NumberFormatException num) {
            if (salaryField.isEditable()) {
                salaryField.setBackground(new Color(255, 150, 150));
                valid = false;
            } // end if
        } // end catch
        if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
            fullTimeCombo.setBackground(new Color(255, 150, 150));
            valid = false;
        } // end if
        // display message if any input or format is wrong

        if (!valid)
            JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
        // set text field to white colour if text fields are editable
        if (ppsField.isEditable())
            setToWhite();



        return valid;
    }

    public boolean correctPps(String pps, long currentByte) {
        boolean ppsExist = false;
        // check for correct PPS format based on assignment description
        if (isPPsCorrectLength(pps)) {
            if (Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1)) && Character.isDigit(pps.charAt(2))&& Character.isDigit(pps.charAt(3)) && Character.isDigit(pps.charAt(4))	&& Character.isDigit(pps.charAt(5))
                    && Character.isDigit(pps.charAt(6))	&& Character.isLetter(pps.charAt(7))
                    && (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
                // open file for reading
                application.openReadFile(empDetails.getFile().getAbsolutePath());
                // look in file is PPS already in use
                ppsExist = application.isPpsExist(pps, currentByte);
                application.closeReadFile();// close file for reading
            } // end if
            else
                ppsExist = true;
        } // end if
        else
            ppsExist = true;

        return ppsExist;
    }// end correctPPS

    public boolean isPPsCorrectLength(String pps){
        if(pps.length() == 8 || pps.length() == 9){
            return true;
        } else
            return false;
    }


    private void setToWhite() {
        ppsField.setBackground(UIManager.getColor("TextField.background"));
        surnameField.setBackground(UIManager.getColor("TextField.background"));
        firstNameField.setBackground(UIManager.getColor("TextField.background"));
        salaryField.setBackground(UIManager.getColor("TextField.background"));
        genderCombo.setBackground(UIManager.getColor("TextField.background"));
        departmentCombo.setBackground(UIManager.getColor("TextField.background"));
        fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
    }// end setToWhite


    public boolean checkForChanges() {
        boolean anyChanges = false;
        // if changes where made, allow user to save there changes
        if (change) {
            empDetails.saveChanges();// save changes
            anyChanges = true;
        } // end if
        // if no changes made, set text fields as unenabled and display
        // current Employee
        else {
            setEnabled(false);
            //displayRecords(currentEmployee);
        } // end else

        return anyChanges;
    }// end checkForChanges


    public Employee getChangedDetails() {
        boolean fullTime = false;
        Employee theEmployee;
        if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
            fullTime = true;

        theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(),
                surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(),
                genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(),
                Double.parseDouble(salaryField.getText()), fullTime);

        return theEmployee;
    }// end getChangedDetails

    public void displayRecords(Employee thisEmployee) {
        System.out.println("DisplayRecords" + thisEmployee.getPps());
        int countGender = 0;
        int countDep = 0;
        boolean found = false;

        empDetails.getSearchByIdField().setText("");
        empDetails.getSearchBySurnameField().setText("");
        // if Employee is null or ID is 0 do nothing else display Employee
        // details
        if (thisEmployee == null) {
        }

        else if (thisEmployee.getEmployeeId() == 0) {
        }

        else {
            // find corresponding gender combo box value to current employee
            while (!found && countGender < gender.length - 1) {
                if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
                    found = true;
                else
                    countGender++;
            } // end while
            found = false;
            // find corresponding department combo box value to current employee
            while (!found && countDep < department.length - 1) {
                if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
                    found = true;
                else
                    countDep++;
            } // end while
            idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
            ppsField.setText(thisEmployee.getPps().trim());
            surnameField.setText(thisEmployee.getSurname().trim());
            firstNameField.setText(thisEmployee.getFirstName());
            genderCombo.setSelectedIndex(countGender);
            departmentCombo.setSelectedIndex(countDep);
            salaryField.setText(format.format(thisEmployee.getSalary()));
            // set corresponding full time combo box value to current employee
            if (thisEmployee.getFullTime() == true)
                fullTimeCombo.setSelectedIndex(1);
            else
                fullTimeCombo.setSelectedIndex(2);
        }
        change = false;


    }// end display records

    public void setEnabled(boolean booleanValue) {
        boolean search;

        if (booleanValue)
            search = false;
        else
            search = true;
        ppsField.setEditable(booleanValue);
        surnameField.setEditable(booleanValue);
        firstNameField.setEditable(booleanValue);
        genderCombo.setEnabled(booleanValue);
        departmentCombo.setEnabled(booleanValue);
        salaryField.setEditable(booleanValue);
        fullTimeCombo.setEnabled(booleanValue);
        saveChange.setVisible(booleanValue);
        cancelChange.setVisible(booleanValue);
        empDetails.getSearchByIdField().setEnabled(search);
        empDetails.getSearchBySurnameField().setEnabled(search);
        empDetails.getSearchId().setEnabled(search);
        empDetails.getSearchSurname().setEnabled(search);
    }// end setEnabled

    public boolean isSomeoneToDisplay() {
        boolean someoneToDisplay = false;
        // open file for reading
        application.openReadFile(empDetails.getFile().getAbsolutePath());
        // check if any of records in file is active - ID is not 0
        someoneToDisplay = application.isSomeoneToDisplay();
        application.closeReadFile();// close file for reading
        // if no records found clear all text fields and display message
        if (!someoneToDisplay) {
            currentEmployee = null;
            idField.setText("");
            ppsField.setText("");
            surnameField.setText("");
            firstNameField.setText("");
            salaryField.setText("");
            genderCombo.setSelectedIndex(0);
            departmentCombo.setSelectedIndex(0);
            fullTimeCombo.setSelectedIndex(0);
            JOptionPane.showMessageDialog(null, "No Employees registered!");
        }
        return someoneToDisplay;
    }

    public void editDetails() {
        // activate field for editing if there is records to display
        if (isSomeoneToDisplay()) {
            // remove euro sign from salary text field
            salaryField.setText(fieldFormat.format(empDetails.getCurrentEmployee().getSalary()));
            change = false;
            setEnabled(true);// enable text fields for editing
        } // end if
    }
    // display current Employee details



    @Override
    public void insertUpdate(DocumentEvent e) {
        change = true;
        new JTextFieldLimit(20);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        change = true;
        new JTextFieldLimit(20);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        change = true;
        new JTextFieldLimit(20);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        change = true;

    }

    public JTextField getIdField(){
        return idField;
    }

    public JButton getSaveChange(){
        return saveChange;
    }

    public JButton getCancelChange(){
        return cancelChange;
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

    public static EmployeeDetails getFrame() {
        return frame;
    }
}
