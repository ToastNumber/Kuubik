package jCube;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * @author Kelsey McKenna
 */
public class MemberDatabasePopUp extends JFrame implements KeyListener {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This stores the initial width of the window.
	 */
	private static final int WIDTH = 800;

	/**
	 * This stores the padding of the elements in the window. The greater the
	 * padding, the further towards the centre of the window the elements will
	 * be.
	 */
	private final int pad = 10;
	/**
	 * This indicates the vertical spacing between the text boxes etc. in the
	 * window.
	 */
	private final int fieldYSpacing = 50;
	/**
	 * This indicates the vertical spacing between the buttons etc. in the
	 * window.
	 */
	private final int buttonYSpacing = 40;

	/**
	 * This variable keeps track of the current y position of the last element
	 * placed in the window.
	 */
	private int y = 0;
	/**
	 * Stores the font to be used in the text fields
	 */
	private Font fieldFont = new Font("Arial", 0, 25);
	/**
	 * If this variable is true, it indicates that a record is being edited,
	 * otherwise a record is being added.
	 */
	private boolean editing = false;

	/**
	 * After choosing to edit a record in the table, the index of this row in
	 * the table is stored in this variable.
	 */
	private int selectedRowIndex;

	/**
	 * This panel is used to store the table.
	 */
	private JPanel memberListPanel;
	/**
	 * memberTable is placed ‘inside’ this variable so that when the size of the
	 * table exceeds the size of the window, the user can scroll in order to
	 * view the rest of the table.
	 */
	private JScrollPane tableContainer;
	/**
	 * This is the table that is displayed in the window; it stores the contents
	 * of the table and the rendering features required to display the data.
	 */
	private final JTable memberTable;
	/**
	 * This variable can be customised so that certain cells of the table are
	 * uneditable and the columns of the table can be given text. This variable
	 * is then set as the model of memberTable.
	 */
	private final DefaultTableModel model;
	/**
	 * The buttons in the window are placed in this panel.
	 */
	private JPanel buttonPanel;

	/**
	 * Clicking this button opens the 'Member Form' window.
	 */
	private JButton addMemberButton;
	/**
	 * Clicking this button opens the 'Member Form' window if a row has been
	 * selected.
	 */
	private JButton editMemberButton;
	/**
	 * Clicking this button deletes the selected rows from the table.
	 */
	private JButton deleteMemberButton;

	/**
	 * This label is shown in the window with the text ‘Forenames’.
	 */
	private JLabel forenamesLabel;
	/**
	 * This label is shown in the window with the text ‘Surname’.
	 */
	private JLabel surnameLabel;
	/**
	 * This label is shown in the window with the text ‘Gender’.
	 */
	private JLabel genderLabel;
	/**
	 * This label is shown in the window with the text ‘Date of Birth’.
	 */
	private JLabel dateOfBirthLabel;
	/**
	 * This label is shown in the window with the text ‘Email’.
	 */
	private JLabel emailLabel;
	/**
	 * This label is shown in the window with the text ‘Form Class’.
	 */
	private JLabel formClassLabel;

	/**
	 * This field is shown in the window and the user can enter the forenames
	 * into this field.
	 */
	private JTextField forenamesField;
	/**
	 * This field is shown in the window and the user can enter the surname into
	 * this field.
	 */
	private JTextField surnameField;
	/**
	 * This drop-down list stores the items 'Male' and 'Female' * window.
	 */
	private JComboBox<String> genderField;
	/**
	 * This field is shown in the window and the user can enter the date of
	 * birth into this field.
	 */
	private JTextField dateOfBirthField;
	/**
	 * This field is shown in the window and the user can enter the email into
	 * this field.
	 */
	private JTextField emailField;
	/**
	 * This field is shown in the window and the user can enter the form class
	 * into this field.
	 */
	private JTextField formClassField;

	/**
	 * This represents the 'Member Form' window.
	 */
	private JFrame memberInputForm;

	/**
	 * Clicking this button submits the data in the 'Member Form' window for
	 * validation.
	 */
	private JButton submitButton;

	/**
	 * This panel stores the elements of the 'Member Form' window.
	 */
	private JPanel contentPane;

	/**
	 * Constructor - sets up the 'Member Table' window
	 */
	public MemberDatabasePopUp() {
		super("Member Table");

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(WIDTH, 400));
		setVisible(false);
		setUpMemberInputForm();

		memberListPanel = new JPanel();
		memberListPanel.setOpaque(true);

		final String[] columnNames = { "ID", "Forenames", "Surname", "Gender", "Date of Birth", "Email", "Form Class" };

		model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public int getColumnCount() {
				return columnNames.length;
			}

