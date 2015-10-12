package jCube;

import java.awt.Color;

/**
 * @author Kelsey McKenna
 */
public class PermutationSolver extends SolveMaster {

	/**
	 * This stores Edge objects representing the Yellow-Green, Yellow-Orange,
	 * Yellow-Blue, Yellow-Red edges.
	 */
	private Edge[] topEdges = new Edge[4];

	/**
	 * Constructor - assigns a value to the cube field in the parent class and
	 * assigns Edges to the topEdges array
	 * 
	 * @param cube
	 *            the Cube for which a solution will be generated
	 */
	public PermutationSolver(Cube cube) {
		super(cube);

		for (int i = 0; i < 4; ++i) {
			topEdges[i] = new Edge(Edge.getInitialStickers(i + 8));
		}
	}

	/**
	 * Performs and records the moves required to solve the permutation of
	 * <b>cube</b>
	 */
	public void solvePermutation() {
		rotateToTop(Color.yellow);
		solveCornerPermutation();
		solveEdgePermutation();
		performAUF();
	}

	/**
	 * After solving the relative permutation of the pieces, this method
	 * performs the move "U" until the cube is solved.
	 */
	private void performAUF() {
		while (!cube.getEdge(0).getSecondaryColor().equals(cube.getSlice(5).getCentre())) {
			cube.performAbsoluteMoves("U");
			catalogMoves("U");
		}
	}

	/**
	 * Performs and records the moves required to solve the corner permutation
	 * of <b>cube</b>
	 */
	private void solveCornerPermutation() {
		int count = 0;
		if (isCornerPermutationSolved()) {
			return;
		}

		while ((count < 4) && (!headlightsAtBack())) {
			++count;
			cube.performAbsoluteMoves("U");
			catalogMoves("U");
		}

		if (count < 4) { // headlights found
			solutionExplanation += String.format(
					"Bring the %s headlights to the back then perform  L' U R' D2 R U' R' D2 R L",
					Cubie.getColorToWord(cube.getCorner(0).getStickers()[2]));
			cube.performAbsoluteMoves("L' U R' D2 R U' R' D2 R L");
			catalogMoves("L' U R' D2 R U' R' D2 R L");
		} else {
			/*
			 * if (!cube.getCorner(2).getStickers()[2].equals(cube.getEdge(1).
			 * getStickers()[1])) { cube.performAbsoluteMoves("U");
			 * catalogMoves("U"); }
			 */
			solutionExplanation += "There are no headlights, so we must perform  x' R U' R' D R U R' D' R U R' D R U' R' D' x";
			cube.performAbsoluteMoves("x' R U' R' D R U R' D' R U R' D R U' R' D' x");
			catalogMoves("x' R U' R' D R U R' D' R U R' D R U' R' D' x");
		}

		solutionExplanation += "\n";
	}

	/**
	 * Performs and records the moves required to solve the edge permutation of
	 * <b>cube</b>
	 */
	private void solveEdgePermutation() {
		// Stores the number of U moves performed so that no more than 3 are
		// perfomed during the initial inspection.
		int count = 0;
		// Stores the moves to be performed after inspection of the state.
		String moves = "";

		if (isEdgePermutationSolved()) {
			return;
		}

		// Get solid block on back
		while ((count < 4) && (!cube.getEdge(0).getSecondaryColor().equals(cube.getCorner(0).getStickers()[2]))) {
			++count;
			cube.performAbsoluteMoves("U");
			catalogMoves("U");
		}

		if (count < 4) { // if block on back
			solutionExplanation += String.format("Bring the %s block to the back and then cycle the pieces ",
					Cubie.getColorToWord(cube.getEdge(0).getSecondaryColor()));

			if (edgesAreOpposite(cube.getEdge(1), cube.getEdge(2))) {
				solutionExplanation += String.format("anti-clockwise using  R U' R U R U R U' R' U' R2");
				moves = ("R U' R U R U R U' R' U' R2");
			} else {
				solutionExplanation += String.format("clockwise using  R2 U R U R' U' R' U' R' U R'");
				moves = ("R2 U R U R' U' R' U' R' U R'");
			}
		} else if ((cube.getEdge(0).getSecondaryColor().equals(cube.getCorner(3).getStickers()[1]))
				&& (cube.getEdge(1).getSecondaryColor().equals(cube.getCorner(3).getStickers()[2]))) {
			solutionExplanation += "All edges needed swapped vertically or horizontally, so we must perform  M2 U M2 U2 M2 U M2";
			moves = ("M2 U M2 U2 M2 U M2");
		} else {
			solutionExplanation += "The edges need swapped in a z formation, so ";
			if (!cube.getEdge(2).getSecondaryColor().equals(cube.getCorner(1).getStickers()[2])) {
				moves = "U ";
				solutionExplanation += "set them up by performing  U  then ";
			}

			solutionExplanation += "perform  M2 U M2 U M' U2 M2 U2 M'";
			moves += "M2 U M2 U M' U2 M2 U2 M'";
		}

		cube.performAbsoluteMoves(moves);
		catalogMoves(moves);
	}

	/**
	 * @return <b>true</b> if the permutation of the Edges of the last layer is
	 *         solved; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isEdgePermutationSolved() {
		for (int i = 0; i < 4; ++i) {
			if (!cube.getEdge(0).getSecondaryColor().equals(cube.getCorner(0).getStickers()[2]))
				return false;

			cube.performAbsoluteMoves("U");
			catalogMoves("U");
		}

		return true;
	}

	/**
	 * @param edgeOne
	 *            this Edge is compared to the second Edge
	 * @param edgeTwo
	 *            this Edge is compared to the first Edge
	 * @return <b>true</b> if the edges are opposite each other and are on the
	 *         same face on a solved cube; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean edgesAreOpposite(Edge edgeOne, Edge edgeTwo) {
		Color colorOne, colorTwo;

		colorOne = edgeOne.getSecondaryColor();
		colorTwo = edgeTwo.getSecondaryColor();

		int one = -1, two = -2;

		for (int i = 0; i < 4; ++i) {
			if (topEdges[i].getSecondaryColor().equals(colorOne))
				one = i;
			else if (topEdges[i].getSecondaryColor().equals(colorTwo))
				two = i;
		}

		return ((one + two) % 2 == 0);
	}

	/**
	 * @return <b>true</b> if the permutation of the Corners of the last layer
	 *         is solved; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isCornerPermutationSolved() {
		for (int i = 0; i < 4; ++i) {
			if (!headlightsAtBack())
				return false;

			cube.performAbsoluteMoves("U");
			catalogMoves("U");
		}

		return true;
	}

	/**
	 * @return <b>true</b> if there are 'headlights' at the back face of the
	 *         cube, i.e. two matching Corner stickers; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean headlightsAtBack() {
		if (cube.getCorner(0).getStickers()[2].equals(cube.getCorner(1).getStickers()[1]))
			return true;

		return false;
	}

	/**
	 * @return <b>true</b> if the permutation of all pieces in the top layer is
	 *         solved; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean permutationSolved() {
		return (isEdgePermutationSolved() && isCornerPermutationSolved());
	}

}
