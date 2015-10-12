package jCube;

import java.awt.Color;

/**
 * @author Kelsey McKenna
 */
public class EdgeSolver extends SolveMaster {

	/**
	 * @author Kelsey McKenna
	 */
	private class SolveCandidate {
		/**
		 * This stores the index of the edge on the cube.
		 */
		int index = -1000;
		/**
		 * This stores the number of moves required to solve the edge.
		 */
		int score = -1000;
	}

	/**
	 * This stores the Edge objects representing the red-green, green-orange,
	 * orange-blue, and blue-red edges.
	 */
	private static Edge[] mLEdges = new Edge[4]; // The 'middle-layer' edges
	/**
	 * This stores the remaining middle-layer Edge objects that need to be
	 * solved.
	 */
	private SolveCandidate[] solveCandidates;

	/**
	 * Constructor - assigns an object to the parent class's cube field
	 * 
	 * @param cube
	 *            the cube to be solved
	 */
	public EdgeSolver(Cube cube) {
		super(cube);
		for (int i = 4; i < 8; ++i) {
			mLEdges[i - 4] = new Edge(Edge.getInitialStickers(i));
		}
	}

	/*
	 * public void solveMiddleLayerEdges() { Edge edge;
	 * rotateToTop(Color.yellow); int numEdgesToSolve = 4; int
	 * solveCandidatesIndex; int edgeIndex; Color[] stickers;
	 * 
	 * while (!middleLayerEdgesSolved()) { solveCandidatesIndex = 0;
	 * solveCandidates = new SolveCandidate[numEdgesToSolve--];
	 * 
	 * for (int i = 0; i < 8; ++i) { edge = cube.getEdge(i); if (isMLEdge(edge)
	 * && (!pieceSolved(edge))) { solveCandidates[solveCandidatesIndex] = new
	 * SolveCandidate(); solveCandidates[solveCandidatesIndex].index = i;
	 * solveCandidates[solveCandidatesIndex].score = getScore(i);
	 * ++solveCandidatesIndex; } }
	 * 
	 * edgeIndex =
	 * solveCandidates[getIndexOfMinScore(solveCandidatesIndex)].index; stickers
	 * = cube.getEdge(edgeIndex).getStickers(); solutionExplanation +=
	 * String.format("Edges - %s-%s edge:\n", Cubie.getColorWord(stickers[0]),
	 * Cubie.getColorWord(stickers[1])); solveEdge(edgeIndex); } }
	 */

	/**
	 * Solves the edges in the middle layer, and records the solution and
	 * explanation at the same time.
	 */
	public void solveMiddleLayerEdges() {
		// Stores the properties of the edge to be solved.
		Edge edge;
		rotateToTop(Color.yellow);
		// Stores the index of the unsolved corner being examined.
		int solveCandidatesIndex;
		// Stores the index of the corner to solve.
		int edgeIndex;
		// Stores the stickers of the edge so that a better explanation can be
		// generated.
		Color[] stickers;

		solveCandidates = new SolveCandidate[4];
		for (int i = 0; i < 4; ++i)
			// initialise solveCandidates
			solveCandidates[i] = new SolveCandidate();

		while (!middleLayerEdgesSolved()) {
			solveCandidatesIndex = 0;

			for (int i = 0; i < 8; ++i) {
				edge = cube.getEdge(i);
				if (isMLEdge(edge) && (!isPieceSolved(edge))) {
					solveCandidates[solveCandidatesIndex].index = i;
					solveCandidates[solveCandidatesIndex].score = getScore(i);
					++solveCandidatesIndex;
				}
			}

			edgeIndex = solveCandidates[getIndexOfMinScore(solveCandidatesIndex)].index;

			stickers = cube.getEdge(edgeIndex).getStickers();
			solutionExplanation += String.format("Edges - %s-%s edge:\n", Cubie.getColorToWord(stickers[0]),
					Cubie.getColorToWord(stickers[1]));

			solveEdge(edgeIndex);
		}

		try {
			// Remove last newline character
			solutionExplanation = solutionExplanation.substring(0, solutionExplanation.lastIndexOf("\n"));
		} catch (IndexOutOfBoundsException e) {
		}
	}

