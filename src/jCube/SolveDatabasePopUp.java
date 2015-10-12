package jCube;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * @author Kelsey McKenna
 */
public class SolveDatabasePopUp extends JFrame implements KeyListener {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This stores the initial width of the window.
	 */
	private static final int WIDTH = 850;
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
	 * This variable stores the text that is shown in the error message when the
	 * user enters an invalid time.
	 */
	private String timeHelpMessage = "You entered the time incorrectly\n\n" + "Valid formats include:\n" + "MM:SS.ss\n"
			+ "MM:S.ss\n" + "M:SS.ss\n" + "M:S.ss\n" + "SS.ss\n" + "S.ss\n" + "DNF\n";

	/**
	 * After choosing to edit a record in the table, the index of this row in
	 * the table is stored in this variable.
	 */
	private int selectedRowIndex;

	/**
	 * The lower bound for the times shown in the table
	 */
	private String lowerTimeBoundary = "-1";
	/**
	 * The upper bound for the times shown in the table
	 */
	private String upperTimeBoundary = "10000000000";

	/**
	 * If this variable is true, it indicates that a record is being edited,
	 * otherwise a record is being added.
	 */
	private boolean editing = false;

	/**
	 * tableContainer is placed ‘inside’ this variable so that is can be
	 * positioned and displayed in the window.
	 */
	private JPanel solveTablePanel;
	/**
	 * solveTable is placed inside this variable so that when the size of the
	 * table exceeds the size of the window, the user can scroll to view the
	 * rest of the table.
	 */
	private JScrollPane tableContainer;
	/**
	 * This stores the contents of the table displayed in the window.
	 */
	private final JTable solveTable;
	/**
	 * This variable can be customised so that certain cells of the table are
	 * uneditable and the columns of the table can be given text. This variable
	 * is then set as the model of solveTable.
	 */
	private final DefaultTableModel model;
	/**
	 * The buttons are placed in this panel.
	 */
	private JPanel buttonPanel;

	/**
	 * Clicking this button opens the Solve Form window.
	 */
	private JButton addSolveButton;
	/**
	 * Clicking this button opens the Solve Form window displaying the existing
	 * data for the selected solve.
	 */
	private JButton editSolveButton;
	/**
	 * Clicking this button deletes the selected rows from the table.
	 */
	private JButton deleteSolveButton;
	/**
	 * Clicking this button loads the selected solves into the main window.
	 */
	private JButton loadIntoProgramButton;

	/**
	 * This indicates the attribute by which the records in the table should be
	 * sorted.
	 */
	private String orderByAttribute = "solveTime";
	/**
	 * This indicates whether the records should be sorted in ascending or
	 * descending order. This should be "ASC" or "DESC"
	 */
	private String orderDirection = "ASC";

	/**
	 * This label is shown in the Solve Form window with the text ‘Time’.
	 */
	private JLabel timeLabel;
	/**
	 * This label is shown in the Solve Form window with the text ‘Penalty’.
	 */
	private JLabel penaltyLabel;
	/**
	 * This label is shown in the Solve Form window with the text ‘Comment’.
	 */
	private JLabel commentLabel;
	/**
	 * This label is shown in the Solve Form window with the text ‘Scramble’.
	 */
	private JLabel scrambleLabel;
	/**
	 * This label is shown in the Solve Form window with the text ‘Solution’.
	 */
	private JLabel solutionLabel;
	/**
	 * This label is shown in the Solve Form window with the text ‘Date Added’.
	 */
	private JLabel dateAddedLabel;

	/**
	 * This field is shown in the Solve Form and the user can enter the time
	 * into this field.
	 */
	private JTextField timeField;
	/**
	 * This field is shown in the Solve Form and the user can enter the penalty
	 * into this field.
	 */
	private JTextField penaltyField;
	/**
	 * This field is shown in the Solve Form and the user can enter the date
	 * added into this field.
	 */
	private JTextField dateAddedField;
	/**
	 * This is shown in the Solve Form and the user can enter the comment into
	 * this field.
	 */
	private JTextArea commentField;
	/**
	 * This is shown in the Solve Form and the user can enter the scramble into
	 * this field.
	 */
	private JTextArea scrambleField;
	/**
	 * This is shown in the Solve Form and the user can enter the solution into
	 * this field.
	 */
	private JTextArea solutionField;

