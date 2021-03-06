package jCube;

import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

import java.awt.Color;
import java.util.Arrays;

import javax.swing.JOptionPane;

/**
 * @author Kelsey McKenna
 */
public class MouseSelectionSolver extends SolveMaster {

	/**
	 * This is so that an identifier in the code instead of the String "BLANK"
	 */
	public static final String BLANK = "BLANK";

	/**
	 * This variable allows certain methods to be accessed to perform general
	 * operations on the cube.
	 */
	private SolveMaster solveMaster;
	/**
	 * This variable allows a solution for the cross to be generated.
	 */
	private CrossSolver crossSolver;
	/**
	 * This variable allows a solution for the first-layer corners to be
	 * generated.
	 */
	private CornerSolver cornerSolver;
	/**
	 * This variable allows a solution for the middle-layer edges to be
	 * generated.
	 */
	private EdgeSolver edgeSolver;
	/**
	 * This variable allows a solution for the orientation of the last layer to
	 * be generated.
	 */
	private OrientationSolver orientationSolver;
	/**
	 * This variable allows a solution for the permutation of the last layer to
	 * be generated.
	 */
	private PermutationSolver permutationSolver;

	/**
	 * This variable is used in calculations for determining the indices of
	 * pieces on the screen.
	 */
	private static double xFactor = 190. / 156;
	/**
	 * This variable is used in calculations for determining the indices of
	 * pieces on the screen.
	 */
	private static double uAngle = tan(toRadians(33));
	/**
	 * This variable is used in calculations for determining the indices of
	 * pieces on the screen.
	 */
	private static double rAngle = tan(toRadians(55));

	/**
	 * This accumulates the solutions generated by crossSolver, edgeSolver etc.
	 */
	private String solution = BLANK;

	/**
	 * Constructor - assigns values to fields
	 * 
	 * @param cube
	 *            the Cube to be used
	 * @param a
	 *            the CrossSolver to be used
	 * @param b
	 *            the CornerSolver to be used
	 * @param c
	 *            the EdgeSolver to be used
	 * @param d
	 *            the OrientationSolver to be used
	 * @param e
	 *            the PermutationSolver to be used
	 */
	public MouseSelectionSolver(Cube cube, CrossSolver a, CornerSolver b, EdgeSolver c, OrientationSolver d,
			PermutationSolver e) {
		super(cube);
		solveMaster = new SolveMaster(cube);
		this.crossSolver = a;
		this.cornerSolver = b;
		this.edgeSolver = c;
		this.orientationSolver = d;
		this.permutationSolver = e;
	}

	/**
	 * @return the generated solution
	 */
	public String getSolution() {
		return this.solution;
	}

