package jCube;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * @author Kelsey McKenna
 */
public class Preferences extends JFrame implements ActionListener {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This indicates the vertical spacing beween the elements in the window.
	 */
	private static final int ySpacing = 40;
	/**
	 * This stores the padding of the elements in the window. The greater the
	 * padding, the further towards the centre of the window the elements will
	 * be.
	 */
	private static final int pad = 5;
	/**
	 * This stores the width of the window.
	 */
	private static final int FRAME_WIDTH = 400;
	/**
	 * This stores (FRAME_WIDTH/2) and is used to identify the middle
	 * x-coordinate of the window.
	 */
	private static final int MIDDLE_X = FRAME_WIDTH / 2;
	/**
	 * This stores the width of each label and text field added to the window.
	 */
	private static final int COMPONENT_WIDTH = MIDDLE_X - pad - 5;
	/**
	 * This stores the height of each label and text field added to the window.
	 */
	private static final int COMPONENT_HEIGHT = 40;
	/**
	 * This stores the default real-time solving speed.
	 */
	private static final int REAL_TIME_SOLVING_SPEED = 250;
	/**
	 * This stores the default inspection time.
	 */
	private static final int INSPECTION_TIME = 15;
	/**
	 * This stores the default scramble text size.
	 */
	private static final int SCRAMBLE_TEXT_SIZE = 27;

	/**
	 * This stores all components of the window other than buttons.
	 */
	private JPanel fieldPanel;
	/**
	 * This stores the buttons in the window.
	 */
	private JPanel buttonPanel;

	/**
	 * This label is shown in the window with the text ‘Real-time solving speed
	 * (ms)’.
	 */
	private JLabel realTimeSolvingRateLabel;
	/**
	 * This label is shown in the window with the text ‘Inspection time
	 * (seconds)'.
	 */
	private JLabel inspectionTimeLabel;
	/**
	 * This label is shown in the window with the text ‘Show Click-to-Solve
	 * warning’.
	 */
	private JLabel solvePieceWarningLabel;
	/**
	 * This label is shown in the window with the text ‘Scramble text size'.
	 */
	private JLabel scrambleTextSizeLabel;

	/**
	 * This field is shown in the window and the user can enter the speed into
	 * this field.
	 */
	private JTextField realTimeSolvingSpeedField;
	/**
	 * This field is shown in the window and the user can enter the inspection
	 * time into this field.
	 */
	private JTextField inspectionTimeField;
	/**
	 * This field is shown in the window and the user can enter the scramble
	 * text size this field.
	 */
	private JTextField scrambleTextSizeField;

	/**
	 * This button group stores the solvePieceWarningYesItem and the
	 * solvePieceWarningNoItem radio buttons. Using the button group, only one
	 * of the two radio buttons can be selected, not both.
	 */
	private static ButtonGroup solvePieceWarningButtonGroup;
	/**
	 * By selecting this radio button, a warning/information message will be
	 * shown when ‘Click to Solve’ mode is enabled. If the
	 * solvePieceWarningNoItem is selected, then no message will be shown.
	 */
	private static JRadioButton solvePieceWarningYesItem;
	/**
	 * By selecting this radio button, the warning/information message will not
	 * be shown when ‘Click to Solve’ mode is enabled. If the
	 * solvePieceWarningYesItem is selected, then the message will be shown.
	 */
	private static JRadioButton solvePieceWarningNoItem;

	/**
	 * Clicking this button submits the data in the window for validation. If
	 * the data is valid, then the window Preferences window will close.
	 */
	private JButton saveAndCloseButton;
	/**
	 * Clicking this button discards any unsaved changes and closes the window.
	 */
	private JButton cancelButton;
	/**
	 * Clicking this button restores the default preferences and updates the
	 * fields accordingly.
	 */
	private JButton restoreDefaultsButton;

	/**
	 * This stores the current real-time solving speed saved in the preferences
	 * file.
	 */
	private static int realTimeSolvingSpeed = REAL_TIME_SOLVING_SPEED;
	/**
	 * This stores the current inspection time saved in the preferences file.
	 */
	private static int inspectionTime = INSPECTION_TIME;
	/**
	 * This stores the current scramble text size saved in the preferences file.
	 */
	private static int scrambleTextSize = SCRAMBLE_TEXT_SIZE;

