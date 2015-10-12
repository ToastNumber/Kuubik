package jCube;

import java.awt.Color;
import java.util.LinkedList;

/**
 * @author Kelsey McKenna
 */
public class SolveMaster {

	/**
	 * This is used for readability in the code.
	 */
	public static final int CANCELLATIONS = 0;
	/**
	 * This is used for readability in the code.
	 */
	public static final int CORNER_EDGE = 1;
	/**
	 * This is used for readability in the code.
	 */
	public static final int CROSS = 2;

	/**
	 * This represents the cube displayed on screen. It can be used to generate
	 * a solution for the current state of the cube.
	 */
	protected Cube cube;
	/**
	 * This array indicates which slices are adjacent to each other such that
	 * they share edges.
	 */
	protected int[][] sliceEdgeSharing = { { 0, 5 }, { 0, 2 }, { 0, 4 }, { 0, 3 }, { 5, 3 }, { 5, 2 }, { 4, 2 },
			{ 4, 3 }, { 1, 5 }, { 1, 3 }, { 1, 4 }, { 1, 2 } };
	/**
	 * This array indicates which slices are adjacent to each other such that
	 * they share corners.
	 */
	private int[][] sliceCornerSharing = { { 0, 3, 5 }, { 0, 5, 2 }, { 0, 2, 4 }, { 0, 4, 3 }, { 1, 2, 5 },
			{ 1, 5, 3 }, { 1, 3, 4 }, { 1, 4, 2 } };
	/**
	 * This accumulates the moves required to solve the cube.
	 */
	private LinkedList<String> solveMoves;
	/**
	 * This accumulates the explanation of how to solve the cube.
	 */
	protected String solutionExplanation = "";

	/**
	 * Constructor - assigns a value to the cube field, i.e. the cube to be
	 * solved.
	 * 
	 * @param cube
	 *            the cube for which a solution will be generated.
	 */
	public SolveMaster(Cube cube) {
		this.cube = cube;
		solveMoves = new LinkedList<>();
	}

	/**
	 * @return the explanation of the solution (prose)
	 */
	public String getSolutionExplanation() {
		return solutionExplanation;
	}

