package jCube;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Kelsey McKenna
 */
public class TimeListPopUp extends JFrame implements KeyListener {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
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
	 * This indicates the vertical spacing between the buttons in the window.
	 */
	private final int buttonYSpacing = 40;

	/**
	 * This variable keeps track of the current y position of the last element
	 * placed in the window.
	 */
	private int y = 0;

	/**
	 * This panel stores the elements of the Solve Editor window.
	 */
	private JPanel contentPane;

	/**
	 * This label is shown in the Solve Editor window with the text ‘Time’.
	 */
	private JLabel timeLabel;
	/**
	 * This label is shown in the Solve Editor window with the text ‘Penalty’.
	 */
	private JLabel penaltyLabel;
	/**
	 * This label is shown in the Solve Editor window with the text ‘Comment’.
	 */
	private JLabel commentLabel;
	/**
	 * This label is shown in the Solve Editor window with the text ‘Scramble’.
	 */
	private JLabel scrambleLabel;
	/**
	 * This label is shown in the Solve Editor window with the text ‘Solution’.
	 */
	private JLabel solutionLabel;

	/**
	 * This field is shown in the Solve Editor window and the user can enter the
	 * time into this field.
	 */
	private JTextField timeField;
	/**
	 * This field is shown in the Solve Editor window and the user can enter the
	 * penalty into this field.
	 */
	private JTextField penaltyField;
	/**
	 * This field is shown in the Solve Editor window and the user can enter the
	 * comment into this field.
	 */
	private JTextArea commentField;
	/**
	 * This field is shown in the Solve Editor window and the user can enter the
	 * scramble into this field.
	 */
	private JTextArea scrambleField;
	/**
	 * This field is shown in the Solve Editor window and the user can enter the
	 * solution into this field.
	 */
	private JTextArea solutionField;

	/**
	 * commentField is placed ‘inside’ this variable so that when the length of
	 * the comment exceeds the size of commentField, the user can scroll to view
	 * the rest of the comment.
	 */
	private JScrollPane commentScrollPane;
	/**
	 * scrambleField is placed ‘inside’ this variable so that when the length of
	 * the scramble exceeds the size of scrambleField, the user can scroll to
	 * view the rest of the scramble.
	 */
	private JScrollPane scrambleScrollPane;
	/**
	 * solutionField is placed ‘inside’ this variable so that when the length of
	 * the solution exceeds the size of solutionField, the user can scroll to
	 * view the rest of the solution.
	 */
	private JScrollPane solutionScrollPane;

	/**
	 * Clicking this button submits the data in the Solve Editor form for
	 * validation.
	 */
	private JButton submitButton;
	/**
	 * Clicking this button opens a save dialog so that the information in the
	 * Solve Editor window can be saved to a chosen location.
	 */
	private JButton saveToFileButton;
	/**
	 * Clicking this button discards any changes and reloads the information in
	 * the window.
	 */
	private JButton restoreValuesButton;
	/**
	 * Clicking this button deletes the time/solve being viewed
	 */
	private JButton deleteTimeButton;
	/**
	 * Clicking this button scrambles the cube in the main window and performs
	 * the solution in real time.
	 */
	private JButton viewExecutionButton;