	/**
	 * Constructor - sets up the 'Preferences' window
	 */
	public Preferences() {
		super("Preferences");

		fieldPanel = new JPanel();
		fieldPanel.setLayout(null);

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		// setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setLocation(600, 150);
		setVisible(false);
		addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				populateFieldsWithValues();
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

		int y = 0;

		fieldPanel = new JPanel();
		fieldPanel.setLayout(null);

		/************* FIELDS AND LABELS *************/

		realTimeSolvingRateLabel = new JLabel("Real-time solving speed (ms)");
		realTimeSolvingRateLabel.setSize(COMPONENT_WIDTH, COMPONENT_HEIGHT);
		realTimeSolvingRateLabel.setLocation(0 + pad, y + pad);

		realTimeSolvingSpeedField = new JTextField();
		realTimeSolvingSpeedField.setSize(COMPONENT_WIDTH, 40);
		realTimeSolvingSpeedField.setLocation(MIDDLE_X, y + pad);
		realTimeSolvingSpeedField.addActionListener(this);
		realTimeSolvingSpeedField.setToolTipText("0 < x < 10000");

		y += ySpacing;

		inspectionTimeLabel = new JLabel("Inspection time (seconds)");
		inspectionTimeLabel.setSize(COMPONENT_WIDTH, COMPONENT_HEIGHT);
		inspectionTimeLabel.setLocation(0 + pad, y + pad);

		inspectionTimeField = new JTextField();
		inspectionTimeField.setSize(COMPONENT_WIDTH, COMPONENT_HEIGHT);
		inspectionTimeField.setLocation(MIDDLE_X, y + pad);
		inspectionTimeField.addActionListener(this);
		inspectionTimeField.setToolTipText("0 < x < 100");

		y += ySpacing;

		solvePieceWarningLabel = new JLabel();
		solvePieceWarningLabel.setText("Show Click-to-Solve warning");
		solvePieceWarningLabel.setSize(COMPONENT_WIDTH, COMPONENT_HEIGHT);
		solvePieceWarningLabel.setLocation(0 + pad, y + pad);

		solvePieceWarningButtonGroup = new ButtonGroup();

		solvePieceWarningYesItem = new JRadioButton("Yes");
		solvePieceWarningYesItem.setEnabled(true);
		solvePieceWarningYesItem.setFocusable(false);
		solvePieceWarningYesItem.setSize(COMPONENT_WIDTH / 2, COMPONENT_HEIGHT);
		solvePieceWarningYesItem.setLocation(MIDDLE_X, y + pad);

		solvePieceWarningNoItem = new JRadioButton("No");
		solvePieceWarningNoItem.setFocusable(false);
		solvePieceWarningNoItem.setSize(COMPONENT_WIDTH / 2, COMPONENT_HEIGHT);
		solvePieceWarningNoItem.setLocation(MIDDLE_X + COMPONENT_WIDTH / 2, y + pad);

		solvePieceWarningButtonGroup.add(solvePieceWarningYesItem);
		solvePieceWarningButtonGroup.add(solvePieceWarningNoItem);

		y += ySpacing;

		scrambleTextSizeLabel = new JLabel();
		scrambleTextSizeLabel.setText("Scramble text size");
		scrambleTextSizeLabel.setSize(COMPONENT_WIDTH, COMPONENT_HEIGHT);
		scrambleTextSizeLabel.setLocation(0 + pad, y + pad);

		scrambleTextSizeField = new JTextField(scrambleTextSize + "");
		scrambleTextSizeField.setSize(COMPONENT_WIDTH, COMPONENT_HEIGHT);
		scrambleTextSizeField.setLocation(MIDDLE_X, y + pad);
		scrambleTextSizeField.addActionListener(this);
		scrambleTextSizeField.setToolTipText("0 < x < 100");

		y += ySpacing;

		fieldPanel.add(realTimeSolvingRateLabel);
		fieldPanel.add(realTimeSolvingSpeedField);
		fieldPanel.add(inspectionTimeLabel);
		fieldPanel.add(inspectionTimeField);
		fieldPanel.add(solvePieceWarningLabel);
		fieldPanel.add(solvePieceWarningYesItem);
		fieldPanel.add(solvePieceWarningNoItem);
		fieldPanel.add(scrambleTextSizeLabel);
		fieldPanel.add(scrambleTextSizeField);

		fieldPanel.setPreferredSize(new Dimension(FRAME_WIDTH, y + 30));
		/************* END FIELDS AND LABELS *************/

		/************* BUTTON PANE *************/
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout());

		buttonPanel.setSize(0, 30);

		saveAndCloseButton = new JButton("Save and Close");
		saveAndCloseButton.setFocusable(false);
		saveAndCloseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean valid = true;

				try {
					realTimeSolvingSpeed = (int) Double.parseDouble(realTimeSolvingSpeedField.getText().trim());

					if ((realTimeSolvingSpeed <= 0) || (realTimeSolvingSpeed >= 10000)) {
						throw new Exception();
					}

					realTimeSolvingSpeed = Math.abs(realTimeSolvingSpeed);
					realTimeSolvingSpeedField.setBackground(Color.white);
				} catch (Exception exc) {
					realTimeSolvingSpeedField.setBackground(new Color(255, 150, 150));
					valid = false;
				}

				try {
					inspectionTime = (int) Double.parseDouble(inspectionTimeField.getText().trim());

					if ((inspectionTime <= 0) || (inspectionTime >= 100)) {
						throw new Exception();
					}

					inspectionTimeField.setBackground(Color.white);
				} catch (Exception exc) {
					inspectionTimeField.setBackground(new Color(255, 150, 150));
					valid = false;
				}