	/**
	 * Returns the index of the specified cubie on the cube
	 * 
	 * @param cubie
	 *            the cubie to be found
	 * @return the index of the cubie on the cube
	 */
	public int getIndexOf(Cubie cubie) {
		/*
		 * Slice currentSlice; Cubie currentCubie;
		 * 
		 * for (int slice = 0; slice < 6; ++slice) { currentSlice =
		 * cube.getSlice(slice); for (int piece = 0; piece < 4; ++piece) {
		 * currentCubie = currentSlice.getCorner(piece);
		 * 
		 * if (currentCubie.compareTo(cubie) == 0) return
		 * currentCubie.getCubieIndex();
		 * 
		 * currentCubie = currentSlice.getEdge(piece);
		 * 
		 * if (currentCubie.compareTo(cubie) == 0) return
		 * currentCubie.getCubieIndex(); } }
		 */
		for (int i = 0; i < 12; ++i) {
			try {
				if ((cube.getEdge(i).compareTo(cubie) == 0) || ((cube.getCorner(i).compareTo(cubie) == 0)))
					return i;
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}

		return -1;
	}

	/**
	 * Performs the rotations required so that the cube shows the specified
	 * centre colour on top. The rotations performed are recorded.
	 * 
	 * @param color
	 *            the colour which should end up on top
	 */
	public void rotateToTop(Color color) {
		if (getSliceIndexOfCentre(color) == 0)
			return;

		for (int i = 0; i < 4; ++i) {
			cube.rotate("x");
			catalogMove("x");
			if (getSliceIndexOfCentre(color) == 0)
				return;

			cube.rotate("z");
			catalogMove("z");
			if (getSliceIndexOfCentre(color) == 0)
				return;
		}
	}

	/**
	 * Performs the rotations required so that the cube shows the specified
	 * centre colour on top. The rotations performed are <i>not</i> recorded.
	 * 
	 * @param color
	 *            the colour which should end up on top
	 */
	public void rotateToTopDoNotRecord(Color color) {
		if (getSliceIndexOfCentre(color) == 0)
			return;

		for (int i = 0; i < 4; ++i) {
			cube.rotate("x");
			if (getSliceIndexOfCentre(color) == 0)
				return;

			cube.rotate("z");
			if (getSliceIndexOfCentre(color) == 0)
				return;
		}
	}

	/**
	 * Performs the rotations required so that the specified centre colour end
	 * up on top and front. This will get stuck in an infinite loop if the
	 * centre are opposite, e.g. white and yellow. These parameters cannot be
	 * given during runtime, so if this gets stuck, there is a problem in code.
	 * The rotations are recorded.
	 * 
	 * @param top
	 *            the centre to end up on top.
	 * @param front
	 *            the centre to end up on front.
	 */
	public void rotateToTopFront(Color top, Color front) {
		rotateToTop(top);

		while (getSliceIndexOfCentre(front) != 4) {
			cube.performAbsoluteMoves("y");
			catalogMoves("y");
		}
	}

	/**
	 * Performs the rotations required so that the specified centre colour end
	 * up on top and front. This will get stuck in an infinite loop if the
	 * centre are opposite, e.g. white and yellow. These parameters cannot be
	 * given during runtime, so if this gets stuck, there is a problem in code.
	 * The rotations are <i>not</i> recorded.
	 * 
	 * @param top
	 *            the centre to end up on top.
	 * @param front
	 *            the centre to end up on front.
	 */
	public void rotateToTopFrontDoNotRecord(Color top, Color front) {
		rotateToTopDoNotRecord(top);

		while (getSliceIndexOfCentre(front) != 4) {
			cube.performAbsoluteMoves("y");
		}
	}

	/**
	 * Simplifies the specified moves in the specified way.
	 * 
	 * @param originalMoves
	 *            the moves to be simplified
	 * @param type
	 *            the type of simplication to be applied
	 */
	public static void simplifyMoves(LinkedList<String> originalMoves, int type) {

		switch (type) {
		case CANCELLATIONS:
			cancelMoves(originalMoves);
			break;
		case CORNER_EDGE:
			cancelMoves(originalMoves);
			simplifyCornerEdgeSolution(originalMoves);
			break;
		case CROSS:
			simplifyCross(originalMoves);
			cancelMoves(originalMoves);
			break;
		}

	}

	/**
	 * Simplifies the specified moves so that cancellations occur, <br>
	 * e.g. R R R = R' <br>
	 * and so that split rotations are grouped, <br>
	 * e.g. y U y U = y2 U2
	 * 
	 * @param originalMoves
	 *            the moves to be simplified
	 */
	private static void simplifyCornerEdgeSolution(LinkedList<String> originalMoves) {
		String temp;

		for (int i = originalMoves.size() - 3; i >= 0; --i) {
			if ((originalMoves.get(i).substring(0, 1).equals(originalMoves.get(i + 2).substring(0, 1)))
					&& (("yU".contains(originalMoves.get(i).substring(0, 1))) && ("yU".contains(originalMoves
							.get(i + 1).substring(0, 1))))) {
				temp = originalMoves.get(i);
				originalMoves.set(i, originalMoves.get(i + 1));
				originalMoves.set(i + 1, temp);
				cancelMoves(originalMoves);
			}
		}

	}

	/**
	 * Simplifies the cross solution so that there are no rotations, <br>
	 * e.g. R2 U y2 R = R2 U L
	 * 
	 * @param originalMoves
	 *            the moves to be simplified
	 */
	private static void simplifyCross(LinkedList<String> originalMoves) {
		// Stores the current move being examined.
		String current;

		/*
		 * Starts at the penultimate element since last element will be a z
		 * rotation.
		 */
		for (int i = originalMoves.size() - 2; i >= 0; --i) {
			current = originalMoves.get(i);

			/*
			 * If y rotation is found then, alter all elements after the
			 * rotation so that the rotation is not required.
			 */
			if (current.substring(0, 1).equals("y") && (current.length() <= 2)) {
				for (int j = i + 1; j < originalMoves.size(); ++j) {
					originalMoves.set(j, applyRotationToMove(current, originalMoves.get(j)));
				}

				originalMoves.remove(i);
			}
		}
	}

	/**
	 * Returns the move that performs the same action on the cube if the cube
	 * was not rotated before the move.
	 * 
	 * @param rotation
	 *            the rotation that would have been made.
	 * @param move
	 *            the move that would have been made after the rotation
	 * @return the move that performs the same action as the specified move
	 *         <i>without</i> a rotation
	 */
	private static String applyRotationToMove(String rotation, String move) {
		// Stores the moves so that
		String[] movePairings = { "D", "U", "R", "L", "B", "F" };
		// Stores the offset used in later calculations for the index of the
		// element to return.
		int offset = (rotation.contains("'") ? 1 : 0);
		// Stores the index of the element which is equal to the first character
		// of 'move'
		int index;

		if (!rotation.contains("2")) {
			switch (rotation.substring(0, 1)) {
			case "x":
				switch (move.substring(0, 1)) {
				case "U":
					return movePairings[5 - offset] + move.substring(1);
				case "D":
					return movePairings[4 + offset] + move.substring(1);
				case "F":
					return movePairings[offset] + move.substring(1);
				case "B":
					return movePairings[1 - offset] + move.substring(1);
				default:
					return move;
				}
			case "y":
				switch (move.substring(0, 1)) {
				case "R":
					return movePairings[4 + offset] + move.substring(1);
				case "L":
					return movePairings[5 - offset] + move.substring(1);
				case "F":
					return movePairings[2 + offset] + move.substring(1);
				case "B":
					return movePairings[3 - offset] + move.substring(1);
				default:
					return move;
				}
			case "z":
				switch (move.substring(0, 1)) {
				case "U":
					return movePairings[3 - offset] + move.substring(1);
				case "D":
					return movePairings[2 + offset] + move.substring(1);
				case "L":
					return movePairings[offset] + move.substring(1);
				case "R":
					return movePairings[1 - offset] + move.substring(1);
				default:
					return move;
				}
			}
		} else {
			switch (rotation.substring(0, 1)) {
			case "x":
				if ("LR".contains(move.substring(0, 1)))
					return move;
			case "y":
				if ("UD".contains(move.substring(0, 1)))
					return move;
			case "z":
				if ("FB".contains(move.substring(0, 1)))
					return move;
			}

			index = LinearSearch.linearSearch(movePairings, move.substring(0, 1));
			return movePairings[index + ((index % 2 == 0) ? 1 : -1)];
		}

		return "-1";
	}

	/**
	 * Cancels moves such as L2 L' -> L.
	 * 
	 * @param originalMoves
	 *            the moves to simplify
	 */
	private static void cancelMoves(LinkedList<String> originalMoves) {
		// Stores the index of the current move being examined.
		int index = 0;
		// Stores the resulting combination the two moves being examined.
		String combination;
		// Stores the first element being examined.
		String one;
		// Stores the second element being examined.
		String two;
		// Stores the plane of the move, e.g. R2 has plane = R
		String moveType;

		while (index < (originalMoves.size() - 1)) {
			one = originalMoves.get(index).trim();
			two = originalMoves.get(index + 1).trim();

			// If the moves act on the same slice then
			if (one.substring(0, 1).equals(two.substring(0, 1))) {
				moveType = one.substring(0, 1);
				one = one.substring(1);
				two = two.substring(1);

				combination = getCombination(one, two);

				if (combination.equals("NOT_MATCHING"))
					++index;
				else {
					if (combination.equals("-1")) {
						/*
						 * The moves cancel, so remove each of them.
						 */
						originalMoves.remove(index);
						originalMoves.remove(index);
					} else {
						/*
						 * The two moves can be simplified to one, so remove the
						 * second move and alter the first one.
						 */
						originalMoves.remove(index + 1);
						originalMoves.set(index, moveType + combination);
					}

					/*
					 * The moves have been simplified, so index needs to go back
					 * to check if further simplifications will occur.
					 */
					index = (index == 0) ? 0 : index - 1;
				}
			} else {
				++index;
			}
		}

		/*
		 * This simplifies "x y x" to "z2 y"
		 */
		for (int i = 0; i < originalMoves.size() - 2; ++i) {
			if ((originalMoves.get(i).equals("x")) && (originalMoves.get(i + 1).equals("z"))
					&& (originalMoves.get(i + 2).equals("x"))) {
				originalMoves.remove(i);
				originalMoves.set(i, "z2");
				originalMoves.set(i + 1, "y'");
			}
		}
	}

	/**
	 * @param one
	 *            the first direction
	 * @param two
	 *            the second direction
	 * @return the resultant direction, e.g. ("2", "'") would return ""
	 */
	private static String getCombination(String one, String two) {

		if (((one.startsWith("w")) || (two.startsWith("w")))) {
			try {
				/*
				 * if they are not both wide moves, then return NOT_MATCHING
				 */
				if (!one.substring(0, 1).equals(two.substring(0, 1)))
					return "NOT_MATCHING";
				else {
					one = one.substring(1);
					two = two.substring(1);
				}
			} catch (IndexOutOfBoundsException e) {
				// This will happen when one
				// of them is of the form
				// Xw* and the other is only
				// X
				return "NOT_MATCHING";
			}

		}

		if ((one.equals("")) && (two.equals("")))
			return "2";
		else if (((one.equals("")) && (two.equals("'"))) || ((one.equals("'")) && (two.equals(""))))
			return "-1"; // cancels
		else if (((one.equals("")) && (two.equals("2"))) || ((one.equals("2")) && (two.equals(""))))
			return "'";
		else if ((one.equals("'")) && (two.equals("'")))
			return "2";
		else if (((one.equals("'")) && (two.equals("2"))) || ((one.equals("2")) && (two.equals("'"))))
			return "";
		else
			/* if ((one.equals("2")) && (two.equals("2"))) */
			return "-1"; // cancels
	}

	/**
	 * Determines whether or not the specified corner is solved. This method
	 * uses the actual argument in the further method calls.
	 * 
	 * @param corner
	 *            the corner to be analysed
	 * @return <b>true</b> if the specified corner is solved; <br>
	 *         <b>false</b> otherwise
	 */
	protected boolean isPieceSolved(Corner corner) {
		return (isPieceIsInCorrectPosition(corner) && (cube.getCorner(getIndexOf(corner)).getOrientation() == 0));
	}

	/**
	 * Determines whether or not the specified cubie is solved. This method uses
	 * a retrieved cubie in the further method calls rather than the original
	 * argument. This is useful if the argument does not actually belong to the
	 * same instance of Cube as the field in this class.
	 * 
	 * @param cubie
	 *            the cubie to be analysed
	 * @return <b>true</b> if the specified cubie is solved; <br>
	 *         <b>false</b> otherwise
	 */
	protected boolean newPieceSolved(Cubie cubie) {
		int index = getIndexOf(cubie);

		if (cubie.getStickers().length == 2)
			return isPieceSolved(cube.getEdge(index));
		else
			return isPieceSolved(cube.getCorner(index));

		/*
		 * int orientation = cube.getEdge(index).getOrientation();
		 * 
		 * if ((cube.getSlice(sliceEdgeSharing[index][orientation]).getCentre()
		 * .equals(edge.getStickers()[0])) && (cube.getSlice(
		 * sliceEdgeSharing[index][(orientation + 1) % 2])
		 * .getCentre().equals(edge.getStickers()[1]))) return true;
		 * 
		 * return false;
		 */
	}

	/**
	 * Determines whether or not the specified edge is solved. This method uses
	 * the actual argument in the further method calls.
	 * 
	 * @param edge
	 *            the edge to be analysed
	 * @return <b>true</b> if the specified edge is solved; <br>
	 *         <b>false</b> otherwise
	 */
	protected boolean isPieceSolved(Edge edge) {
		int index = getIndexOf(edge);
		Edge tempEdge = cube.getEdge(index);

		if ((cube.getSlice(sliceEdgeSharing[index][0]).getCentre().equals(tempEdge.getStickers()[0]))
				&& (cube.getSlice(sliceEdgeSharing[index][1]).getCentre().equals(tempEdge.getStickers()[1])))
			return true;

		return false;
	}

	/*
	 * protected boolean pieceSolved(Cubie cubie) { if
	 * (cubie.getStickers().length == 2) return (pieceIsInCorrectPosition(cubie)
	 * && (cube.getEdge(getIndexOf(cubie)).getOrientation() == 0)); else return
	 * (pieceIsInCorrectPosition(cubie) &&
	 * (cube.getCorner(getIndexOf(cubie)).getOrientation() == 0)); }
	 */

	/**
	 * Returns the index of the specified centre colour on the cube. <br>
	 * 0 = Top <br>
	 * 1 = Bottom <br>
	 * 2 = Right <br>
	 * 3 = Left <br>
	 * 4 = Front <br>
	 * 5 = Back <br>
	 * 
	 * @param centre
	 *            the centre colour to be found
	 * @return the index of the slice whose centre is the specified colour
	 */
	private int getSliceIndexOfCentre(Color centre) {
		for (int i = 0; i < 6; ++i)
			if (cube.getSlice(i).getCentre().equals(centre))
				return i;

		return -1;
	}

	/**
	 * Records the specified move
	 * 
	 * @param move
	 *            the move to be recorded
	 */
	private void catalogMove(String move) {
		move = move.trim();

		if (!move.equals(""))
			solveMoves.add(move);
	}

	/*
	 * public void catalogMoves(String moves) { moves = moves.trim();
	 * 
	 * if (moves.equals("")) return;
	 * 
	 * int i = 0; int indexOfSpace; String remainingMoves;
	 * 
	 * while (i < moves.length()) { remainingMoves = moves.substring(i);
	 * indexOfSpace = remainingMoves.indexOf(" ");
	 * 
	 * if (indexOfSpace == -1) indexOfSpace = remainingMoves.length();
	 * 
	 * catalogMove(remainingMoves.substring(0, indexOfSpace)); i += indexOfSpace
	 * + 1; } }
	 */

	/**
	 * Checks that the argument is not null, then records the moves.
	 * 
	 * @param moves
	 *            the moves to be recorded
	 */
	public void catalogMoves(String moves) {
		if ((moves == null) || (moves.trim().equals("")))
			return;

		catMoves(moves.trim(), 0);
	}

	/**
	 * Recursively records the specified moves, one by one. <br>
	 * E.g. "R U R' F" will be recorded as "R", "U", "R'", "F"
	 * 
	 * @param moves
	 *            the moves to be recorded
	 * @param index
	 *            the last index of a space
	 */
	private void catMoves(String moves, int index) {
		// This indicates that all moves have been recorded.
		if (index >= moves.length())
			return;

		// Stores the remaining moves to be recorded.
		String remainingMoves = moves.substring(index).trim();
		// Stores the index of the first space in the remaining moves to be
		// recorded so that the next move can be identified.
		int indexOfSpace = remainingMoves.indexOf(" ");

		if (indexOfSpace == -1)
			indexOfSpace = remainingMoves.length();

		catalogMove(remainingMoves.substring(0, indexOfSpace));
		catMoves(moves, index + indexOfSpace + 1);
	}

	/*
	 * public String[] getCatalogMoves() { String[] moves = new
	 * String[solveMoves.size()];
	 * 
	 * for (int i = 0; i < solveMoves.size(); ++i){ moves[i] =
	 * solveMoves.get(i); }
	 * 
	 * return moves; }
	 */

	/**
	 * @return the recorded moves in a linked list of strings
	 */
	public LinkedList<String> getCatalogMoves() {
		return this.solveMoves;
	}

	/**
	 * @param moves
	 *            the moves to be analysed
	 * @return the number of moves contained within <b>moves</b>
	 */
	public int getNumMoves(String[] moves) {
		int numMoves = 0;
		boolean u = false;

		for (int i = 0; i < moves.length; ++i) {
			if (!("xyz").contains(moves[i].substring(0, 1))) {
				if (moves[i].substring(0, 1).equals("U")) {
					if (!u) {
						++numMoves;
						u = true;
					}
				} else {
					++numMoves;
					u = false;
				}

			}
		}

		return numMoves;
	}

	/**
	 * Clears all moves and the associated explanation
	 */
	public void clearMoves() {
		solveMoves.clear();
		solutionExplanation = "";
	}

	/**
	 * @param cubie
	 *            the cubie to be analysed
	 * @return <b>true</b> if the specified cubie is in the correct position on
	 *         the cube (regardless of orientation); <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isPieceIsInCorrectPosition(Cubie cubie) {
		// Stores the index of the cubie on the cube.
		int index = getIndexOf(cubie);
		// Stores the colours of stickers on the cubie so that the expected
		// centres around the cubie can be compared with the actual centres.
		Color[] centres = new Color[cubie.getStickers().length];
		// Stores true if the current centre sticker being examined is found.
		boolean found;
		// Stores the indices of the slices that immediately surround the cubie.
		int[] slicesIndex = (centres.length == 2) ? sliceEdgeSharing[index] : sliceCornerSharing[index];

		for (int i = 0; i < centres.length; ++i)
			centres[i] = cube.getSlice(slicesIndex[i]).getCentre();

		/*
		 * This compares each expected centre colour with each colour of the
		 * cubie. If all colours are the shared, then the piece is in the
		 * correct position.
		 */
		for (int i = 0; i < centres.length; ++i) {
			found = false;
			for (int j = 0; j < centres.length; ++j) {
				if (centres[i].equals(cubie.getStickers()[j])) {
					found = true;
					break;
				}
			}

			if (!found)
				return false;
		}

		return true;
	}