	/**
	 * Solves the piece at the specified index, and records the solution and
	 * explanation simultaneously.
	 * 
	 * @param index
	 *            the index of the piece to be solved
	 */
	public void solvePiece(int index) {
		// int choice = -1;
		Edge edge;
		Corner corner;

		Main.clearAllSolverMoves();

		if (index < 0)
			return;
		else if (index >= 8) { // an Edge
			index = index - 8;
			edge = new Edge(Arrays.copyOf(cube.getEdge(index).getStickers(), 2));
			solveMaster.rotateToTopFront(Color.green, Color.white);
			// edge.setOrientation(cube.getEdge(solveMaster.getIndexOf(edge)).getOrientation());
			edge.setOrientation(LinearSearch.linearSearch(edge.getStickers(), Color.white));
			if (edge.getOrientation() == -1)
				edge.setOrientation(LinearSearch.linearSearch(edge.getStickers(), Color.yellow));

			if (CrossSolver.isCrossEdge(edge)) {
				crossSolver.solveCross();
			} else if (EdgeSolver.isMLEdge(edge)) {
				crossSolver.solveCross();
				cornerSolver.solveFirstLayerCorners();
				edgeSolver.solveEdge(getIndexOf(edge));
			} else {
				crossSolver.solveCross();
				cornerSolver.solveFirstLayerCorners();
				edgeSolver.solveMiddleLayerEdges();
				orientationSolver.solveOrientation();
				permutationSolver.solvePermutation();
			}
		} else { // Corner
			corner = new Corner(Arrays.copyOf(cube.getCorner(index).getStickers(), 3));
			solveMaster.rotateToTopFront(Color.white, Color.green);
			corner.setOrientation(LinearSearch.linearSearchCornerOrientation(corner.getStickers(), Color.white));
			if (corner.getOrientation() == -2)
				corner.setOrientation(LinearSearch.linearSearchCornerOrientation(corner.getStickers(), Color.yellow));

			if (CornerSolver.isFLCorner(corner)) {
				crossSolver.solveCross();
				cornerSolver.solveCorner(getIndexOf(corner));
			} else {
				crossSolver.solveCross();
				cornerSolver.solveFirstLayerCorners();
				edgeSolver.solveMiddleLayerEdges();
				orientationSolver.solveOrientation();
				permutationSolver.solvePermutation();
			}
		}

		simplifyMoves(crossSolver.getCatalogMoves(), CROSS);
		simplifyMoves(cornerSolver.getCatalogMoves(), CORNER_EDGE);
		simplifyMoves(edgeSolver.getCatalogMoves(), CORNER_EDGE);
		simplifyMoves(orientationSolver.getCatalogMoves(), CANCELLATIONS);
		simplifyMoves(permutationSolver.getCatalogMoves(), CANCELLATIONS);

		/*
		 * this.solution = ("------------Solution------------\n" +
		 * ((crossSolver.getStringMoves().trim().equals("")) ? "" : "Cross: \t"
		 * + crossSolver.getStringMoves()) +
		 * ((cornerSolver.getStringMoves().trim().equals("")) ? "" :
		 * "\nCorners: \t" + cornerSolver.getStringMoves()) +
		 * ((edgeSolver.getStringMoves().trim().equals("")) ? "" : "\nEdges: \t"
		 * + edgeSolver.getStringMoves()) +
		 * ((orientationSolver.getStringMoves().trim().equals("")) ? "" :
		 * "\nOrientation: \t" + orientationSolver.getStringMoves()) +
		 * ((permutationSolver.getStringMoves().trim().equals("")) ? "" :
		 * "\nPermutation: \t" + permutationSolver.getStringMoves()) +
		 * "\n\n------------Explanation------------\n" +
		 * ((cornerSolver.getSolutionExplanation().trim().equals("")) ? "" :
		 * "Corners:\n" + cornerSolver.getSolutionExplanation() + "\n") +
		 * ((edgeSolver.getSolutionExplanation().trim().equals("")) ? "" :
		 * "Edges:\n" + edgeSolver.getSolutionExplanation() + "\n") +
		 * ((orientationSolver.getSolutionExplanation().trim().equals("")) ? ""
		 * : "Orientation:\n" + orientationSolver.getSolutionExplanation() +
		 * "\n") +
		 * ((permutationSolver.getSolutionExplanation().trim().equals("")) ? ""
		 * : "Permutation:\n" + permutationSolver.getSolutionExplanation()));
		 */

		solution = Main.getFormattedCubeSolution();

		if (!solution.contains("U") && !solution.contains("R") && !solution.contains("F") && !solution.contains("D")
				&& !solution.contains("L") && !solution.contains("B") && !solution.contains("M"))
			solution = BLANK;

		cube.performAbsoluteMoves(SolveMaster.getReverseStringMoves(permutationSolver.getCatalogMoves()));
		cube.performAbsoluteMoves(SolveMaster.getReverseStringMoves(orientationSolver.getCatalogMoves()));
		cube.performAbsoluteMoves(SolveMaster.getReverseStringMoves(edgeSolver.getCatalogMoves()));
		cube.performAbsoluteMoves(SolveMaster.getReverseStringMoves(cornerSolver.getCatalogMoves()));
		cube.performAbsoluteMoves(SolveMaster.getReverseStringMoves(crossSolver.getCatalogMoves()));

		/*
		 * crossSolver.clearMoves(); cornerSolver.clearMoves();
		 * edgeSolver.clearMoves(); orientationSolver.clearMoves();
		 * permutationSolver.clearMoves();
		 */
	}

	/**
	 * Returns the index of the piece on screen with the coordinates (x, y).
	 * 
	 * @param x
	 *            the x coordinate of the piece on screen
	 * @param y
	 *            the y coordinate of the piece on screen
	 * @return Corners: 0, 1, 2, ..., 7 <br>
	 *         Edges: 8, 9, 10, ..., 19 <br>
	 *         <b>-2</b> if the selection is invalid
	 */
	public static int getIndexOfPieceOnScreen(int x, int y) {
		double cartY = 166 - y;
		double cartX = x - 266;
		double uExtension = cartY * uAngle * xFactor;
		double rExtension = cartX * rAngle / xFactor;
		int row, col;

		// U-Slice
		if ((cartY >= 0) && (cartY <= 156) && (x >= 70 + uExtension) && (x <= uExtension + 260)) {
			if (cartY >= 106)
				row = 0;
			else if (cartY >= 56) {
				row = 1;
			} else
				row = 2;

			if (x >= 203 + uExtension)
				col = 2;
			else if (x >= 137 + uExtension)
				col = 1;
			else
				col = 0;

			switch (row) {
			case 0:
				switch (col) {
				case 0:
					return 0;
				case 1:
					return 8;
				case 2:
					return 1;
				}
			case 1:
				switch (col) {
				case 0:
					return 11;
				case 1:
					return -1; // Centre
				case 2:
					return 9;
				}
			case 2:
				switch (col) {
				case 0:
					return 3;
				case 1:
					return 10;
				case 2:
					return 2;
				}
			}
		}
		// F-slice
		else if ((y >= 169) && (y <= 363) && (x >= 67) && (x <= 260)) {
			if (y <= 231)
				row = 0;
			else if (y <= 298)
				row = 1;
			else
				row = 2;

			if (x <= 128)
				col = 0;
			else if (x <= 194)
				col = 1;
			else
				col = 2;

			switch (row) {
			case 0:
				switch (col) {
				case 0:
					return 3;
				case 1:
					return 10;
				case 2:
					return 2;
				}
			case 1:
				switch (col) {
				case 0:
					return 15;
				case 1:
					return -1; // Centre
				case 2:
					return 14;
				}
			case 2:
				switch (col) {
				case 0:
					return 6;
				case 1:
					return 18;
				case 2:
					return 7;
				}
			}
		}
		// R-slice
		else if ((x >= 266) && (x <= 387) && (y <= 359 - rExtension) && (y >= 169 - rExtension)) {
			if (x >= 350)
				col = 2;
			else if (x >= 308)
				col = 1;
			else
				col = 0;

			if (y >= 300 - rExtension)
				row = 2;
			else if (y >= 234 - rExtension)
				row = 1;
			else
				row = 0;

			switch (row) {
			case 0:
				switch (col) {
				case 0:
					return 2;
				case 1:
					return 9;
				case 2:
					return 1;
				}
			case 1:
				switch (col) {
				case 0:
					return 14;
				case 1:
					return -1; // Centre
				case 2:
					return 13;
				}
			case 2:
				switch (col) {
				case 0:
					return 7;
				case 1:
					return 19;
				case 2:
					return 4;
				}
			}
		}

		return -2; // Not a valid selection
	}

