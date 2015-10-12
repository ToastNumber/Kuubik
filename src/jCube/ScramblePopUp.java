package jCube;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * @author Kelsey McKenna
 */
public class ScramblePopUp extends JFrame {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of scrambles is stored in this panel.
	 */
	private JPanel scrambleListPanel;
	/**
	 * This stores the contents (scrambles) in the list.
	 */
	private DefaultListModel<String> scrambleList;
	/**
	 * This is the list that is added to scrambleListPanel so that the contents
	 * of the list can be displayed.
	 */
	private JList<String> listHolder;
	/**
	 * listHolder is placed ‘inside’ this variable so that once the size of the
	 * list exceeds the size of the window, the user can scroll to view the rest
	 * of the list.
	 */
	private JScrollPane listScrollPane;

	/**
	 * The buttons of the window are stored on this panel.
	 */
	private JPanel buttonPanel;

	/**
	 * Clicking this button opens an input dialog into which the user can enter
	 * a new scramble.
	 */
	private JButton addScrambleButton;
	/**
	 * Clicking this button opens an input dialog displaying the existing
	 * scramble, which the user can edit.
	 */
	private JButton editScrambleButton;
	/**
	 * Clicking this button deletes the selected scrambles from the list
	 */
	private JButton deleteScrambleButton;
	/**
	 * Clicking this button opens a save dialog in which the user can choose a
	 * location to save the selected scrambles
	 */
	private JButton saveScrambleToFileButton;
	/**
	 * Clicking this button opens a load dialog in which the user can select the
	 * text file which contains the scrambles they want to load
	 */
	private JButton loadScrambleFromFileButton;
	/**
	 * Clicking this button applies hte selected scramble to the cube in the
	 * main window and displays the scramble at the top of the main window
	 */
	private JButton applyScrambleButton;

	/**
	 * This stores the index of the next scramble to be used if the ‘Use
	 * Scrambles in List’ option is selected in the main window.
	 */
	private int scrambleIndex = 0;