	/**
	 * Solves the Edge at the specified index, and record the solution and
	 * explanation at the same time.
	 * 
	 * @param currentIndex
	 *            the index of the Edge to be solved.
	 */
	public void solveEdge(int currentIndex) {
		rotateToTop(Color.yellow);
		// Stores a copy of the edge to be solved.
		Edge edge = new Edge(cube.getEdge(currentIndex).getStickers());

		if (isPieceSolved(edge))
			return;

		if /* edge is in top layer */(currentIndex < 4) {
			while (!edgeInSetupPosition(edge)) {
				cube.performAbsoluteMoves("U");
				catalogMoves("U");
			}

			while (cube.getEdge(2).compareTo(edge) == -1) {
				cube.rotate("y");
				catalogMoves("y");
			}

			if (edge.getStickers()[0].equals(cube.getSlice(2).getCentre())) {
				cube.performAbsoluteMoves("U R U' R' U' F' U F");
				catalogMoves("U R U' R' U' F' U F");
				solutionExplanation += "The edge needs to go to the right, so set up the edge then perform  R U' R' U' F' U F\n\n";
			} else {
				cube.performAbsoluteMoves("U' L' U L U F U' F'");
				catalogMoves("U' L' U L U F U' F'");
				solutionExplanation += "The edge needs to go to the left, so set up the edge then perform  L' U L U F U' F'\n\n";
			}
		} else /* edge is in E slice/middle layer */{
			// Rotate the cube until the edge is at FR
			while (cube.getEdge(6).compareTo(edge) == -1) {
				cube.rotate("y");
				catalogMoves("y");
			}

			cube.performAbsoluteMoves("R U' R' U' F' U F");
			catalogMoves("R U' R' U' F' U F");
			solutionExplanation += "The edge is trapped in the middle layer, so remove it using  R U' R' U' F' U F.\n";

			// The edge is now at UB, so solve the edge at this index
			solveEdge(0);
		}
	}

	/**
	 * @param edge
	 *            the edge to be analysed
	 * @return <b>true</b> if the specified Edge is in the correct setup
	 *         position for solving; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean edgeInSetupPosition(Edge edge) {
		int index = getIndexOf(edge);

		return (cube.getSlice(sliceEdgeSharing[index][1]).getCentre().equals(edge.getStickers()[1]));
	}

	/**
	 * @param edge
	 *            the edge to be analysed
	 * @return <b>true</b> if the specified edge belongs in the middle layer,
	 *         i.e. it does not have a yellow or white sticker <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isMLEdge(Edge edge) {
		Color[] stickers = edge.getStickers();

		if ((!stickers[0].equals(Color.white)) && (!stickers[1].equals(Color.white))
				&& (!stickers[0].equals(Color.yellow)) && (!stickers[1].equals(Color.yellow)))
			return true;
		else
			return false;
	}

	/**
	 * @return <b>true</b> if the Edges in the middle layer are solved;
	 *         <b>false</b> otherwise
	 */
	public boolean middleLayerEdgesSolved() {
		for (int i = 4; i < 8; ++i)
			if (!isPieceSolved(cube.getEdge(i)))
				return false;

		return true;
	}

	/*
	 * private int getScore(Edge edge) { int score = 0; int index =
	 * getIndexOf(edge); int orientation = edge.getOrientation(); int
	 * overEdgeIndex = ((getIndexOfDestination(edge) - 4) + ((orientation == 0)
	 * ? -2 : 1) + 4) % 4;
	 * 
	 * if (index < 4) score -= getShortestOffset(overEdgeIndex, index); else {
	 * score -= 7; if (pieceIsInCorrectPosition(edge) && (orientation == 1))
	 * score -= 4; }
	 * 
	 * return score; }
	 */

	/**
	 * This determines how many moves are required to solve the edge at the
	 * specified index.
	 * 
	 * @param index
	 *            the index of Edge to be analysed
	 * @return the number of moves required to solve the Edge at the specified
	 *         <b>index</b>
	 */
	private int getScore(int index) {
		/*
		 * This determines the number of moves to solve the edge at the
		 * specified index by solving the piece, counting the moves, then
		 * reverses the moves so that the cube is in its initial state before
		 * the method was invoked.
		 */

		// Stores the number of moves to solve the edge.
		int score;

		EdgeSolver es = new EdgeSolver(cube);
		es.solveEdge(index);
		simplifyMoves(es.getCatalogMoves(), SolveMaster.CORNER_EDGE);
		score = es.getCatalogMoves().size();
		cube.performAbsoluteMoves(getReverseStringMoves(es.getCatalogMoves()));
		es.clearMoves();

		return score;
	}

	/**
	 * Returns the index of the element in the <b>solveCandidates</b> array that
	 * requires the fewest number of moves to solve
	 * 
	 * @param length
	 *            the number of remaining unsolved edges
	 * @return the index of the element in <b>solveCandidates</b> that requires
	 *         the fewest number of moves to solve
	 */
	private int getIndexOfMinScore(int length) {
		int min = solveCandidates[0].score;
		int indexOfMin = 0;

		for (int i = 1; i < length; ++i) {
			if (solveCandidates[i].score < min) {
				indexOfMin = i;
				min = solveCandidates[i].score;
			}
		}

		return indexOfMin;
	}

}