	/**
	 * @param cubie
	 *            the cubie to be analysed
	 * @return the destination of the cubie on a solved cube according the
	 *         current permutation of the centres.
	 */
	protected int getIndexOfDestination(Cubie cubie) {
		// Stores the number of stickers on the cubie, indicating whether it is
		// a corner or edge.
		int numStickers = cubie.getStickers().length;
		// Stores the indices of the slices that surround each cubie at each
		// location.
		int[][] slicesIndices = (numStickers == 2) ? sliceEdgeSharing : sliceCornerSharing;
		// Stores true if the current centre has been found on the cubie.
		boolean foundCentre;
		// Stores true if the current slicesIndices element represents the
		// destination of the cubie.
		boolean foundPosition;

		/*
		 * Goes through each position on the cube to see if the centre
		 * surrounding that position match the stickers of the specified cubie.
		 */
		for (int i = 0; i < slicesIndices.length; ++i) {
			foundPosition = true;
			for (int j = 0; j < slicesIndices[i].length; ++j) {
				foundCentre = false;
				for (int k = 0; k < numStickers; ++k) {
					if (cube.getSlice(slicesIndices[i][j]).getCentre().equals(cubie.getStickers()[k])) {
						foundCentre = true;
						break;
					}
				}

				if (!foundCentre) {
					foundPosition = false;
					break;
				}
			}
			if (foundPosition)
				return i;
		}

		return -1;
	}

