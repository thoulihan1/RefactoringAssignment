package employee;

/**
 * Created by Thomas on 2/27/17.
 */

/* *
 * This is a menu driven system that will allow users to define a data structure representing a collection of
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 *
 * */

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

public class EmployeeDetails extends JFrame implements  ItemListener, DocumentListener, WindowListener {

	private long currentByteStart = 0;
	private RandomFile application = new RandomFile();
	private FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	private File file;
	private boolean change = false;
	boolean changesMade = false;
	private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
			searchBySurname, listAll, closeApp;
	private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname, saveChange, cancelChange;

	private static EmployeeDetails frame = new EmployeeDetails();

	EmpDetailsPanel empDetailsPanel;


	Font font1 = new Font("SansSerif", Font.BOLD, 16);
	String generatedFileName;
	Employee currentEmployee;
	JTextField searchByIdField, searchBySurnameField;

	ActionListener firstItemListener, prevItemListener, nextItemListener, lastItemListener, allItemsListener, createItemListener, modifyItemListener, deleteItemListener;

	// initialize menu bar
	private JMenuBar menuBar() {
		System.out.println("menuBar being called");
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu("Records");
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu("Navigate");
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu("Exit");
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);

		open = new JMenuItem("Open");
		open.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					openFile();
			}
		});

		fileMenu.add(open);
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));


		save = new JMenuItem("Save");
		save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					saveFile();
				change = false;
			}
		});

		fileMenu.add(save);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveAs = new JMenuItem("Save As");
		saveAs.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					saveFileAs();
				change = false;
			}
		});
		fileMenu.add(saveAs);
		saveAs.setMnemonic(KeyEvent.VK_F2);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));

		recordMenu.add(create = new JMenuItem("Create new Record")).addActionListener(createItemListener);
		create.setMnemonic(KeyEvent.VK_N);
		create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		recordMenu.add(modify = new JMenuItem("Modify Record")).addActionListener(modifyItemListener);
		modify.setMnemonic(KeyEvent.VK_E);
		modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		recordMenu.add(delete = new JMenuItem("Delete Record")).addActionListener(deleteItemListener);

		navigateMenu.add(firstItem = new JMenuItem("First"));
		firstItem.addActionListener(firstItemListener);
		navigateMenu.add(prevItem = new JMenuItem("Previous"));
		prevItem.addActionListener(prevItemListener);
		navigateMenu.add(nextItem = new JMenuItem("Next"));
		nextItem.addActionListener(nextItemListener);
		navigateMenu.add(lastItem = new JMenuItem("Last"));
		lastItem.addActionListener(lastItemListener);
		navigateMenu.addSeparator();

		searchById = new JMenuItem("Search by ID");
		searchById.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges()){
					if (empDetailsPanel.isSomeoneToDisplay())
						new SearchByIdDialog(EmployeeDetails.this);
				}
			}
		});
		navigateMenu.add(searchById);

		searchBySurname = new JMenuItem("Search by Surname");
		searchBySurname.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					if (empDetailsPanel.isSomeoneToDisplay())
						new SearchBySurnameDialog(EmployeeDetails.this);
			}
		});

		navigateMenu.add(searchBySurname);
		navigateMenu.add(listAll = new JMenuItem("List all Records")).addActionListener(allItemsListener);

		ActionListener closeAppListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					exitApp();
			}
		};
		closeApp = new JMenuItem("Close");
		closeApp.addActionListener(closeAppListener);

		closeMenu.add(closeApp);
		closeApp.setMnemonic(KeyEvent.VK_F4);
		closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));

		return menuBar;
	}// end menuBar

	private JPanel searchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());
		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

		ImageIcon imgIcon = new ImageIcon(new ImageIcon("imgres.png").getImage().getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH));
		String buttonMeasurements = "width 35:35:35, height 20:20:20, growx, pushx, wrap";
		String textFieldMeasurements = "width 200:200:200, growx, pushx";
		searchByIdField = new JTextField(20);
		searchBySurnameField = new JTextField(20);

		ActionListener idSearchListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				searchEmployeeById();
			}
		};

		ActionListener surnameSearchListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				searchEmployeeBySurname();
			}
		};

		searchByIdField.setDocument(new JTextFieldLimit(20));
		searchByIdField.addActionListener(idSearchListener);
		searchId = new JButton(imgIcon);
		searchId.addActionListener(idSearchListener);
		searchId.setToolTipText("Search Employee By ID");

		searchBySurnameField.addActionListener(surnameSearchListener);
		searchBySurnameField.setDocument(new JTextFieldLimit(20));
		searchSurname = new JButton(imgIcon);
		searchSurname.addActionListener(surnameSearchListener);
		searchSurname.setToolTipText("Search Employee By Surname");

		//Search By Id
		searchPanel.add(new JLabel("Search by ID:"), "growx, pushx");
		searchPanel.add(searchByIdField, textFieldMeasurements);
		searchPanel.add(searchId, buttonMeasurements);

		//Search By Surname
		searchPanel.add(new JLabel("Search by Surname:"), "growx, pushx");
		searchPanel.add(searchBySurnameField, textFieldMeasurements);
		searchPanel.add(searchSurname, buttonMeasurements);

		return searchPanel;
	}// end searchPanel

	private JPanel navigPanel() {
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		navigPanel.add(first = new JButton(new ImageIcon(new ImageIcon("first.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		first.setPreferredSize(new Dimension(17, 17));
		first.addActionListener(firstItemListener);
		first.setToolTipText("Display first Record");

		navigPanel.add(previous = new JButton(new ImageIcon(new ImageIcon("previous.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		previous.setPreferredSize(new Dimension(17, 17));
		previous.addActionListener(prevItemListener);
		previous.setToolTipText("Display next Record");

		navigPanel.add(next = new JButton(new ImageIcon(new ImageIcon("next.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		next.setPreferredSize(new Dimension(17, 17));
		next.addActionListener(nextItemListener);
		next.setToolTipText("Display previous Record");

		navigPanel.add(last = new JButton(new ImageIcon(new ImageIcon("last.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		last.setPreferredSize(new Dimension(17, 17));
		last.addActionListener(lastItemListener);
		last.setToolTipText("Display last Record");

		return navigPanel;
	}// end naviPanel

	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton("Add Record"), "growx, pushx");
		add.addActionListener(createItemListener);
		add.setToolTipText("Add new Employee Record");
		buttonPanel.add(edit = new JButton("Edit Record"), "growx, pushx");
		edit.addActionListener(modifyItemListener);
		edit.setToolTipText("Edit current Employee");
		buttonPanel.add(deleteButton = new JButton("Delete Record"), "growx, pushx, wrap");
		deleteButton.addActionListener(deleteItemListener);
		deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(displayAll = new JButton("List all Records"), "growx, pushx");
		displayAll.addActionListener(allItemsListener);
		displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}


	private void displayEmployeeSummaryDialog() {
		if (empDetailsPanel.isSomeoneToDisplay())
			new EmployeeSummaryDialog(getAllEmloyees());
	}

	private void firstRecord() {

		if (empDetailsPanel.isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getFirst();

			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();

			if (currentEmployee.getEmployeeId() == 0)
				nextRecord();
		}
	}

	private void previousRecord() {
		// if any active record in file look for first record
		if (empDetailsPanel.isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for previous record
			currentByteStart = application.getPrevious(currentByteStart);
			// assign current Employee to previous record in file
			currentEmployee = application.readRecords(currentByteStart);
			// loop to previous record until Employee is active - ID is not 0
			while (currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for previous record
				currentByteStart = application.getPrevious(currentByteStart);
				// assign current Employee to previous record in file
				currentEmployee = application.readRecords(currentByteStart);
			} // end while
			application.closeReadFile();// close file for reading
		}
	}

	private void nextRecord() {

		if (empDetailsPanel.isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getNext(currentByteStart);

			currentEmployee = application.readRecords(currentByteStart);

			while (currentEmployee.getEmployeeId() == 0) {

				currentByteStart = application.getNext(currentByteStart);

				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();// close file for reading
		}
	}

	private void lastRecord() {
		// if any active record in file look for first record
		if (empDetailsPanel.isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());

			currentByteStart = application.getLast();

			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();// close file for reading

			if (currentEmployee.getEmployeeId() == 0)
				previousRecord();// look for previous record
		} // end if
	}

	// search Employee by ID
	public void searchEmployeeById() {
		boolean found = false;
		try {
			if (empDetailsPanel.isSomeoneToDisplay()) {
				firstRecord();

				int firstId = currentEmployee.getEmployeeId();

				if (searchByIdField.getText().trim().equals(empDetailsPanel.getIdField().getText().trim()))
					found = true;
				else if (searchByIdField.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					empDetailsPanel.displayRecords(currentEmployee);
				}
				else {
					nextRecord();
					while (firstId != currentEmployee.getEmployeeId()) {

						if (Integer.parseInt(searchByIdField.getText().trim()) == currentEmployee.getEmployeeId()) {
							found = true;
							empDetailsPanel.displayRecords(currentEmployee);
							break;
						} else
							nextRecord();
					}
				}
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			}
		}
		catch (NumberFormatException e) {
			searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		}
		searchByIdField.setBackground(Color.WHITE);
		searchByIdField.setText("");
	}

	// search Employee by surname
	public void searchEmployeeBySurname() {
		boolean found = false;

		if (empDetailsPanel.isSomeoneToDisplay()) {
			firstRecord();
			String firstSurname = currentEmployee.getSurname().trim();

			if (searchBySurnameField.getText().trim().equalsIgnoreCase(empDetailsPanel.getSurnameField().getText().trim()))
				found = true;
			else if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
				found = true;
				empDetailsPanel.displayRecords(currentEmployee);
			}
			else {
				nextRecord();

				while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {

					if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
						found = true;
						empDetailsPanel.displayRecords(currentEmployee);
						break;
					}
					else
						nextRecord();
				}
			}

			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		}
		searchBySurnameField.setText("");
	}

	// get next free ID from Employees in the file
	public int getNextFreeId() {
		int nextFreeId = 0;


		if (file.length() == 0 || !empDetailsPanel.isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();

			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}// end getNextFreeId

	public void addRecord(Employee newEmployee) {
		// open file for writing
		application.openWriteFile(file.getAbsolutePath());
		// write into a file
		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();
	}// end addRecord

	// delete (make inactive - empty) record from file
	private void deleteRecord() {
		if (empDetailsPanel.isSomeoneToDisplay()) {// if any active record in file display
			// message and delete record
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if answer yes delete (make inactive - empty) record
			if (returnVal == JOptionPane.YES_OPTION) {
				// open file for writing
				application.openWriteFile(file.getAbsolutePath());
				// delete (make inactive - empty) record in file proper position
				application.deleteRecords(currentByteStart);
				application.closeWriteFile();// close file for writing
				// if any active record in file display next record
				if (empDetailsPanel.isSomeoneToDisplay()) {
					nextRecord();// look for next record
					empDetailsPanel.displayRecords(currentEmployee);
				} // end if
			} // end if
		} // end if
	}// end deleteDecord

	// create vector of vectors with all Employee details
	private Vector<Object> getAllEmloyees() {
		// vector of Employee objects
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;// vector of each employee details
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();// look for first record
		firstId = currentEmployee.getEmployeeId();
		// loop until all Employees are added to vector
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(new Character(currentEmployee.getGender()));
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(new Double(currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();// look for next record
		} while (firstId != currentEmployee.getEmployeeId());// end do - while
		currentByteStart = byteStart;

		return allEmployee;
	}

	// check for correct PPS format and look if PPS already in use



	// check if file name has extension .dat
	private boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		// check if last characters in file name is .dat
		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
				&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}// end checkFileName

	// open file
	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		// display files in File Chooser only with extension .dat
		fc.setFileFilter(datfilter);
		File newFile; // holds opened file name and path
		// if old file is not empty or changes has been made, offer user to save
		// old file
		if (file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if user wants to save file, save it
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();// save file
			} // end if
		} // end if

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// if old file wasn't saved and its name is generated file name,
			// delete this file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			file = newFile;// assign opened file to file
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			firstRecord();// look for first record
			empDetailsPanel.displayRecords(currentEmployee);
			application.closeReadFile();// close file for reading
		} // end if
	}// end openFile

	// save file
	public void saveFile() {
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (file.getName().equals(generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			// if changes has been made to text field offer user to save these
			// changes
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// save changes if user choose this option
				if (returnVal == JOptionPane.YES_OPTION) {
					// save changes if ID field is not empty
					if (!empDetailsPanel.getIdField().getText().equals("")) {
						changeRecords();

					} // end if
				} // end if
			} // end if

			empDetailsPanel.displayRecords(currentEmployee);
			empDetailsPanel.setEnabled(false);
		} // end else
	}// end saveFile

	// save changes to current Employee
	public void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {

			changeRecords();
		} // end if
		empDetailsPanel.displayRecords(currentEmployee);
		empDetailsPanel.setEnabled(false);
	}// end saveChanges

	public void changeRecords(){
		// open file for writing
		application.openWriteFile(file.getAbsolutePath());
		// get changes for current Employee
		currentEmployee = empDetailsPanel.getChangedDetails();
		// write changes to file for corresponding Employee
		// record
		application.changeRecords(currentEmployee, currentByteStart);
		application.closeWriteFile();// close file for writing
	}

	// save file as 'save as'
	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");
		// display files only with .dat extension
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		// if file has chosen or written, save old file in new file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// check for file name
			if (!checkFileName(newFile)) {
				// add .dat extension if it was not there
				newFile = new File(newFile.getAbsolutePath() + ".dat");
				// create new file
				application.createFile(newFile.getAbsolutePath());
			} // end id
			else
				// create new file
				application.createFile(newFile.getAbsolutePath());

			try {// try to copy old file to new file
				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				// if old file name was generated file name, delete it
				if (file.getName().equals(generatedFileName))
					file.delete();
				file = newFile;
			}
			catch (IOException e) {
			}
		}
		changesMade = false;
	}// end saveFileAs

	// allow to save changes to file when exiting the application
	private void exitApp() {
		// if file is not empty allow to save changes
		if (file.length() != 0) {
			if (changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// if user chooses to save file, save file
				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();// save file
					// delete generated file if user saved details to other file
					if (file.getName().equals(generatedFileName))
						file.delete();// delete file
					System.exit(0);// exit application
				} // end if
				// else exit application
				else if (returnVal == JOptionPane.NO_OPTION) {
					// delete generated file if user chooses not to save file
					if (file.getName().equals(generatedFileName))
						file.delete();// delete file
					System.exit(0);// exit application
				} // end else if
			} // end if
			else {
				// delete generated file if user chooses not to save file
				if (file.getName().equals(generatedFileName))
					file.delete();// delete file
				System.exit(0);// exit application
			} // end else
			// else exit application
		} else {
			// delete generated file if user chooses not to save file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			System.exit(0);// exit application
		} // end else
	}// end exitApp

	// generate 20 character long file name
	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		// loop until 20 character long file name is generated
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}// end getFileName

	// create file with generated file name when application is opened
	private void createRandomFile() {
		generatedFileName = getFileName() + ".dat";
		// assign generated file name to file
		file = new File(generatedFileName);
		// create file
		application.createFile(file.getName());
	}// end createRandomFile

	// content pane for main dialog
	private void createContentPane() {
		System.out.println("createContentPane being called");

		addActionListeners();
		setTitle("Employee Details");
		createRandomFile();// create random file name
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());
		dialog.add(searchPanel(), "width 400:400:400, growx, pushx");
		dialog.add(navigPanel(), "width 150:150:150, wrap");
		dialog.add(buttonPanel(), "growx, pushx, span 2,wrap");

		empDetailsPanel = new EmpDetailsPanel(this);

		dialog.add(empDetailsPanel.detailsPanel(), "gap top 30, gap left 150, center");

		//dialog.add(detailsPanel(), "gap top 30, gap left 150, center");
		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);



	}// end createContentPane

	// create and show main dialog
	private static void createAndShowGUI() {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();// add content pane to frame
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}// end createAndShowGUI

	public void addActionListeners(){
		firstItemListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges()) {
					firstRecord();
					empDetailsPanel.displayRecords(currentEmployee);
				}
			}
		};

		System.out.println("ActionListener being done");
		prevItemListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges()) {
					previousRecord();
					empDetailsPanel.displayRecords(currentEmployee);
				}
			}
		};

		nextItemListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges()) {
					nextRecord();
					empDetailsPanel.displayRecords(currentEmployee);
				}
			}
		};

		lastItemListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges()) {
					lastRecord();
					empDetailsPanel.displayRecords(currentEmployee);
				}
			}
		};

		allItemsListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					if (empDetailsPanel.isSomeoneToDisplay())
						displayEmployeeSummaryDialog();
			}
		};

		createItemListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					new AddRecordDialog(EmployeeDetails.this);

				//new AddRecordDialog(EmployeeDetails.this);
			}
		};

		modifyItemListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					empDetailsPanel.editDetails();
			}
		};

		deleteItemListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (empDetailsPanel.checkInput() && !empDetailsPanel.checkForChanges())
					deleteRecord();
			}
		};

	}

	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	@Override
	public void insertUpdate(DocumentEvent e) {

	}

	@Override
	public void removeUpdate(DocumentEvent e) {

	}

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
		change = true;
		new PlainDocument() {
			private int limit = 20;

			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
				if (str == null)
					return;

				if ((getLength() + str.length()) <= limit)
					super.insertString(offset, str, attr);
				else
					JOptionPane.showMessageDialog(null, "For input " + limit + " characters maximum!");
			}// end insertString
		};
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {
		// exit application
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public JTextField getSearchByIdField() {
		return searchByIdField;
	}

	public JButton getSearchId(){
		return searchId;
	}

	public JButton getSearchSurname(){
		return searchSurname;
	}

	public JTextField getSearchBySurnameField() {
		return searchBySurnameField;
	}

	public EmpDetailsPanel getDetailsPanel(){
		return empDetailsPanel;
	}

	public File getFile(){
		return file;
	}

	public Employee getCurrentEmployee(){
		return currentEmployee;
	}
}// end class EmployeeDetails