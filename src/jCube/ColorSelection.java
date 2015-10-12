package jCube;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Kelsey McKenna
 */
public class ColorSelection extends JFrame implements MouseListener {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The contents of the window are placed in this JPanel
	 */
	private JPanel contentPane;

	/**
	 * This label is displayed at the bottom of the window with a black
	 * background and shows the text 'COLOR'. The foreground of the text
	 * indicates the selected colour
	 */
	private JLabel selectedColorLabel;

	/**
	 * This is displayed as a white rectangle on the screen. When clicked, the
	 * selected colour is changed to white.
	 */
	private JTextField whiteButton;
	/**
	 * This is displayed as a yellow rectangle on the screen. When clicked, the
	 * selected colour is changed to yellow.
	 */
	private JTextField yellowButton;
	/**
	 * This is displayed as a red rectangle on the screen. When clicked, the
	 * selected colour is changed to red.
	 */
	private JTextField redButton;
	/**
	 * This is displayed as an orange rectangle on the screen. When clicked, the
	 * selected colour is changed to orange.
	 */
	private JTextField orangeButton;
	/**
	 * This is displayed as a green rectangle on the screen. When clicked, the
	 * selected colour is changed to green.
	 */
	private JTextField greenButton;
	/**
	 * This is displayed as a blue rectangle on the screen. When clicked, the
	 * selected colour is changed to blue.
	 */
	private JTextField blueButton;

	/**
	 * This stores the selected color
	 */
	private Color selectedColor = Color.white;

	/**
	 * This indicates the width of the window
	 */
	private static final int WIDTH = 215;
	/**
	 * This indicates the height of the window
	 */
	private static final int HEIGHT = 270;
	/**
	 * This indicates the height of each of the rectangles in the window
	 */
	private static final int BUTTON_HEIGHT = HEIGHT / 7 - 3;

	/**
	 * Constructor - sets up the window
	 */
	public ColorSelection() {
		super("Color Selection");

		int y = 0;

		contentPane = new JPanel();
		contentPane.setLayout(null);

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setContentPane(contentPane);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setSize(new Dimension(WIDTH, HEIGHT));
		setResizable(false);
		setFocusable(false);
		setAlwaysOnTop(true);

		whiteButton = new JTextField();
		whiteButton.setEditable(false);
		whiteButton.addMouseListener(this);

		yellowButton = new JTextField();
		yellowButton.setEditable(false);
		yellowButton.addMouseListener(this);

		redButton = new JTextField();
		redButton.setEditable(false);
		redButton.addMouseListener(this);

		orangeButton = new JTextField();
		orangeButton.setEditable(false);
		orangeButton.addMouseListener(this);

		greenButton = new JTextField();
		greenButton.setEditable(false);
		greenButton.addMouseListener(this);

		blueButton = new JTextField();
		blueButton.setEditable(false);
		blueButton.addMouseListener(this);

		selectedColorLabel = new JLabel("COLOR", SwingConstants.CENTER);
		selectedColorLabel.setForeground(Color.BLACK);
		selectedColorLabel.setBackground(selectedColor.brighter());
		selectedColorLabel.setOpaque(true);
		selectedColorLabel.setFont(new Font("Lucida Sans", 0, 25));

		whiteButton.setBackground(Color.white);
		yellowButton.setBackground(Color.yellow);
		redButton.setBackground(Color.red);
		orangeButton.setBackground(Cubie.orange);
		greenButton.setBackground(Color.green);
		blueButton.setBackground(Color.blue);

		whiteButton.setSize(WIDTH, BUTTON_HEIGHT);
		whiteButton.setLocation(0, y);
		y += BUTTON_HEIGHT;

		yellowButton.setSize(WIDTH, BUTTON_HEIGHT);
		yellowButton.setLocation(0, y);
		y += BUTTON_HEIGHT;

		redButton.setSize(WIDTH, BUTTON_HEIGHT);
		redButton.setLocation(0, y);
		y += BUTTON_HEIGHT;

		orangeButton.setSize(WIDTH, BUTTON_HEIGHT);
		orangeButton.setLocation(0, y);
		y += BUTTON_HEIGHT;

		greenButton.setSize(WIDTH, BUTTON_HEIGHT);
		greenButton.setLocation(0, y);
		y += BUTTON_HEIGHT;

		blueButton.setSize(WIDTH, BUTTON_HEIGHT);
		blueButton.setLocation(0, y);
		y += BUTTON_HEIGHT;

		selectedColorLabel.setSize(WIDTH, BUTTON_HEIGHT);
		selectedColorLabel.setLocation(0, y);

		contentPane.add(whiteButton);
		contentPane.add(yellowButton);
		contentPane.add(redButton);
		contentPane.add(orangeButton);
		contentPane.add(greenButton);
		contentPane.add(blueButton);
		contentPane.add(selectedColorLabel);

		setVisible(false);
	}

	/**
	 * @return the selected colour to be painted on the cube in the main window
	 */
	public Color getSelectedColor() {
		return selectedColor;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		Object source = arg0.getSource();

		selectedColorLabel.setForeground(Color.BLACK);

		if (source == whiteButton)
			selectedColor = (Color.white);
		else if (source == yellowButton)
			selectedColor = (Color.yellow);
		else if (source == redButton)
			selectedColor = (Color.red);
		else if (source == orangeButton)
			selectedColor = (Cubie.orange);
		else if (source == greenButton)
			selectedColor = (Color.green);
		else if (source == blueButton) {
			selectedColorLabel.setForeground(Color.white);
			selectedColor = (Color.blue);
		}

		selectedColorLabel.setBackground(selectedColor.brighter());
		Main.requestCubePanelFocus();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}