	/**
	 * Returns the index of the facelet/sticker on screen with the coordinates
	 * (x, y).
	 * 
	 * @param x
	 *            the x coordinate of the facelet/sticker on screen
	 * @param y
	 *            the y coordinate of the facelet/sticker on screen
	 * @return index of facelet/sticker on screen <br>
	 *         <b>-2</b> if the selection is invalid
	 */
	public static int getIndexOfFaceletOnScreen(int x, int y) {
		double cartY = 166 - y;
		double cartX = x - 266;
		double uExtension = cartY * uAngle * xFactor;
		double rExtension = cartX * rAngle / xFactor;
		int row, col;

		// U-slice
		if ((cartY >= 0) && (cartY <= 156) && (x >= 70 + uExtension) && (x <= uExtension + 260)) {
			if (cartY >= 106)
				row = 0;
			else if (cartY >= 56) {
				row = 1;
			} else
				row = 2;

			if (x >= 203 + uExtension)
				col = 2;
			else if (x >= 137 + uExtension)
				col = 1;
			else
				col = 0;

			switch (row) {
			case 0:
				switch (col) {
				case 0:
					return 0;
				case 1:
					return 1;
				case 2:
					return 2;
				}
			case 1:
				switch (col) {
				case 0:
					return 3;
				case 1:
					return -1; // Centre
				case 2:
					return 5;
				}
			case 2:
				switch (col) {
				case 0:
					return 6;
				case 1:
					return 7;
				case 2:
					return 8;
				}
			}
		}
		// F-slice
		else if ((y >= 169) && (y <= 363) && (x >= 67) && (x <= 260)) {
			if (y <= 231)
				row = 0;
			else if (y <= 298)
				row = 1;
			else
				row = 2;

			if (x <= 128)
				col = 0;
			else if (x <= 194)
				col = 1;
			else
				col = 2;

			switch (row) {
			case 0:
				switch (col) {
				case 0:
					return 9;
				case 1:
					return 10;
				case 2:
					return 11;
				}
			case 1:
				switch (col) {
				case 0:
					return 12;
				case 1:
					return -1; // Centre
				case 2:
					return 14;
				}
			case 2:
				switch (col) {
				case 0:
					return 15;
				case 1:
					return 16;
				case 2:
					return 17;
				}
			}
		}
		// R-slice
		else if ((x >= 266) && (x <= 387) && (y <= 359 - rExtension) && (y >= 169 - rExtension)) {
			if (x >= 350)
				col = 2;
			else if (x >= 308)
				col = 1;
			else
				col = 0;

			if (y >= 300 - rExtension)
				row = 2;
			else if (y >= 234 - rExtension)
				row = 1;
			else
				row = 0;

			switch (row) {
			case 0:
				switch (col) {
				case 0:
					return 18;
				case 1:
					return 19;
				case 2:
					return 20;
				}
			case 1:
				switch (col) {
				case 0:
					return 21;
				case 1:
					return -1; // Centre
				case 2:
					return 23;
				}
			case 2:
				switch (col) {
				case 0:
					return 24;
				case 1:
					return 25;
				case 2:
					return 26;
				}
			}
		}

		return -2; // Not a valid selection
	}

	/**
	 * Shows a question dialog and gets a decision from a user
	 * 
	 * @param message
	 *            the text to be shown in the dialog
	 * @return <b>0</b> if 'Yes' is selected; <br>
	 *         <b>1</b> if 'No' is selected; <br>
	 *         <b>-1</b> if nothing is selected
	 */
	public static int getQuestionDialogResponse(String message) {
		Object[] options = { "Yes", "No" };
		int choice = -1;

		choice = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[0]);

		return choice;
	}

}
