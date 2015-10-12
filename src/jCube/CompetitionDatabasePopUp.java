package jCube;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

/**
 * @author Kelsey McKenna
 */
public class CompetitionDatabasePopUp extends JFrame {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The initial width of the window
	 */
	private static final int WIDTH = 700;

	/**
	 * The list of competitions is stored inside this panel
	 */
	private JPanel competitionListPanel;
	/**
	 * competitionTable is placed inside this so that when the size of the table
	 * exceeds the size of the window, the rest of the table can be viewed by
	 * scrolling
	 */
	private JScrollPane tableContainer;
	/**
	 * Stores the contents of the table
	 */
	private final JTable competitionTable;
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
	 * Clicking this button adds a row to the table
	 */
	private JButton addCompetitionButton;
	/**
	 * Clicking this button opens an input dialog so that the date of the
	 * competition can be edited
	 */
	private JButton editCompetitionButton;
	/**
	 * Clicking this button deletes the selected competitions from the table and
	 * from the database
	 */
	private JButton deleteCompetitionButton;
	/**
	 * Clicking this button opens the 'Member-Competition Table' window and
	 * displays the rankings for the selected competition
	 */
	private JButton viewRankingsButton;

	/**
	 * This is the window that opens when the viewRankingsButton is clicked
	 */
	private MemberCompetitionDatabasePopUp memberCompetitionDatabasePopUp;

	/**
	 * Constructor - sets up the window
	 */
	public CompetitionDatabasePopUp() {
		super("Competition Table");

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(WIDTH, 400));
		setVisible(false);

		memberCompetitionDatabasePopUp = new MemberCompetitionDatabasePopUp();

		competitionListPanel = new JPanel();
		competitionListPanel.setOpaque(true);

		final String[] columnNames = { "ID", "Date" };