	/**
	 * This stores the Solve currently being edited.
	 */
	private Solve currentTime = new Solve("0", "0", "");
	/**
	 * Stores the font to be used in the text fields.
	 */
	private Font fieldFont = new Font("Arial", 0, 25);
	/**
	 * This variable stores the text that is shown in the error message when the
	 * user enters an invalid time.
	 */
	private String helpMessage = "You entered the time incorrectly\n\n" + "Valid formats include:\n" + "MM:SS.ss\n"
			+ "MM:S.ss\n" + "M:SS.ss\n" + "M:S.ss\n" + "SS.ss\n" + "S.ss\n" + "DNF\n";

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
	 * Constructor - sets up the Solve Editor window
	 * 
	 * @param applicationTitle
	 *            the title for the window
	 */
	public TimeListPopUp(String applicationTitle) {
		super(applicationTitle);

		contentPane = new JPanel();
		contentPane.setLayout(null);

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setContentPane(contentPane);
		setPreferredSize(new Dimension(480, 520));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setLocation(600, 150);

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

		// ***************Text Fields*************************

		y = 0;

		timeField = new JTextField();
		timeField.setMargin(new Insets(0, 10, 0, 15));
		timeField.setFont(fieldFont);
		timeField.setSize(350, 40);
		timeField.setLocation(100 + pad, y + pad);
		timeField.addKeyListener(this);

		y += fieldYSpacing;

		penaltyField = new JTextField(currentTime.getPenalty());
		penaltyField.setMargin(new Insets(0, 10, 0, 15));
		penaltyField.setFont(new Font("Arial", 0, 15));
		penaltyField.setSize(350, 40);
		penaltyField.setLocation(100 + pad, y + pad);
		penaltyField.addKeyListener(this);

		y += fieldYSpacing;

		commentField = new JTextArea();
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

		scrambleField = new JTextArea();
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

		// ************Submission Button**********
		y = 290;

		submitButton = new JButton("Submit");
		submitButton.setSize(480, 30);
		submitButton.setLocation(0, y + pad);
		submitButton.setFocusable(false);
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String time = timeField.getText().trim();

				if (!Solve.isValidTime(time)) {
					JOptionPane.showMessageDialog(contentPane, helpMessage, "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {
					if (time.equalsIgnoreCase("DNF")) {
						currentTime.setStringTime("DNF");
						currentTime.setPenalty("0");
					} else {
						currentTime.setStringTime(Solve.getPaddedTime(Solve.getSecondsToFormattedString(Solve
								.getFormattedStringToDouble(time))));
						currentTime.setPenalty(penaltyField.getText().trim());
					}

					if (penaltyField.getText().trim().equals(""))
						currentTime.setPenalty("0");

					currentTime.setComment(commentField.getText().trim());
					currentTime.setScramble(scrambleField.getText().trim());
					currentTime.setSolution(solutionField.getText().trim());
					setVisible(false);
					Main.refreshTimeGraph(true);
					Main.refreshStatistics();
				}
			}
		});

		y += buttonYSpacing;

		saveToFileButton = new JButton("Save to File");
		saveToFileButton.setSize(480, 30);
		saveToFileButton.setLocation(0, y + pad);
		saveToFileButton.setFocusable(false);
		saveToFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String time = timeField.getText().trim();

				if (!Solve.isValidTime(time)) {
					JOptionPane.showMessageDialog(contentPane, helpMessage, "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {
					if (time.equalsIgnoreCase("DNF")) {
						currentTime.setStringTime("DNF");
						currentTime.setPenalty("0");
					} else {
						currentTime.setStringTime(Solve.getPaddedTime(time));
						currentTime.setPenalty(penaltyField.getText().trim());
					}

					currentTime.setComment(commentField.getText().trim());
					currentTime.setScramble(scrambleField.getText().trim());
					currentTime.setSolution(solutionField.getText().trim());
					setVisible(false);

					if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						TextFile currentFile = new TextFile();

						try {
							currentFile.setFilePath(fileChooser.getSelectedFile().toString());
							currentFile.setIO(TextFile.WRITE);

							String comment = commentField.getText().trim();
							String scramble = scrambleField.getText().trim();
							String solution = solutionField.getText().trim();

							currentFile.writeLine(timeField.getText());
							currentFile.writeLine(penaltyField.getText());
							currentFile.writeLine(comment.equals("") ? "*" : comment);
							currentFile.writeLine(scramble.equals("") ? "*" : scramble);
							currentFile.write(solution.equals("") ? "*" : solutionField.getText());
						} catch (Exception e) {
							JOptionPane.showMessageDialog(contentPane, "Could not save to file", "Error",
									JOptionPane.ERROR_MESSAGE);
						} finally {
							currentFile.close();
						}
					}
				}
			}
		});

		y += buttonYSpacing;

		viewExecutionButton = new JButton("View Execution");
		viewExecutionButton.setSize(480, 30);
		viewExecutionButton.setLocation(0, y + pad);
		viewExecutionButton.setFocusable(false);
		viewExecutionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!(currentTime.getStringTime()).equals(timeField.getText())
						|| !(currentTime.getPenalty()).equals(penaltyField.getText())
						|| !(currentTime.getComment()).equals(commentField.getText())
						|| !(currentTime.getScramble()).equals(scrambleField.getText())
						|| !(currentTime.getSolution()).equals(solutionField.getText())) {
					JOptionPane.showMessageDialog(contentPane, "You must submit the form first.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					Main.performRealTimeSolving(currentTime.getScramble(), currentTime.getSolution());
				}
			}
		});

		y += buttonYSpacing;

		restoreValuesButton = new JButton("Restore");
		restoreValuesButton.setSize(480, 30);
		restoreValuesButton.setLocation(0, y + pad);
		restoreValuesButton.setFocusable(false);
		restoreValuesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timeField.setText(currentTime.getStringTime());
				penaltyField.setText(currentTime.getPenalty());
				commentField.setText(currentTime.getComment());
				scrambleField.setText(currentTime.getScramble());
				solutionField.setText(currentTime.getSolution());
			}
		});

		y += buttonYSpacing;

		deleteTimeButton = new JButton("Delete");
		deleteTimeButton.setSize(480, 30);
		deleteTimeButton.setLocation(0, y + pad);
		deleteTimeButton.setFocusable(false);
		deleteTimeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentTime.setStringTime("-1");
				Main.copyAllTimesToDisplay();
				Main.refreshTimeList();
				setVisible(false);
			}
		});

		contentPane.add(timeLabel);
		contentPane.add(penaltyLabel);
		contentPane.add(commentLabel);
		contentPane.add(scrambleLabel);
		contentPane.add(solutionLabel);

		contentPane.add(timeField);
		contentPane.add(penaltyField);
		contentPane.add(commentScrollPane);
		contentPane.add(scrambleScrollPane);
		contentPane.add(solutionScrollPane);

		contentPane.add(submitButton);
		contentPane.add(saveToFileButton);
		contentPane.add(viewExecutionButton);
		contentPane.add(restoreValuesButton);
		contentPane.add(deleteTimeButton);
	}

	/**
	 * Selects the text in the time field so that the user can edit it without
	 * having to use backspace.
	 */
	public void selectAllTimeText() {
		timeField.selectAll();
	}

	/**
	 * Sets the current solve to work with in the Solve Editor window.
	 * 
	 * @param currentSolve
	 *            the solve to work with.
	 */
	public void setSolve(Solve currentSolve) {
		this.currentTime = currentSolve;
		currentSolve.setStringTime(Solve.getPaddedTime(currentSolve.getStringTime()));
		restoreValuesButton.doClick();
	}

	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		Main.transferFormFocus(arg0);
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		Object source = arg0.getSource();

		if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			if (!arg0.isShiftDown())
				submitButton.doClick();
			else if (source.equals(commentField) || 
					source.equals(scrambleField) || 
					source.equals(solutionField)) {
				((JTextArea) source).setText(((JTextArea) source).getText() + "\n");
			}
		}
	}
}