	/**
	 * commentField is placed ‘inside’ this variable so that when the length of
	 * the comment exceeds the length of the commentField, the user can scroll
	 * to view the rest of the comment.
	 */
	private JScrollPane commentScrollPane;
	/**
	 * scrambleField is placed ‘inside’ this variable so that when the length of
	 * the comment exceeds the length of the scrambleField, the user can scroll
	 * to view the rest of the scramble.
	 */
	private JScrollPane scrambleScrollPane;
	/**
	 * solutionField is placed ‘inside’ this variable so that when the length of
	 * the comment exceeds the length of the solutionField, the user can scroll
	 * to view the rest of the solution.
	 */
	private JScrollPane solutionScrollPane;

	/**
	 * This represents Solve Form window.
	 */
	private JFrame solveInputForm;

	/**
	 * Clicking this button submits the data in the Solve Form window for
	 * validation.
	 */
	private JButton submitButton;

	/**
	 * This stores the contents of the Solve Form.
	 */
	private JPanel contentPane;

	/**
	 * This stores the contents of the menu bar in the Solve Table window.
	 */
	private MenuBar menuBar;

	/**
	 * Constructor - Sets up the Solve Table window.
	 */
	public SolveDatabasePopUp() {
		super("Solve Table");

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(WIDTH, 600));
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
				solveInputForm.setVisible(false);
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});
		menuBar = new MenuBar();
		setJMenuBar(menuBar.createMenuBar());
		setUpSolveInputForm();

		solveTablePanel = new JPanel();
		solveTablePanel.setOpaque(true);

		final String[] columnNames = { "ID", "Time", "Penalty", "Comment", "Scramble", "Solution", "Date Added" };

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
				/*
				 * resizeColumnWidths(solveTable); int row = e.getFirstRow();
				 * 
				 * TableModel model = (TableModel)e.getSource(); try {
				 * SolveDatabaseConnection.executeUpdate( String.format(
				 * "UPDATE solve " + "SET solveTime = \"%s\", " +
				 * "penalty = \"%s\", " + "comment = \"%s\", " +
				 * "scramble = \"%s\", " + "solution = \"%s\", " +
				 * "dateAdded = \"%s\" " + "WHERE solveID = %d", "" +
				 * model.getValueAt(row, 1), "" + model.getValueAt(row, 2), "" +
				 * model.getValueAt(row, 3), "" + model.getValueAt(row, 4), "" +
				 * model.getValueAt(row, 5), "" + model.getValueAt(row, 6),
				 * Integer.valueOf("" + model.getValueAt(row, 0))) ); } catch
				 * (Exception exc) { }
				 */
			}
		});

		solveTable = new JTable();
		solveTable.setModel(model);
		solveTable.setColumnSelectionAllowed(false);
		solveTable.setPreferredScrollableViewportSize(new Dimension(WIDTH, 70));
		solveTable.setFillsViewportHeight(true);
		solveTable.setAutoscrolls(true);
		solveTable.getTableHeader().setReorderingAllowed(false);
		solveTable.setFont(new Font("Arial", 0, 15));
		solveTable.setRowHeight(20);
		solveTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					editSolveButtonFunction();
				}
			}
		});

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3));
		buttonPanel.setPreferredSize(new Dimension(WIDTH, 40));
		buttonPanel.setSize(500, 50);

		addSolveButton = new JButton("Add Solve");
		addSolveButton.setFocusable(false);
		addSolveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timeField.setText("");
				penaltyField.setText("");
				commentField.setText("");
				scrambleField.setText("");
				solutionField.setText("");
				dateAddedField.setText("");
				editing = false;
				solveInputForm.setVisible(true);
				menuBar.clearButtonGroupSelection();
				menuBar.setEnabledButtonGroup(false);
			}
		});

		editSolveButton = new JButton("Edit Solve");
		editSolveButton.setFocusable(false);
		editSolveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editSolveButtonFunction();
			}
		});

		deleteSolveButton = new JButton("Delete Solve");
		deleteSolveButton.setFocusable(false);
		deleteSolveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					Object[] options = { "Yes", "No" };
					int choice = -1;

					if (solveTable.getSelectedRows().length >= 5) {
						choice = JOptionPane.showOptionDialog(null, "Are you sure you want to delete?", "Warning",
								JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
					} else
						choice = 0;

					if (choice == 0) {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						deleteRow();
					}
				} catch (ClassNotFoundException | SQLException e) {
					JOptionPane.showMessageDialog(solveTablePanel, "Unable to delete record from database", "Error",
							JOptionPane.ERROR_MESSAGE);
				} finally {
				}

				setCursor(Cursor.getDefaultCursor());
			}
		});

		loadIntoProgramButton = new JButton("Load into Program");
		loadIntoProgramButton.setToolTipText("Loads the selected solves into the main window");
		loadIntoProgramButton.setFocusable(false);
		loadIntoProgramButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedIndices = solveTable.getSelectedRows();
				String test;

				for (int i = 0; i < selectedIndices.length; ++i) {
					test = "" + model.getValueAt(selectedIndices[i], 2);
					Main.addSolveToList(new Solve("" + model.getValueAt(selectedIndices[i], 1),
							test.trim().equals("") ? "0" : test, "" + model.getValueAt(selectedIndices[i], 3), ""
									+ model.getValueAt(selectedIndices[i], 4), ""
									+ model.getValueAt(selectedIndices[i], 5)));
				}
			}
		});

		buttonPanel.add(addSolveButton);
		buttonPanel.add(editSolveButton);
		buttonPanel.add(deleteSolveButton);
		buttonPanel.add(loadIntoProgramButton);

		tableContainer = new JScrollPane(solveTable);
		tableContainer.getViewport().add(solveTable, null);

		resizeColumnWidths(solveTable);
		populateCellsWithDatabaseData();

		add(tableContainer, null);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
	}

	/**
	 * Sets up solveInputForm so that the data from the selected row in the
	 * table is placed in its fields. It is also indicated that a record is
	 * being edited (rather than added).
	 */
	private void editSolveButtonFunction() {
		int selectedRow = solveTable.getSelectedRow();

		if (selectedRow != -1) {
			timeField.setText("" + solveTable.getValueAt(selectedRow, 1));
			penaltyField.setText("" + solveTable.getValueAt(selectedRow, 2));
			commentField.setText("" + solveTable.getValueAt(selectedRow, 3));
			scrambleField.setText("" + solveTable.getValueAt(selectedRow, 4));
			solutionField.setText("" + solveTable.getValueAt(selectedRow, 5));
			dateAddedField.setText("" + solveTable.getValueAt(selectedRow, 6));
			menuBar.clearButtonGroupSelection();
			menuBar.setEnabledButtonGroup(false);
			solveInputForm.setVisible(true);
			selectedRowIndex = selectedRow;
			editing = true;
		}
	}

	/**
	 * Retrieves the data from the 'solve' table and adds it to the table in the
	 * window
	 */
	public void populateCellsWithDatabaseData() {
		int rowCount = model.getRowCount();
		String dateString;

		for (int i = 0; i < rowCount; ++i) { // Remove all rows from table
			model.removeRow(0);
		}

		SolveDBType[] solves = new SolveDBType[0];

		try {
			solves = SolveDatabaseConnection.executeQuery(String.format("SELECT * " + "FROM solve;"));

			switch (orderByAttribute) {
			case "solveTime":
				Sorter.sortByTime(solves, 0, solves.length - 1);
				break;
			case "dateAdded":
				try {
					Sorter.sortByDateAdded(solves, 0, solves.length - 1);
				} catch (Exception e) {
					System.out.println(e.getMessage() + "\n");
				}
				break;
			case "ID":
				try {
					Sorter.sortBySolveID(solves, 0, solves.length - 1);
				} catch (Exception e) {
					System.out.println(e.getMessage() + "\n");
				}
			}

			if (orderDirection.equals("DESC"))
				Sorter.reverseArray(solves);

		} catch (SQLException e) {
			// JOptionPane.showMessageDialog(null, "Could not load solves",
			// "Error", JOptionPane.ERROR_MESSAGE);
			System.err.print(e.getMessage());
		} catch (ClassNotFoundException e) {
			// JOptionPane.showMessageDialog(null, "Could not load solves",
			// "Error", JOptionPane.ERROR_MESSAGE);
			System.err.print(e.getMessage());
		}

		if (solves != null) {
			for (int i = 0; i < solves.length; ++i) {
				if ((solves[i].getNumericTime() >= Double.valueOf(lowerTimeBoundary))
						&& (solves[i].getNumericTime() <= Double.valueOf(upperTimeBoundary))) {
					dateString = solves[i].getDateAdded();
					model.addRow(new Object[] { "" + solves[i].getID(), solves[i].getStringTime(),
							solves[i].getPenalty(), solves[i].getComment(), solves[i].getScramble(),
							solves[i].getSolution(), (dateString.equals("null") ? "" : dateString) });
				}
			}
		}

		resizeColumnWidths(solveTable);
	}

	/**
	 * Retrieves the data matching the specified query from the 'solve' table
	 * and adds it to the table in the window
	 * 
	 * @param query
	 *            the query to be used on the database
	 */
	public void populateCellsWithDatabaseData(String query) {
		int rowCount = model.getRowCount();
		String dateString;

		for (int i = 0; i < rowCount; ++i) { // Remove all rows from table
			model.removeRow(0);
			// solveTable.removeAll();
		}

		SolveDBType[] solves = new SolveDBType[0];

		try {
			solves = SolveDatabaseConnection.executeQuery(query);
		} catch (SQLException e) {
			// JOptionPane.showMessageDialog(null, "Could not load solves",
			// "Error", JOptionPane.ERROR_MESSAGE);
			System.err.print(e.getMessage());
		} catch (ClassNotFoundException e) {
			// JOptionPane.showMessageDialog(null, "Could not load solves",
			// "Error", JOptionPane.ERROR_MESSAGE);
			System.err.print(e.getMessage());
		}

		if (solves != null) {
			for (int i = 0; i < solves.length; ++i) {
				dateString = solves[i].getDateAdded();
				model.addRow(new Object[] { "" + solves[i].getID(), solves[i].getStringTime(), solves[i].getPenalty(),
						solves[i].getComment(), solves[i].getScramble(), solves[i].getSolution(),
						(dateString.equals("null") ? "" : dateString) });
			}
		}

		resizeColumnWidths(solveTable);
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
		SolveDatabaseConnection
				.executeUpdate("INSERT INTO solve(solveTime, penalty, comment, scramble, solution, dateAdded) "
						+ "VALUES (\"\", \"\", \"\", \"\", \"\", \"\")");

		SolveDBType[] temp = SolveDatabaseConnection.executeQuery("SELECT * FROM solve ORDER BY solveID DESC LIMIT 1");

		model.addRow(new Object[] { "" + temp[0].getID(), "", "", "", "", "" });

		resizeColumnWidths(solveTable);
		solveTable.setRowSelectionInterval(solveTable.getRowCount() - 1, solveTable.getRowCount() - 1);
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
		int[] selectedIndices = solveTable.getSelectedRows();

		if (selectedIndices.length == 0)
			return;

		solveInputForm.setVisible(false);
		menuBar.setEnabledButtonGroup(true);

		for (int i = 0; i < selectedIndices.length; ++i) {
			SolveDatabaseConnection.executeUpdate("DELETE FROM solve WHERE solveID = "
					+ ("" + model.getValueAt(selectedIndices[i], 0)) + ";");
		}

		for (int i = selectedIndices.length - 1; i >= 0; --i)
			model.removeRow(selectedIndices[i]);

		// SolveDatabaseConnection.resetIDs();

		try {
			solveTable.setRowSelectionInterval(selectedIndices[selectedIndices.length - 1],
					selectedIndices[selectedIndices.length - 1]);
		} catch (Exception e) {
			try {
				solveTable.setRowSelectionInterval((int) Math.max(solveTable.getRowCount() - 1, 0),
						(int) Math.max(solveTable.getRowCount() - 1, 0));
			} catch (Exception exc) {
			}
		}
	}

	/**
	 * Resizes the widths of the columns in the specified table so that there is
	 * maximum visibility in each column
	 * 
	 * @param table
	 *            the table whose columns need resized
	 */
	
	private void resizeColumnWidths(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 50; // Min width

			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width, width);
				width = Math.min(width, comp.getWidth() / 7);
			}

			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	/**
	 * This method is invoked if the user is trying to filter the data in the
	 * table, but enters invalid data. The data will be reset and an error
	 * message will be shown.
	 * 
	 * @param originalLowerBoundary
	 *            the value that will be assigned to the
	 *            {@link #lowerTimeBoundary}
	 * @param originalUpperBoundary
	 *            the value that will be assigned to the
	 *            {@link #upperTimeBoundary}
	 */
	private void resetTimeBoundariesAndShowErrorMessage(String originalLowerBoundary, String originalUpperBoundary) {
		lowerTimeBoundary = originalLowerBoundary;
		upperTimeBoundary = originalUpperBoundary;

		JOptionPane.showMessageDialog(this, "The data you entered was invalid", "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * @author Kelsey McKenna
	 */
	private class MenuBar {
		/**
		 * This variable holds the contents of the menu bar
		 */
		private JMenuBar menuBar;
		/**
		 * This stores the contents of the sorting menu
		 */
		private JMenu sortingMenu;
		/**
		 * This stores the contents of the filter menu
		 */
		private JMenu filterMenu;

		/**
		 * The radio buttons are assigned to this button group so that only one
		 * of the radio buttons can selected at one time.
		 */
		private ButtonGroup sortingButtonGroup;
		/**
		 * Selecting this radio button causes the rows in the table to be sorted
		 * by the time column in ascending order.
		 */
		private JRadioButtonMenuItem sortByTimeAscendingItem;
		/**
		 * Selecting this radio button causes the rows in the table to be sorted
		 * by the time column in descending order.
		 */
		private JRadioButtonMenuItem sortByTimeDescendingItem;
		/**
		 * Selecting this radio button causes the rows in the table to be sorted
		 * by the date added column in ascending order.
		 */
		private JRadioButtonMenuItem sortByDateAscendingItem;
		/**
		 * Selecting this radio button causes the rows in the table to be sorted
		 * by the date added column in descending order.
		 */
		private JRadioButtonMenuItem sortByDateDescendingItem;
		/**
		 * Selecting this radio button causes the rows in the table to be sorted
		 * by the ID column in ascending order.
		 */
		private JRadioButtonMenuItem sortByIDAscendingItem;
		/**
		 * Selecting this radio button causes the rows in the table to be sorted
		 * by the ID column in descending order.
		 */
		private JRadioButtonMenuItem sortByIDDescendingItem;

		/**
		 * Clicking this menu item allows the user to filter the data in the
		 * table by certain criteria relating to the time column.
		 */
		private JMenuItem filterByTimeItem;
		/**
		 * Clicking this menu item removes any filter present so that all data
		 * from the table is shown
		 */
		private JMenuItem removeFilterItem;

		/**
		 * @return the menu bar for the Solve Form window
		 */
		public JMenuBar createMenuBar() {
			menuBar = new JMenuBar();

			/******** SORTING ***************/
			sortingMenu = new JMenu("Sorting");
			sortingButtonGroup = new ButtonGroup();

			sortByTimeAscendingItem = new JRadioButtonMenuItem("Sort by Time (Ascending)");
			sortByTimeAscendingItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					orderByAttribute = "solveTime";
					orderDirection = "ASC";
					populateCellsWithDatabaseData();
				}
			});

			sortByTimeDescendingItem = new JRadioButtonMenuItem("Sort by Time (Descending)");
			sortByTimeDescendingItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					orderByAttribute = "solveTime";
					orderDirection = "DESC";
					populateCellsWithDatabaseData();
				}
			});

			sortByDateAscendingItem = new JRadioButtonMenuItem("Sort by Date (Ascending)");
			sortByDateAscendingItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					orderByAttribute = "dateAdded";
					orderDirection = "ASC";
					populateCellsWithDatabaseData();
				}
			});

			sortByDateDescendingItem = new JRadioButtonMenuItem("Sort by Date (Descending)");
			sortByDateDescendingItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					orderByAttribute = "dateAdded";
					orderDirection = "DESC";
					populateCellsWithDatabaseData();
				}
			});

			sortByIDAscendingItem = new JRadioButtonMenuItem("Sort by ID (Ascending)");
			sortByIDAscendingItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					orderByAttribute = "ID";
					orderDirection = "ASC";
					populateCellsWithDatabaseData();
				}
			});

			sortByIDDescendingItem = new JRadioButtonMenuItem("Sort by ID (Descending)");
			sortByIDDescendingItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					orderByAttribute = "ID";
					orderDirection = "DESC";
					populateCellsWithDatabaseData();
				}
			});

			sortingButtonGroup.add(sortByTimeAscendingItem);
			sortingButtonGroup.add(sortByTimeDescendingItem);
			sortingButtonGroup.add(sortByDateAscendingItem);
			sortingButtonGroup.add(sortByDateDescendingItem);
			sortingButtonGroup.add(sortByIDAscendingItem);
			sortingButtonGroup.add(sortByIDDescendingItem);

			sortingMenu.add(sortByTimeAscendingItem);
			sortingMenu.add(sortByTimeDescendingItem);
			sortingMenu.add(sortByDateAscendingItem);
			sortingMenu.add(sortByDateDescendingItem);
			sortingMenu.add(sortByIDAscendingItem);
			sortingMenu.add(sortByIDDescendingItem);
			/******** END SORTING ***************/

			/******** FILTER ***************/
			filterMenu = new JMenu("Filter");

			filterByTimeItem = new JMenuItem("Filter by Time");
			filterByTimeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String originalLowerBoundary = lowerTimeBoundary;
					String originalUpperBoundary = upperTimeBoundary;
					lowerTimeBoundary = JOptionPane.showInputDialog(null, "Enter lower time boundary",
							"Lower time boundary");
					if (lowerTimeBoundary != null) {
						if (!Solve.isValidTime(lowerTimeBoundary)) {
							resetTimeBoundariesAndShowErrorMessage(originalLowerBoundary, originalUpperBoundary);
							return;
						} else
							lowerTimeBoundary = "" + Solve.getFormattedStringToDouble(lowerTimeBoundary);
					} else {
						lowerTimeBoundary = originalLowerBoundary;
						populateCellsWithDatabaseData();
						return;
					}

					upperTimeBoundary = JOptionPane.showInputDialog(null, "Enter upper time boundary",
							"Upper time boundary");
					if (upperTimeBoundary != null) {
						if (!Solve.isValidTime(upperTimeBoundary)) {
							resetTimeBoundariesAndShowErrorMessage(originalLowerBoundary, originalUpperBoundary);
							return;
						} else
							upperTimeBoundary = "" + Solve.getFormattedStringToDouble(upperTimeBoundary);
					} else {
						upperTimeBoundary = originalUpperBoundary;
						populateCellsWithDatabaseData();
						return;
					}

					if (Solve.getFormattedStringToDouble(lowerTimeBoundary) > Solve
							.getFormattedStringToDouble(upperTimeBoundary))
						resetTimeBoundariesAndShowErrorMessage(originalLowerBoundary, originalUpperBoundary);

					populateCellsWithDatabaseData();
				}
			});

			removeFilterItem = new JMenuItem("Remove Filter");
			removeFilterItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					lowerTimeBoundary = "-1";
					upperTimeBoundary = "10000000000";
					populateCellsWithDatabaseData();
				}
			});

			filterMenu.add(filterByTimeItem);
			filterMenu.add(removeFilterItem);
			/******** END FILTER ***************/

			menuBar.add(sortingMenu);
			menuBar.add(filterMenu);

			return menuBar;
		}

		/**
		 * Clear any selection in the sorting button group.
		 */
		public void clearButtonGroupSelection() {
			sortingButtonGroup.clearSelection();
		}

		/**
		 * Enables or disables all of the menu items in the menu bar.
		 * 
		 * @param state
		 *            <b>true</b> indicates that all should be enabled; <br>
		 *            <b>false</b> indicates that all should be 'greyed' out.
		 */
		public void setEnabledButtonGroup(boolean state) {
			sortByTimeAscendingItem.setEnabled(state);
			sortByTimeDescendingItem.setEnabled(state);
			sortByDateAscendingItem.setEnabled(state);
			sortByDateDescendingItem.setEnabled(state);
			sortByIDAscendingItem.setEnabled(state);
			sortByIDDescendingItem.setEnabled(state);
			filterByTimeItem.setEnabled(state);
			removeFilterItem.setEnabled(state);
		}
	}

	/**
	 * Sets up the Solve Form
	 */
	private void setUpSolveInputForm() {
		solveInputForm = new JFrame("Solve Form");

		contentPane = new JPanel();
		contentPane.setLayout(null);

		solveInputForm.setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		solveInputForm.setContentPane(contentPane);
		solveInputForm.setPreferredSize(new Dimension(480, 440));
		solveInputForm.setSize(new Dimension(480, 440));
		solveInputForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		solveInputForm.setResizable(false);
		solveInputForm.setLocation(600, 150);
		solveInputForm.setVisible(false);
		solveInputForm.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				menuBar.setEnabledButtonGroup(true);
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
		timeLabel = new JLabel("Time ");
		timeLabel.setSize(120, 40);
		timeLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		penaltyLabel = new JLabel("Penalty ");
		penaltyLabel.setSize(120, 40);
		penaltyLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		commentLabel = new JLabel("Comment ");
		commentLabel.setSize(120, 40);
		commentLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		scrambleLabel = new JLabel("Scramble ");
		scrambleLabel.setSize(120, 40);
		scrambleLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing;

		solutionLabel = new JLabel("Solution ");
		solutionLabel.setSize(120, 40);
		solutionLabel.setLocation(0 + pad, y + pad);

		y += fieldYSpacing + 50;

		dateAddedLabel = new JLabel("Date Added");
		dateAddedLabel.setSize(120, 40);
		dateAddedLabel.setLocation(0 + pad, y + pad);

		// ***************Text Fields*************************

		y = 0;

		timeField = new JTextField();
		timeField.setMargin(new Insets(0, 10, 0, 15));
		timeField.setFont(fieldFont);
		timeField.setSize(350, 40);
		timeField.setLocation(100 + pad, y + pad);
		timeField.addKeyListener(this);

		y += fieldYSpacing;

		penaltyField = new JTextField("");
		penaltyField.setMargin(new Insets(0, 10, 0, 15));
		penaltyField.setFont(new Font("Arial", 0, 15));
		penaltyField.setSize(350, 40);
		penaltyField.setLocation(100 + pad, y + pad);
		penaltyField.addKeyListener(this);

		y += fieldYSpacing;

		commentField = new JTextArea("");
		commentField.setMargin(new Insets(0, 10, 0, 15));
		commentField.setLineWrap(true);
		commentField.setWrapStyleWord(true);
		commentField.setEditable(true);
		commentField.setFont(new Font("Arial", 0, 15));
		commentField.addKeyListener(this);

		commentScrollPane = new JScrollPane(commentField);
		commentScrollPane.setLocation(100 + pad, y + pad);
		commentScrollPane.setSize(new Dimension(350, 40));
		commentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		y += fieldYSpacing;

		scrambleField = new JTextArea("");
		scrambleField.setMargin(new Insets(0, 10, 0, 15));
		scrambleField.setEditable(true);
		scrambleField.setLineWrap(true);
		scrambleField.setWrapStyleWord(true);
		scrambleField.setFont(new Font("Arial", 0, 15));
		scrambleField.addKeyListener(this);

		scrambleScrollPane = new JScrollPane(scrambleField);
		scrambleScrollPane.setLocation(100 + pad, y + pad);
		scrambleScrollPane.setSize(new Dimension(350, 40));
		scrambleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		y += fieldYSpacing;

		solutionField = new JTextArea();
		solutionField.setMargin(new Insets(0, 10, 0, 15));
		solutionField.setEditable(true);
		solutionField.setLineWrap(true);
		solutionField.setWrapStyleWord(true);
		solutionField.setFont(new Font("Arial", 0, 15));
		solutionField.addKeyListener(this);

		solutionScrollPane = new JScrollPane(solutionField);
		solutionScrollPane.setLocation(100 + pad, y + pad);
		solutionScrollPane.setSize(new Dimension(350, 80));
		solutionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		y += fieldYSpacing + 50;

		dateAddedField = new JTextField("");
		dateAddedField.setMargin(new Insets(0, 10, 0, 15));
		dateAddedField.setFont(new Font("Arial", 0, 15));
		dateAddedField.setLocation(100 + pad, y + pad);
		dateAddedField.setSize(350, 40);
		dateAddedField.setToolTipText("yyyy-mm-dd hh:mm.ss");
		dateAddedField.addKeyListener(this);

		// ************Submission Button**********
		y = 370;

		submitButton = new JButton("Submit");
		submitButton.setSize(480, 30);
		submitButton.setLocation(0, y + pad);
		submitButton.setFocusable(false);
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String time = timeField.getText().trim();

				if (!Solve.isValidTime(time)) {
					JOptionPane.showMessageDialog(solveTablePanel, timeHelpMessage, "Error", JOptionPane.ERROR_MESSAGE,
							null);
					return;
				} else if ((dateAddedField.getText().trim().equals(""))
						|| (!Solve.isValidDate(dateAddedField.getText().trim()))) {
					int choice = -1;
					Object[] options = { "Yes", "No" };
					choice = JOptionPane
							.showOptionDialog(
									null,
									"Date must be valid and have the form: yyyy-MM-dd HH:mm:ss\nWould you like to use the current time?",
									"Error", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
									options[0]);

					if (choice != 0)
						return;
					else {
						dateAddedField.setText(SolveDatabaseConnection.getCurrentTimeInSQLFormat());
					}
				}

				try {
					int row;
					if (!editing) {
						addRow();
						row = solveTable.getRowCount() - 1;
					} else {
						row = selectedRowIndex;
					}

					if (time.trim().equalsIgnoreCase("DNF")) {
						solveTable.setValueAt("DNF", row, 1);
						solveTable.setValueAt("0", row, 2);
					} else {
						solveTable.setValueAt(Solve.getPaddedTime(Solve.getSecondsToFormattedString(Solve
								.getFormattedStringToDouble(time))), row, 1);
						solveTable.setValueAt(penaltyField.getText().trim(), row, 2);
					}

					solveTable.setValueAt(commentField.getText().trim(), row, 3);
					solveTable.setValueAt(scrambleField.getText().trim(), row, 4);
					solveTable.setValueAt(solutionField.getText().trim(), row, 5);
					solveTable.setValueAt(dateAddedField.getText().trim(), row, 6);
					solveInputForm.setVisible(false);
					menuBar.setEnabledButtonGroup(true);
					editing = false;

					menuBar.clearButtonGroupSelection();

					SolveDatabaseConnection.executeUpdate(String.format("UPDATE solve " + "SET solveTime = \"%s\", "
							+ "penalty = \"%s\", " + "comment = \"%s\", " + "scramble = \"%s\", "
							+ "solution = \"%s\", " + "dateAdded = \"%s\" " + "WHERE solveID = %d",
							"" + model.getValueAt(row, 1), "" + model.getValueAt(row, 2),
							"" + model.getValueAt(row, 3), "" + model.getValueAt(row, 4),
							"" + model.getValueAt(row, 5), "" + model.getValueAt(row, 6),
							Integer.valueOf("" + model.getValueAt(row, 0))));
					resizeColumnWidths(solveTable);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		y += buttonYSpacing;

		contentPane.add(timeLabel);
		contentPane.add(penaltyLabel);
		contentPane.add(commentLabel);
		contentPane.add(scrambleLabel);
		contentPane.add(solutionLabel);
		contentPane.add(dateAddedLabel);

		contentPane.add(timeField);
		contentPane.add(penaltyField);
		contentPane.add(commentScrollPane);
		contentPane.add(scrambleScrollPane);
		contentPane.add(solutionScrollPane);
		contentPane.add(dateAddedField);

		contentPane.add(submitButton);

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		Main.transferFormFocus(arg0);
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
