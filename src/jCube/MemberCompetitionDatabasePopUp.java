package jCube;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

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
public class MemberCompetitionDatabasePopUp extends JFrame implements KeyListener {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This stores the initial width of the Member-Competition window.
	 */
	private static final int WIDTH = 700;

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
	 * Stores the competition ID of the selected competition.
	 */
	private int currentCompetitionID;
	/**
	 * After choosing to edit a record in the table, the index of this row in
	 * the table is stored in this variable.
	 */
	private int selectedMCIndex;

	/**
	 * This panel is used to store the table.
	 */
	private JPanel memberCompetitionListPanel;
	/**
	 * memberCompetitionTable is placed ‘inside’ this variable so that when the
	 * size of the table exceeds the size of the window, the user can scroll in
	 * order to view the rest of the table.
	 */
	private JScrollPane tableContainer;
	/**
	 * This is the table that is displayed in the window; it stores the contents
	 * of the table and the rendering features required to display the data.
	 */
	private final JTable memberCompetitionTable;
	/**
	 * This variable can be customised so that certain cells of the table are
	 * uneditable and the columns of the table can be given text. This variable
	 * is then set as the model of memberCompetitionTable.
	 */
	private final DefaultTableModel model;
	/**
	 * The buttons in the window are placed in this panel.
	 */
	private JPanel buttonPanel;

	/**
	 * Clicking this button opens the Member-Competition Form window.
	 */
	private JButton addRecordButton;
	/**
	 * Clicking this button opens the Member-Competition Form window if a row
	 * has been selected.
	 */
	private JButton editRecordButton;
	/**
	 * Clicking this button deletes the selected rows from the table.
	 */
	private JButton deleteRecordButton;
	/**
	 * This label is shown at the bottom left of the window an indicates the
	 * competition ID of the selected competition.
	 */
	private JLabel competitionIndicatorLabel;

	/**
	 * This label is shown in the Member-Competition Form window with the text
	 * ‘Member ID’.
	 */
	private JLabel memberIDLabel;
	/**
	 * This label is shown in the Member-Competition Form window with the text
	 * ‘Time 1’.
	 */
	private JLabel time1Label;
	/**
	 * This label is shown in the Member-Competition Form window with the text
	 * ‘Time 2’.
	 */
	private JLabel time2Label;
	/**
	 * This label is shown in the Member-Competition Form window with the text
	 * ‘Time 3’.
	 */
	private JLabel time3Label;
	/**
	 * This label is shown in the Member-Competition Form window with the text
	 * ‘Time 4’.
	 */
	private JLabel time4Label;
	/**
	 * This label is shown in the Member-Competition Form window with the text
	 * ‘Time 5’.
	 */
	private JLabel time5Label;
	/**
	 * This label is shown in the Member-Competition Form window when the user
	 * enters invalid data.
	 */
	private JLabel errorMessageLabel;

	/**
	 * This drop-down list stores the member IDs in the Member-Competition Form
	 * window.
	 */
	private JComboBox<Integer> memberIDComboBox;
	/**
	 * This field is shown in the Member-Competition Form window and the user
	 * can enter a time into this field.
	 */
	private JTextField time1Field;
	/**
	 * This field is shown in the Member-Competition Form window and the user
	 * can enter a time into this field.
	 */
	private JTextField time2Field;
	/**
	 * This field is shown in the Member-Competition Form window and the user
	 * can enter a time into this field.
	 */
	private JTextField time3Field;
	/**
	 * This field is shown in the Member-Competition Form window and the user
	 * can enter a time into this field.
	 */
	private JTextField time4Field;
	/**
	 * This field is shown in the Member-Competition Form window and the user
	 * can enter a time into this field.
	 */
	private JTextField time5Field;

	/**
	 * This represents the Member-Competition Form window.
	 */
	private JFrame memberCompetitionInputForm;

	/**
	 * Clicking this button submits the data in the Member-Competition Form
	 * window for validation.
	 */
	private JButton submitButton;

	/**
	 * This panel stores the elements of the Member-Competition Form window.
	 */
	private JPanel contentPane;

