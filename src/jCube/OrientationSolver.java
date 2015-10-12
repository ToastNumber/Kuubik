package jCube;

import java.awt.Color;

/**
 * @author Kelsey McKenna
 */
public class OrientationSolver extends SolveMaster {

	/**
	 * Constructor - assigns a value to the parent class's cube field
	 * 
	 * @param cube
	 *            the cube for which the solution will be generated
	 */
	public OrientationSolver(Cube cube) {
		super(cube);
	}

	/**
	 * Performs and records the moves required to solve the orientation of
	 * <b>cube</b>
	 */
	public void solveOrientation() {
		rotateToTop(Color.yellow);
		solveEdgeOrientation();
		solveCornerOrientation();
	}

	/**
	 * Performs and records the moves required to solve the edge orientation of
	 * <b>cube</b>
	 */
	private void solveEdgeOrientation() {
		int numEdgesOriented = getNumOriented();
		String moves = "";

		if (numEdgesOriented < 4) {
			if (numEdgesOriented == 0) {
				moves = "F R U R' U' F' U2 F U R U' R' F'";
				solutionExplanation += "\nThere are no edges oriented, so orient two edges using  F R U R' U' F' ";
				solutionExplanation += "then orient the remaining edges using  U2 F U R U' R' F' \n";
				// cube.performAbsoluteMoves("M U R U R' U' M2 U R U' Rw'");
			} else {
				// while ((isEdgeOriented(2)) || (!isEdgeOriented(3)))
				while ((cube.getEdge(2).getOrientation() == 0) || (cube.getEdge(3).getOrientation() == 1)) {
					cube.performAbsoluteMoves("U");
					catalogMoves("U");
				}

				// if (!isEdgeOriented(0))
				if (cube.getEdge(0).getOrientation() == 1) {
					moves = "F R U R' U' F'";
					solutionExplanation += "Here we have the \"bar\" formation, so perform  F R U R' U' F' \n";
				} else {
					moves = "F U R U' R' F'";
					solutionExplanation += "Here we have the \"r\" formation, so perform  F U R U' R' F' \n";
				}
			}
			cube.performAbsoluteMoves(moves);
			catalogMoves(moves);
		}
	}

	/**
	 * Performs the moves required to solve the corner orientation of
	 * <b>cube</b>
	 */
	private void solveCornerOrientation() {
		// Stores the orientation of the current corner being examined.
		int orientation = 0;
		// Stores the number of trailing U moves performed.
		int numTrailing;
		// Stores the moves to be performed after inspection of the corner.
		String moves;

		for (int i = 0; i < 4; ++i) {
			moves = "";
			orientation = cube.getCorner(2).getOrientation();
			if (orientation == 1) {
				moves = "R' D' R D R' D' R D";
				solutionExplanation += String.format(
						"Bring the %s-%s-%s corner to the URF position then twist it anti-clockwise using %s%n",
						Cubie.getColorToWord(cube.getCorner(2).getStickers()[0]),
						Cubie.getColorToWord(cube.getCorner(2).getStickers()[1]),
						Cubie.getColorToWord(cube.getCorner(2).getStickers()[2]), "R' D' R D R' D' R D");

			} else if (orientation == -1) {
				moves = ("D' R' D R D' R' D R");
				solutionExplanation += String.format(
						"Bring the %s-%s-%s corner to the URF position then twist it clockwise using %s%n",
						Cubie.getColorToWord(cube.getCorner(2).getStickers()[0]),
						Cubie.getColorToWord(cube.getCorner(2).getStickers()[1]),
						Cubie.getColorToWord(cube.getCorner(2).getStickers()[2]), "D' R' D R D' R' D R");
			}

			cube.performAbsoluteMoves(moves);
			cube.performAbsoluteMoves("U");
			catalogMoves(moves + " U");
		}

		/*
		 * Undo unnecessary U moves
		 */
		numTrailing = getNumTrailingU();
		for (int i = 0; i < numTrailing; ++i) {
			cube.performAbsoluteMoves("U'");
		}
	}

	/**
	 * @return the number of edges in the top layer are oriented correctly
	 */
	private int getNumOriented() {
		int numOriented = 0;

		for (int i = 0; i < 4; ++i) {
			// if (cube.getEdge(i).getStickers()[0].equals(Color.yellow))
			if (cube.getEdge(i).getOrientation() == 0)
				++numOriented;
		}

		return numOriented;
	}

	/**
	 * Determines whether the edge at the specified index is oriented correctly
	 * 
	 * @param edgeIndex
	 *            the index of the Edge to be analysed
	 * @return <b>true</b> if the edge at <b>index</b> is oriented correctly; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean isEdgeOriented(int edgeIndex) {
		return (cube.getEdge(edgeIndex).getStickers()[0].equals(Color.yellow));
	}

	/**
	 * Determines whether the corner at the specified index is oriented
	 * correctly
	 * 
	 * @param cornerIndex
	 *            the index of the Corner to be analysed
	 * @return <b>true</b> if the Corner at <b>index</b> is oriented correctly; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean isCornerOriented(int cornerIndex) {
		return (cube.getCorner(cornerIndex).getStickers()[0].equals(Color.yellow));
	}

	/**
	 * @return <b>true</b> if all pieces in the top layer are oriented
	 *         correctly; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isOrientationSolved() {
		for (int i = 0; i < 4; ++i) {
			if (!isEdgeOriented(i) || !isCornerOriented(i))
				return false;
		}

		return true;
	}

	/**
	 * @return <b>true</b> if all Edges are oriented correctly; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isEdgeOrientationSolved() {
		Color[] stickers;

		for (int i = 0; i < 4; ++i) {
			stickers = Edge.getInitialStickers(i + 8);

			if (!isEdgeOriented(getIndexOf(new Edge(stickers))))
				return false;
		}

		return true;
	}

	/**
	 * @return <b>true</b> if all Corners are oriented correctly; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isCornerOrientationSolved() {
		Color[] stickers;

		for (int i = 0; i < 4; ++i) {
			stickers = Corner.getInitialStickers(i + 4);

			if (cube.getCorner(getIndexOf(new Corner(stickers))).getOrientation() != 0)
				return false;
		}

		return true;
	}

}