				try {
					scrambleTextSize = (int) Double.parseDouble(scrambleTextSizeField.getText().trim());

					if ((scrambleTextSize <= 0) || (scrambleTextSize >= 100))
						throw new Exception();

					scrambleTextSizeField.setBackground(Color.white);
				} catch (Exception exc) {
					scrambleTextSizeField.setBackground(new Color(255, 150, 150));
					valid = false;
				}

				if (valid) {
					setVisible(false);
					savePreferences();
					populateFieldsWithValues();
					Main.setScrambleTextSize(scrambleTextSize);
				}
			}
		});

		cancelButton = new JButton("Cancel");
		cancelButton.setFocusable(false);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				populateFieldsWithValues();
			}
		});

		restoreDefaultsButton = new JButton("Restore Defaults");
		restoreDefaultsButton.setFocusable(false);
		restoreDefaultsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				restoreDefaults();
			}
		});

		buttonPanel.add(saveAndCloseButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(restoreDefaultsButton);

		/************* END BUTTON PANE *************/

		populateFieldsWithValues();
		getContentPane().add(fieldPanel);
		getContentPane().add(buttonPanel);

		pack();
	}

	/**
	 * @return the real-time solving speed in milliseconds
	 */
	public int getRealTimeSolvingRate() {
		return realTimeSolvingSpeed;
	}

	/**
	 * @return the time allowed to the inspect the cube before a solve starts
	 *         (seconds)
	 */
	public int getInspectionTime() {
		return inspectionTime;
	}

	/**
	 * @return <b>true</b> if the solvePieceWarningYesItem is selected; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean solvePieceWarningEnabled() {
		return solvePieceWarningYesItem.isSelected();
	}

	/**
	 * @return the scramble text size as saved in the 'Preferences' window
	 * 
	 */
	public int getScrambleTextSize() {
		return scrambleTextSize;
	}

	/**
	 * This method is called when the enter key is pressed when typing data into
	 * one of the text fields.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		saveAndCloseButton.doClick();
	}

	/**
	 * Retrieves the data from preferences.txt and inserts it into the text
	 * fields
	 */
	public void populateFieldsWithValues() {
		TextFile currentFile = new TextFile();
		try {
			currentFile.setFilePath("settings/preferences.txt");
			currentFile.setIO(TextFile.READ);

			String[] data = currentFile.readAllLines();

			realTimeSolvingSpeedField.setText(data[0].trim());
			inspectionTimeField.setText(data[1].trim());
			solvePieceWarningYesItem.setSelected((data[2].trim().equalsIgnoreCase("Yes")) ? true : false);
			solvePieceWarningNoItem.setSelected(!solvePieceWarningYesItem.isSelected());
			scrambleTextSizeField.setText(data[3].trim());

			try {
				realTimeSolvingSpeed = Integer.valueOf(data[0].trim());
				inspectionTime = Integer.valueOf(data[1].trim());
				scrambleTextSize = Integer.valueOf(data[3].trim());
			} catch (Exception e) {

			}

			realTimeSolvingSpeedField.setBackground(Color.white);
			inspectionTimeField.setBackground(Color.white);
			scrambleTextSizeField.setBackground(Color.white);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not open preferences file. Restoring defaults.", "Error",
					JOptionPane.ERROR_MESSAGE);
			restoreDefaults();
		} finally {
			currentFile.close();
		}
	}

	/**
	 * Restores and saves the system's preferences to its default settings
	 */
	private void restoreDefaults() {
		solvePieceWarningYesItem.setSelected(true);
		solvePieceWarningNoItem.setSelected(false);

		realTimeSolvingSpeedField.setText(String.format("%d", REAL_TIME_SOLVING_SPEED));
		realTimeSolvingSpeed = REAL_TIME_SOLVING_SPEED;
		realTimeSolvingSpeedField.setBackground(Color.white);

		inspectionTimeField.setText(String.format("%d", INSPECTION_TIME));
		inspectionTime = INSPECTION_TIME;
		inspectionTimeField.setBackground(Color.white);

		scrambleTextSize = SCRAMBLE_TEXT_SIZE;
		scrambleTextSizeField.setText("" + SCRAMBLE_TEXT_SIZE);
		scrambleTextSizeField.setBackground(Color.white);

		solvePieceWarningEnabled();
		Main.setScrambleTextSize(scrambleTextSize);
		savePreferences();
	}

	/**
	 * Writes the preferences to preferences.txt
	 */
	private void savePreferences() {
		TextFile currentFile = new TextFile();
		try {
			currentFile.setFilePath("settings/preferences.txt");
			currentFile.setIO(TextFile.WRITE);

			currentFile.writeLine(realTimeSolvingSpeed);
			currentFile.writeLine(inspectionTime);
			currentFile.writeLine((solvePieceWarningYesItem.isSelected()) ? "Yes" : "No");
			currentFile.writeLine(scrambleTextSize);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not save preferences to file", "Error",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			currentFile.close();
		}
	}

}
