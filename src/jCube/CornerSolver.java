package jCube;

import java.awt.Color;
import java.util.LinkedList;

/**
 * @author Kelsey McKenna
 */
public class CornerSolver extends SolveMaster {

	/**
	 * @author Kelsey McKenna
	 */
	private class SolveCandidate {
		/**
		 * This stores the index of the corner on the cube
		 */
		int index = -1000;
		/**
		 * This stores the number of moves required to solve the corner
		 */
		int score = -1000;
	}

	/**
	 * This stores Corner objects representing the white-red-green,
	 * white-green-orange, white-orange-blue, and white-blue-red corners.
	 */
	private static Corner[] fLCorners = new Corner[4];
	/**
	 * This stores the remaining first layer Corner objects that need to be
	 * solved.
	 */
	private SolveCandidate[] solveCandidates;

	/**
	 * Constructor - sets the cube to be solved
	 * 
	 * @param cube
	 *            to be used to generate a solution
	 */
	public CornerSolver(Cube cube) {
		super(cube);

		for (int i = 0; i < 4; ++i) {
			fLCorners[i] = new Corner(Corner.getInitialStickers(i));
		}
	}

	/*
	 * public void solveFirstLayerCorners() { Corner corner;
	 * rotateToTop(Color.yellow); int numCornersToSolve = 4; int
	 * solveCandidatesIndex; int cornerIndex; Color[] stickers;
	 * 
	 * while (!firstLayerCornersSolved()) { solveCandidatesIndex = 0;
	 * solveCandidates = new SolveCandidate[numCornersToSolve--];
	 * 
	 * for (int i = 0; i < 8; ++i) { corner = cube.getCorner(i); if
	 * (isFLCorner(corner) && (!pieceSolved(corner))) {
	 * solveCandidates[solveCandidatesIndex] = new SolveCandidate();
	 * solveCandidates[solveCandidatesIndex].index = i;
	 * solveCandidates[solveCandidatesIndex].score = getScore(i);
	 * ++solveCandidatesIndex; } }
	 * 
	 * cornerIndex =
	 * solveCandidates[getIndexOfMinScore(solveCandidatesIndex)].index; stickers
	 * = cube.getCorner(cornerIndex).getStickers(); solutionExplanation +=
	 * String.format("Corners - %s-%s-%s corner:\n",
	 * Cubie.getColorWord(stickers[0]), Cubie.getColorWord(stickers[1]),
	 * Cubie.getColorWord(stickers[2]));
	 * solveCorner(solveCandidates[getIndexOfMinScore
	 * (solveCandidatesIndex)].index); } }
	 */

	/**
	 * Solves the corners in the first layer of the cube and records the
	 * solution and explanation at the same time
	 */
	public void solveFirstLayerCorners() {
		// Stores the properties of the corner to be solved.
		Corner corner;
		rotateToTop(Color.yellow);
		// Stores the index of the unsolved corner being examined.
		int solveCandidatesIndex;
		// Stores the index of the corner to solve.
		int indexOfCornerToSolve;
		// Stores the stickers of the corner so that a better explanation can be
		// generated.
		Color[] stickers;

		solveCandidates = new SolveCandidate[4];
		for (int i = 0; i < 4; ++i)
			// Initialise solveCandidates
			solveCandidates[i] = new SolveCandidate();

		while (!firstLayerCornersSolved()) {
			solveCandidatesIndex = 0;

			for (int i = 0; i < 8; ++i) {
				corner = cube.getCorner(i);
				if (isFLCorner(corner) && (!isPieceSolved(corner))) {
					solveCandidates[solveCandidatesIndex].index = i;
					solveCandidates[solveCandidatesIndex].score = getScore(i);
					++solveCandidatesIndex;
				}
			}

			indexOfCornerToSolve = solveCandidates[getIndexOfMinScore(solveCandidatesIndex)].index;

			stickers = cube.getCorner(indexOfCornerToSolve).getStickers();
			solutionExplanation += String.format("Corners - %s-%s-%s corner:\n", Cubie.getColorToWord(stickers[0]),
					Cubie.getColorToWord(stickers[1]), Cubie.getColorToWord(stickers[2]));

			solveCorner(indexOfCornerToSolve);
		}

		try {
			// Removes the last newline character
			solutionExplanation = solutionExplanation.substring(0, solutionExplanation.lastIndexOf("\n"));
		} catch (IndexOutOfBoundsException e) {
		}
	}