			public String getColumnName(int col) {
				return columnNames[col];
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
			}
		});

		memberTable = new JTable();
		memberTable.setModel(model);
		memberTable.setColumnSelectionAllowed(false);
		memberTable.setPreferredScrollableViewportSize(new Dimension(WIDTH, 70));
		memberTable.setFillsViewportHeight(true);
		memberTable.setAutoscrolls(true);
		memberTable.getTableHeader().setReorderingAllowed(false);
		memberTable.setFont(new Font("Arial", 0, 15));
		memberTable.setRowHeight(20);
		memberTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					editMemberButtonFunction();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3));
		buttonPanel.setPreferredSize(new Dimension(WIDTH, 40));
		buttonPanel.setSize(WIDTH, 50);

		addMemberButton = new JButton("Add Member");
		addMemberButton.setFocusable(false);
		addMemberButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				memberTable.clearSelection();
				editing = false;
				forenamesField.setText("");
				surnameField.setText("");
				dateOfBirthField.setText("");
				emailField.setText("");
				formClassField.setText("");
				memberInputForm.setVisible(true);
			}
		});

		editMemberButton = new JButton("Edit Member");
		editMemberButton.setFocusable(false);
		editMemberButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editMemberButtonFunction();
			}
		});

		deleteMemberButton = new JButton("Delete Member");
		deleteMemberButton.setFocusable(false);
		deleteMemberButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Object[] options = { "Yes", "No" };
					int choice = -1;

					int selectedIndex = memberTable.getSelectedRow();

					if (selectedIndex == -1)
						return;

					choice = JOptionPane.showOptionDialog(null, "Are you sure you want to delete?", "Warning",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

					if (choice == 0)
						deleteRow();
				} catch (ClassNotFoundException | SQLException e) {
					JOptionPane.showMessageDialog(memberListPanel, "Unable to delete record from database", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		buttonPanel.add(addMemberButton);
		buttonPanel.add(editMemberButton);
		buttonPanel.add(deleteMemberButton);

		tableContainer = new JScrollPane(memberTable);
		tableContainer.getViewport().add(memberTable, null);

		Main.resizeColumnWidths(memberTable);
		populateCellsWithDatabaseData();

		add(tableContainer, null);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
	}

	/**
	 * Performs the operations required to set up the 'Member Form' window with
	 * the contents of the selected row in the table and indicates that the row
	 * is being edited (and not added)
	 */
	private void editMemberButtonFunction() {
		int selectedRow = memberTable.getSelectedRow();

		if (selectedRow != -1) {
			forenamesField.setText("" + memberTable.getValueAt(selectedRow, 1));
			surnameField.setText("" + memberTable.getValueAt(selectedRow, 2));
			genderField.setSelectedItem("" + memberTable.getValueAt(selectedRow, 3));
			dateOfBirthField.setText("" + memberTable.getValueAt(selectedRow, 4));
			emailField.setText("" + memberTable.getValueAt(selectedRow, 5));
			formClassField.setText("" + memberTable.getValueAt(selectedRow, 6));
			memberInputForm.setVisible(true);
			editing = true;
			selectedRowIndex = selectedRow;
		}
	}

	/**
	 * Retrieves the data from the 'member' table and adds it to the table in
	 * the window
	 */
	private void populateCellsWithDatabaseData() {
		int rowCount = model.getRowCount();

		for (int i = 0; i < rowCount; ++i) { // Remove all rows from table
			model.removeRow(0);
		}

		Member[] members = new Member[0];

		try {
			members = MemberDatabaseConnection.executeQuery("SELECT * " + "FROM member;");
		} catch (SQLException e) {
			JOptionPane
					.showMessageDialog(memberListPanel, "Could not load members", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			JOptionPane
					.showMessageDialog(memberListPanel, "Could not load members", "Error", JOptionPane.ERROR_MESSAGE);
		}

		if (members != null) {
			for (int i = 0; i < members.length; ++i) {
				model.addRow(new Object[] { "" + members[i].getMemberID(), members[i].getForenames(),
						members[i].getSurname(), members[i].getGender(), members[i].getDateOfBirth(),
						members[i].getEmail(), members[i].getFormClass() });
			}
		}

		Main.resizeColumnWidths(memberTable);
	}

	/**
	 * Adds a row to the table and to the database
	 * 
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist etc.
	 */
	private void addRow() throws ClassNotFoundException, SQLException {
		MemberDatabaseConnection
				.executeUpdate("INSERT INTO member(forenames, surname, gender, dateOfBirth, email, formClass) VALUES (\"\",\"\",\"\",\"\",\"\",\"\")");
		Member[] member = MemberDatabaseConnection.executeQuery("SELECT * FROM member ORDER BY memberID DESC LIMIT 1");
		model.addRow(new Object[] { "" + (member[0].getMemberID()), "", "", "", "", "", "" });

		Main.resizeColumnWidths(memberTable);
		memberTable.setRowSelectionInterval(memberTable.getRowCount() - 1, memberTable.getRowCount() - 1);
	}

	/**
	 * Deletes the selected rows from the table, and deletes the corresponding
	 * records from the database
	 * 
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist or the updates fail
	 */
	private void deleteRow() throws ClassNotFoundException, SQLException {
		int[] selectedIndices = memberTable.getSelectedRows();
		Member[] members;

		if (selectedIndices.length == 0)
			return;

		for (int i = 0; i < selectedIndices.length; ++i) {
			MemberDatabaseConnection.executeUpdate("DELETE FROM member WHERE memberID = "
					+ ("" + model.getValueAt(selectedIndices[i], 0)) + ";");
		}

		int rowCount = memberTable.getRowCount();
		for (int i = 0; i < rowCount; ++i)
			model.removeRow(0);

		members = MemberDatabaseConnection.executeQuery("SELECT * FROM member");

		for (int i = 0; i < members.length; ++i)
			model.addRow(new Object[] { members[i].getMemberID(), members[i].getForenames(), members[i].getSurname(),
					members[i].getGender(), members[i].getDateOfBirth(), members[i].getEmail(),
					members[i].getFormClass() });

		try {
			memberTable.setRowSelectionInterval(selectedIndices[selectedIndices.length - 1],
					selectedIndices[selectedIndices.length - 1]);
		} catch (Exception e) {
		}
	}

	/**
	 * Returns a value indicating whether the argument is a valid date for a
	 * date of birth
	 * 
	 * @param dateString
	 *            the date to be analysed
	 * @return true if the argument is valid and is in the format dd/MM/yyyy;
	 *         false otherwise
	 */
	private static boolean isValidDate(String dateString) {
		try {
			Date dateEntered = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);

			if (!dateString.matches("((\\d)*/){2,2}\\d{4,4}"))
				return false;

			Date currentDate = new Date();
			int day, month, year;

			day = Integer.valueOf(dateString.substring(0, dateString.indexOf("/")));
			dateString = dateString.substring(dateString.indexOf("/") + 1);

			month = Integer.valueOf(dateString.substring(0, dateString.indexOf("/")));
			year = Integer.valueOf(dateString.substring(dateString.indexOf("/") + 1));

			if ((month > 12) || (getNumDaysInMonth(month, year) < day) || (dateEntered.after(currentDate)))
				return false;
			else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param month
	 *            the month of the date in question
	 * @param year
	 *            the year of the date in question
	 * @return the number of days in the month
	 */
	private static int getNumDaysInMonth(int month, int year) {
		switch (month) {
		case 2:
			if ((year % 4 == 0) && ((year % 100 == 0) ? (year % 400 == 0) : true)) {
				return 29;
			} else {
				return 28;
			}
		case 4:
		case 6:
		case 7:
		case 11:
			return 30;
		default:
			return 31;
		}
	}

	/**
	 * Sets up the 'Member Form' window
	 */
	private void setUpMemberInputForm() {
		memberInputForm = new JFrame("Member Form");

		contentPane = new JPanel();
		contentPane.setLayout(null);

		memberInputForm.setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		memberInputForm.setContentPane(contentPane);
		memberInputForm.setPreferredSize(new Dimension(480, 440));
		memberInputForm.setSize(new Dimension(480, 380));
		memberInputForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		memberInputForm.setResizable(false);
		memberInputForm.setLocation(600, 150);
		memberInputForm.setVisible(false);

		// ***********Labels***************
		forenamesLabel = new JLabel("Forenames ");
		forenamesLabel.setSize(120, 40);
		forenamesLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		surnameLabel = new JLabel("Surname ");
		surnameLabel.setSize(120, 40);
		surnameLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		genderLabel = new JLabel("Gender ");
		genderLabel.setSize(120, 40);
		genderLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		dateOfBirthLabel = new JLabel("Date of Birth ");
		dateOfBirthLabel.setSize(120, 40);
		dateOfBirthLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		emailLabel = new JLabel("Email ");
		emailLabel.setSize(120, 40);
		emailLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		formClassLabel = new JLabel("Form Class ");
		formClassLabel.setSize(120, 40);
		formClassLabel.setLocation(0 + pad, y + pad);

		// ***************Text Fields*************************

		y = 0;

		forenamesField = new JTextField();
		forenamesField.setFont(fieldFont);
		forenamesField.setSize(350, 40);
		forenamesField.setLocation(100 + pad, y + pad);
		forenamesField.addKeyListener(this);

		y += fieldYSpacing;

		surnameField = new JTextField("");
		surnameField.setFont(fieldFont);
		surnameField.setSize(350, 40);
		surnameField.setLocation(100 + pad, y + pad);
		surnameField.addKeyListener(this);

		y += fieldYSpacing;

		genderField = new JComboBox<String>();
		genderField.addItem("Male");
		genderField.addItem("Female");
		genderField.setFont(fieldFont);
		genderField.setSize(350, 40);
		genderField.setLocation(100 + pad, y + pad);
		genderField.addKeyListener(this);

		y += fieldYSpacing;

		dateOfBirthField = new JTextField("");
		dateOfBirthField.setFont(fieldFont);
		dateOfBirthField.setSize(350, 40);
		dateOfBirthField.setLocation(100 + pad, y + pad);
		dateOfBirthField.addKeyListener(this);

		y += fieldYSpacing;

		emailField = new JTextField("");
		emailField.setFont(fieldFont);
		emailField.setSize(350, 40);
		emailField.setLocation(100 + pad, y + pad);
		emailField.addKeyListener(this);

		y += fieldYSpacing;

		formClassField = new JTextField();
		formClassField.setFont(fieldFont);
		formClassField.setSize(350, 40);
		formClassField.setLocation(100 + pad, y + pad);
		formClassField.addKeyListener(this);

		y += fieldYSpacing;

		// ************Submission Button**********
		y = 310;

		submitButton = new JButton("Submit");
		submitButton.setSize(480, 30);
		submitButton.setLocation(0, y + pad);
		submitButton.setFocusable(false);
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row, memberID;
				String dateOfBirth = dateOfBirthField.getText().trim();
				String email = emailField.getText().trim();
				String formClass = formClassField.getText().trim();
				String forenames = forenamesField.getText().trim();
				String surname = surnameField.getText().trim();
				String gender = (genderField.getSelectedItem() + "").trim();

				if ((dateOfBirth.equals("")) || (!isValidDate(dateOfBirth))) {
					JOptionPane.showMessageDialog(memberInputForm, "Invalid date of birth", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} else if ((email.equals("")) || (!Member.isValidEmail(email))) {
					JOptionPane.showMessageDialog(memberInputForm, "Invalid email", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if ((formClass.equals("")) || (!Member.isValidFormClass(formClass))) {
					JOptionPane.showMessageDialog(memberInputForm, "Invalid form class", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} else if ((forenames.equals(""))) {
					JOptionPane.showMessageDialog(memberInputForm, "Empty forenames", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} else if ((surname.equals(""))) {
					JOptionPane.showMessageDialog(memberInputForm, "Empty surname", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					if (!editing) {
						addRow();
						row = memberTable.getRowCount() - 1;
					} else {
						// row = memberTable.getSelectedRow();
						row = selectedRowIndex;
					}
					memberID = Integer.valueOf("" + memberTable.getValueAt(row, 0));

					model.setValueAt(forenames + "", row, 1);
					model.setValueAt(surname + "", row, 2);
					model.setValueAt(gender + "", row, 3);
					model.setValueAt(dateOfBirth + "", row, 4);
					model.setValueAt(email + "", row, 5);
					model.setValueAt(formClass + "", row, 6);

					memberInputForm.setVisible(false);
					forenamesField.requestFocus();
					editing = false;

					MemberDatabaseConnection.executeUpdate(String.format("UPDATE member " + "SET forenames = \"%s\", "
							+ "surname = \"%s\", " + "gender = \"%s\", " + "dateOfBirth = \"%s\", "
							+ "email = \"%s\", " + "formClass = \"%s\" " + "WHERE memberID = %d", "" + forenames, ""
							+ surname, "" + gender, "" + dateOfBirth, "" + email, "" + formClass, memberID));
					Main.resizeColumnWidths(memberTable);
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}

			}
		});

		y += buttonYSpacing;

		contentPane.add(forenamesLabel);
		contentPane.add(surnameLabel);
		contentPane.add(genderLabel);
		contentPane.add(dateOfBirthLabel);
		contentPane.add(emailLabel);
		contentPane.add(formClassLabel);

		contentPane.add(forenamesField);
		contentPane.add(surnameField);
		contentPane.add(genderField);
		contentPane.add(dateOfBirthField);
		contentPane.add(emailField);
		contentPane.add(formClassField);

		contentPane.add(submitButton);

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			submitButton.doClick();
		}
	}

}