	/**
	 * This variable is used to select a location to save or load scrambles.
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
	 * Constructor - sets up the "Scramble List" window
	 */
	public ScramblePopUp() {
		super("Scramble List");

		initListPanel();

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		setVisible(false);
		setPreferredSize(new Dimension(500, 285));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 3));

		addScrambleButton = new JButton("Add Scramble");
		addScrambleButton.setFocusable(false);
		addScrambleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String scramble = JOptionPane.showInputDialog(null, "Enter Scramble", "R U R' U'");

				if (scramble != null && (!scramble.trim().equals(""))) {
					scrambleList.addElement(" " + scramble.trim());
					listHolder.ensureIndexIsVisible(scrambleList.getSize() - 1);
					listHolder.setSelectedIndex(scrambleList.getSize() - 1);
				}
			}
		});

		editScrambleButton = new JButton("Edit Scramble");
		editScrambleButton.setFocusable(false);
		editScrambleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = listHolder.getSelectedIndex();

				if (selectedIndex == -1)
					return;
				else {
					String scramble = scrambleList.get(selectedIndex);
					scramble = JOptionPane.showInputDialog(null, "Enter New Scramble", scramble.trim());

					if (scramble != null && (!scramble.trim().equals(""))) {
						scrambleList.set(selectedIndex, " " + scramble.trim());
					}
				}
			}
		});

		deleteScrambleButton = new JButton("Delete Scramble");
		deleteScrambleButton.setFocusable(false);
		deleteScrambleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedIndex = listHolder.getSelectedIndex();

				if (selectedIndex == -1)
					return;
				else {
					int[] selectedIndices = listHolder.getSelectedIndices();

					for (int i = selectedIndices.length - 1; i >= 0; --i) {
						scrambleList.remove(selectedIndices[i]);
						listHolder.clearSelection();
					}

					if (scrambleList.size() > 0)
						listHolder.setSelectedIndex((selectedIndex < scrambleList.size()) ? selectedIndex
								: selectedIndex - 1);
				}
			}
		});

		applyScrambleButton = new JButton("Apply Scramble");
		applyScrambleButton.setFocusable(false);
		applyScrambleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = listHolder.getSelectedIndex();

				if (selectedIndex == -1)
					return;
				else
					Main.handleScramble(scrambleList.get(selectedIndex));
			}
		});

		saveScrambleToFileButton = new JButton("Save Selected to File");
		saveScrambleToFileButton.setFocusable(false);
		saveScrambleToFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = listHolder.getSelectedIndex();

				if (selectedIndex == -1)
					return;
				else {
					int[] selectedIndices = listHolder.getSelectedIndices();

					if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						TextFile currentFile = new TextFile();

						try {
							currentFile.setFilePath(fileChooser.getSelectedFile().toString());
							currentFile.setIO(TextFile.WRITE);

							for (int i = 0; i < selectedIndices.length; ++i) {
								currentFile.writeLine(scrambleList.get(selectedIndices[i]).trim());
							}
						} catch (Exception exc) {
							JOptionPane.showMessageDialog(scrambleListPanel, "Could not save to file", "Error",
									JOptionPane.ERROR_MESSAGE);
						} finally {
							currentFile.close();
						}
					}
				}
			}
		});

		loadScrambleFromFileButton = new JButton("Load Scrambles");
		loadScrambleFromFileButton.setFocusable(false);
		loadScrambleFromFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						String current;
						int numLines;

						TextFile currentFile = new TextFile();
						currentFile.setFilePath(fileChooser.getSelectedFile().toString());
						currentFile.setIO(TextFile.READ);
						numLines = currentFile.getNumLines();

						for (int i = 0; i < numLines; ++i) {
							current = currentFile.readLine().trim();

							if (!current.equals("")) {
								scrambleList.addElement(" " + current);
							}
						}

						currentFile.close();
					}
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(scrambleListPanel, "Error Reading File", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		buttonPanel.add(addScrambleButton);
		buttonPanel.add(editScrambleButton);
		buttonPanel.add(deleteScrambleButton);
		buttonPanel.add(applyScrambleButton);
		buttonPanel.add(saveScrambleToFileButton);
		buttonPanel.add(loadScrambleFromFileButton);

		getContentPane().add(buttonPanel);
	}

	/**
	 * Initialises the components associated with the list of scrambles in the
	 * window. Fonts, selection modes, and contents are declared here.
	 */
	private void initListPanel() {
		scrambleListPanel = new JPanel();
		scrambleList = new DefaultListModel<String>();

		listHolder = new JList<String>(scrambleList);
		listHolder.setFont(new Font("Arial", 0, 15));
		listHolder.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listHolder.setLayoutOrientation(JList.VERTICAL);
		listHolder.setSelectedIndex(0);
		listHolder.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (scrambleList.size() > 0)) {
					int selectedIndex = listHolder.getSelectedIndex();

					if (selectedIndex == -1)
						return;
					else {
						String scramble = scrambleList.get(selectedIndex);
						scramble = JOptionPane.showInputDialog(null, "Enter New Scramble", scramble.trim());

						if (scramble != null && (!scramble.trim().equals(""))) {
							scrambleList.set(selectedIndex, " " + scramble.trim());
						}
					}
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
					int[] selectedIndices = listHolder.getSelectedIndices();

					for (int i = 0; i < selectedIndices.length; ++i) {
						scrambleList.remove(selectedIndex);
						listHolder.clearSelection();
					}

					if (scrambleList.size() > 0)
						listHolder.setSelectedIndex((selectedIndex < scrambleList.size()) ? selectedIndex
								: selectedIndex - 1);
				} else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					String scramble = scrambleList.get(selectedIndex);
					scramble = JOptionPane.showInputDialog(null, "Enter New Scramble", scramble.trim());

					if (scramble != null && (!scramble.trim().equals(""))) {
						scrambleList.set(selectedIndex, " " + scramble.trim());
					}
				}
			}
		});

		listScrollPane = new JScrollPane(listHolder);
		listScrollPane.setPreferredSize(new Dimension(500, 200));
		scrambleListPanel.add(listScrollPane);

		listHolder.setFocusable(true);

		getContentPane().add(scrambleListPanel);
	}

	/**
	 * @return the next scramble in the list
	 * @throws IndexOutOfBoundsException
	 *             if there are no elements in the list
	 */
	public String getCurrentScramble() throws IndexOutOfBoundsException {
		if (scrambleIndex >= scrambleList.size())
			scrambleIndex = 0;

		String currentScramble = scrambleList.get(scrambleIndex);
		scrambleIndex = (scrambleIndex + 1) % scrambleList.size();
		return currentScramble;
	}

}
