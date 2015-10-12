package jCube;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author Kelsey McKenna
 */
public class Main extends JPanel implements KeyListener {

	/**
	 * @author Kelsey McKenna This is used to run the visible incrementing timer
	 *         in the main window
	 */
	private static class TimerListener implements ActionListener {
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent evt) {
			/*
			 * This routine will be called every x milliseconds (defined by
			 * Timer constructor) So, we want to increase elapsedTime
			 * accordingly and change the text of timeLabel to show elapsedTime
			 * to 2 decimal places
			 */
			elapsedTimingSeconds += timingDisplayInterval;
			if (elapsedTimingSeconds >= 60.0) {
				++elapsedMinutes;
				elapsedTimingSeconds = 0;
			}

			if (elapsedMinutes > 0)
				timeLabel.setText(String.format("%d:%02d.%02d", elapsedMinutes, (int) elapsedTimingSeconds,
						(int) (100 * (elapsedTimingSeconds - (int) elapsedTimingSeconds))));
			else
				timeLabel.setText(String.format("%d.%02d", (int) elapsedTimingSeconds,
						(int) (100 * (elapsedTimingSeconds - (int) elapsedTimingSeconds))));

		}
	}

	/**
	 * @author Kelsey McKenna This is used to run the inspection timer in the
	 *         main window
	 */
	private static class InspectionTimerListener implements ActionListener {

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			inspectionTimeRemaining -= 1;

			if (inspectionTimeRemaining > 0)
				timeLabel.setText("" + inspectionTimeRemaining);
			else if (inspectionTimeRemaining > -2) {
				timeLabel.setText("+2");
				currentPenalty = "+2";
			} else {
				timeLabel.setText("DNF");
				currentPenalty = "DNF";
				inspectionTimer.stop();
				inspectionTimer = null;
			}
		}
	}

	/**
	 * @author Kelsey McKenna This is used to perform real-time animations
	 */
	private static class RealTimeTimerListener implements ActionListener {
		/**
		 * Stores the move count of the solution
		 */
		private static int moveCount;
		/**
		 * The index of the current move being applied
		 */
		private static int index;
		/**
		 * Stores the moves to be performed
		 */
		private static LinkedList<String> movesToPerform;

		/**
		 * @param moves
		 *            the moves to perform
		 */
		public RealTimeTimerListener(LinkedList<String> moves) {
			moveCount = moves.size();
			movesToPerform = new LinkedList<>();

			for (int i = 0; i < moveCount; ++i) {
				movesToPerform.add(moves.get(i));
			}

			index = 0;
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (index < moveCount) {
				cube.performAbsoluteMoves(movesToPerform.get(index));
				++index;
				cubePanel.repaint();
			} else {
				try {
					realTimeSolutionTimer.stop();
					realTimeSolutionTimer = null;
				} catch (Exception exc) {
				}
				/*
				 * MenuBar.setRandomScrambleEnabled(true); boolean enabled =
				 * !customPaintingInProgress && !tutorialIsRunning;
				 * MenuBar.setRandomScrambleEnabled(enabled);
				 * MenuBar.clickToSolveItem.setEnabled(!tutorialIsRunning);
				 * MenuBar.paintCustomStateItem.setEnabled(!tutorialIsRunning);
				 * MenuBar.solveCubeItem.setEnabled(!tutorialIsRunning);
				 * timingDetailsStartNewSolveButton.setEnabled(true);
				 */
				MenuBar.clickToSolveItem.setSelected(false);
				MenuBar.clearStickersItem.setEnabled(customPaintingInProgress);
				movesToPerform.clear();
				movesAllowed = !customPaintingInProgress;
			}
		}
	}

	/**
	 * @author Kelsey McKenna This WindowListener is used for most windows so
	 *         that the all windows are updated in the appropriate manner.
	 */
	public static class PopUpWindowListener implements WindowListener {
		/**
		 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowActivated(WindowEvent arg0) {
			/*
			 * try { MouseListener[] m = cubePanel.getMouseListeners(); for (int
			 * i = 0; i < m.length; ++i)
			 * cubePanel.removeMouseListener(cubePanel.getMouseListeners()[i]);
			 * } catch (Exception e) { //All mouse listeners are removed }
			 */
		}

		/**
		 * Updates the scramble label and gives the cube panel back the
		 * appropriate MouseListeners
		 * 
		 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent arg0) {
			scrambleLabel.setFont(new Font("Courier New", 0, preferencesPopUp.getScrambleTextSize()));
			cubePanel.addMouseListener(getCubePanelMouseListener());
		}

		/**
		 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosing(WindowEvent arg0) {
		}

		/**
		 * Updates the times and statistics
		 * 
		 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowDeactivated(WindowEvent arg0) {
			cubePanel.addMouseListener(getCubePanelMouseListener());
			copyAllTimesToDisplay();
			refreshTimeList();
			refreshTimeGraph(false);
		}

		/**
		 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		/**
		 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		/**
		 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowOpened(WindowEvent arg0) {
			/*
			 * try { MouseListener[] m = cubePanel.getMouseListeners(); for (int
			 * i = 0; i < m.length; ++i)
			 * cubePanel.removeMouseListener(cubePanel.getMouseListeners()[i]);
			 * } catch (Exception e) { //All mouse listeners are removed }
			 */
		}
	}

	/**
	 * @author Kelsey McKenna
	 */
	private static class MenuBar {
		/**
		 * Stores the contents of the menu bar
		 */
		private static JMenuBar menuBar;
		/**
		 * Stores the contents of the file menu
		 */
		private static JMenu fileMenu;
		/**
		 * Stores the contents of the edit menu
		 */
		private static JMenu editMenu;
		/**
		 * Stores the contents of the tools menu
		 */
		private static JMenu toolMenu;
		/**
		 * Stores the contents of the view menu
		 */
		private static JMenu viewMenu;
		/**
		 * Stores the contents of the options menu
		 */
		private static JMenu optionsMenu;
		/**
		 * Stores the contents of the tutorial menu
		 */
		private static JMenu tutorialMenu;
		/**
		 * Stores the contents of the help menu
		 */
		private static JMenu helpMenu;

		// File
		/**
		 * Clicking this menu item opens a save dialog, allowing a location to
		 * be chosen to save a text file containing a state-string for the
		 * current state.
		 */
		private static JMenuItem saveCubeStateItem;
		/**
		 * Clicking this menu item opens a load dialog, allowing a location to
		 * be chosen to load a text file containing a state-string for the
		 * current state.
		 */
		private static JMenuItem loadCubeStateItem;
		/**
		 * Clicking this menu item opens a load dialog so that solve information
		 * can be loaded from a specified location.
		 */
		private static JMenuItem loadSolveInfoItem;
		/**
		 * Clicking this menu item opens a save dialog so that the statistics
		 * can be saved as a pdf to a specified location.
		 */
		private static JMenuItem saveStatsToFileItem;
		/**
		 * Clicking this menu item closes the system
		 */
		private static JMenuItem exitProgramItem;
		/**
		 * Clicking this menu item saves the times in the list, i.e. the
		 * session, in the main window to the database.
		 */
		private static JMenuItem saveSessionToDatabaseItem;

		// Edit
		/**
		 * Clicking this menu item opens the Solve Editor window
		 */
		private static JMenuItem manuallyEnterSolveInfoItem;
		/**
		 * Clicking this menu item opens the Solve Editor window with the
		 * selected solves information in its fields
		 */
		private static JMenuItem editSelectedSolveItem;
		/**
		 * Clicking this menu item deletes the selected solve from the list
		 */
		private static JMenuItem deleteSelectedSolveItem;

		// View
		/**
		 * Clicking this menu item shows the Time Graph window
		 */
		private static JMenuItem showTimeGraphItem;
		/**
		 * Clicking this menu item shows the Scramble List window
		 */
		private static JMenuItem showScrambleListItem;
		/**
		 * Clicking this menu item toggles whether the current scramble is shown
		 * at the top of the main window
		 */
		private static JCheckBoxMenuItem showScrambleItem;
		/**
		 * Clicking this menu item shows the Algorithm Table window
		 */
		private static JMenuItem showAlgorithmTableItem;
		/**
		 * Clicking this menu item shows the Solve Table window
		 */
		private static JMenuItem showSolveTableItem;
		/**
		 * Clicking this menu item shows the statistics at the bottom of the
		 * main window
		 */
		private static JMenuItem showStatisticsItem;
		/**
		 * Clicking this menu item shows the Competition Table window
		 */
		private static JMenuItem showCompetitionTableItem;
		/**
		 * Clicking this menu item shows the Member Table window
		 */
		private static JMenuItem showMemberTableItem;

		// Options
		/**
		 * Clicking this menu item opens the Preferences window
		 */
		private static JMenuItem preferencesItem;
		/**
		 * Clicking this menu item toggles whether or not the scrambles in the
		 * Scramble List window are used to scramble the cube for each solve
		 */
		private static JCheckBoxMenuItem useScramblesInListItem;

		// Tools
		/**
		 * Clicking this menu item cancels all timers, animations etc.
		 */
		private static JMenuItem cancelSolveItem;
		/**
		 * Clicking this menu item clears all colours on the cube so that all
		 * stickers show grey.
		 */
		private static JMenuItem clearStickersItem;
		/**
		 * Clicking this menu item allows a custom state to be painted on cube.
		 */
		private static JCheckBoxMenuItem paintCustomStateItem;
		/**
		 * Clicking this menu item generates a solution for the cube, displays
		 * this solution, and starts solving the cube in real time
		 */
		private static JMenuItem solveCubeItem;
		/**
		 * Clicking this menu item allows the user to click on a piece on the
		 * cube in order to generate a solution for that piece.
		 */
		private static JCheckBoxMenuItem clickToSolveItem;

		// Scramble
		/**
		 * Clicking this menu item generates a random scramble and applies it to
		 * the cube, showing the scramble in the main window
		 */
		private static JMenuItem applyRandomScrambleItem;

		// Tutorial
		/**
		 * Clicking this menu item opens the controls tutorial
		 */
		private static JMenuItem controlsTutorialItem;
		/**
		 * This menu holds the cross tutorials
		 */
		private static JMenu crossTutorialMenu;
		/**
		 * Clicking this menu item opens the first cross tutorial
		 */
		private static JMenuItem crossTutorialOneItem;
		/**
		 * Clicking this menu item opens the second cross tutorial tutorial
		 */
		private static JMenuItem crossTutorialTwoItem;
		/**
		 * This menu holds the corner tutorials
		 */
		private static JMenu cornerTutorialMenu;
		/**
		 * Clicking this menu item opens the corner tutorial
		 */
		private static JMenuItem cornerTutorialOneItem;
		/**
		 * This menu holds the edge tutorials
		 */
		private static JMenu edgeTutorialMenu;
		/**
		 * Clicking this menu item opens the edge tutorial
		 */
		private static JMenuItem edgeTutorialOneItem;
		/**
		 * This menu holds the orientation tutorials
		 */
		private static JMenu orientationTutorialMenu;
		/**
		 * Clicking this menu item opens the orientation tutorial
		 */
		private static JMenuItem orientationTutorialOneItem;
		/**
		 * This menu holds the permutation tutorials
		 */
		private static JMenu permutationTutorialMenu;
		/**
		 * Clicking this menu item opens the permutation tutorial
		 */
		private static JMenuItem permutationTutorialOneItem;
		/**
		 * Clicking this menu item opens a load dialog so that a tutorial can be
		 * loaded from the specified location
		 */
		private static JMenuItem loadTutorialFromFileItem;
		/**
		 * Clicking this menu item switches from tutorial mode to timing mode
		 */
		private static JMenuItem exitTutorialItem;

		// Help
		/**
		 * Clicking this menu item opens the Terminology.pdf
		 */
		private static JMenuItem terminologyItem;

		/**
		 * This variable is used to select a location to save or load text
		 * files.
		 */
		private JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop") {
			private static final long serialVersionUID = 1L;

			@Override
			public void approveSelection() {
				// String filePath = getSelectedFile() + ".txt";
				// filePath = filePath.substring(0, filePath.indexOf(".txt")) +
				// ".txt";
				setSelectedFile(new File((("" + getSelectedFile()).replaceAll("\\.txt", "")) + ".txt"));
				File f = new File(getSelectedFile().toString());

				if ((f.exists()) && (getDialogType() == SAVE_DIALOG)) {
					int result = JOptionPane.showConfirmDialog(this, String.format(
							"The file %s already exists. Do you want to overwrite?", getSelectedFile().toString()),
							"Existing File", JOptionPane.YES_NO_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						return;
					}
				}
				super.approveSelection();
			}
		};

		/**
		 * This variable is used to select a location to save or load pdf files
		 * (statistics)
		 */
		private JFileChooser pdfFileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop") {
			private static final long serialVersionUID = 1L;

			@Override
			public void approveSelection() {
				// String filePath = getSelectedFile() + ".txt";
				// filePath = filePath.substring(0, filePath.indexOf(".txt")) +
				// ".txt";
				setSelectedFile(new File((("" + getSelectedFile()).replaceAll("\\.pdf", "")) + ".pdf"));
				File f = new File(getSelectedFile().toString());

				if ((f.exists()) && (getDialogType() == SAVE_DIALOG)) {
					int result = JOptionPane.showConfirmDialog(this, String.format(
							"The file %s already exists. Do you want to overwrite?", getSelectedFile().toString()),
							"Existing File", JOptionPane.YES_NO_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						return;
					}
				}
				super.approveSelection();
			}
		};

		/**
		 * @return the menu bar for the main window
		 */
		public JMenuBar createMenuBar() {
			menuBar = new JMenuBar();
			ImageIcon icon;

			/******** FILE ***************/
			fileMenu = new JMenu("File");

			icon = createImageIcon("res/images/SaveIcon.png");
			saveCubeStateItem = new JMenuItem("Save Cube-State - txt", icon);
			saveCubeStateItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!isValidCubeState(true)) {
						cubePanel.repaint();
						JOptionPane.showMessageDialog(totalFrame, "This is not a valid state", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						String cubeStateString = solveMaster.getStateString();

						if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							TextFile currentFile = new TextFile();

							try {
								currentFile.setFilePath(fileChooser.getSelectedFile().toString());
								currentFile.setIO(TextFile.WRITE);
								currentFile.write(cubeStateString);
							} catch (Exception exc) {
								JOptionPane.showMessageDialog(totalFrame, "Could not save to file", "Error",
										JOptionPane.ERROR_MESSAGE);
							} finally {
								currentFile.close();
							}
						}
					}

					cubePanel.repaint();
				}
			});

			icon = createImageIcon("res/images/OpenIcon.png");
			loadCubeStateItem = new JMenuItem("Load Cube-State - txt", icon);
			loadCubeStateItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Check that nothing else is running

					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						TextFile currentFile = new TextFile();
						String cubeStateString;

						try {
							currentFile.setFilePath(fileChooser.getSelectedFile().toString());
							currentFile.setIO(TextFile.READ);
							cubeStateString = currentFile.readLine();

							try {
								solveMaster.applyStateString(cubeStateString);
							} catch (Exception exc) {
								JOptionPane.showMessageDialog(totalFrame, "Invalid File", "Error",
										JOptionPane.ERROR_MESSAGE);
							}

							if (!isValidCubeState(false)) {
								resetCube();
								JOptionPane.showMessageDialog(totalFrame, "Invalid Cube-State", "Error",
										JOptionPane.ERROR_MESSAGE);
							}
						} catch (Exception exc) {
							JOptionPane.showMessageDialog(totalFrame, "Could not open file", "Error",
									JOptionPane.ERROR_MESSAGE);
						} finally {
							currentFile.close();
						}

						cubePanel.repaint();
						cubePanel.requestFocus();
					}
				}
			});
			loadCubeStateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));

			icon = createImageIcon("res/images/OpenIcon.png");
			loadSolveInfoItem = new JMenuItem("Load Solve Information - txt", icon);
			loadSolveInfoItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelSolve();
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						TextFile currentFile = new TextFile();
						String[] data;
						int numLines;

						try {
							currentFile.setFilePath(fileChooser.getSelectedFile().toString());
							currentFile.setIO(TextFile.READ);
							numLines = currentFile.getNumLines();

							if (numLines != 5) {
								throw new Exception();
							}

							data = new String[numLines];

							for (int i = 0; i < numLines; ++i)
								data[i] = currentFile.readLine();

							currentFile.close();

							solves.add(new Solve(data[0], data[1], data[2], data[3], data[4]));
							copyAllTimesToDisplay();
							listHolder.setSelectedIndex(solves.size() - 1);
						} catch (Exception exc) {
							JOptionPane.showMessageDialog(totalFrame, "Invalid File", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}

					refreshStatistics();
				}
			});

			icon = createImageIcon("res/images/SaveIcon.png");
			saveStatsToFileItem = new JMenuItem("Save Statistics - pdf", icon);
			saveStatsToFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
			saveStatsToFileItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (pdfFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						System.out.println(solves.size());
						CustomPdfWriter.createStatisticsPdf(pdfFileChooser.getSelectedFile().toString(),
								Statistics.getFormattedStatisticsArray(solves));
					}
				}
			});

			icon = createImageIcon("res/images/SaveIcon.png");
			saveSessionToDatabaseItem = new JMenuItem("Save Session to Database", icon);
			saveSessionToDatabaseItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String dateAdded = SolveDatabaseConnection.getCurrentTimeInSQLFormat();
					Solve currentSolve;

					totalFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					try {
						int size = solves.size();
						for (int i = 0; i < size; ++i) {
							currentSolve = solves.get(i);

							SolveDatabaseConnection.executeUpdate(String.format(
									"INSERT INTO solve(solveTime, penalty, comment, scramble, solution, dateAdded) "
											+ "VALUES(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")",
									currentSolve.getStringTime(), currentSolve.getPenalty(), currentSolve.getComment(),
									currentSolve.getScramble(), currentSolve.getSolution(), dateAdded));
						}

						solveDatabasePopUp.populateCellsWithDatabaseData();
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(totalFrame, "Could not access database", "Error",
								JOptionPane.ERROR_MESSAGE);
					}

					totalFrame.setCursor(Cursor.getDefaultCursor());
				}
			});

			exitProgramItem = new JMenuItem("Exit");
			exitProgramItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});

			fileMenu.add(saveCubeStateItem);
			fileMenu.add(saveStatsToFileItem);
			fileMenu.add(saveSessionToDatabaseItem);
			fileMenu.add(loadCubeStateItem);
			fileMenu.add(loadSolveInfoItem);
			fileMenu.addSeparator();
			fileMenu.add(exitProgramItem);
			/********* END FILE **************/

			/********** EDIT *************/
			editMenu = new JMenu("Edit");

			manuallyEnterSolveInfoItem = new JMenuItem("Add Solve");
			manuallyEnterSolveInfoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
			manuallyEnterSolveInfoItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					solves.add(new Solve("DNF", "0", "*"));
					copyAllTimesToDisplay();
					listHolder.setSelectedIndex(solves.size() - 1);
					timeListPopUp.setSolve(solves.get(listHolder.getSelectedIndex()));
					timeListPopUp.selectAllTimeText();
					timeListPopUp.setVisible(true);
				}
			});

			editSelectedSolveItem = new JMenuItem("Edit Selected Solve");
			editSelectedSolveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
			editSelectedSolveItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					int selectedIndex = listHolder.getSelectedIndex();

					if (selectedIndex == -1) {
						JOptionPane.showMessageDialog(totalFrame, "No Solve Selected", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					timeListPopUp.setSolve(solves.get(selectedIndex));
					timeListPopUp.selectAllTimeText();
					timeListPopUp.setVisible(true);
				}

			});

			deleteSelectedSolveItem = new JMenuItem("Delete Selected Solve");
			deleteSelectedSolveItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int selectedIndex = listHolder.getSelectedIndex();

					if (selectedIndex == -1) {
						JOptionPane.showMessageDialog(totalFrame, "No Solve Selected", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					solves.get(selectedIndex).setStringTime("-1");
					refreshTimeList();
					refreshStatistics();
					copyAllTimesToDisplay();
					refreshTimeGraph(true);

					if (solves.size() > 0)
						listHolder.setSelectedIndex((selectedIndex < solves.size()) ? selectedIndex : selectedIndex - 1);

					listHolder.requestFocus();
				}
			});

			editMenu.add(manuallyEnterSolveInfoItem);
			editMenu.add(editSelectedSolveItem);
			editMenu.add(deleteSelectedSolveItem);
			/********** END EDIT *************/

			/********** VIEW *************/
			viewMenu = new JMenu("View");

			showTimeGraphItem = new JMenuItem("Show Time Graph");
			showTimeGraphItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					timeGraph.setDataset(getDatasetOfSelectedTimes());
					timeGraph.draw();
					timeGraph.setVisible(true);
				}
			});

			showScrambleItem = new JCheckBoxMenuItem("Show Current Scramble");
			showScrambleItem.setSelected(true);
			showScrambleItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					scrambleLabel.setVisible(showScrambleItem.isSelected());
				}
			});

			showScrambleListItem = new JMenuItem("Show Scramble List");
			showScrambleListItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					scramblePopUp.setVisible(true);
				}
			});

			showAlgorithmTableItem = new JMenuItem("Show Algorithm Table");
			showAlgorithmTableItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					algorithmDatabasePopUp.setVisible(true);
				}
			});

			showSolveTableItem = new JMenuItem("Show Solve Table");
			showSolveTableItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					solveDatabasePopUp.setVisible(true);
				}
			});

			showStatisticsItem = new JMenuItem("Show Statistics");
			showStatisticsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					refreshStatistics();
				}
			});

			showCompetitionTableItem = new JMenuItem("Show Competition Table");
			showCompetitionTableItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					competitionDatabasePopUp.setVisible(true);
				}
			});

			showMemberTableItem = new JMenuItem("Show Member Table");
			showMemberTableItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					memberDatabasePopUp.setVisible(true);
				}
			});

			viewMenu.add(showTimeGraphItem);
			viewMenu.add(showScrambleItem);
			viewMenu.add(showScrambleListItem);
			viewMenu.add(showAlgorithmTableItem);
			viewMenu.add(showSolveTableItem);
			viewMenu.add(showStatisticsItem);
			viewMenu.addSeparator();
			viewMenu.add(showCompetitionTableItem);
			viewMenu.add(showMemberTableItem);
			/********** END VIEW *************/

			/********** TOOLS *************/
			toolMenu = new JMenu("Tools");

			cancelSolveItem = new JMenuItem("Cancel Solve");
			cancelSolveItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Main.cancelSolve();
				}

			});
			cancelSolveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));

			paintCustomStateItem = new JCheckBoxMenuItem("Paint Custom State");
			paintCustomStateItem.setSelected(false);
			paintCustomStateItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean isSelected = paintCustomStateItem.isSelected();

					if ((tutorialIsRunning) || ((inspectionTimer != null)) || (incTimer != null)
							|| (realTimeSolutionTimer != null) || (clickToSolve)) {
						cubePanel.requestFocus();
						paintCustomStateItem.setSelected(false);
						colorSelection.setVisible(false);
						return;
					}
					if (!isSelected && !isValidCubeState(true)) {
						int choice = MouseSelectionSolver
								.getQuestionDialogResponse("This is not a valid state. Would you like to reset the cube?");
						if (choice == 0)
							resetCube();
						else {
							paintCustomStateItem.setSelected(true);
							colorSelection.setVisible(true);
							cubePanel.repaint();
							cubePanel.requestFocus();
							return;
						}
					}
					customPaintingInProgress = !customPaintingInProgress;
					timerHasPermissionToStart = !timerHasPermissionToStart;
					movesAllowed = !isSelected;
					if ((realTimeSolutionTimer != null) && (realTimeSolutionTimer.isRunning()))
						movesAllowed = false;

					colorSelection.setVisible(isSelected);
					/*
					 * applyRandomScrambleItem.setEnabled(!isSelected);
					 * timingDetailsStartNewSolveButton.setEnabled(!isSelected
					 * && (!clickToSolveItem.isSelected()));
					 */
					clearStickersItem.setEnabled(customPaintingInProgress);
					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			clearStickersItem = new JMenuItem("Clear Stickers");
			clearStickersItem.setEnabled(false);
			clearStickersItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (customPaintingInProgress && (realTimeSolutionTimer == null)) {
						for (int i = 0; i < 8; ++i)
							cube.getCorner(i).setStickers(Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
						for (int i = 0; i < 12; ++i)
							cube.getEdge(i).setStickers(Color.LIGHT_GRAY, Color.LIGHT_GRAY);
					}

					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			solveCubeItem = new JMenuItem("Solve Cube");
			solveCubeItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (customPaintingInProgress) {
						solveCubeItem.setSelected(false);
						return;
					}

					cancelSolve();
					clickToSolveItem.setSelected(false);
					clickToSolve = false;

					if (!isValidCubeState(true)) {
						cubePanel.repaint();
						// colorSelection.setAlwaysOnTop(false);
						JOptionPane.showMessageDialog(colorSelection,
								"Some of the stickers are incorrect. Please try again.", "Error",
								JOptionPane.ERROR_MESSAGE);
						// colorSelection.setAlwaysOnTop(true);

						cubePanel.requestFocus();
					} else {
						/*
						 * applyRandomScrambleItem.setEnabled(false);
						 * clickToSolveItem.setEnabled(false);
						 * clearStickersItem.setEnabled(false);
						 * paintCustomStateItem.setEnabled(false);
						 * solveCubeItem.setEnabled(false);
						 */
						timingDetailsTextArea.setText(getFormattedCubeSolution());
						timingDetailsTextArea.setCaretPosition(0);
						performRealTimeSolving();
					}

					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});
			solveCubeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK)); // delete
																									// for
																									// final
																									// program

			clickToSolveItem = new JCheckBoxMenuItem("Solve Piece");
			clickToSolveItem.setSelected(false);
			clickToSolveItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if ((customPaintingInProgress) || (realTimeSolutionTimer != null) || (inspectionTimer != null)
							|| (incTimer != null)) {
						clickToSolveItem.setSelected(false);
						return;
					}

					clickToSolve = clickToSolveItem.isSelected();

					if (clickToSolveItem.isSelected()) {
						cancelSolve();

						if (!isValidCubeState(true)) {
							cubePanel.repaint();
							// colorSelection.setAlwaysOnTop(false);
							JOptionPane.showMessageDialog(colorSelection,
									"Some of the stickers are incorrect. Please try again.", "Error",
									JOptionPane.ERROR_MESSAGE);
							// colorSelection.setAlwaysOnTop(true);
							cubePanel.requestFocus();
							clickToSolveItem.setSelected(false);
							clickToSolve = false;
							return;
						}

						if (preferencesPopUp.solvePieceWarningEnabled()) {
							// colorSelection.setAlwaysOnTop(false);
							JOptionPane.showMessageDialog(colorSelection, "Click on a piece to view solution",
									"Click to Solve", JOptionPane.INFORMATION_MESSAGE);
							// colorSelection.setAlwaysOnTop(true);
						}
						// timingDetailsStartNewSolveButton.setEnabled(false);
					} else {
						// timingDetailsStartNewSolveButton.setEnabled(!paintCustomStateItem.isSelected());
					}
				}
			});

			applyRandomScrambleItem = new JMenuItem("Apply Random Scramble");
			applyRandomScrambleItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if ((inspectionTimer == null) && (realTimeSolutionTimer == null) && (incTimer == null)
							&& (!customPaintingInProgress)) {
						currentScramble = Scramble.generateScramble();
						cube.performAbsoluteMoves(currentScramble);
						scrambleLabel.setText("Scramble: " + currentScramble);
						cubePanel.repaint();
						cubePanel.requestFocus();
					}
				}
			});

			toolMenu.add(cancelSolveItem);
			toolMenu.add(clearStickersItem);
			toolMenu.add(paintCustomStateItem);
			toolMenu.add(solveCubeItem);
			toolMenu.add(clickToSolveItem);
			toolMenu.add(applyRandomScrambleItem);
			/********** END TOOLS *************/

			/********** OPTIONS *************/
			optionsMenu = new JMenu("Options");

			preferencesItem = new JMenuItem("Preferences");
			preferencesItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// preferencesPopUp.populateFieldsWithValues();
					preferencesPopUp.setVisible(true);
				}
			});

			useScramblesInListItem = new JCheckBoxMenuItem("Use Scrambles in List");
			useScramblesInListItem.setSelected(false);

			optionsMenu.add(preferencesItem);
			optionsMenu.add(useScramblesInListItem);
			/********** END OPTIONS *************/

			/********** TUTORIAL *************/
			tutorialMenu = new JMenu("Tutorial");
			crossTutorialMenu = new JMenu("Cross");
			cornerTutorialMenu = new JMenu("Corners");
			edgeTutorialMenu = new JMenu("Edges");
			orientationTutorialMenu = new JMenu("Orientation");
			permutationTutorialMenu = new JMenu("Permutation");

			controlsTutorialItem = new JMenuItem("Controls");
			controlsTutorialItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switchToTutorialView();
					try {
						tutorial.loadTutorial("res/Controls.txt");
					} catch (Exception exc) {
						JOptionPane
								.showMessageDialog(null, "Invalid Tutorial File", "Error", JOptionPane.ERROR_MESSAGE);
						switchToTimingView();
					}

					setUpTutorial();
					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			crossTutorialOneItem = new JMenuItem("Introduction");
			crossTutorialOneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchToTutorialView();
					try {
						tutorial.loadTutorial("res/CrossIntroduction.txt");
					} catch (Exception exc) {
						JOptionPane
								.showMessageDialog(null, "Invalid Tutorial File", "Error", JOptionPane.ERROR_MESSAGE);
						switchToTimingView();
					}

					setUpTutorial();
					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			crossTutorialTwoItem = new JMenuItem("Tutorial Two");
			crossTutorialTwoItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchToTutorialView();
					try {
						tutorial.loadTutorial("res/CrossTutorial.txt");
					} catch (Exception e1) {
						JOptionPane
								.showMessageDialog(null, "Invalid Tutorial File", "Error", JOptionPane.ERROR_MESSAGE);
						switchToTimingView();
					}
					setUpTutorial();
					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			crossTutorialMenu.add(crossTutorialOneItem);
			crossTutorialMenu.add(crossTutorialTwoItem);

			cornerTutorialOneItem = new JMenuItem("Introduction");
			cornerTutorialOneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					switchToTutorialView();
					try {
						tutorial.loadTutorial("res/CornersIntroduction.txt");
					} catch (Exception exc) {
						JOptionPane
								.showMessageDialog(null, "Invalid Tutorial File", "Error", JOptionPane.ERROR_MESSAGE);
						switchToTimingView();
					}

					setUpTutorial();
					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			cornerTutorialMenu.add(cornerTutorialOneItem);

			edgeTutorialOneItem = new JMenuItem("Introduction");
			edgeTutorialOneItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switchToTutorialView();
					try {
						tutorial.loadTutorial("res/EdgesIntroduction.txt");
					} catch (Exception exc) {
						JOptionPane
								.showMessageDialog(null, "Invalid Tutorial File", "Error", JOptionPane.ERROR_MESSAGE);
						switchToTimingView();
					}

					setUpTutorial();
					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			edgeTutorialMenu.add(edgeTutorialOneItem);

			orientationTutorialOneItem = new JMenuItem("Introduction");
			orientationTutorialOneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					switchToTutorialView();
					try {
						tutorial.loadTutorial("res/OrientationIntroduction.txt");
						setUpTutorial();
					} catch (Exception exc) {
						JOptionPane
								.showMessageDialog(null, "Invalid Tutorial File", "Error", JOptionPane.ERROR_MESSAGE);
						switchToTimingView();
					}

					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			orientationTutorialMenu.add(orientationTutorialOneItem);

			permutationTutorialOneItem = new JMenuItem("Introduction");
			permutationTutorialOneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					switchToTutorialView();
					try {
						tutorial.loadTutorial("res/PermutationIntroduction.txt");
					} catch (Exception exc) {
						JOptionPane
								.showMessageDialog(null, "Invalid Tutorial File", "Error", JOptionPane.ERROR_MESSAGE);
						switchToTimingView();
					}

					setUpTutorial();
					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			});

			permutationTutorialMenu.add(permutationTutorialOneItem);

			exitTutorialItem = new JMenuItem("Exit Tutorial");
			exitTutorialItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchToTimingView();
				}
			});

			loadTutorialFromFileItem = new JMenuItem("Load Tutorial from File - txt");
			loadTutorialFromFileItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						TextFile currentFile = new TextFile();
						try {
							switchToTutorialView();
							currentFile.setFilePath(fileChooser.getSelectedFile().toString());
							currentFile.setIO(TextFile.READ);

							tutorial.loadTutorial(fileChooser.getSelectedFile().toString());
							setUpTutorial();
						} catch (Exception exc) {
							JOptionPane.showMessageDialog(totalFrame, "Invalid File", "Error",
									JOptionPane.ERROR_MESSAGE);
							switchToTimingView();
						} finally {
							currentFile.close();
						}

					}
				}
			});

			tutorialMenu.add(controlsTutorialItem);
			tutorialMenu.add(crossTutorialMenu);
			tutorialMenu.add(cornerTutorialMenu);
			tutorialMenu.add(edgeTutorialMenu);
			tutorialMenu.add(orientationTutorialMenu);
			tutorialMenu.add(permutationTutorialMenu);
			tutorialMenu.add(loadTutorialFromFileItem);
			tutorialMenu.addSeparator();
			tutorialMenu.add(exitTutorialItem);
			/********** END TUTORIAL *************/

			/********** HELP *************/
			helpMenu = new JMenu("Help");

			terminologyItem = new JMenuItem("Terminology");
			terminologyItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (Desktop.isDesktopSupported()) {
						try {
							File currentFile = new File("res/Terminology.pdf");
							Desktop.getDesktop().open(currentFile);
						} catch (IOException e) {
							JOptionPane.showMessageDialog(totalFrame, "No application installed to open PDFs", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});

			helpMenu.add(terminologyItem);
			/********** END HELP *************/

			menuBar.add(fileMenu);
			menuBar.add(editMenu);
			menuBar.add(viewMenu);
			menuBar.add(optionsMenu);
			menuBar.add(toolMenu);
			menuBar.add(tutorialMenu);
			menuBar.add(helpMenu);

			return menuBar;
		}

		/**
		 * Switches the main window from timing mode to tutorial mode. The text
		 * area the bottom of the window displays the correct information.
		 */
		private void setUpTutorial() {
			resetCube();
			solveMaster.rotateToTopFront(Color.white, Color.green);
			solveMaster.clearMoves();
			tutorialIsRunning = true;
			tutorialTextArea.setText(tutorial.getDescription());
			tutorialTextArea.setCaretPosition(0);
			cube.performAbsoluteMoves(tutorial.getScramble());
			movesAllowed = tutorial.requiresUserAction();
			tutorialBackButton.setEnabled(!tutorial.isFirstSubTutorial());
			tutorialNextButton.setEnabled(!tutorial.isLastSubTutorial());
			tutorialHintButton.setEnabled(tutorial.requiresUserSolution());
			tutorialShowSolutionButton.setEnabled(tutorial.requiresUserSolution());
		}

		/**
		 * Switches the main window from timing mode to tutorial mode. The
		 * buttons at the bottom of the window are changed and the appropriate
		 * fields are changed. Menu items are enabled and disabled accordingly
		 */
		private void switchToTutorialView() {
			timingDetailsComponentsPanel.setVisible(false);
			tutorialComponentsPanel.setVisible(true);
			tutorialIsRunning = true;
			timeToBeRecorded = false;
			cubeSolved = false;
			timerHasPermissionToStart = false;
			movesToBeRecorded = false;
			clickToSolve = false;
			customPaintingInProgress = false;
			customTimerRunning = false;
			movesAllowed = false;

			trackingMoves.clear();

			solveCubeItem.setEnabled(false);
			clickToSolveItem.setEnabled(false);
			clearStickersItem.setEnabled(false);
			paintCustomStateItem.setEnabled(false);
			paintCustomStateItem.setSelected(false);
			colorSelection.setVisible(false);
			cancelSolveItem.setEnabled(false);
			loadCubeStateItem.setEnabled(false);
			applyRandomScrambleItem.setEnabled(false);
			showStatisticsItem.setEnabled(false);

			scrambleLabel.setText("Tutorial");

			cancelSolve();
			cubePanel.repaint();
		}

		/**
		 * Switches the main window from tutorial mode to timing mode. The
		 * buttons at the bottom of the window are changed and the appropriate
		 * fields are changed. Menu items are enabled and disabled accordingly
		 */
		private static void switchToTimingView() {
			timingDetailsComponentsPanel.setVisible(true);
			tutorialComponentsPanel.setVisible(false);
			tutorialIsRunning = false;
			timeToBeRecorded = false;
			cubeSolved = false;
			timerHasPermissionToStart = true;
			movesToBeRecorded = false;
			clickToSolve = false;
			customPaintingInProgress = false;
			customTimerRunning = false;
			movesAllowed = true;

			trackingMoves.clear();

			solveCubeItem.setEnabled(true);
			clickToSolveItem.setEnabled(true);
			paintCustomStateItem.setEnabled(true);
			cancelSolveItem.setEnabled(true);
			loadCubeStateItem.setEnabled(true);
			applyRandomScrambleItem.setEnabled(true);
			showStatisticsItem.setEnabled(true);
			paintCustomStateItem.setEnabled(true);
			timingDetailsStartNewSolveButton.setEnabled(true);
			scrambleLabel.setText("Scramble: ");
			cubePanel.repaint();
		}

		/**
		 * Determines whether <code>clickToSolveItem</code> is enabled or not
		 * 
		 * @param state
		 *            the resulting state for the <code>clickToSolve</code> item
		 */
		public static void setSolvePieceSelected(boolean state) {
			clickToSolveItem.setSelected(state);
			clickToSolve = state;
		}

		/**
		 * Determines whether <code>applyRandomScrambleItem</code> is enabled or
		 * not
		 * 
		 * @param state
		 *            the resulting state for the
		 *            <code>applyRandomScramble</code> item
		 */
		public static void setRandomScrambleEnabled(boolean state) {
			applyRandomScrambleItem.setEnabled(state);
		}

		/**
		 * @return <b>true</b> if <code>useScramblesInListItem</code> is
		 *         selected; <br>
		 *         <b>false</b> otherwise
		 */
		public static boolean isUsingScramblesInList() {
			return useScramblesInListItem.isSelected();
		}

	}

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This stores the thickness of the strokes used to paint the visual cube
	 */
	final static float strokeThickness = 2.0f;
	/**
	 * This stroke is used to paint the lines on the visual cube
	 */
	final static BasicStroke stroke = new BasicStroke(strokeThickness);

	/**
	 * The cube shown in the main window
	 */
	private static Cube cube;
	/**
	 * This variable allows useful methods to be accessed to operate on the cube
	 */
	private static SolveMaster solveMaster;
	/**
	 * This allows a solution for the cross to be generated
	 */
	private static CrossSolver crossSolver;
	/**
	 * This allows a solution for the corners to be generated
	 */
	private static CornerSolver cornerSolver;
	/**
	 * This allows a solution for the edges to be generated
	 */
	private static EdgeSolver edgeSolver;
	/**
	 * This allows a solution for the orientation to be generated
	 */
	private static OrientationSolver orientationSolver;
	/**
	 * This allows a solution for the permutation to be generated
	 */
	private static PermutationSolver permutationSolver;
	/**
	 * This is used to load tutorials and access their features
	 */
	private static Tutorial tutorial;
	/**
	 * This is used to solve a piece that is clicked when in Click-to-Solve mode
	 */
	private static MouseSelectionSolver selectionSolver;
	/**
	 * This represents the Color Selection window
	 */
	private static ColorSelection colorSelection;
	/**
	 * This represents the Preferences window
	 */
	private static Preferences preferencesPopUp;
	/**
	 * This represents the Scramble List window
	 */
	private static ScramblePopUp scramblePopUp;
	/**
	 * This represents the Algorithm Table window
	 */
	private static AlgorithmDatabasePopUp algorithmDatabasePopUp;
	/**
	 * This represents the Solve Table window
	 */
	private static SolveDatabasePopUp solveDatabasePopUp;
	/**
	 * This represents the Competition Table window
	 */
	private static CompetitionDatabasePopUp competitionDatabasePopUp;
	/**
	 * This represents the Member Table window
	 */
	private static MemberDatabasePopUp memberDatabasePopUp;

	/**
	 * This array stores the facelet/sticker colours for each sticker on the
	 * cube. The first dimension stores the stickers for the top face,
	 */
	private static Color[][][] faceletColors = new Color[3][3][3];
	/**
	 * This specifies the order in which the edges should be painted when using
	 * the for loops later
	 */
	private static int[] edgePaintOrder = { 0, 3, 1, 2 };
	/**
	 * This specifies the order in which the corners should be painted when
	 * using the for loops later
	 */
	private static int[] cornerPaintOrder = { 0, 1, 3, 2 };
	/**
	 * This stores the number of times that should be graphed
	 */
	private static int numTimesToGraph = 12;
	/**
	 * This is used as the display-timer used to record times for solves
	 */
	private static Timer incTimer;
	/**
	 * This is used as the count-down timer for inspection
	 */
	private static Timer inspectionTimer;
	/**
	 * This is used to regulate the speed of animations (real-time solving etc.)
	 */
	private static Timer realTimeSolutionTimer;
	/**
	 * Stores the number of elapsed seconds while timing
	 */
	private static float elapsedTimingSeconds = 0.00F;
	/**
	 * Stores the number of elapsed minutes while timing
	 */
	private static int elapsedMinutes = 0;
	/**
	 * Stores the number of seconds remaining during inspection time
	 */
	private static int inspectionTimeRemaining;

	/**
	 * This indicates whether the system is in tutorial mode or not
	 */
	private static boolean tutorialIsRunning = false;
	/**
	 * This indicates whether the time/solve should be recorded when the timer
	 * stops
	 */
	private static boolean timeToBeRecorded;
	/**
	 * Indicates whether or not the user is allowed to perform moves.
	 */
	private static boolean movesAllowed = true;
	/**
	 * Indicates whether or not the cube is solved
	 */
	private static boolean cubeSolved = false;
	/**
	 * Indicates whether or not <code>incTimer</code> has permission to start
	 */
	private static boolean timerHasPermissionToStart = false;
	/**
	 * Indicates whether or not the moves being performed by the user should be
	 * recorded
	 */
	private static boolean movesToBeRecorded = false;
	/**
	 * Indicates whether or not the user can click to solve a piece
	 */
	private static boolean clickToSolve = false;
	/**
	 * Indicates whether or not the user can paint a custom state on the cube
	 */
	private static boolean customPaintingInProgress = false;
	/**
	 * Indicates whether or not incTimer is running for a time that is being
	 * performed by the user manually (i.e. the 'Start New Solve' button was not
	 * clicked; spacebar was pressed to start)
	 */
	private static boolean customTimerRunning = false;

	/**
	 * This specifies how many seconds to wait before rendering the next time on
	 * the display.
	 */
	private static float timingDisplayInterval = 0.053F;
	/**
	 * This can store moves performed by the user in different situations
	 */
	private static LinkedList<String> trackingMoves = new LinkedList<>();
	/**
	 * This stores the data for the solves listed at the right-hand side of the
	 * main window
	 */
	private static LinkedList<Solve> solves = new LinkedList<>();
	/**
	 * Stores the last scramble used to store the cube
	 */
	private static String currentScramble;

	/**
	 * Stores the penalty to apply to the current solve
	 */
	private static String currentPenalty = "0";

	/**
	 * Clicking this button resets the cube to the solve state white on top and
	 * green on front
	 */
	private static JButton resetCubeButton;
	/**
	 * This can be used to calculate statistics for the current session
	 */
	private static Statistics statistics;
	/**
	 * This represents the Solve Editor window
	 */
	private static TimeListPopUp timeListPopUp;
	/**
	 * This represents the Time Graph window
	 */
	private static TimeGraph timeGraph;

	/**
	 * This label shows the current time of <code>incTimer</code> or
	 * <code>inspectionTimer</code> at the top-left of the window
	 */
	private static JLabel timeLabel;
	/**
	 * This label shows the current scramble being used
	 */
	private static JTextArea scrambleLabel;
	/**
	 * This panel stores the components at the top of the main window
	 */
	private static JPanel topComponentsPanel;

	/**
	 * This list can be placed in the panel at the right-hand side of the screen
	 * so that the list can be displayed
	 */
	private static JList<String> listHolder;
	/**
	 * This list stores the contents of the displayed elements in the time list
	 * at the right-hand side of the screen
	 */
	private static DefaultListModel<String> timeDisplayList;

	/**
	 * This stores the listed times
	 */
	private static JPanel timeListPanel;
	/**
	 * This panel holds the painted components of the main window (such as
	 * <code>cubePanel</code>)
	 */
	private static JPanel totalPaintingPanel;
	/**
	 * Stores the components unique to timing mode
	 */
	private static JPanel timingDetailsComponentsPanel;
	/**
	 * Stores the buttons unique to timing mode
	 */
	private static JPanel timingDetailsButtonPanel;
	/**
	 * Stores the components at the bottom of the main window
	 */
	private static JPanel bottomPanel;
	/**
	 * The total frame/window to be shown
	 */
	private static JFrame totalFrame;
	/**
	 * Stores the buttons unique to tutorial mode
	 */
	private static JPanel tutorialButtonsPanel;
	/**
	 * Stores the components unique to tutorial mode
	 */
	private static JPanel tutorialComponentsPanel;

	/**
	 * The text for sub-tutorials are shown in this text area
	 */
	private static JTextArea tutorialTextArea;
	/**
	 * The text used in timing mode, such as statistics or solutions to
	 * cube-states, is shown in this text area.
	 */
	private static JTextArea timingDetailsTextArea;

	/**
	 * This scroll pane allows all contents of the time-list to be viewed when
	 * its size exceeds the size of the panel in which it is placed
	 */
	private static JScrollPane timeListScrollPane;
	/**
	 * This scroll pane allows all text in the tutorial text area to be viewed
	 * when the length of the text exceeds the size of the text area
	 */
	private static JScrollPane tutorialScrollPane;
	/**
	 * This scroll pane allows all text in the timing-details text area to be
	 * viewed when the length of the text exceeds the size of the text area
	 */
	private static JScrollPane timingDetailsScrollPane;

	/**
	 * Clicking this button clears all times in the list at the right-hand side
	 * of the main window
	 */
	private static JButton timingDetailsResetSessionButton;
	/**
	 * Clicking this button starts a new solve
	 */
	private static JButton timingDetailsStartNewSolveButton;
	/**
	 * Clicking this button resets the cube to the solved state with white on
	 * top and green on front
	 */
	private static JButton tutorialResetStateButton;
	/**
	 * Clicking this button loads the next sub-tutorial
	 */
	private static JButton tutorialNextButton;
	/**
	 * Clicking this button loads the previous sub-tutorial
	 */
	private static JButton tutorialBackButton;
	/**
	 * Clicking this button loads the next hint in the sub-tutorial
	 */
	private static JButton tutorialHintButton;
	/**
	 * Clicking this button shows the description for the current sub-tutorial
	 */
	private static JButton tutorialShowDescriptionButton;
	/**
	 * Clicking this button shows the solution for the current sub-tutorial
	 */
	private static JButton tutorialShowSolutionButton;

	/**
	 * The visual cube is stored in this panel
	 */
	final static Main cubePanel = new Main();

	/**
	 * Constructor - initialises the fields
	 */
	public Main() {
		super(new BorderLayout());
		cube = new Cube();
		solveMaster = new SolveMaster(cube);
		crossSolver = new CrossSolver(cube);
		cornerSolver = new CornerSolver(cube);
		edgeSolver = new EdgeSolver(cube);
		orientationSolver = new OrientationSolver(cube);
		permutationSolver = new PermutationSolver(cube);
		tutorial = new Tutorial(cube);
		statistics = new Statistics(solves);
		selectionSolver = new MouseSelectionSolver(cube, crossSolver, cornerSolver, edgeSolver, orientationSolver,
				permutationSolver);

		addKeyListener(this);
	}

	/**
	 * This is used so that pressing tab transfers focus correctly in forms.
	 * This is needed because JTextAreas treat tab as a character rather than a
	 * traversal key.
	 * 
	 * @param arg0
	 *            The key event triggered by the key press
	 */
	public static void transferFormFocus(KeyEvent arg0) {
		Object source = arg0.getSource();

		if (arg0.getKeyCode() == KeyEvent.VK_TAB) {
			if (arg0.isShiftDown()) {
				((Component) source).transferFocusBackward();
			} else {
				((Component) source).transferFocus();
			}
			arg0.consume();
		}
	}

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 * 
	 * @param path
	 *            the path of the icon
	 * @return the ImageIcon to be used
	 */
	public static ImageIcon createImageIcon(String path) {
		try {
			return new ImageIcon(path);
		} catch (Exception e) {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Returns an Image, or null if the path was invalid.
	 * 
	 * @param path
	 *            the path of the icon
	 * @return the Image to be used
	 */
	public static Image createImage(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (Exception e) {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Resizes the widths of the columns in the specified table so that there is
	 * maximum visibility in each column
	 * 
	 * @param table
	 *            the table whose columns need resized
	 */
	public static void resizeColumnWidths(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 50; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width, width);
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	/**
	 * Returns the dataset to be used by {@link #timeGraph}
	 * 
	 * @return the dataset of the past {@link #numTimesToGraph} times
	 */
	private static DefaultCategoryDataset getDatasetOfSelectedTimes() {
		// Stores the data contents of the graph
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		// Stores the index of the first element in 'solves' to be graphed.
		int start = solves.size() - numTimesToGraph;
		// Stores the number of DNF times in the past 'numTimesToGraph' times.
		int numDNF = Statistics.getNumDNF(numTimesToGraph, solves);
		int end = solves.size();
		// Stores the numeric representation of the current time be examined.
		double current;
		// Accumulates the number of times graphed.
		int numDataValues = 0;
		// Stores the index of a valid time in case there is only one valid time
		// to be graphed.
		int indexOfValidTime = 0;

		/*
		 * DNF times are not graphed, so this gets a start index that will allow
		 * the maximum numbers of times (<= numTimesToGraph) to be graphed.
		 */
		if (start > 0) {
			/*
			 * The start index must be greater than or equal to zero, so exit
			 * when 'start' will become -1. When numDNF = 0, then this block of
			 * code has completed its purpose.
			 */
			while ((start > 0) && (numDNF > 0)) {
				--start;
				if (solves.get(start).getNumericTime() != -1)
					--numDNF;
			}
		}
		/*
		 * This will execute when the number of solves is less than the number
		 * of times to graph, but if this is the case, then start must be reset
		 * to 0. There will be fewer than numTimesToGraph times graphed.
		 */
		else
			start = 0;

		for (int i = start; i < end; ++i) {
			current = solves.get(i).getNumericTime();

			if (current != -1) {
				dataset.addValue(solves.get(i).getNumericTime(), "times", "" + Integer.toString(i + 1));
				++numDataValues;
				indexOfValidTime = i;
			}
		}

		/*
		 * If the number of dataset values added is 1, then the graph will
		 * consist of an invisible point. This if statement adds an identical
		 * value to the graph so that instead of a point, it shows a straight
		 * line.
		 */
		if (numDataValues == 1)
			dataset.addValue(solves.get(indexOfValidTime).getNumericTime(), "times", "");

		return dataset;
	}

	/**
	 * Determines whether or not the cube is in a valid state
	 * 
	 * @param clearStickers
	 *            if <b>true</b>, then when an invalid piece is found, its
	 *            stickers will be cleared, i.e. turn grey; <br>
	 *            if <b>false</b> then the stickers will remain as they are
	 * @return <b>true</b> if the cube is in a valid state; <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isValidCubeState(boolean clearStickers) {
		// Stores all valid corner sticker options.
		Color[][] validCornerStickers = Corner.getAllInitialStickers();
		// Stores all valid edge sticker options.
		Color[][] validEdgeStickers = Edge.getAllInitialStickers();
		// Stores all valid corners on a standard Rubik's cube.
		Corner[] validCorners = new Corner[8];
		// Stores all valid edges on a standard Rubik's cube.
		Edge[] validEdges = new Edge[12];
		boolean isValidCubeState = true;
		// Indicates whether the ith corner has already been found. This can be
		// used to check for duplicate pieces.
		boolean[] cornersFound = new boolean[8];
		// Indicates whether the ith edge has already been found. This can be
		// used to check for duplicate pieces.
		boolean[] edgesFound = new boolean[12];
		int validCubieIndex;
		String originalState = solveMaster.getStateString();

		for (int i = 0; i < 8; ++i) {
			validCorners[i] = new Corner(validCornerStickers[i]);
		}

		for (int i = 0; i < 12; ++i) {
			validEdges[i] = new Edge(validEdgeStickers[i]);
		}

		for (int i = 0; i < 8; ++i) { // These two for loops check if every
										// cubie has valid stickers.
			validCubieIndex = getValidCubieIndex(cube.getCorner(i), validCorners);

			/*
			 * If valueCubieIndex = -1, then the current corner does not exist
			 * on a standard cube, so set isValidCubeState to false. If
			 * cornersFound[validCubieIndex] is already true then this corner is
			 * a duplicate, so set isValidCubeState to false.
			 */
			if ((validCubieIndex == -1) || (cornersFound[validCubieIndex])) {
				isValidCubeState = false;
				if (clearStickers)
					cube.getCorner(i).setStickers(Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
			} else
				cornersFound[validCubieIndex] = true;
		}
		for (int i = 0; i < 12; ++i) {
			validCubieIndex = getValidCubieIndex(cube.getEdge(i), validEdges);

			if ((validCubieIndex == -1) || (edgesFound[validCubieIndex])) {
				isValidCubeState = false;

				if (clearStickers)
					cube.getEdge(i).setStickers(Color.LIGHT_GRAY, Color.LIGHT_GRAY);
			} else
				edgesFound[validCubieIndex] = true;
		}

		if (!isValidCubeState)
			return false;

		// Start solving cube
		solveMaster.rotateToTop(Color.white);
		assignOrientationsToCubies();

		crossSolver.solveCross();
		cornerSolver.solveFirstLayerCorners();
		edgeSolver.solveMiddleLayerEdges();
		orientationSolver.solveOrientation();
		permutationSolver.solvePermutation();
		// Stop solving cube

		/*
		 * This block checks if any pieces are unsolved after the solve masters
		 * have generated a solution.
		 */
		for (int i = 0; i < 4; ++i) {
			if (!crossSolver.isPieceSolved(cube.getCorner(i))) {
				isValidCubeState = false;

				if (clearStickers)
					cube.getCorner(i).setStickers(Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
			}
			if (!crossSolver.isPieceSolved(cube.getEdge(i))) {
				isValidCubeState = false;

				if (clearStickers)
					cube.getEdge(i).setStickers(Color.LIGHT_GRAY, Color.LIGHT_GRAY);
			}
		}

		solveMaster.clearMoves();
		SolveMaster.simplifyMoves(crossSolver.getCatalogMoves(), SolveMaster.CROSS);
		SolveMaster.simplifyMoves(cornerSolver.getCatalogMoves(), SolveMaster.CORNER_EDGE);
		SolveMaster.simplifyMoves(edgeSolver.getCatalogMoves(), SolveMaster.CORNER_EDGE);
		SolveMaster.simplifyMoves(orientationSolver.getCatalogMoves(), SolveMaster.CANCELLATIONS);
		SolveMaster.simplifyMoves(permutationSolver.getCatalogMoves(), SolveMaster.CANCELLATIONS);

		solveMaster.applyStateString(originalState);

		return isValidCubeState;
	}

	/**
	 * Returns the index of <code>cubie</code> in <code>validCubies</code>
	 * 
	 * @param cubie
	 *            the cubie to find
	 * @param validCubies
	 *            the array in which <code>cubie</code> will be found
	 * @return the index of <code>cubie</code> in <code>validCubies</code>. <br>
	 *         If the cubie cannot be found, then <code>-1</code> will be
	 *         returned
	 */
	private static int getValidCubieIndex(Cubie cubie, Cubie[] validCubies) {
		for (int i = 0; i < validCubies.length; ++i) {
			if (cubie.strictCompareTo(validCubies[i]) == 0)
				return i;
		}

		return -1;
	}

	/**
	 * Assigns the corresponding orientation to each cubie on the cube. This
	 * method is used to find the orientation of each piece after, e.g. painting
	 * a custom state or loading a state from file
	 */
	public static void assignOrientationsToCubies() {
		// Stores the original colours of the centres on top and front.
		Color[] originalTopFront = { cube.getSlice(0).getCentre(), cube.getSlice(4).getCentre() };
		solveMaster.rotateToTopFront(Color.white, Color.green);

		// Stores the colour of the current centre at the top
		Color topCentre = cube.getSlice(0).getCentre();
		// Stores the colour of the current centre at the bottom
		Color bottomCentre = cube.getSlice(1).getCentre();
		// Stores the colour of the current centre at the right
		Color rightCentre = cube.getSlice(2).getCentre();
		// Stores the colour of the current centre at the left
		Color leftCentre = cube.getSlice(3).getCentre();

		// Stores the orientation of the current cubie.
		int orientation;
		// Stores the properties of the current corner being examined.
		Corner currentCorner;
		// Stores the properties of the current edge being examined.
		Edge currentEdge;
		// Stores the stickers on the current edge being examined.
		Color[] edgeStickers;

		for (int i = 0; i < 8; ++i) {
			currentCorner = cube.getCorner(i);

			/*
			 * Finds the index of the top or bottom centre on the corner so that
			 * the orientation can be determined.
			 */
			if ((orientation = LinearSearch.linearSearchCornerOrientation(currentCorner.getStickers(), topCentre)) == -2)
				currentCorner.setOrientation(LinearSearch.linearSearchCornerOrientation(currentCorner.getStickers(),
						bottomCentre));
			else
				currentCorner.setOrientation(orientation);
		}

		for (int i = 0; i < 12; ++i) {
			currentEdge = cube.getEdge(i);
			edgeStickers = currentEdge.getStickers();

			if ((edgeStickers[0].equals(rightCentre)) || (edgeStickers[0].equals(leftCentre))
					|| (edgeStickers[1].equals(topCentre)) || (edgeStickers[1].equals(bottomCentre)))
				currentEdge.setOrientation(1);
			else
				currentEdge.setOrientation(0);
		}

		solveMaster.rotateToTopFront(originalTopFront[0], originalTopFront[1]);
	}

	/**
	 * @see javax.swing.JComponent#addNotify()
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		requestFocus();
	}

	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * This method handles the release of spacebar or the solving of the cube
	 * after a key is released.
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		/*
		 * This method stops timers and records times, so if a tutorial is
		 * running or custom painting is in progress, then nothing should
		 * happen, so return.
		 */
		if (tutorialIsRunning || customPaintingInProgress)
			return;

		if (e.getKeyChar() == ' ') {
			/*
			 * This starts the timer when the inspection timer is running
			 */
			if (timerHasPermissionToStart && (inspectionTimer != null)) {
				inspectionTimer.stop();
				inspectionTimer = null;
				timerHasPermissionToStart = false;
				timeToBeRecorded = true;
				movesAllowed = true;
				timeLabel.setForeground(Color.black);
				elapsedTimingSeconds = 0.00F;
				elapsedMinutes = 0;
				incTimer = new Timer((int) (timingDisplayInterval * 1000.0), new TimerListener());
				incTimer.start();

				int size = solves.size();
				if (size > 0) {
					listHolder.setSelectedIndex(size - 1);
					listHolder.ensureIndexIsVisible(size - 1);
				}
			} else if ((inspectionTimer == null) && (realTimeSolutionTimer == null) && (incTimer == null)
					&& (movesAllowed) && (!tutorialIsRunning)) {
				// Start timer with spacebar (no inspection)
				customTimerRunning = true;
				timerHasPermissionToStart = false;
				timeToBeRecorded = false;
				movesToBeRecorded = false;
				movesAllowed = true;
				timeLabel.setForeground(Color.black);
				elapsedTimingSeconds = 0.00F;
				elapsedMinutes = 0;
				incTimer = new Timer((int) (timingDisplayInterval * 1000.0), new TimerListener());
				incTimer.start();
			} else if ((!timeToBeRecorded) && (incTimer != null) && (incTimer.isRunning())) {
				// Stop timer with spacebar
				customTimerRunning = false;
				incTimer.stop();
				incTimer = null;
				timerHasPermissionToStart = true;

				int previousSelectedIndex = listHolder.getSelectedIndex();
				SolveMaster.simplifyMoves(trackingMoves, SolveMaster.CANCELLATIONS);

				Solve currentTime = new Solve(timeLabel.getText(), "0", "");
				solves.add(currentTime);
				timeDisplayList.addElement(timeLabel.getText());

				/*
				 * Makes sure the appropriate element in the list is selected.
				 */
				if (previousSelectedIndex == solves.size() - 2)
					listHolder.setSelectedIndex(previousSelectedIndex + 1);

				listHolder.ensureIndexIsVisible(listHolder.getSelectedIndex());

				refreshTimeGraph(true);
				refreshStatistics();
			}
		} else if (cubeSolved && timeToBeRecorded) {
			endSolve();
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
		if (e.isAltDown() || (e.getKeyChar() == ' '))
			return;

		// Stores the move in the correct notation, e.g. instead of "j", it
		// stores "U"
		String move = SolveMaster.getKeyToMove("" + e.getKeyChar());

		if (SolveMaster.isValidMove(move)) {
			if (movesToBeRecorded) {
				if (inspectionTimer != null) {
					if ("xyz".contains(move.substring(0, 1))) {
						trackingMoves.add(move);
					}
				} else {
					trackingMoves.add(move);
				}
			}

			if (!movesAllowed) {
				if (realTimeSolutionTimer == null) {
					cube.rotate(move);
					repaint();
					return;
				}
			} else if (!customPaintingInProgress) {
				cube.performAbsoluteMoves(move);
			}
		}

		if ((!tutorialIsRunning) && (!customPaintingInProgress) && (!customTimerRunning) && (incTimer != null)
				&& (incTimer.isRunning()) && (isCubeSolved())) {
			// Stops timer when cube is solved
			incTimer.stop();
			incTimer = null;
			movesToBeRecorded = false;
			timerHasPermissionToStart = false;
			cubeSolved = true;
		}

		repaint();

		if (tutorialIsRunning) { // Check if criteria is filled
			if (tutorial.criteriaFilled()) {
				// Stores the text to be shown in the dialog box
				String dialogText = "";

				SolveMaster.simplifyMoves(trackingMoves, SolveMaster.CANCELLATIONS);

				if (Tutorial.getNumMovesWithoutRotations(trackingMoves) <= tutorial.getOptimalSolutionLength()) {
					dialogText = "Well done!" + "\nWould you like to play again?";
				} else {
					dialogText = "You solved the problem, but you could have done it in fewer moves."
							+ "\nWould you like to try again?";
				}

				trackingMoves.clear();

				Object[] options = { "Yes", "No" };
				// Stores 0 if the user selects 'Yes'.
				int choice = JOptionPane.showOptionDialog(null, dialogText, "Congratulations",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

				if (choice == 0) // User wants to play again
					tutorialResetStateButton.doClick();
				else { // User wants to move on OR user closes dialog
					tutorialShowSolutionButton.doClick();
					movesAllowed = false;
					movesToBeRecorded = false;
				}
			}
		}
	}

	/**
	 * Assigns the sticker colours to the appropriate elements of
	 * <code>faceColors</code>
	 */
	private void assignFaceletPaintingColors() {
		/*
		 * This specifies that the top face should be painted first, then the
		 * front face, then the right face.
		 */
		int[] sliceIndices = { 0, 4, 2 };
		// Stores the properties of the current slice be examined.
		Slice currentSlice;
		// Stores the index of the current corner being examined on the current
		// slice.
		int cornerIndex;
		// Stores the index of the current corner being examined on the current
		// slice.
		int edgeIndex;

		currentSlice = cube.getSlice(sliceIndices[0]);
		cornerIndex = 0;
		edgeIndex = 0;
		// Iterates over each row of the current face
		for (int i = 0; i < 3; ++i) {
			// Iterates over each column of the current face
			for (int j = 0; j < 3; ++j) {
				if ((i == 1) && (j == 1))
					faceletColors[0][i][j] = currentSlice.getCentre();
				else if ((i + j) % 2 == 0)
					faceletColors[0][i][j] = currentSlice.getCorner(cornerPaintOrder[cornerIndex++]).getStickers()[0];
				else
					faceletColors[0][i][j] = currentSlice.getEdge(edgePaintOrder[edgeIndex++]).getStickers()[0];
			}
		}

		currentSlice = cube.getSlice(sliceIndices[1]);
		cornerIndex = 0;
		edgeIndex = 0;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if ((i == 1) && (j == 1))
					faceletColors[1][i][j] = currentSlice.getCentre();
				else if ((i + j) % 2 == 0)
					/*
					 * i.e. if it is the FLU or FRD corner, then get the second
					 * sticker, and if it is the FUR or FDL corner, then get the
					 * third sticker
					 */
					faceletColors[1][i][j] = currentSlice.getCorner(cornerPaintOrder[cornerIndex]).getStickers()[(cornerPaintOrder[cornerIndex++] % 2 == 0) ? 1
							: 2];
				else
					/*
					 * i.e. if it is the FU or FD edge, then get the second
					 * sticker, otherwise get the first sticker.
					 */
					faceletColors[1][i][j] = currentSlice.getEdge(edgePaintOrder[edgeIndex]).getStickers()[(edgePaintOrder[edgeIndex++] % 2 == 0) ? 1
							: 0];
			}
		}

		currentSlice = cube.getSlice(sliceIndices[2]);
		cornerIndex = 0;
		edgeIndex = 0;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if ((i == 1) && (j == 1))
					faceletColors[2][i][j] = currentSlice.getCentre();
				else if ((i + j) % 2 == 0)
					faceletColors[2][i][j] = currentSlice.getCorner(cornerPaintOrder[cornerIndex]).getStickers()[(cornerPaintOrder[cornerIndex++] % 2 == 0) ? 1
							: 2];
				else
					faceletColors[2][i][j] = currentSlice.getEdge(edgePaintOrder[edgeIndex++]).getStickers()[1];
			}
		}
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		int rectHeight, rectWidth, gridWidth, gridHeight;
		double x, y = 10;
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.setPaint(this.getBackground());
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());

		Dimension d = getSize();
		gridWidth = d.width / 9;
		gridHeight = d.height / 9;
		rectWidth = gridWidth;
		rectHeight = gridHeight;

		assignFaceletPaintingColors();

		// TOP FACE
		g2.shear(-0.8, 0);
		for (int i = 0; i < 3; ++i) {
			x = d.width / 3;
			for (int j = 0; j < 3; ++j) {
				g2.setStroke(stroke);
				g2.setPaint(Color.black);
				g2.draw(new Rectangle2D.Double(x, y, rectWidth, 0.8 * rectHeight));

				g2.setPaint(faceletColors[0][i][j]);

				g2.fill(new Rectangle2D.Double(x + strokeThickness - 1, y + strokeThickness - 1, rectWidth
						- strokeThickness + 0.5, (0.8 * rectHeight) - strokeThickness + 0.1));

				x += gridWidth;
			}
			y += 0.8 * gridHeight;
		}

		// FRONT FACE
		g2.shear(0.8, 0);
		for (int i = 0; i < 3; ++i) {
			x = 0.328 * (d.width / 3);
			for (int j = 0; j < 3; ++j) {
				g2.setStroke(stroke);
				g2.setPaint(Color.black);
				g2.draw(new Rectangle2D.Double(x, y, rectWidth, rectHeight));

				g2.setPaint(faceletColors[1][i][j]);

				g2.fill(new Rectangle2D.Double(x + strokeThickness - 1, y + strokeThickness - 1, rectWidth
						- strokeThickness + 0.5, rectHeight - strokeThickness + 0.1));

				x += gridWidth;
			}
			x = 250;
			y += gridHeight;
		}

		// RIGHT FACE
		g2.shear(0, -1.26);
		y = 501;
		for (int i = 0; i < 3; ++i) {
			x = 0.328 * (d.width / 3) + (3 * gridWidth);
			for (int j = 0; j < 3; ++j) {
				g2.setStroke(stroke);
				g2.setPaint(Color.black);
				g2.draw(new Rectangle2D.Double(x, y, (0.8 * rectWidth) - 10.5, rectHeight - 1));

				g2.setPaint(faceletColors[2][i][j]);

				g2.fill(new Rectangle2D.Double(x + strokeThickness - 1, y + strokeThickness - 1, (0.8 * rectWidth) - 11
						- strokeThickness + 0.3, rectHeight - 1 - strokeThickness + 0.2));

				x += gridWidth - 24;
			}
			x = 250;
			y += gridHeight;
		}

	}

	/**
	 * Sets whether or not the 'Apply Random Scramble' menu item is enabled.
	 * 
	 * @param state
	 *            the resulting enabled/disabled state of the menu item
	 */
	public static void setRandomScrambleEnabled(boolean state) {
		MenuBar.setRandomScrambleEnabled(state);
	}

	/*
	 * public static void printRuntimeStats() { Runtime runtime1 =
	 * Runtime.getRuntime(); System.out.println("max memory: " +
	 * runtime1.maxMemory() / (1024 * 1024));
	 * 
	 * Runtime runtime2 = Runtime.getRuntime();
	 * System.out.println("allocated memory: " + runtime2.totalMemory() / (1024
	 * * 1024));
	 * 
	 * Runtime runtime3 = Runtime.getRuntime();
	 * System.out.println("free memory: " + runtime3.freeMemory() / (1024 *
	 * 1024)); }
	 */

	/**
	 * Records the specified list in {@link #solves} and appends the time to the
	 * display-list
	 * 
	 * @param solve
	 *            the solve to be added
	 */
	public static void addSolveToList(Solve solve) {
		solves.add(new Solve(solve.getStringTime(), solve.getPenalty(), solve.getComment(), solve.getScramble(), solve
				.getSolution()));
		copyAllTimesToDisplay();
		listHolder.setSelectedIndex(solves.size() - 1);
	}

	/**
	 * Scrambles the cube (instantly) with the specified scramble and performs
	 * the solution in real-time as an animation at the speed defined in
	 * Preferences
	 * 
	 * @param scramble
	 *            the scramble to be applied before the animation starts
	 * @param solution
	 *            the moves to be performed in real-time
	 */
	public static void performRealTimeSolving(String scramble, String solution) {
		try {
			if (realTimeSolutionTimer != null)
				realTimeSolutionTimer.stop();

			movesAllowed = false;

			LinkedList<String> movesToPerform = new LinkedList<>();
			LinkedList<String> movesCopy;
			int size;
			String current;

			resetCube();
			cube.performAbsoluteMoves(scramble);

			solveMaster.catalogMoves(solution);

			movesCopy = solveMaster.getCatalogMoves();

			size = movesCopy.size();
			for (int i = 0; i < size; ++i) {
				current = movesCopy.get(i);
				if (current.substring(current.length() - 1).equals("2")) {
					movesToPerform.add(current.substring(0, current.length() - 1));
					movesToPerform.add(current.substring(0, current.length() - 1));
				} else
					movesToPerform.add(current);
			}

			realTimeSolutionTimer = null;
			realTimeSolutionTimer = new Timer(preferencesPopUp.getRealTimeSolvingRate(), new RealTimeTimerListener(
					movesToPerform));
			realTimeSolutionTimer.start();

			// MenuBar.setRandomScrambleEnabled(false);
			// timingDetailsStartNewSolveButton.setEnabled(false);

			cubePanel.repaint();

			solveMaster.clearMoves();
		} catch (Exception e) {
			movesAllowed = true;
		}
	}

	/**
	 * This solves the cube and shows the moves as an animation (real-time) at
	 * the speed specified in Preferences
	 */
	public static void performRealTimeSolving() {
		try {
			if (realTimeSolutionTimer != null)
				realTimeSolutionTimer.stop();

			movesAllowed = false;
			LinkedList<String> moves = new LinkedList<>();
			LinkedList<String> crossSolverMoves = crossSolver.getCatalogMoves();
			LinkedList<String> cornerSolverMoves = cornerSolver.getCatalogMoves();
			LinkedList<String> edgeSolverMoves = edgeSolver.getCatalogMoves();
			LinkedList<String> orientationSolverMoves = orientationSolver.getCatalogMoves();
			LinkedList<String> permutationSolverMoves = permutationSolver.getCatalogMoves();
			String current;

			solveMaster.rotateToTopFront(Cubie.getWordToColor(crossSolverMoves.get(1)),
					Cubie.getWordToColor(crossSolverMoves.get(4)));
			solveMaster.clearMoves();

			int[] length = { crossSolver.getCatalogMoves().size(), cornerSolver.getCatalogMoves().size(),
					edgeSolver.getCatalogMoves().size(), orientationSolver.getCatalogMoves().size(),
					permutationSolver.getCatalogMoves().size() };

			for (int i = 0; i < length[0]; ++i) {
				current = crossSolverMoves.get(i);
				if (SolveMaster.isValidMove(current)) { // We need this check
														// for the cross because
														// of the
														// "X on top and Y on front"
														// thing.
					if (current.substring(current.length() - 1).equals("2")) {
						moves.add(current.substring(0, current.length() - 1));
						moves.add(current.substring(0, current.length() - 1));
					} else
						moves.add(current);
				}
			}
			for (int i = 0; i < length[1]; ++i) {
				current = cornerSolverMoves.get(i);
				if (current.substring(current.length() - 1).equals("2")) {
					moves.add(current.substring(0, current.length() - 1));
					moves.add(current.substring(0, current.length() - 1));
				} else
					moves.add(current);
			}
			for (int i = 0; i < length[2]; ++i) {
				current = edgeSolverMoves.get(i);
				if (current.substring(current.length() - 1).equals("2")) {
					moves.add(current.substring(0, current.length() - 1));
					moves.add(current.substring(0, current.length() - 1));
				} else
					moves.add(current);
			}
			for (int i = 0; i < length[3]; ++i) {
				current = orientationSolverMoves.get(i);
				if (current.substring(current.length() - 1).equals("2")) {
					moves.add(current.substring(0, current.length() - 1));
					moves.add(current.substring(0, current.length() - 1));
				} else
					moves.add(current);
			}
			for (int i = 0; i < length[4]; ++i) {
				current = permutationSolverMoves.get(i);
				if (current.substring(current.length() - 1).equals("2")) {
					moves.add(current.substring(0, current.length() - 1));
					moves.add(current.substring(0, current.length() - 1));
				} else
					moves.add(current);
			}

			realTimeSolutionTimer = null;
			realTimeSolutionTimer = new Timer((int) (preferencesPopUp.getRealTimeSolvingRate()),
					new RealTimeTimerListener(moves));
			realTimeSolutionTimer.start();

			// MenuBar.setRandomScrambleEnabled(false);
			// timingDetailsStartNewSolveButton.setEnabled(false);

			crossSolverMoves.clear();
			cornerSolverMoves.clear();
			edgeSolverMoves.clear();
			orientationSolverMoves.clear();
			permutationSolverMoves.clear();
		} catch (Exception e) {
			movesAllowed = true;
		}

		crossSolver.clearMoves();
		cornerSolver.clearMoves();
		edgeSolver.clearMoves();
		orientationSolver.clearMoves();
		permutationSolver.clearMoves();
	}

	/**
	 * Cancels all animations, timers, etc., and labels reset to their default
	 * values.
	 */
	private static void cancelSolve() {
		/*
		 * boolean enabled = !customPaintingInProgress && !tutorialIsRunning;
		 * MenuBar.setRandomScrambleEnabled(enabled);
		 * MenuBar.clickToSolveItem.setEnabled(enabled);
		 * MenuBar.paintCustomStateItem.setEnabled(enabled);
		 * MenuBar.solveCubeItem.setEnabled(true);
		 * timingDetailsStartNewSolveButton.setEnabled(true);
		 */
		MenuBar.clickToSolveItem.setSelected(clickToSolve);
		timeToBeRecorded = false;
		timerHasPermissionToStart = false;
		movesAllowed = !customPaintingInProgress;

		trackingMoves.clear();
		clearAllSolverMoves();

		try {
			inspectionTimer.stop();
			inspectionTimer = null;
		} catch (Exception e) {
		}
		try {
			incTimer.stop();
			incTimer = null;
		} catch (Exception e) {
		}
		try {
			realTimeSolutionTimer.stop();
			realTimeSolutionTimer = null;
		} catch (Exception e) {
		}

		timeLabel.setForeground(Color.black);
		timeLabel.setText("0.00");
	}

	/**
	 * Performs the operations required to end the solve in the appropriate way.
	 * After the user completes a solve after clicking the 'Start New Solve'
	 * button, this method will be invoked and the time will be recorded. But in
	 * other situations, the time will not be recorded.
	 */
	private static void endSolve() {
		/*
		 * boolean enabled = !customPaintingInProgress && !tutorialIsRunning;
		 * MenuBar.setRandomScrambleEnabled(enabled);
		 * MenuBar.clickToSolveItem.setEnabled(enabled);
		 * MenuBar.paintCustomStateItem.setEnabled(enabled);
		 * MenuBar.solveCubeItem.setEnabled(true);
		 * timingDetailsStartNewSolveButton.setEnabled(true);
		 */
		MenuBar.clickToSolveItem.setSelected(false);
		timeToBeRecorded = false;

		String currentTimeString = timeLabel.getText();

		if (currentPenalty.equals("+2")) {
			currentTimeString = "" + (Double.parseDouble(currentTimeString) + 2);
		} else if (currentPenalty.equals("DNF")) {
			currentTimeString = "DNF";
		}

		int previousSelectedIndex = listHolder.getSelectedIndex();
		SolveMaster.simplifyMoves(trackingMoves, SolveMaster.CANCELLATIONS);

		Solve currentTime = new Solve(currentTimeString, currentPenalty.replaceAll("[+]", ""), "", currentScramble,
				trackingMoves);
		solves.add(currentTime);
		String timeFormat = currentTime.getStringTime() + (currentPenalty.equals("+2") ? " (+2)" : "");
		timeDisplayList.addElement(timeFormat);

		if (previousSelectedIndex == solves.size() - 2)
			listHolder.setSelectedIndex(previousSelectedIndex + 1);

		listHolder.ensureIndexIsVisible(listHolder.getSelectedIndex());

		refreshTimeGraph(true);
		refreshStatistics();

		movesToBeRecorded = false;
		trackingMoves.clear();
	}

	/**
	 * @return the solution to the cube state in a formatted fashion
	 */
	public static String getFormattedCubeSolution() {
		// String formattedString = "";

		// These are done in 'isValidCubeState(...)', so don't do them again
		/*
		 * crossSolver.solveCross(); cornerSolver.solveFirstLayerCorners();
		 * edgeSolver.solveMiddleLayerEdges();
		 * orientationSolver.solveOrientation();
		 * permutationSolver.solvePermutation();
		 */
		/*
		 * formattedString = ("------------Solution------------\n" + "Cross: \t"
		 * + crossSolver.getStringMoves() + "\nCorners: \t" +
		 * cornerSolver.getStringMoves() + "\nEdges: \t" +
		 * edgeSolver.getStringMoves() + "\nOrientation: \t" +
		 * orientationSolver.getStringMoves() + "\nPermutation: \t" +
		 * permutationSolver.getStringMoves() + "\n\n" +
		 * "----------Explanation-----------\n" +
		 * cornerSolver.getSolutionExplanation() + "\n" +
		 * edgeSolver.getSolutionExplanation() + "\n" +
		 * orientationSolver.getSolutionExplanation() + "\n" +
		 * permutationSolver.getSolutionExplanation());
		 */

		return ("------------Solution------------\n"
				+ ((crossSolver.getStringMoves().trim().equals("")) ? "" : "Cross: \t" + crossSolver.getStringMoves())
				+ ((cornerSolver.getStringMoves().trim().equals("")) ? "" : "\nCorners: \t"
						+ cornerSolver.getStringMoves())
				+ ((edgeSolver.getStringMoves().trim().equals("")) ? "" : "\nEdges: \t" + edgeSolver.getStringMoves())
				+ ((orientationSolver.getStringMoves().trim().equals("")) ? "" : "\nOrientation: \t"
						+ orientationSolver.getStringMoves())
				+ ((permutationSolver.getStringMoves().trim().equals("")) ? "" : "\nPermutation: \t"
						+ permutationSolver.getStringMoves())
				+ "\n\n------------Explanation------------\n"
				+ ((cornerSolver.getSolutionExplanation().trim().equals("")) ? "" : "Corners:\n"
						+ cornerSolver.getSolutionExplanation() + "\n")
				+ ((edgeSolver.getSolutionExplanation().trim().equals("")) ? "" : "Edges:\n"
						+ edgeSolver.getSolutionExplanation() + "\n")
				+ ((orientationSolver.getSolutionExplanation().trim().equals("")) ? "" : "Orientation:\n"
						+ orientationSolver.getSolutionExplanation() + "\n") + ((permutationSolver
				.getSolutionExplanation().trim().equals("")) ? "" : "Permutation:\n"
				+ permutationSolver.getSolutionExplanation()));

		// return formattedString;
	}

	/**
	 * Refreshes the list of times after a time is added, edited or deleted
	 */
	public static void refreshTimeList() {
		// int selectedIndex = listHolder.getSelectedIndex();
		Solve current;
		timeDisplayList.clear();

		for (int i = 0; i < solves.size(); ++i) {
			current = solves.get(i);
			if (current.getStringTime().equals("-1")) {
				solves.remove(i);
				--i; // so that i stays the same
			} else
				timeDisplayList.addElement(current.getStringTime()
						+ ((!current.getPenalty().equals("0") && (!current.getPenalty().equals(""))) ? String.format(
								" (+%s)", current.getPenalty()) : ""));
		}
		/*
		 * if (selectedIndex != -1) { String penalty =
		 * solves.get(selectedIndex).getPenalty(); if
		 * (solves.get(selectedIndex).getStringTime().equals("-1")) {
		 * solves.remove(selectedIndex); timeDisplayList.clear();
		 * 
		 * for (int i = 0; i < solves.size(); ++i) {
		 * timeDisplayList.addElement(solves.get(i).getStringTime()); } } else
		 * timeDisplayList.set(selectedIndex,
		 * solves.get(selectedIndex).getStringTime() + ((!penalty.equals("0") &&
		 * (!penalty.equals(""))) ? String.format(" (+%s)", penalty) : "")); }
		 */
	}

	/**
	 * Copies all times from {@link #solves} to {@link #timeDisplayList}
	 */
	public static void copyAllTimesToDisplay() {
		int size = solves.size();
		String penalty;
		Solve current;
		timeDisplayList.clear();

		for (int i = 0; i < size; ++i) {
			current = solves.get(i);
			penalty = current.getPenalty();
			timeDisplayList.addElement(solves.get(i).getStringTime()
					+ (!penalty.equals("0") ? String.format(" (+%s)", penalty) : ""));
		}
	}

	/**
	 * Refreshes the statistics and resets the scrolling position to the top of
	 * the text area
	 */
	public static void refreshStatistics() {
		timingDetailsTextArea.setText(statistics.getRecentFormattedStandardStatistics());
		timingDetailsTextArea.setCaretPosition(0);
	}

	/**
	 * Refreshes the dataset being drawn in the Time Graph window.
	 * 
	 * @param cubePanelFocus
	 *            if <code>true</code>, then the cube panel should be focused
	 *            after method executes; <br>
	 *            otherwise the existing focus-window should keep focus
	 */
	public static void refreshTimeGraph(boolean cubePanelFocus) {
		timeGraph.setDataset(getDatasetOfSelectedTimes());
		timeGraph.draw();
		timeGraph.validate();

		if (cubePanelFocus)
			cubePanel.requestFocus();
	}

	/**
	 * Initialises the components relating to the list of times at the
	 * right-hand side of the main window.
	 */
	public static void initListPanel() {
		timeListPanel = new JPanel();
		timeDisplayList = new DefaultListModel<String>();

		listHolder = new JList<String>(timeDisplayList);
		listHolder.setFont(new Font("Arial", 0, 20));
		listHolder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listHolder.setLayoutOrientation(JList.VERTICAL);
		listHolder.setSelectedIndex(0);
		listHolder.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (solves.size() > 0)) {
					timeListPopUp.setSolve(solves.get(listHolder.getSelectedIndex()));
					timeListPopUp.selectAllTimeText();
					timeListPopUp.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		listHolder.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
				int selectedIndex = listHolder.getSelectedIndex();

				if (selectedIndex == -1)
					return;

				if (e.getKeyChar() == KeyEvent.VK_DELETE) {
					solves.get(selectedIndex).setStringTime("-1");
					refreshTimeList();
					refreshStatistics();
					copyAllTimesToDisplay();
					refreshTimeGraph(true);

					if (solves.size() > 0)
						listHolder.setSelectedIndex((selectedIndex < solves.size()) ? selectedIndex : selectedIndex - 1);

					listHolder.requestFocus();
				} else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					timeListPopUp.setSolve(solves.get(listHolder.getSelectedIndex()));
					timeListPopUp.selectAllTimeText();
					timeListPopUp.setVisible(true);
				}
			}
		});

		timeListScrollPane = new JScrollPane(listHolder);
		timeListPanel.add(timeListScrollPane);

		listHolder.setFocusable(true);
	}

	/**
	 * @return <code>true</code> if the cube is solved; <br>
	 *         <code>false</code> otherwise
	 */
	public static boolean isCubeSolved() {
		Edge[] edges = new Edge[12];
		Corner[] corners = new Corner[8];
		Color[][] edgeStickers = Edge.getAllInitialStickers();
		Color[][] cornerStickers = Corner.getAllInitialStickers();
		boolean isSolved = true;

		solveMaster.rotateToTop(Color.yellow);

		for (int i = 0; i < edges.length; ++i) {
			edges[i] = new Edge(edgeStickers[i]);

			if (!crossSolver.isPieceSolved(edges[i])) { // These used to be
														// 'pieceSolved(...)',
														// so if something goes
														// wrong, try changing
														// these back
				isSolved = false;
				break;
			}
		}

		if (isSolved) {
			for (int i = 0; i < corners.length; ++i) {
				corners[i] = new Corner(cornerStickers[i]);

				if (!crossSolver.isPieceSolved(corners[i])) { // These used to
																// be
																// 'pieceSolved(...)',
																// so if
																// something
																// goes wrong,
																// try changing
																// these back
					isSolved = false;
					break;
				}
			}
		}

		cube.performAbsoluteMoves(SolveMaster.getReverseStringMoves(solveMaster.getCatalogMoves()));
		solveMaster.clearMoves();

		return isSolved;
	}

	/**
	 * Requests focus for {@link #cubePanel}
	 */
	public static void requestCubePanelFocus() {
		cubePanel.requestFocus();
	}

	/*
	 * private static void solveCube() { crossSolver.solveCross();
	 * cornerSolver.solveFirstLayerCorners();
	 * edgeSolver.solveMiddleLayerEdges(); orientationSolver.solveOrientation();
	 * permutationSolver.solvePermutation();
	 * 
	 * 
	 * crossSolver.clearMoves(); cornerSolver.clearMoves();
	 * edgeSolver.clearMoves(); orientationSolver.clearMoves();
	 * permutationSolver.clearMoves(); }
	 */
	/**
	 * Resets the cube to the solve state with white on top and green on front
	 */
	private static void resetCube() {
		solveMaster.rotateToTopFront(Color.white, Color.green);
		solveMaster.clearMoves();
		cube.resetCube();
	}

	/**
	 * Sets the size of the scramble text to the specified size
	 * 
	 * @param size
	 *            the size for the text of the scramble
	 */
	public static void setScrambleTextSize(int size) {
		scrambleLabel.setFont(new Font("Courier New", 0, size));
	}

	/**
	 * Scrambles the cube with the specified scramble and shows the scramble in
	 * {@link #scrambleLabel}
	 * 
	 * @param scramble
	 *            the scramble to be applied
	 */
	public static void handleScramble(String scramble) {
		if (!tutorialIsRunning && (incTimer == null) && (inspectionTimer == null) && !customPaintingInProgress
				&& (realTimeSolutionTimer == null)) {
			currentScramble = scramble;
			cube.performAbsoluteMoves(currentScramble);
			scrambleLabel.setText("Scramble: " + currentScramble);

			cubePanel.repaint();
			// cubePanel.requestFocus();
		} else
			return;
	}

	/**
	 * Clears all moves stored by the children of {@link #solveMaster}
	 */
	public static void clearAllSolverMoves() {
		crossSolver.clearMoves();
		cornerSolver.clearMoves();
		edgeSolver.clearMoves();
		orientationSolver.clearMoves();
		permutationSolver.clearMoves();
	}

	/**
	 * @return a MouseListener for the cube panel so that the clicked piece can
	 *         be solved when in Click-to-Solve mode
	 */
	private static MouseListener getCubePanelMouseListener() {
		MouseListener cubePanelMouseListener = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent e) {
				cubePanel.requestFocus();

				if (clickToSolve) {
					// Stores the index of the piece selected.
					int index = MouseSelectionSolver.getIndexOfPieceOnScreen(e.getX(), e.getY());
					// Stores an integer representing the choice made by the
					// user in the question dialog.
					int choice = -1;
					// Stores the colours of the stickers of the cubie selected.
					Color[] stickers;
					String solution = "";

					if (index < 0)
						return;

					MenuBar.setSolvePieceSelected(false);
					clickToSolve = false;

					if (index >= 8)
						stickers = cube.getEdge(index - 8).getStickers();
					else
						stickers = cube.getCorner(index).getStickers();

					colorSelection.setAlwaysOnTop(false);
					choice = MouseSelectionSolver.getQuestionDialogResponse(String.format(
							"You have selected the %s-%s%s. Do you wish to continue?",
							Cubie.getColorToWord(stickers[0]), Cubie.getColorToWord(stickers[1]),
							(stickers.length == 3) ? "-" + Cubie.getColorToWord(stickers[2]) + " Corner" : " Edge"));
					colorSelection.setAlwaysOnTop(true);

					if (choice == 0) {
						selectionSolver.solvePiece(index);
					} else
						return;

					solution = selectionSolver.getSolution();

					timingDetailsTextArea.setText(String.format("Solving the %s-%s%s",
							Cubie.getColorToWord(stickers[0]), Cubie.getColorToWord(stickers[1]),
							(stickers.length == 3) ? "-" + Cubie.getColorToWord(stickers[2]) + " Corner" : " Edge"));

					if (!solution.equals(MouseSelectionSolver.BLANK)) {
						timingDetailsTextArea.setText(solution);
						timingDetailsTextArea.setCaretPosition(0);
					}

					if (MenuBar.paintCustomStateItem.isSelected())
						MenuBar.paintCustomStateItem.doClick();

					performRealTimeSolving();
					cubePanel.repaint();
				} else if (customPaintingInProgress && (realTimeSolutionTimer == null)) {
					// Stores the index of the facelet selected on screen.
					int index = MouseSelectionSolver.getIndexOfFaceletOnScreen(e.getX(), e.getY());
					// Stores the current colour which will be painted on the
					// selected sticker.
					Color currentCustomPaintingColor = colorSelection.getSelectedColor();

					if (index < 0)
						return;

					switch (index) {
					case 0:
						cube.getCorner(0).setSticker(0, currentCustomPaintingColor);
						break;
					case 1:
						cube.getEdge(0).setSticker(0, currentCustomPaintingColor);
						break;
					case 2:
						cube.getCorner(1).setSticker(0, currentCustomPaintingColor);
						break;
					case 3:
						cube.getEdge(3).setSticker(0, currentCustomPaintingColor);
						break;
					case 5:
						cube.getEdge(1).setSticker(0, currentCustomPaintingColor);
						break;
					case 6:
						cube.getCorner(3).setSticker(0, currentCustomPaintingColor);
						break;
					case 7:
						cube.getEdge(2).setSticker(0, currentCustomPaintingColor);
						break;
					case 8:
						cube.getCorner(2).setSticker(0, currentCustomPaintingColor);
						break;

					case 9:
						cube.getCorner(3).setSticker(1, currentCustomPaintingColor);
						break;
					case 10:
						cube.getEdge(2).setSticker(1, currentCustomPaintingColor);
						break;
					case 11:
						cube.getCorner(2).setSticker(2, currentCustomPaintingColor);
						break;
					case 12:
						cube.getEdge(7).setSticker(0, currentCustomPaintingColor);
						break;
					case 14:
						cube.getEdge(6).setSticker(0, currentCustomPaintingColor);
						break;
					case 15:
						cube.getCorner(6).setSticker(2, currentCustomPaintingColor);
						break;
					case 16:
						cube.getEdge(10).setSticker(1, currentCustomPaintingColor);
						break;
					case 17:
						cube.getCorner(7).setSticker(1, currentCustomPaintingColor);
						break;

					case 18:
						cube.getCorner(2).setSticker(1, currentCustomPaintingColor);
						break;
					case 19:
						cube.getEdge(1).setSticker(1, currentCustomPaintingColor);
						break;
					case 20:
						cube.getCorner(1).setSticker(2, currentCustomPaintingColor);
						break;
					case 21:
						cube.getEdge(6).setSticker(1, currentCustomPaintingColor);
						break;
					case 23:
						cube.getEdge(5).setSticker(1, currentCustomPaintingColor);
						break;
					case 24:
						cube.getCorner(7).setSticker(2, currentCustomPaintingColor);
						break;
					case 25:
						cube.getEdge(11).setSticker(1, currentCustomPaintingColor);
						break;
					case 26:
						cube.getCorner(4).setSticker(1, currentCustomPaintingColor);
						break;
					}

					cubePanel.repaint();
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
		};

		return cubePanelMouseListener;
	}

	/**
	 * Sets up the main window
	 */
	public static void createAndShowGUI() {
		Color bg = null;
		int textSize = 17;

		timeGraph = new TimeGraph("Time Graph", "", "Solve No.", "Time / seconds");
		timeGraph.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		timeGraph.setMinimumSize(new Dimension(400, 400));
		timeGraph.setPreferredSize(new Dimension(600, 450));
		timeGraph.setLocation(new Point(510, 50));
		timeGraph.pack();
		timeGraph.addWindowListener(new PopUpWindowListener());

		timeListPopUp = new TimeListPopUp("Solve Editor");
		timeListPopUp.addWindowListener(new PopUpWindowListener());
		timeListPopUp.pack();

		preferencesPopUp = new Preferences();
		preferencesPopUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		preferencesPopUp.addWindowListener(new PopUpWindowListener());

		algorithmDatabasePopUp = new AlgorithmDatabasePopUp();
		algorithmDatabasePopUp.addWindowListener(new PopUpWindowListener());

		solveDatabasePopUp = new SolveDatabasePopUp();
		solveDatabasePopUp.addWindowListener(new PopUpWindowListener());

		competitionDatabasePopUp = new CompetitionDatabasePopUp();
		competitionDatabasePopUp.addWindowListener(new PopUpWindowListener());

		memberDatabasePopUp = new MemberDatabasePopUp();
		memberDatabasePopUp.addWindowListener(new PopUpWindowListener());

		colorSelection = new ColorSelection();
		colorSelection.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		colorSelection.setLocation(600, 150);
		colorSelection.pack();
		colorSelection.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				MenuBar.paintCustomStateItem.doClick();
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}

		});

		scramblePopUp = new ScramblePopUp();
		scramblePopUp.setLocation(600, 150);
		scramblePopUp.addWindowListener(new PopUpWindowListener());

		totalFrame = new JFrame("Kuubik");
		totalFrame.setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		totalFrame.setJMenuBar(new MenuBar().createMenuBar());
		// totalFrame.setContentPane(new MenuBar().createContentPane());

		cubePanel.setSize(600, 600);
		cubePanel.setLocation(50, 0);
		cubePanel.setBackground(bg);
		cubePanel.addMouseListener(getCubePanelMouseListener());

		totalPaintingPanel = new JPanel();
		totalPaintingPanel.setVisible(true);
		totalPaintingPanel.setLayout(null);
		totalPaintingPanel.setSize(500, 500);
		totalPaintingPanel.setBackground(bg);
		totalFrame.getContentPane().add(totalPaintingPanel);

		topComponentsPanel = new JPanel();
		topComponentsPanel.setLayout(new GridLayout(2, 1, 0, -11)); // BoxLayout(topComponentsPanel,
																	// BoxLayout.PAGE_AXIS));
		totalFrame.getContentPane().add(topComponentsPanel, BorderLayout.NORTH);

		scrambleLabel = new JTextArea("Scramble:");
		scrambleLabel.setEditable(false);
		scrambleLabel.setLineWrap(true);
		scrambleLabel.setWrapStyleWord(true);
		scrambleLabel.setFont(new Font("Courier New", 0, preferencesPopUp.getScrambleTextSize()));
		scrambleLabel.setBackground(bg);
		topComponentsPanel.add(scrambleLabel);

		timeLabel = new JLabel("0.00");
		timeLabel.setFont(new Font("Arial", 0, 50));
		// totalFrame.getContentPane().add(timeLabel, BorderLayout.NORTH);
		topComponentsPanel.add(timeLabel);

		initListPanel();
		timeListScrollPane.setPreferredSize(new Dimension(150, 360));
		totalFrame.getContentPane().add(timeListPanel, BorderLayout.EAST);

		/************************* BOTTOM PANELS ************************/

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));

		tutorialComponentsPanel = new JPanel();
		tutorialButtonsPanel = new JPanel();
		tutorialTextArea = new JTextArea();
		tutorialScrollPane = new JScrollPane(tutorialTextArea);
		tutorialNextButton = new JButton();
		tutorialBackButton = new JButton();
		tutorialHintButton = new JButton();
		tutorialResetStateButton = new JButton();
		tutorialShowDescriptionButton = new JButton();
		tutorialShowSolutionButton = new JButton();
		tutorialComponentsPanel.setVisible(false);

		tutorialComponentsPanel.setLayout(new BoxLayout(tutorialComponentsPanel, BoxLayout.PAGE_AXIS));
		tutorialComponentsPanel.setPreferredSize(new Dimension(800, 190));
		tutorialComponentsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		tutorialTextArea.setLineWrap(true);
		tutorialTextArea.setWrapStyleWord(true);
		tutorialTextArea.setEditable(false);
		tutorialTextArea.setBorder(BorderFactory.createLineBorder(Color.black));
		tutorialTextArea.setFont(new Font("Arial", Font.PLAIN, textSize));

		tutorialScrollPane.setPreferredSize(new Dimension(700, 150));
		tutorialScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		tutorialNextButton.setSize(150, 20);
		tutorialNextButton.setText("Next");
		tutorialNextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tutorial.isLoaded()) {
					resetCube();
					solveMaster.rotateToTopFront(Color.white, Color.green);
					solveMaster.clearMoves();

					trackingMoves.clear();

					tutorial.loadNextSubTutorial();
					tutorialTextArea.setText(tutorial.getDescription());
					tutorialTextArea.setCaretPosition(0);
					cube.performAbsoluteMoves(tutorial.getScramble());
					cubePanel.repaint();
					cubePanel.requestFocus();
					movesAllowed = tutorial.requiresUserAction();
					movesToBeRecorded = movesAllowed;
					tutorialBackButton.setEnabled(!tutorial.isFirstSubTutorial());
					tutorialNextButton.setEnabled(!tutorial.isLastSubTutorial());
					tutorialHintButton.setEnabled(tutorial.requiresUserAction());
					tutorialShowSolutionButton.setEnabled(tutorial.requiresUserAction());
				}
			}
		});

		tutorialBackButton.setSize(150, 20);
		tutorialBackButton.setText("Back");
		tutorialBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tutorial.isLoaded()) {
					resetCube();
					solveMaster.rotateToTopFront(Color.white, Color.green);
					solveMaster.clearMoves();
					trackingMoves.clear();

					tutorial.loadPreviousSubTutorial();
					tutorialTextArea.setText(tutorial.getDescription());
					tutorialTextArea.setCaretPosition(0);
					cube.performAbsoluteMoves(tutorial.getScramble());
					cubePanel.repaint();
					cubePanel.requestFocus();
					movesAllowed = tutorial.requiresUserAction();
					tutorialBackButton.setEnabled(!tutorial.isFirstSubTutorial());
					tutorialNextButton.setEnabled(!tutorial.isLastSubTutorial());
					tutorialHintButton.setEnabled(tutorial.requiresUserAction());
					tutorialShowSolutionButton.setEnabled(tutorial.requiresUserAction());
				}
			}
		});

		tutorialShowDescriptionButton.setSize(150, 20);
		tutorialShowDescriptionButton.setText("Description");
		tutorialShowDescriptionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tutorial.isLoaded()) {
					tutorialTextArea.setText(tutorial.getDescription());
					tutorialTextArea.setCaretPosition(0);
					cubePanel.requestFocus();
				}
			}
		});

		tutorialHintButton.setSize(150, 20);
		tutorialHintButton.setText("Hint");
		tutorialHintButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tutorial.isLoaded() && (tutorial.requiresUserAction())) {
					// solveCube();
					// resetCube();
					// solveMaster.rotateToTopFront(Color.white, Color.green);
					// solveMaster.clearMoves();

					tutorial.loadNextHint();
					tutorialTextArea.setText(tutorial.getHint());
					tutorialTextArea.setCaretPosition(0);
					cubePanel.requestFocus();
				}
			}
		});

		tutorialResetStateButton.setSize(150, 20);
		tutorialResetStateButton.setText("Reset");
		tutorialResetStateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tutorial.isLoaded() && (tutorial.requiresUserAction())) {
					// solveCube();
					resetCube();
					solveMaster.rotateToTopFront(Color.white, Color.green);
					solveMaster.clearMoves();

					cube.performAbsoluteMoves(tutorial.getScramble());
					cubePanel.requestFocus();
					cubePanel.repaint();
				}
			}
		});

		tutorialShowSolutionButton.setSize(150, 20);
		tutorialShowSolutionButton.setText("Solution");
		tutorialShowSolutionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tutorial.isLoaded() && (tutorial.requiresUserAction())) {
					resetCube();
					solveMaster.rotateToTopFront(Color.white, Color.green);
					solveMaster.clearMoves();

					movesAllowed = false;

					cube.performAbsoluteMoves(tutorial.getScramble());
					cube.performAbsoluteMoves(tutorial.getOptimalSolutions()[0]);

					String[] optimalSolutions = tutorial.getOptimalSolutions();
					tutorialTextArea.setText("Solution: " + tutorial.getExplanation()
							+ "\n\nOptimal solutions include:");

					for (int i = 0; i < optimalSolutions.length; ++i)
						tutorialTextArea.setText(tutorialTextArea.getText() + "\n" + optimalSolutions[i]);

					tutorialTextArea.setCaretPosition(0);

					cubePanel.repaint();
					cubePanel.requestFocus();
				}
			}
		});

		tutorialButtonsPanel.add(tutorialShowDescriptionButton);
		tutorialButtonsPanel.add(tutorialHintButton);
		tutorialButtonsPanel.add(tutorialResetStateButton);
		tutorialButtonsPanel.add(tutorialShowSolutionButton);
		tutorialButtonsPanel.add(tutorialBackButton);
		tutorialButtonsPanel.add(tutorialNextButton);
		tutorialComponentsPanel.add(tutorialScrollPane);
		tutorialComponentsPanel.add(tutorialButtonsPanel);

		// tutorialComponentsPanel.add(tutorialScrollPane); //Looks like these
		// are redundant
		// tutorialComponentsPanel.add(tutorialButtonsPanel);

		timingDetailsComponentsPanel = new JPanel();
		timingDetailsButtonPanel = new JPanel();
		timingDetailsTextArea = new JTextArea();
		timingDetailsScrollPane = new JScrollPane(timingDetailsTextArea);
		timingDetailsResetSessionButton = new JButton();
		timingDetailsStartNewSolveButton = new JButton();
		resetCubeButton = new JButton("Reset Cube");
		timingDetailsComponentsPanel.setVisible(true);

		timingDetailsComponentsPanel.setLayout(new BoxLayout(timingDetailsComponentsPanel, BoxLayout.PAGE_AXIS));
		timingDetailsComponentsPanel.setPreferredSize(new Dimension(800, 190));
		timingDetailsComponentsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		timingDetailsTextArea.setLineWrap(true);
		timingDetailsTextArea.setWrapStyleWord(true);
		timingDetailsTextArea.setEditable(false);
		timingDetailsTextArea.setBorder(BorderFactory.createLineBorder(Color.black));
		timingDetailsTextArea.setFont(new Font("Arial", Font.PLAIN, textSize));

		timingDetailsScrollPane.setPreferredSize(new Dimension(700, 150));
		timingDetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		timingDetailsResetSessionButton.setSize(150, 20);
		timingDetailsResetSessionButton.setText("Reset Times");
		timingDetailsResetSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solves.clear();
				copyAllTimesToDisplay();
				refreshTimeGraph(true);
				refreshStatistics();
			}
		});

		timingDetailsStartNewSolveButton.setSize(150, 20);
		timingDetailsStartNewSolveButton.setText("Start New Solve");
		timingDetailsStartNewSolveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (((customPaintingInProgress) || (tutorialIsRunning))
						|| ((inspectionTimer != null) && (inspectionTimer.isRunning()))
						|| ((incTimer != null) && (incTimer.isRunning()))
						|| ((realTimeSolutionTimer != null) && (realTimeSolutionTimer.isRunning()))) {
					cubePanel.requestFocus();
					return;
				}

				if (!isValidCubeState(false)) {
					int choice = MouseSelectionSolver
							.getQuestionDialogResponse("The cube is not in a valid state, do you want to reset it?");

					if (choice == 0)
						resetCube();
					else
						return;
				}

				if (MenuBar.isUsingScramblesInList()) {
					try {
						currentScramble = scramblePopUp.getCurrentScramble();
					} catch (IndexOutOfBoundsException scrambleListIsEmpty) {
						JOptionPane.showMessageDialog(totalFrame, "No scrambles in scramble list", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					currentScramble = Scramble.generateScramble();
				}

				movesAllowed = false;
				timerHasPermissionToStart = true;
				movesToBeRecorded = true;
				cubeSolved = false;

				inspectionTimeRemaining = preferencesPopUp.getInspectionTime();
				inspectionTimer = new Timer(1000, new InspectionTimerListener());
				MenuBar.setSolvePieceSelected(false);
				inspectionTimer.start();
				currentPenalty = "0";

				timeLabel.setForeground(Color.red);
				timeLabel.setText("" + inspectionTimeRemaining);

				scrambleLabel.setText("Scramble: " + currentScramble);
				resetCube();
				solveMaster.rotateToTopFront(Color.white, Color.green);
				solveMaster.clearMoves();
				trackingMoves.clear();

				cube.performAbsoluteMoves(currentScramble);
				cubePanel.repaint();
				cubePanel.requestFocus();
			}
		});
		timingDetailsStartNewSolveButton.setMnemonic(KeyEvent.VK_1);
		timingDetailsStartNewSolveButton.setToolTipText("Alt + 1");

		resetCubeButton.setSize(150, 20);
		resetCubeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelSolve();
				resetCube();
				refreshStatistics();
				cubePanel.repaint();
				cubePanel.requestFocus();
			}
		});

		timingDetailsButtonPanel.add(timingDetailsResetSessionButton);
		timingDetailsButtonPanel.add(timingDetailsStartNewSolveButton);
		timingDetailsButtonPanel.add(resetCubeButton);

		timingDetailsComponentsPanel.add(timingDetailsScrollPane);
		timingDetailsComponentsPanel.add(timingDetailsButtonPanel);

		// totalFrame.getContentPane().add(tutorialComponentsPanel,
		// BorderLayout.SOUTH);
		// totalFrame.getContentPane().add(timingDetailsPanel,
		// BorderLayout.SOUTH);

		bottomPanel.add(tutorialComponentsPanel);
		bottomPanel.add(timingDetailsComponentsPanel);

		totalFrame.add(bottomPanel, BorderLayout.SOUTH);

		/************************* BOTTOM PANELS ************************/

		totalPaintingPanel.add(cubePanel);
		// Display the window.
		totalFrame.pack();
		totalFrame.setExtendedState(totalFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		totalFrame.setVisible(true);
		totalFrame.setResizable(true);
		totalFrame.setMinimumSize(new Dimension(500, 500));
		totalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		refreshStatistics();

		JOptionPane.showMessageDialog(totalFrame, "Make sure you backup your data regularly", "Tip",
				JOptionPane.INFORMATION_MESSAGE);

	}

	/**
	 * Runs the program
	 * 
	 * @param s
	 *            runtime argument
	 */
	public static void main(String s[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

		/*
		 * JFrame f = new JFrame("ShapesDemo2D");
		 * 
		 * final Component applet = new CubeDrawingTest(); applet.setSize(600,
		 * 600);
		 * 
		 * JPanel totalGUI = new JPanel(); totalGUI.setLayout(null);
		 * totalGUI.addKeyListener(new KeyListener() {
		 * 
		 * @Override public void keyPressed(KeyEvent arg0) {
		 * 
		 * }
		 * 
		 * @Override public void keyReleased(KeyEvent arg0) {
		 * MyColors.changeColour(); applet.repaint(); }
		 * 
		 * @Override public void keyTyped(KeyEvent arg0) {
		 * 
		 * } });
		 * 
		 * f.getContentPane().add("Center", totalGUI); totalGUI.add("Center",
		 * applet);
		 * 
		 * 
		 * JButton changeColourButton = new JButton("Shuffle");
		 * changeColourButton.setSize(120, 30);
		 * changeColourButton.setLocation(0,0);
		 * changeColourButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent arg0) {
		 * MyColors.changeColour(); applet.repaint(); }
		 * 
		 * }); totalGUI.add(changeColourButton);
		 * 
		 * f.pack(); f.setExtendedState(f.getExtendedState() |
		 * JFrame.MAXIMIZED_BOTH); f.setVisible(true); f.setMinimumSize(new
		 * Dimension(700, 450));
		 * f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 */
	}

}