	/**
	 * @param start
	 *            the starting position
	 * @param end
	 *            the ending position
	 * @return the shortest offset between the start and end, e.g. 3 -> -1
	 */
	@SuppressWarnings("unused")
	private int getShortestOffset(int start, int end) {
		return Math.abs(((-4 * (int) ((Math.abs(start - end) % 4) / 2)) + (Math.abs(start - end) % 4)));
	}

	/**
	 * @return the recorded moves as a string with a space between each move
	 */
	public String getStringMoves() {
		return getStringMoves(this.solveMoves);
	}

	/**
	 * @param moves
	 *            the moves to be returned as a string
	 * @return the specified moves as a string with a space between each move
	 */
	public static String getStringMoves(LinkedList<String> moves) {
		String stringMoves = "";
		int size = moves.size();

		for (int i = 0; i < size; ++i) {
			stringMoves += (moves.get(i) + " ");
		}

		return stringMoves;
	}

	/**
	 * Returns the number of trailing U moves in the recorded moves and removes
	 * them simultaneously.
	 * 
	 * @return the number of trailing U moves in the recorded moves
	 */
	public int getNumTrailingU() {
		int i = solveMoves.size() - 1;
		int numTrailing = 0;

		while ((i >= 0) && (solveMoves.get(i).equals("U"))) {
			solveMoves.remove(i);
			++numTrailing;
			--i;
		}

		return numTrailing;
	}

