package jCube;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * @author Kelsey McKenna
 */
public class AlgorithmDatabasePopUp extends JFrame {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This indicates the inital width of the 'Algorithm Table' window
	 */
	private static final int WIDTH = 700;

	/**
	 * The list of algorithms is stored inside this panel
	 */
	private JPanel algorithmListPanel;
	/**
	 * algorithmTable is placed inside this so that when the size of the table
	 * exceeds the size of the window, the rest of the table can be viewed by
	 * scrolling
	 */
	private JScrollPane tableContainer;
	/**
	 * This stores the contents of the table
	 */
	private final JTable algorithmTable;
	/**
	 * By setting the model of the algorithmTable to 'model', certain cells of
	 * the table can be made uneditable, and the columns can be given names
	 */
	private final DefaultTableModel model;
	/**
	 * The buttons are placed in this panel so that they are grouped together
	 */
	private JPanel buttonPanel;

	/**
	 * Clicking this button opens an input dialog so that a new algorithm can be
	 * entered
	 */
	private JButton addAlgorithmButton;
	/**
	 * Clicking this button applies the reverse of the selected algorithm to the
	 * virtual cube in the main window
	 */
	private JButton applyReverseAlgorithmButton;
	/**
	 * Clicking this button performs the algorithm in real-time on the virtual
	 * cube in the main window
	 */
	private JButton viewExecutionButton;
	/**
	 * Clicking this button deletes the selected rows from the table
	 */
	private JButton deleteAlgorithmButton;

	/**
	 * Constructor - sets up the window
	 */
	public AlgorithmDatabasePopUp() {
		super("Algorithm Table");

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(WIDTH, 400));
		setVisible(false);

		algorithmListPanel = new JPanel();
		algorithmListPanel.setOpaque(true);

		final String[] columnNames = { "ID", "Algorithm", "Comment", };

		model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public int getColumnCount() {
				return 3;
			}

			public String getColumnName(int col) {
				return columnNames[col];
			}