	/**
	 * @param originalMoves
	 *            the moves to be examined
	 * @return <b>true</b> if the last move (after simplification and ignoring
	 *         rotations) was U; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean lastMoveWasU(LinkedList<String> originalMoves) {
		// Stores a copy of originalMoves because 'moves' is later altered to
		// check for cancellations.
		LinkedList<String> moves = new LinkedList<>();
		int size = originalMoves.size();

		for (int i = 0; i < size; ++i)
			moves.add(originalMoves.get(i));

		simplifyMoves(moves, SolveMaster.CANCELLATIONS);

		if (moves.size() == 0)
			return false;

		else {
			for (int i = moves.size() - 1; i >= 0; --i) {
				if ("xyz".contains(moves.get(i).substring(0, 1)))
					continue;
				else if (!moves.get(i).contains("U"))
					return false;
				else
					return true;
			}
		}

		return false;
	}

	/**
	 * Solves the specified corner, and records the solution and explanation at
	 * the same time
	 * 
	 * @param currentIndex
	 *            the index of the corner to be solved
	 */
	public void solveCorner(int currentIndex) {
		// Stores a copy of the properties of the corner to be solved.
		Corner corner = cube.getCorner(currentIndex);
		// Stores the orientation (-1, 0, 1) of the corner.
		int orientation = corner.getOrientation();
		// Stores the index of the setup location for the corner.
		int overCornerIndex = getIndexOfDestination(corner);
		overCornerIndex += (overCornerIndex % 2 == 0) ? -3 : -5;

		if (isPieceSolved(corner))
			return;

		/*
		 * The cube will perform y rotatations until the corner is at URF (cubie
		 * index 2) or URD (cubie index 7) (
		 */
		while (cube.getCorner((currentIndex >= 4) ? 7 : 2).compareTo(corner) == -1) {
			cube.rotate("y");
			catalogMoves("y");
		}

		// i.e. if the corner is in the bottom layer
		if (currentIndex >= 4) {
			if (orientation > 0) {
				cube.performAbsoluteMoves("R U' R'");
				catalogMoves("R U' R'");
				solutionExplanation += "The corner is trapped in the bottom layer, so remove it using  R U' R'";
			} else {
				cube.performAbsoluteMoves("R U R' U'");
				catalogMoves("R U R' U'");
				solutionExplanation += "The corner is trapped in the bottom layer, so remove it using  R U R'";
			}
			solutionExplanation += "\n";
			// The corner is now at cubie index 2, so recursively call the
			// method to solve the corner from this index
			solveCorner(2);
		}
		/*
		 * overCornerIndex is basically the setup position for the corner. If
		 * the corner is at this index, then only one more sequence of moves
		 * needs to be performed in order to solve the corner
		 */
		else if (currentIndex == overCornerIndex) {
			if (lastMoveWasU(getCatalogMoves()))
				solutionExplanation += "Bring the corner over its destination. ";

			if (orientation == 0) {
				cube.performAbsoluteMoves("R U2 R' U' R U R'");
				catalogMoves("R U2 R' U' R U R'");
				solutionExplanation += "White is facing top, so perform  R U2 R' U' R U R'";
			} else if (orientation == 1) {
				cube.performAbsoluteMoves("R U R'");
				catalogMoves("R U R'");
				solutionExplanation += "White is facing right, so perform  R U R'";
			} else {
				cube.performAbsoluteMoves("F' U' F");
				catalogMoves("F' U' F");
				solutionExplanation += "White is facing front, so perform  F' U' F";
			}
			solutionExplanation += "\n\n";
		}
		/*
		 * If this block is reached, then it means the corner is in the top
		 * layer, is at cubie index 2, and needs to be moved (using U) to its
		 * setup/overCornerIndex position. This method performs U until the
		 * corner is at its setup position then rotates the cube so that the
		 * corner is still at cubie index 2.
		 */
		else {
			for (int i = 0; i < ((overCornerIndex - currentIndex) + 4) % 4; ++i) {
				cube.performAbsoluteMoves("U");
				catalogMoves("U");
			}

			for (int i = 0; i < ((overCornerIndex - currentIndex) + 4) % 4; ++i) {
				cube.rotate("y'");
				catalogMoves("y'");
			}
			solveCorner(2);
		}
	}

