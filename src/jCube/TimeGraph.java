package jCube;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author Kelsey McKenna
 */
public class TimeGraph extends JFrame {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This is the chart displayed on the screen.
	 */
	private JFreeChart chart;
	/**
	 * The chart is placed ‘inside’ this variable so that it can be displayed.
	 */
	private ChartPanel chartPanel;
	/**
	 * This stores the dataset that is rendered ont the graph.
	 */
	private DefaultCategoryDataset dataset;
	/**
	 * This stores the main title of the chart.
	 */
	private String chartTitle;
	/**
	 * This stores the label that is shown on the x-axis.
	 */
	private String xLabel;
	/**
	 * This stores the label that is shown on the y-axis.
	 */
	private String yLabel;
	/**
	 * This variables stores either ‘2’ or ‘3’, indicating that the graph should
	 * be displayed in 2D or 3D respectively.
	 */
	private int dimension = 2;
	/**
	 * This stores the properties of the stroke used to paint the graph.
	 */
	private BasicStroke stroke = new BasicStroke(4.0f);
	/**
	 * If true, then the window will be on top of all other windows, otherwise
	 * it can be hidden.
	 */
	private boolean alwaysOnTop = true;

	/**
	 * Constructor - assigns values to fields and sets up Time Graph window.
	 * 
	 * @param applicationTitle
	 *            the main title of the window
	 * @param chartTitle
	 *            the title of the chart
	 * @param xLabel
	 *            the label shown on the x axis
	 * @param yLabel
	 *            the label shown on the y axis
	 */
	public TimeGraph(String applicationTitle, String chartTitle, String xLabel, String yLabel) {
		super(applicationTitle);
		this.chartTitle = chartTitle;
		this.xLabel = xLabel;
		this.yLabel = yLabel;

		setIconImage(Main.createImage("res/images/RubikCubeBig.png"));
		setAlwaysOnTop(true);
		setJMenuBar(new MenuBar().createMenuBar());
	}

	/**
	 * Draws the graph in the window
	 */
	public void draw() {
		if (dimension == 2)
			chart = ChartFactory.createLineChart(chartTitle, xLabel, yLabel, dataset);
		else
			chart = ChartFactory.createLineChart3D(chartTitle, xLabel, yLabel, dataset);

		chart.removeLegend();

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.getRenderer().setSeriesPaint(0, Color.BLUE);
		plot.getRenderer().setSeriesStroke(0, stroke);

		chartPanel = new ChartPanel(chart);
		setContentPane(chartPanel);
	}

	/**
	 * Sets the title of the chart
	 * 
	 * @param chartTitle
	 *            the title of the chart
	 */
	public void setGraphTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	/**
	 * Resets the graph to its default zoom.
	 */
	public void resetGraphView() {
		chartPanel.restoreAutoBounds();
	}

	/**
	 * Sets the dataset for the graph
	 * 
	 * @param dataset
	 *            the dataset to be used to draw the graph
	 */
	public void setDataset(DefaultCategoryDataset dataset) {
		this.dataset = dataset;
	}

	/**
	 * @author Kelsey McKenna
	 */
	private class MenuBar {
		/**
		 * @author Kelsey McKenna
		 */
		private class ImageFilter extends FileFilter {
			/**
			 * Used to prevent typing errors etc.
			 */
			public final static String png = "png";

			/**
			 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
			 */
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				String extension = getExtension(f);
				if (extension != null) {
					if (extension.equals(png)) {
						return true;
					} else {
						return false;
					}
				}

				return false;
			}

			/**
			 * @see javax.swing.filechooser.FileFilter#getDescription()
			 */
			public String getDescription() {
				return ".png - Extension will be appended automatically.";
			}

			/**
			 * @param file
			 *            the file to be checked
			 * @return the extension of the file
			 */
			public String getExtension(File file) {
				String extension = null;
				String fileName = file.getName();
				int i = fileName.lastIndexOf('.');

				if ((i > 0) && (i < fileName.length() - 1)) {
					extension = fileName.substring(i + 1).toLowerCase();
				}
				return extension;
			}
		}