			public boolean isCellEditable(int row, int col) {
				return ((col != 0) && (row >= AlgorithmDatabaseConnection.PRESET_ALGORITHM_UPDATES.length));
			}
		};
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				Main.resizeColumnWidths(algorithmTable);
				int row = e.getFirstRow();

				TableModel model = (TableModel) e.getSource();
				try {
					AlgorithmDatabaseConnection.executeUpdate(String.format("UPDATE algorithm "
							+ "SET moveSequence = \"%s\", " + "comment = \"%s\" " + "WHERE algorithmID = %d", ""
							+ model.getValueAt(row, 1), "" + model.getValueAt(row, 2),
							Integer.valueOf("" + model.getValueAt(row, 0))));
				} catch (Exception exc) {
				}
			}
		});

		algorithmTable = new JTable();
		algorithmTable.setModel(model);
		algorithmTable.setColumnSelectionAllowed(false);
		algorithmTable.setPreferredScrollableViewportSize(new Dimension(WIDTH, 70));
		algorithmTable.setFillsViewportHeight(true);
		algorithmTable.setAutoscrolls(true);
		algorithmTable.getTableHeader().setReorderingAllowed(false);
		algorithmTable.setFont(new Font("Arial", 0, 15));
		algorithmTable.setRowHeight(20);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3));
		buttonPanel.setPreferredSize(new Dimension(WIDTH, 40));
		buttonPanel.setSize(WIDTH, 50);

		addAlgorithmButton = new JButton("Add Algorithm");
		addAlgorithmButton.setFocusable(false);
		addAlgorithmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					addRow();
				} catch (SQLException exc) {
					JOptionPane
							.showMessageDialog(null, "Could not access database", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (ClassNotFoundException exc) {
					JOptionPane
							.showMessageDialog(null, "Could not access database", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		viewExecutionButton = new JButton("View Execution");
		viewExecutionButton.setFocusable(false);
		viewExecutionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = algorithmTable.getSelectedRow();
				if (selectedRow != -1) {
					String algorithm = "" + model.getValueAt(selectedRow, 1);
					Main.performRealTimeSolving(SolveMaster.getReverseStringMoves(algorithm), algorithm);
				}
			}
		});

		applyReverseAlgorithmButton = new JButton("Apply Reverse");
		applyReverseAlgorithmButton.setFocusable(false);
		applyReverseAlgorithmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = algorithmTable.getSelectedRow();
				if (selectedRow != -1) {
					// Main.performRealTimeSolving("",
					// SolveMaster.getReverseStringMoves("" +
					// model.getValueAt(algorithmTable.getSelectedRow(), 1)));
					Main.handleScramble(SolveMaster.getReverseStringMoves("" + model.getValueAt(selectedRow, 1)));
				}
			}
		});

		deleteAlgorithmButton = new JButton("Delete Algorithm");
		deleteAlgorithmButton.setFocusable(false);
		deleteAlgorithmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Object[] options = { "Yes", "No" };
					int choice = -1;

					if (algorithmTable.getSelectedRows().length > 5) {
						choice = JOptionPane.showOptionDialog(null, "Are you sure you want to delete?", "Warning",
								JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
					} else
						choice = 0;

					if (choice == 0)
						deleteRow();
				} catch (ClassNotFoundException | SQLException e) {
					JOptionPane.showMessageDialog(algorithmTable, "Unable to delete record from database", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		buttonPanel.add(addAlgorithmButton);
		buttonPanel.add(applyReverseAlgorithmButton);
		buttonPanel.add(viewExecutionButton);
		buttonPanel.add(deleteAlgorithmButton);

		tableContainer = new JScrollPane(algorithmTable);
		tableContainer.getViewport().add(algorithmTable, null);

		Main.resizeColumnWidths(algorithmTable);
		populateCellsWithDatabaseData();

		add(tableContainer, null);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
	}

	/**
	 * Retrieves the data from the 'algorithm' table and adds it to the table in
	 * the window
	 */
	private void populateCellsWithDatabaseData() {
		int rowCount = model.getRowCount();

		for (int i = 0; i < rowCount; ++i) { // Remove all rows from table
			model.removeRow(0);
		}

		Algorithm[] algorithms = new Algorithm[0];

		try {
			algorithms = AlgorithmDatabaseConnection.executeQuery("SELECT * " + "FROM algorithm;");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Could not load algorithms", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Could not load algorithms", "Error", JOptionPane.ERROR_MESSAGE);
		}

		if (algorithms != null) {
			for (int i = 0; i < algorithms.length; ++i) {
				model.addRow(new Object[] { "" + algorithms[i].getAlgorithmID(), algorithms[i].getMoveSequence(),
						algorithms[i].getComment() });
			}
		}

		Main.resizeColumnWidths(algorithmTable);
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
		AlgorithmDatabaseConnection.executeUpdate("INSERT INTO algorithm(moveSequence, comment) VALUES (\"\", \"\")");
		model.addRow(new Object[] { model.getRowCount() + 1, "", "" });

		Main.resizeColumnWidths(algorithmTable);
		algorithmTable.setRowSelectionInterval(algorithmTable.getRowCount() - 1, algorithmTable.getRowCount() - 1);
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
		int[] selectedIndices = algorithmTable.getSelectedRows();

		if (selectedIndices.length == 0)
			return;

		for (int i = 0; i < selectedIndices.length; ++i) {
			if (selectedIndices[i] >= AlgorithmDatabaseConnection.PRESET_ALGORITHM_UPDATES.length)
				AlgorithmDatabaseConnection.executeUpdate("DELETE FROM algorithm WHERE algorithmID = "
						+ ("" + model.getValueAt(selectedIndices[i], 0)) + ";");
		}

		for (int i = selectedIndices.length - 1; i >= 0; --i) {
			if (selectedIndices[i] >= AlgorithmDatabaseConnection.PRESET_ALGORITHM_UPDATES.length)
				model.removeRow(selectedIndices[i]);
		}

		for (int i = selectedIndices[0]; i < model.getRowCount(); ++i)
			model.setValueAt("" + (i + 1), i, 0);

		AlgorithmDatabaseConnection.resetIDs();

		try {
			algorithmTable.setRowSelectionInterval(selectedIndices[selectedIndices.length - 1],
					selectedIndices[selectedIndices.length - 1]);
		} catch (Exception e) {
			algorithmTable.setRowSelectionInterval(Math.max(algorithmTable.getRowCount() - 1, 0),
					Math.max(algorithmTable.getRowCount() - 1, 0));
		}
	}
}