	/**
	 * Constructor - sets up the 'Member-Competition Table' window
	 */
	public MemberCompetitionDatabasePopUp() {
		super("Member-Competition");

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(WIDTH, 400));
		setVisible(false);
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				memberCompetitionInputForm.setVisible(false);
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});
		setUpMemberCompetitionInputForm();

		memberCompetitionListPanel = new JPanel();
		memberCompetitionListPanel.setOpaque(true);

		final String[] columnNames = { "Name", "Member ID", "Rank", "Average", "Time 1", "Time 2", "Time 3", "Time 4",
				"Time 5" };

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

		memberCompetitionTable = new JTable();
		memberCompetitionTable.setModel(model);
		memberCompetitionTable.setColumnSelectionAllowed(false);
		memberCompetitionTable.setPreferredScrollableViewportSize(new Dimension(WIDTH, 70));
		memberCompetitionTable.setFillsViewportHeight(true);
		memberCompetitionTable.setAutoscrolls(true);
		memberCompetitionTable.getTableHeader().setReorderingAllowed(false);
		memberCompetitionTable.setFont(new Font("Arial", 0, 15));
		memberCompetitionTable.setRowHeight(20);
		memberCompetitionTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					editRecordFunction();
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

		addRecordButton = new JButton("Add");
		addRecordButton.setFocusable(false);
		addRecordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				memberCompetitionTable.clearSelection();

				editing = false;
				resetMemberIDComboBoxItems();
				if (memberIDComboBox.getItemCount() == 0) {
					memberCompetitionInputForm.setVisible(false);

					JOptionPane.showMessageDialog(memberCompetitionListPanel, "No existing members remaining", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					time1Field.setText("");
					time2Field.setText("");
					time3Field.setText("");
					time4Field.setText("");
					time5Field.setText("");
					memberCompetitionInputForm.setVisible(true);
				}
			}
		});

		editRecordButton = new JButton("Edit");
		editRecordButton.setFocusable(false);
		editRecordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editRecordFunction();
			}
		});

		deleteRecordButton = new JButton("Delete");
		deleteRecordButton.setFocusable(false);
		deleteRecordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Object[] options = { "Yes", "No" };
					int choice = -1;

					int selectedIndex = memberCompetitionTable.getSelectedRow();

					if (selectedIndex == -1)
						return;

					choice = JOptionPane.showOptionDialog(null, "Are you sure you want to delete?", "Warning",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

					if (choice == 0)
						deleteRow();
				} catch (ClassNotFoundException | SQLException e) {
					JOptionPane.showMessageDialog(memberCompetitionListPanel, "Unable to delete record from database",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		competitionIndicatorLabel = new JLabel();
		competitionIndicatorLabel.setHorizontalAlignment(JLabel.CENTER);
		competitionIndicatorLabel.setFont(new Font("Arial", 0, 15));

		buttonPanel.add(competitionIndicatorLabel);
		buttonPanel.add(addRecordButton);
		buttonPanel.add(editRecordButton);
		buttonPanel.add(deleteRecordButton);

		tableContainer = new JScrollPane(memberCompetitionTable);
		tableContainer.getViewport().add(memberCompetitionTable, null);

		Main.resizeColumnWidths(memberCompetitionTable);
		populateCellsWithDatabaseData();

		add(tableContainer, null);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
	}

	/**
	 * Performs the operations required to set up the 'Member-Competition Form'
	 * window with the contents of the selected row in the table and indicates
	 * that the row is being edited (and not added)
	 */
	private void editRecordFunction() {
		int selectedRow = memberCompetitionTable.getSelectedRow();

		if (selectedRow != -1) {
			editing = true;
			time1Field.setText("" + memberCompetitionTable.getValueAt(selectedRow, 4));
			time2Field.setText("" + memberCompetitionTable.getValueAt(selectedRow, 5));
			time3Field.setText("" + memberCompetitionTable.getValueAt(selectedRow, 6));
			time4Field.setText("" + memberCompetitionTable.getValueAt(selectedRow, 7));
			time5Field.setText("" + memberCompetitionTable.getValueAt(selectedRow, 8));
			resetMemberIDComboBoxItems();
			if (!memberIDComboBoxContains(Integer.valueOf("" + memberCompetitionTable.getValueAt(selectedRow, 1)))) {
				JOptionPane.showMessageDialog(memberCompetitionInputForm, "This member no longer exists", "Error",
						JOptionPane.ERROR_MESSAGE);
				editing = false;
				return;
			}

			memberIDComboBox.setSelectedItem(Integer.valueOf("" + memberCompetitionTable.getValueAt(selectedRow, 1)));
			selectedMCIndex = selectedRow;
			memberCompetitionInputForm.setVisible(true);
		}
	}

	/**
	 * Determines whether <b>memberIDComboBox</b> contains the specified item
	 * 
	 * @param item
	 *            the item to be found
	 * @return <b>true</b> if the item is in memberIDComboBox; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean memberIDComboBoxContains(Integer item) {
		int size = memberIDComboBox.getItemCount();

		for (int i = 0; i < size; ++i)
			if (memberIDComboBox.getItemAt(i).equals(item))
				return true;

		return false;
	}

	/**
	 * Retrieves the data from the 'memberCompetition' table and adds it to the
	 * table in the window
	 */
	public void populateCellsWithDatabaseData() {
		int rowCount = model.getRowCount();
		int rank = 1;
		String average;

		for (int i = 0; i < rowCount; ++i) { // Remove all rows from table
			model.removeRow(0);
		}

		MemberCompetition[] memberCompetitions = new MemberCompetition[0];

		try {
			memberCompetitions = MemberCompetitionDatabaseConnection.executeQuery("SELECT * "
					+ "FROM memberCompetition;");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(memberCompetitionListPanel, "Could not load member-competitions", "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(memberCompetitionListPanel, "Could not load member-competitions", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		if (memberCompetitions != null) {
			Sorter.sortByAverageThenTime(memberCompetitions, 0, memberCompetitions.length - 1);
			String[] times;
			Member[] currentMember;

			for (int i = 0; i < memberCompetitions.length; ++i) {
				if (memberCompetitions[i].getCompetitionID() == currentCompetitionID) {
					try {
						currentMember = MemberDatabaseConnection.executeQuery("SELECT * FROM member WHERE memberID = "
								+ memberCompetitions[i].getMemberID());

						if (currentMember.length == 0)
							throw new Exception();
					} catch (Exception e) {
						currentMember = new Member[1];
						currentMember[0] = new Member(0, "UNKNOWN", "", "", "", "", "");
					}

					times = memberCompetitions[i].getTimes();
					average = Solve.getSecondsToFormattedString(memberCompetitions[i].getAverage());

					model.addRow(new Object[] {
							"" + currentMember[0].getForenames() + " " + currentMember[0].getSurname(),
							"" + memberCompetitions[i].getMemberID(), "" + rank++,
							(average.equals("-1.00")) ? "DNF" : average + "", times[0], times[1], times[2], times[3],
							times[4] });
				}
			}
		}

		Main.resizeColumnWidths(memberCompetitionTable);
		memberCompetitionInputForm.setVisible(false);
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
		MemberCompetitionDatabaseConnection.executeUpdate(String.format(
				"INSERT INTO memberCompetition(competitionID, memberID, time1, time2, time3, time4, time5) "
						+ "VALUES (%d, %d, \"\", \"\", \"\", \"\", \"\")", currentCompetitionID,
				Integer.valueOf("" + memberIDComboBox.getSelectedItem())));

		model.addRow(new Object[] { "", 0, "", "", "", "", "", 0, 0 });

		Main.resizeColumnWidths(memberCompetitionTable);
		memberCompetitionTable.setRowSelectionInterval(memberCompetitionTable.getRowCount() - 1,
				memberCompetitionTable.getRowCount() - 1);
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
		int[] selectedIndices = memberCompetitionTable.getSelectedRows();
		// MemberCompetition[] membersCompetitions;

		if (selectedIndices.length == 0)
			return;

		for (int i = 0; i < selectedIndices.length; ++i) {
			MemberCompetitionDatabaseConnection.executeUpdate("DELETE FROM memberCompetition "
					+ "WHERE competitionID = " + ("" + currentCompetitionID) + " " + "AND memberID = "
					+ ("" + model.getValueAt(selectedIndices[i], 1)) + ";");
		}

		// for (int i = selectedIndices.length - 1; i >= 0; --i) {
		// model.removeRow(selectedIndices[i]);
		// }

		populateCellsWithDatabaseData();

		/*
		 * int rowCount = memberCompetitionTable.getRowCount(); for (int i = 0;
		 * i < rowCount; ++i) model.removeRow(0);
		 * 
		 * membersCompetitions =
		 * MemberCompetitionDatabaseConnection.executeQuery
		 * ("SELECT * FROM memberCompetition");
		 * 
		 * String[] times;
		 * 
		 * for (int i = 0; i < membersCompetitions.length; ++i) { times =
		 * membersCompetitions[i].getTimes(); .addRow(new Object[]{"" +
		 * membersCompetitions[i].getCompetitionID(),
		 * membersCompetitions[i].getMemberID(), times[0], times[1], times[2],
		 * times[3], times[4]}); }
		 */
		try {
			memberCompetitionTable.setRowSelectionInterval(selectedIndices[selectedIndices.length - 1],
					selectedIndices[selectedIndices.length - 1]);
		} catch (Exception e) {
		}
	}

	/**
	 * Sets the competition ID to <b>competitionID</b> so that the correct
	 * records are retrieved from the database
	 * 
	 * @param competitionID
	 *            the ID of the competition will be set to this value
	 */
	public void setCurrentCompetitionID(int competitionID) {
		this.currentCompetitionID = competitionID;
		competitionIndicatorLabel.setText("Competition ID = " + competitionID);
	}

	/**
	 * Sets up the 'Member-Competition Form' window
	 */
	private void setUpMemberCompetitionInputForm() {
		memberCompetitionInputForm = new JFrame("Member-Competition Form");

		contentPane = new JPanel();
		contentPane.setLayout(null);

		memberCompetitionInputForm.setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		memberCompetitionInputForm.setContentPane(contentPane);
		memberCompetitionInputForm.setSize(new Dimension(480, 397));
		memberCompetitionInputForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		memberCompetitionInputForm.setResizable(false);
		memberCompetitionInputForm.setLocation(600, 150);
		memberCompetitionInputForm.setVisible(false);

		memberCompetitionInputForm.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				time1Field.setText("");
				time2Field.setText("");
				time3Field.setText("");
				time4Field.setText("");
				time5Field.setText("");
				errorMessageLabel.setVisible(false);
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}

		});

		// ***********Labels***************
		memberIDLabel = new JLabel("Member ID ");
		memberIDLabel.setSize(120, 40);
		memberIDLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		time1Label = new JLabel("Time 1");
		time1Label.setSize(120, 40);
		time1Label.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		time2Label = new JLabel("Time 2");
		time2Label.setSize(120, 40);
		time2Label.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		time3Label = new JLabel("Time 3");
		time3Label.setSize(120, 40);
		time3Label.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		time4Label = new JLabel("Time 4");
		time4Label.setSize(120, 40);
		time4Label.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		time5Label = new JLabel("Time 5");
		time5Label.setSize(120, 40);
		time5Label.setLocation(0 + pad, y + pad);

		y += fieldYSpacing - 10;

		errorMessageLabel = new JLabel("Error Message");
		errorMessageLabel.setForeground(Color.RED);
		errorMessageLabel.setSize(200, 40);
		errorMessageLabel.setLocation(0 + pad, y + pad);
		errorMessageLabel.setVisible(false);

		// ***************Text Fields*************************

		y = 0;

		memberIDComboBox = new JComboBox<Integer>();
		memberIDComboBox.setFont(fieldFont);
		memberIDComboBox.setSize(350, 40);
		memberIDComboBox.setLocation(100 + pad, y + pad);
		memberIDComboBox.addKeyListener(this);

		y += fieldYSpacing;

		time1Field = new JTextField();
		time1Field.setFont(fieldFont);
		time1Field.setSize(350, 40);
		time1Field.setLocation(100 + pad, y + pad);
		time1Field.addKeyListener(this);

		y += fieldYSpacing;

		time2Field = new JTextField("");
		time2Field.setFont(fieldFont);
		time2Field.setSize(350, 40);
		time2Field.setLocation(100 + pad, y + pad);
		time2Field.addKeyListener(this);

		y += fieldYSpacing;

		time3Field = new JTextField("");
		time3Field.setFont(fieldFont);
		time3Field.setSize(350, 40);
		time3Field.setLocation(100 + pad, y + pad);
		time3Field.addKeyListener(this);

		y += fieldYSpacing;

		time4Field = new JTextField("");
		time4Field.setFont(fieldFont);
		time4Field.setSize(350, 40);
		time4Field.setLocation(100 + pad, y + pad);
		time4Field.addKeyListener(this);

		y += fieldYSpacing;

		time5Field = new JTextField("");
		time5Field.setFont(fieldFont);
		time5Field.setSize(350, 40);
		time5Field.setLocation(100 + pad, y + pad);
		time5Field.addKeyListener(this);

		y += fieldYSpacing;

		// ************Submission Button**********
		y = 330;

		submitButton = new JButton("Submit");
		submitButton.setSize(480, 30);
		submitButton.setLocation(0, y + pad);
		submitButton.setFocusable(false);
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String[] times = { "" + time1Field.getText(), "" + time2Field.getText(), "" + time3Field.getText(),
						"" + time4Field.getText(), "" + time5Field.getText() };

				for (int i = 0; i < 5; ++i) {
					if (!Solve.isValidTime(times[i])) {
						errorMessageLabel.setText("Some of your times are invalid");
						errorMessageLabel.setVisible(true);
						return;
					}
				}

				errorMessageLabel.setVisible(false);

				try {
					int row;
					if (!editing) {
						addRow();
						row = memberCompetitionTable.getRowCount() - 1;
					} else {
						// row = memberCompetitionTable.getSelectedRow();
						row = selectedMCIndex;
					}

					memberCompetitionTable.setValueAt("" + memberIDComboBox.getSelectedItem(), row, 1);

					for (int i = 0; i < 5; ++i) {
						if (times[i].trim().equalsIgnoreCase("DNF"))
							memberCompetitionTable.setValueAt("DNF", row, i + 4);
						else
							memberCompetitionTable.setValueAt(Solve.getPaddedTime(Solve
									.getSecondsToFormattedString(Solve.getFormattedStringToDouble(times[i]))), row,
									i + 4);
					}

					editing = false;

					String update = String.format("UPDATE memberCompetition " + "SET time1 = \"%s\", "
							+ "time2 = \"%s\", " + "time3 = \"%s\", " + "time4 = \"%s\", " + "time5 = \"%s\" "
							+ "WHERE memberID = %d AND competitionID = %d", "" + model.getValueAt(row, 4),
							"" + model.getValueAt(row, 5), "" + model.getValueAt(row, 6),
							"" + model.getValueAt(row, 7), "" + model.getValueAt(row, 8),
							Integer.valueOf("" + model.getValueAt(row, 1)), currentCompetitionID);

					MemberCompetitionDatabaseConnection.executeUpdate(update);
					populateCellsWithDatabaseData();
					Main.resizeColumnWidths(memberCompetitionTable);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				errorMessageLabel.setVisible(false);
				memberCompetitionInputForm.setVisible(false);
			}
		});

		y += buttonYSpacing;

		contentPane.add(memberIDLabel);
		contentPane.add(time1Label);
		contentPane.add(time2Label);
		contentPane.add(time3Label);
		contentPane.add(time4Label);
		contentPane.add(time5Label);
		contentPane.add(errorMessageLabel);

		contentPane.add(time1Field);
		contentPane.add(time2Field);
		contentPane.add(memberIDComboBox);
		contentPane.add(time3Field);
		contentPane.add(time4Field);
		contentPane.add(time5Field);

		contentPane.add(submitButton);

	}

	/**
	 * This resets the items in the drop-down list in the 'Member-Competition
	 * Form' window so that it contains the correct items
	 */
	private void resetMemberIDComboBoxItems() {
		Integer[] items = new Integer[memberCompetitionTable.getRowCount()];
		int currentMemberID, selectedMemberID = -1, selectedRow;

		for (int i = 0; i < items.length; ++i) {
			items[i] = Integer.valueOf("" + memberCompetitionTable.getValueAt(i, 1));
		}

		memberIDComboBox.removeAllItems();

		try {
			Member[] members = MemberDatabaseConnection.executeQuery("SELECT * FROM member ORDER BY memberID ASC");
			selectedRow = memberCompetitionTable.getSelectedRow();

			if (selectedRow > -1)
				selectedMemberID = Integer.valueOf("" + memberCompetitionTable.getValueAt(selectedRow, 1));

			for (int i = 0; i < members.length; ++i) {
				currentMemberID = members[i].getMemberID();

				if (!LinearSearch.linearSearchContains(items, currentMemberID)
						|| ((editing) && (currentMemberID == selectedMemberID)))
					memberIDComboBox.addItem(currentMemberID);
			}
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
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