		/**
		 * This stores the contents of the menu bar
		 */
		private JMenuBar menuBar;
		/**
		 * Stores the contents of the file menu
		 */
		private JMenu fileMenu;
		/**
		 * Stores the contents of the view menu
		 */
		private JMenu viewMenu;
		/**
		 * Clicking this menu item opens a save dialog so that the graph can be
		 * saved as an image in the specified location.
		 */
		private JMenuItem saveImageItem;
		/**
		 * Clicking this button resets the graph to its default zoom.
		 */
		private JMenuItem resetZoomItem;
		/**
		 * Clicking this button closes the Time Graph window.
		 */
		private JMenuItem closeWindowItem;
		/**
		 * Clicking this button toggles whether the window is always on top.
		 */
		private JCheckBoxMenuItem alwaysOnTopItem;
		/**
		 * This holds the two dimension radio buttons so that only one of the
		 * radio buttons can be selected.
		 */
		private ButtonGroup dimensionButtonGroup;
		/**
		 * Selecting this radio button changes the graph so that it is rendered
		 * in 2D
		 */
		private JRadioButtonMenuItem radioButton2D;
		/**
		 * Selecting this radio button changes the graph so that it is rendered
		 * in 3D
		 */
		private JRadioButtonMenuItem radioButton3D;

		/**
		 * This variable is used to select a location to save or load scrambles.
		 */
		private JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop") {
			private static final long serialVersionUID = 1L;

			@Override
			public void approveSelection() {
				setSelectedFile(new File((("" + getSelectedFile()).replaceAll("\\.png", "")) + ".png"));
				File f = new File(getSelectedFile().toString());

				if ((f.exists()) && (getDialogType() == SAVE_DIALOG)) {
					int result = JOptionPane.showConfirmDialog(this, String.format(
							"The file %s already exists. Do you want to overwrite?", getSelectedFile().toString()),
							"Existing file", JOptionPane.YES_NO_OPTION);
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
		 * @return the menu bar for the Time Graph window
		 */
		public JMenuBar createMenuBar() {
			ImageIcon icon;
			menuBar = new JMenuBar();
			dimensionButtonGroup = new ButtonGroup();
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addChoosableFileFilter(new ImageFilter());

			/*********** FILE ***************/
			fileMenu = new JMenu("File");
			menuBar.add(fileMenu);

			icon = Main.createImageIcon("res/images/SaveIcon.png");
			saveImageItem = new JMenuItem("Save as Image", icon);
			saveImageItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean alwaysOnTopValue = alwaysOnTop;

					alwaysOnTop = false;
					setAlwaysOnTop(false);
					if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						int width = 1024, height = 768;
						File lineChart = new File(fileChooser.getSelectedFile() + "");

						try {
							ChartUtilities.saveChartAsPNG(lineChart, chart, width, height);
						} catch (IOException exc) {
						}
					}
					alwaysOnTop = alwaysOnTopValue;
					setAlwaysOnTop(alwaysOnTop);
				}
			});
			saveImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));

			closeWindowItem = new JMenuItem("Close Window");
			closeWindowItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setVisible(false);
				}
			});
			closeWindowItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));

			fileMenu.add(saveImageItem);
			fileMenu.addSeparator();
			fileMenu.add(closeWindowItem);
			/*********** END FILE ***************/

			/********* VIEW *********/
			viewMenu = new JMenu("View");
			menuBar.add(viewMenu);

			alwaysOnTopItem = new JCheckBoxMenuItem("Window Always on Top");
			alwaysOnTopItem.setSelected(true);
			alwaysOnTopItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					alwaysOnTop = !alwaysOnTop;
					setAlwaysOnTop(alwaysOnTop);
				}
			});

			resetZoomItem = new JMenuItem("Reset Zoom");
			resetZoomItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					resetGraphView();
					Main.refreshTimeGraph(false);
				}
			});
			resetZoomItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));

			radioButton2D = new JRadioButtonMenuItem("2D");
			radioButton2D.setSelected(true);
			radioButton2D.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dimension = 2;
					Main.refreshTimeGraph(false);
				}
			});
			radioButton2D.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK));

			radioButton3D = new JRadioButtonMenuItem("3D");
			radioButton3D.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dimension = 3;
					Main.refreshTimeGraph(false);
				}
			});
			radioButton3D.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Event.CTRL_MASK));

			dimensionButtonGroup.add(radioButton2D);
			dimensionButtonGroup.add(radioButton3D);

			viewMenu.add(alwaysOnTopItem);
			viewMenu.add(resetZoomItem);
			viewMenu.addSeparator();
			viewMenu.add(radioButton2D);
			viewMenu.add(radioButton3D);
			/********* END VIEW *********/

			return menuBar;
		}
	}

}