	/**
	 * @param originalMoves
	 *            the moves to be reversed
	 * @return the original moves in reverse order as a string with a space
	 *         between each move
	 */
	public static String getReverseStringMoves(LinkedList<String> originalMoves) {
		String reverseMoves = "";
		String current;

		for (int i = originalMoves.size() - 1; i >= 0; --i) {
			current = originalMoves.get(i);
			if (current.substring(1).equals(""))
				reverseMoves += current.substring(0, 1) + "'" + " ";
			else if (current.substring(1).equals("'"))
				reverseMoves += current.substring(0, 1) + "" + " ";
			else
				reverseMoves += current + " ";
		}

		return reverseMoves;
	}

	/**
	 * @param moves
	 *            the moves to be reversed
	 * @return the moves in reverse order as a string with a space between each
	 *         move
	 */
	public static String getReverseStringMoves(String moves) {
		if (moves == null)
			return "";

		moves = moves.trim();
		if (moves.equals(""))
			return "";

		int i = 0;
		int indexOfSpace;
		String remainingMoves;
		LinkedList<String> reverseString = new LinkedList<>();

		while (i < moves.length()) {
			remainingMoves = moves.substring(i);
			indexOfSpace = remainingMoves.indexOf(" ");

			if (indexOfSpace == -1)
				indexOfSpace = remainingMoves.length();

			reverseString.add(remainingMoves.substring(0, indexOfSpace));
			i += indexOfSpace + 1;
		}

		return getReverseStringMoves(reverseString);
	}