	/**
	 * @param corner
	 *            the corner to be analysed
	 * @return <b>true</b> if the corner belongs to the first layer; <br>
	 *         false otherwise
	 */
	public static boolean isFLCorner(Corner corner) {
		Color[] stickers = corner.getStickers();

		if ((stickers[0].equals(Color.white)) || (stickers[1].equals(Color.white)) || (stickers[2].equals(Color.white)))
			return true;
		else
			return false;
	}

	/*
	 * public boolean firstLayerCornersSolved() { //I think the bottom bit about
	 * AUF is unnecessary since AUF isn't required for corners boolean solved;
	 * boolean aufPerformed = false;
	 * 
	 * for (int aUF = 0; aUF < 4; ++aUF) { solved = true; for (int i = 0; i < 4;
	 * ++i) { if (!isPieceSolved(fLCorners[i])) { solved = false; break; } }
	 * 
	 * if (solved) { // Catalog only necessary AUF moves if (aufPerformed) for
	 * (int i = 0; i < aUF; ++i) { catalogMoves("U"); } return true; } else { //
	 * Perform AUF cube.performAbsoluteMoves("u"); aufPerformed = true; }
	 * 
	 * } return false; }
	 */

	/**
	 * @return <b>true</b> if the first-layer corners are solved; <b>false</b>
	 *         otherwise
	 */
	public boolean firstLayerCornersSolved() {
		for (int i = 0; i < 4; ++i)
			if (!isPieceSolved(fLCorners[i]))
				return false;

		return true;
	}

	/*
	 * private int getScore(Corner corner) { int score = 0; int index =
	 * getIndexOf(corner); int orientation = corner.getOrientation(); int
	 * overCornerIndex = getIndexOfDestination(corner); overCornerIndex +=
	 * (overCornerIndex % 2 == 0) ? -3 : -5;
	 * 
	 * if (index >= 4) { score -= 6; index = ((index % 2 == 0) ? -3 : -5); }
	 * else { if (orientation == 0) score -= 7; else score -= 3; }
	 * 
	 * score -= getShortestOffset(overCornerIndex, index);
	 * 
	 * return score;
	 * 
	 * 
	 * }
	 */

	/**
	 * @param index
	 *            the index of the corner to be examined
	 * @return the number of moves required to solve the specified corner
	 */
	private int getScore(int index) {
		int score;

		CornerSolver cs = new CornerSolver(cube);
		cs.solveCorner(index);
		simplifyMoves(cs.getCatalogMoves(), SolveMaster.CORNER_EDGE);
		score = cs.getCatalogMoves().size();
		cube.performAbsoluteMoves(getReverseStringMoves(cs.getCatalogMoves()));
		cs.clearMoves();

		return score;
	}

	/**
	 * @param length
	 *            the number of unsolved corners remaining
	 * @return the index in the solveCandidates array which represents the
	 *         corner that requires the fewest moves to solve
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