		model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public int getColumnCount() {
				return 2;
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
				Main.resizeColumnWidths(competitionTable);
				/*
				 * int row = e.getFirstRow();
				 * 
				 * TableModel model = (TableModel)e.getSource(); try {
				 * CompetitionDatabaseConnection.executeUpdate( String.format(
				 * "UPDATE competition " + "SET competitionDate = \"%s\", " +
				 * "WHERE competitionID = %d", "" + model.getValueAt(row, 1), ""
				 * + model.getValueAt(row, 2)) ); } catch (Exception exc) { }
				 */
			}
		});

		competitionTable = new JTable();
		competitionTable.setModel(model);
		competitionTable.setColumnSelectionAllowed(false);
		competitionTable.setPreferredScrollableViewportSize(new Dimension(WIDTH, 70));
		competitionTable.setFillsViewportHeight(true);
		competitionTable.setAutoscrolls(true);
		competitionTable.getTableHeader().setReorderingAllowed(false);
		competitionTable.setFont(new Font("Arial", 0, 15));
		competitionTable.setRowHeight(20);
		competitionTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2)
					viewRankingsButtonFunction();
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

		addCompetitionButton = new JButton("Add Competition");
		addCompetitionButton.setFocusable(false);
		addCompetitionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					addRow();
				} catch (SQLException exc) {
					exc.printStackTrace();
					JOptionPane
							.showMessageDialog(null, "Could not access database", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (ClassNotFoundException exc) {
					JOptionPane
							.showMessageDialog(null, "Could not access database", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		editCompetitionButton = new JButton("Edit Competition Date");
		editCompetitionButton.setFocusable(false);
		editCompetitionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editButtonFunction();
			}
		});

		deleteCompetitionButton = new JButton("Delete Competition");
		deleteCompetitionButton.setFocusable(false);
		deleteCompetitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Object[] options = { "Yes", "No" };
					int choice = -1;

					if (competitionTable.getSelectedRows().length == 0)
						return;

					choice = JOptionPane.showOptionDialog(null, "Are you sure you want to delete?", "Warning",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

					if (choice == 0)
						deleteRow();
				} catch (ClassNotFoundException | SQLException e) {
					JOptionPane.showMessageDialog(competitionTable, "Unable to delete record from database", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		viewRankingsButton = new JButton("View Rankings");
		viewRankingsButton.setFocusable(false);
		viewRankingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				viewRankingsButtonFunction();
			}
		});

		buttonPanel.add(addCompetitionButton);
		buttonPanel.add(editCompetitionButton);
		buttonPanel.add(viewRankingsButton);
		buttonPanel.add(deleteCompetitionButton);

		tableContainer = new JScrollPane(competitionTable);
		tableContainer.getViewport().add(competitionTable, null);

		Main.resizeColumnWidths(competitionTable);
		populateCellsWithDatabaseData();

		add(tableContainer, null);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
	}

	/**
	 * Sets up the memberCompetitionDatabasePopUp window so that is it visible
	 * and the cells are populated with data relating to the selecting row in
	 * the table
	 */
	private void viewRankingsButtonFunction() {
		if ((competitionTable.getSelectedRow() > -1)) {
			memberCompetitionDatabasePopUp.setCurrentCompetitionID(Integer.valueOf(""
					+ competitionTable.getValueAt(competitionTable.getSelectedRow(), 0)));
			memberCompetitionDatabasePopUp.populateCellsWithDatabaseData();
			memberCompetitionDatabasePopUp.setVisible(true);
		}
	}

	/**
	 * Performs the operations required to allow the selected row to be edited
	 * and validation to be performed on the data entered
	 */
	private void editButtonFunction() {
		// Stores the string entered by the user.
		String date;
		// Stores the index of the row in the table selected by the user.
		int row = competitionTable.getSelectedRow();

		if (row != -1) {
			// Stores the date-string shown in the selected row so that if
			// editing, the existing date can be shown in the input window.
			String originalDate = "" + competitionTable.getValueAt(row, 1);
			date = JOptionPane.showInputDialog(null, "Enter Date of Competition",
					(originalDate.equals("")) ? "dd/MM/YYYY" : originalDate);

			if (date == null)
				return;

			date += "";
			date = date.trim();

			if ((date.equals("")) || (!Competition.isValidDate(date))) {
				JOptionPane.showMessageDialog(this, "Invalid date", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					CompetitionDatabaseConnection.executeUpdate(String.format("UPDATE competition "
							+ "SET competitionDate = \"%s\" " + "WHERE competitionID = %d", date,
							Integer.valueOf("" + model.getValueAt(competitionTable.getSelectedRow(), 0))));
					competitionTable.setValueAt(date, row, 1);
				} catch (Exception exc) {
					exc.printStackTrace();
				}

			}
		}
	}

	/**
	 * Populates the cells of the table with the data from the database
	 */
	private void populateCellsWithDatabaseData() {
		int rowCount = model.getRowCount();

		for (int i = 0; i < rowCount; ++i) { // Remove all rows from table
			model.removeRow(0);
		}

		Competition[] competitions = new Competition[0];

		try {
			competitions = CompetitionDatabaseConnection.executeQuery("SELECT * " + "FROM competition;");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Could not load algorithms", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Could not load algorithms", "Error", JOptionPane.ERROR_MESSAGE);
		}

		if (competitions != null) {
			for (int i = 0; i < competitions.length; ++i) {
				model.addRow(new Object[] { "" + competitions[i].getID(), competitions[i].getDate() });
			}
		}

		Main.resizeColumnWidths(competitionTable);
	}

	/**
	 * Adds a row to the table and a blank record to the 'competition' table in
	 * the database
	 * 
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist etc.
	 */
	private void addRow() throws ClassNotFoundException, SQLException {
		CompetitionDatabaseConnection.executeUpdate("INSERT INTO competition(competitionDate) VALUES (\"\")");
		model.addRow(new Object[] {
				""
						+ CompetitionDatabaseConnection
								.executeQuery("SELECT * FROM competition ORDER BY competitionID DESC LIMIT 1")[0]
								.getID(), "", "" });

		Main.resizeColumnWidths(competitionTable);
		competitionTable
				.setRowSelectionInterval(competitionTable.getRowCount() - 1, competitionTable.getRowCount() - 1);
	}

	/**
	 * Deletes the selected rows from competitionTable
	 * 
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist etc.
	 */
	private void deleteRow() throws ClassNotFoundException, SQLException {
		int[] selectedIndices = competitionTable.getSelectedRows();

		for (int i = 0; i < selectedIndices.length; ++i) {
			CompetitionDatabaseConnection.executeUpdate("DELETE FROM competition WHERE competitionID = "
					+ ("" + model.getValueAt(selectedIndices[i], 0)) + ";");
		}

		for (int i = selectedIndices.length - 1; i >= 0; --i) {
			model.removeRow(selectedIndices[i]);
		}

		try {
			competitionTable.setRowSelectionInterval(selectedIndices[selectedIndices.length - 1],
					selectedIndices[selectedIndices.length - 1]);
		} catch (Exception e) {
			if (competitionTable.getRowCount() > 0)
				competitionTable.setRowSelectionInterval(competitionTable.getRowCount() - 1,
						competitionTable.getRowCount() - 1);
		}
	}

}