	/**
	 * @param key
	 *            the key that was pressed on the keyboard
	 * @return the move associated with that key press
	 */
	public static String getKeyToMove(String key) {
		key = key.toLowerCase();

		switch (key) {
		case "j":
			return "U";
		case "s":
			return "D";
		case "i":
			return "R";
		case "d":
			return "L";
		case "h":
			return "F";
		case "w":
			return "B";
		case "f":
			return "U'";
		case "l":
			return "D'";
		case "k":
			return "R'";
		case "e":
			return "L'";
		case "g":
			return "F'";
		case "o":
			return "B'";
		case "u":
			return "Rw";
		case "m":
			return "Rw'";
		case "r":
			return "Lw'";
		case "v":
			return "Lw";
		case "x":
			return "M";
		case ".":
			return "M'";
		case ";":
			return "y";
		case "a":
			return "y'";
		case "y":
			return "x";
		case "n":
			return "x'";
		case "p":
			return "z";
		case "q":
			return "z'";

		default:
			return "";
		}
	}

	/**
	 * @param move
	 *            the move to be checked
	 * @return <b>true</b> if the move is valid; <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isValidMove(String move) {
		String[] validMoves = { "U", "D", "R", "L", "F", "B", "U'", "D'", "R'", "L'", "F'", "B'", "Rw", "Lw", "Rw'",
				"Lw'", "M", "M'", "y", "y'", "x", "x'", "z", "z'", "U2", "D2", "R2", "L2", "F2", "B2", "Rw2", "Lw2",
				"M2", "y2", "x2", "z2" };

		if ((LinearSearch.linearSearch(validMoves, move) == -1))
			return false;

		return true;
	}

	/**
	 * This returns a string representation of the current state of the cube so
	 * that it can be saved/loaded to/from file. The first two colours are top
	 * and front centres, and other colours are each sticker of each corner then
	 * each edge.
	 * 
	 * @return the current state of the cube represented as a string
	 */
	public String getStateString() {
		// Accumulates the resulting state-string
		String result = "";
		// Stores the stickers of the current cubie being examined.
		Color[] cStickers;

		/*
		 * Stores the top and front centre colours.
		 */
		result = Cubie.getColorToWord(cube.getSlice(0).getCentre()) + ","
				+ Cubie.getColorToWord(cube.getSlice(4).getCentre()) + ",";

		/*
		 * Stores the corner stickers.
		 */
		for (int i = 0; i < 8; ++i) {
			cStickers = cube.getCorner(i).getStickers();

			for (int j = 0; j < 3; ++j)
				result += Cubie.getColorToWord(cStickers[j]) + ",";
		}

		/*
		 * Stores the edge stickers.
		 */
		for (int i = 0; i < 12; ++i) {
			cStickers = cube.getEdge(i).getStickers();

			for (int j = 0; j < 2; ++j)
				result += Cubie.getColorToWord(cStickers[j]) + ",";
		}

		return result;
	}

	/**
	 * Extracts the state stored in <b>stateString</b> and applies the
	 * corresponding data to the cube so that the cube is in the state specified
	 * in <b>stateString</b>
	 * 
	 * @param stateString
	 *            the state to be given to the cube
	 */
	public void applyStateString(String stateString) {
		int stringIndex = 0, indexOfComma = 0;
		Color[] colors = new Color[50];

		for (int i = 0; i < 50; ++i) {
			indexOfComma = stateString.substring(stringIndex).indexOf(",");
			colors[i] = Cubie.getWordToColor(stateString.substring(stringIndex, stringIndex + indexOfComma));
			stringIndex += indexOfComma + 1;
		}

		rotateToTopFrontDoNotRecord(colors[0], colors[1]);

		for (int i = 0; i < 8; ++i) {
			if (!cube.getCorner(i).getStickers()[0].equals(Color.LIGHT_GRAY))
				cube.getCorner(i).setStickers(colors[i * 3 + 2], colors[i * 3 + 3], colors[i * 3 + 4]);
		}
		for (int i = 0; i < 12; ++i) {
			if (!cube.getEdge(i).getStickers()[0].equals(Color.LIGHT_GRAY))
				cube.getEdge(i).setStickers(colors[i * 2 + 26], colors[i * 2 + 27]);
		}

		Main.assignOrientationsToCubies();
		cube.updateAll();
	}

}
