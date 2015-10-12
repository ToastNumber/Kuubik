package jCube;

import java.awt.Color;
import java.util.LinkedList;

/**
 * @author Kelsey McKenna
 */
public class CrossSolver extends SolveMaster {
	/**
	 * Stores the Edge objects representing the white-green, white-red,
	 * white-blue, and white-orange edges.
	 */
	private static Edge[] crossEdges = new Edge[4];

	/**
	 * Constructor - assigns an object to the parent class's 'cube' field
	 * 
	 * @param cube
	 *            the cube to be solved
	 */
	public CrossSolver(Cube cube) {
		super(cube);

		for (int i = 0; i < 4; ++i) {
			crossEdges[i] = new Edge(Edge.getInitialStickers(i));
		}
	}

	/**
	 * Solves the cross of the cube in the main window, and records the solution
	 * and explanation at the same time
	 */
	public void solveCross() {
		// Stores the index of the current cross edge in the current slice.
		int indexOfCrossEdgeInSlice;
		// Stores the index of the target for the current cross edge in the E
		// slice.
		int workingEdgeTarget;
		// Counts the number of rotations made so that no more than four are
		// performed.
		int count;
		// Stores the expected offset between the current cross edge and a
		// solved cross edge in the top slice.
		int expectedOffset;
		// Stores true if a cross edge is found in the E slice; false otherwise.
		boolean workingEdgeFound;

		rotateToTop(Color.white);
		clearMoves();
		catalogMoves("Holding " + Cubie.getColorToWord(cube.getSlice(0).getCentre()) + " top and "
				+ Cubie.getColorToWord(cube.getSlice(4).getCentre()) + " front:");

		while (!isCrossSolved()) {
			count = 0;
			workingEdgeFound = false;

			/*
			 * This searches the middle-layer/E-slice for a cross edge, which is
			 * then designated as the 'working' edge. If a cross edge is found,
			 * it will be at the FR position because the cube is rotated between
			 * each comparison.
			 */
			for (count = 0; count < 4; ++count) {
				if (LinearSearch.linearSearch(cube.getSlice(4).getEdge(1).getStickers(), Color.white) != -1) {
					workingEdgeFound = true;
					break;
				} else {
					cube.performAbsoluteMoves("y");
					catalogMoves("y");
				}
			}

			if (workingEdgeFound) {
				/*
				 * If orientation = 0, then the target will be UR, otherwise it
				 * will be UF. This is because if the edge's orientation is 0,
				 * then you would place it in the top face by performing R, i.e.
				 * by placing it at UR, and if the orientation was 1, then you
				 * would place it in the top face by performing F, i.e. by
				 * placing it at UF
				 */
				workingEdgeTarget = cube.getSlice(4).getEdge(1).getOrientation() + 1;
				// Stores a copy of workingEdgeTarget for later manipulation
				int helperTarget = workingEdgeTarget;

				if /* there is a cross edge in U-slice */(((indexOfCrossEdgeInSlice = getIndexOfCrossEdgeInSlice(0, 0)) != -2)
						|| (((indexOfCrossEdgeInSlice = getIndexOfCrossEdgeInSlice(0, 1)) != -2))) {
					if /* the orientation of the found cross edge is 0 */(cube.getSlice(0)
							.getEdge(indexOfCrossEdgeInSlice).getOrientation() == 0) {
						/*
						 * This calculates the expected offset (relative
						 * distance) between the working edge and the existing
						 * cross edge. See getExpectedOffset(...) for more
						 * information.
						 */
						expectedOffset = getExpectedOffset(
								cube.getSlice(0).getEdge(indexOfCrossEdgeInSlice).getStickers()[1],
								cube.getSlice(4).getEdge(1).getStickers()[(cube.getSlice(4).getEdge(1).getOrientation() + 1) % 2]);

						indexOfCrossEdgeInSlice = (indexOfCrossEdgeInSlice == 3) ? -1 : indexOfCrossEdgeInSlice;

						/*
						 * This moves the existing cross edge to the correct
						 * position.
						 */
						while ((workingEdgeTarget - indexOfCrossEdgeInSlice != expectedOffset)
								&& (workingEdgeTarget - indexOfCrossEdgeInSlice != (expectedOffset + 4) % 4)) {
							cube.performAbsoluteMoves("U");
							catalogMoves("U");
							indexOfCrossEdgeInSlice = ((indexOfCrossEdgeInSlice + 2) % 4) - 1;
						}
					}
					/*
					 * Reaching this block means there is an unoriented edge in
					 * the top slice, so you can remove this from the top slice
					 * and solve the working edge at the same time.
					 */
					else {
						indexOfCrossEdgeInSlice = (indexOfCrossEdgeInSlice == 3) ? -1 : indexOfCrossEdgeInSlice;
						for (int i = 0; i < (workingEdgeTarget - indexOfCrossEdgeInSlice + 4) % 4; ++i) {
							cube.performAbsoluteMoves("U");
							catalogMoves("U");
						}
					}
				} else if /* there is an edge in bottom slice */(((indexOfCrossEdgeInSlice = getIndexOfCrossEdgeInSlice(
						1, 0)) != -2) || ((indexOfCrossEdgeInSlice = getIndexOfCrossEdgeInSlice(1, 1)) != -2)) {
					indexOfCrossEdgeInSlice = (indexOfCrossEdgeInSlice == 3) ? -1 : indexOfCrossEdgeInSlice;

					/*
					 * If the bottom slice contains an unoriented edge, then you
					 * want to place this edge in the plane of motion of the
					 * working edge so that you can solve the working edge and
					 * fix the bad edge in the bottom layer at the same time.
					 */
					if (sliceContainsCrossEdgeOriented(1, 1)) {
						helperTarget = (helperTarget + 2) % 4;

						while (cube.getSlice(1).getEdge(helperTarget).getStickers()[1] != Color.white) {
							cube.performAbsoluteMoves("D");
							catalogMoves("D");
						}
					} else {
						while (isCrossEdge(cube.getSlice(1).getEdge((helperTarget)))) {
							cube.performAbsoluteMoves("D");
							catalogMoves("D");
						}
					}
				} else { // All edges are in the E-Slice

					if /* an oriented edge will become misoriented */(cube.getSlice(helperTarget + 1).getEdge(1)
							.getOrientation() == (helperTarget % 2)) {
						cube.performAbsoluteMoves((helperTarget == 1) ? "B'" : "L");
						catalogMoves((workingEdgeTarget == 1) ? "B'" : "L");
					}
				}

				cube.performAbsoluteMoves((workingEdgeTarget == 1) ? "R" : "F'");
				catalogMoves((workingEdgeTarget == 1) ? "R" : "F'");
			} else { // There are no edges in the top or bottom layers
				// Stores the index of the cross edge's destination in the top
				// slice.
				int uSliceIndex = 0;
				// Stores the location of the cross edge in the bottom slice.
				int dSliceIndex;
				count = 0;

				if /* there is an oriented cross edge in the bottom slice */((dSliceIndex = getIndexOfCrossEdgeInSlice(
						1, 0)) != -2) {
					if (dSliceIndex == 1)
						uSliceIndex = 3;
					else if (dSliceIndex == 3)
						uSliceIndex = 1;
					else
						uSliceIndex = dSliceIndex;

					/*
					 * Move any unoriented cross edges in the top slice to the
					 * target for the edge found in the bottom slice so that two
					 * operations can be performed at once.
					 */
					if (sliceContainsCrossEdgeOriented(0, 1))
						while (cube.getSlice(0).getEdge(uSliceIndex).getStickers()[1] != Color.white) {
							cube.performAbsoluteMoves("u");
							catalogMoves("U");
						}
					else {
						/*
						 * Move any existing (and oriented) cross edges out of
						 * the way
						 */
						while (isCrossEdge(cube.getSlice(0).getEdge(uSliceIndex))) {
							cube.performAbsoluteMoves("u");
							catalogMoves("U");
						}
					}
				}
				/*
				 * If there is an unoriented cross edge in the top slice and no
				 * oriented edges in the bottom slice
				 */
				else if ((uSliceIndex = getIndexOfCrossEdgeInSlice(0, 1)) != -2) {
					if (uSliceIndex == 1)
						dSliceIndex = 3;
					else if (uSliceIndex == 3)
						dSliceIndex = 1;
					else
						dSliceIndex = uSliceIndex;

					/*
					 * If there is an unoriented cross edge in the bottom
					 * slice...
					 */
					if (sliceContainsCrossEdgeOriented(1, 1)) {
						while (cube.getSlice(1).getEdge(dSliceIndex).getStickers()[1] != Color.white) {
							cube.performAbsoluteMoves("d");
							catalogMoves("D");
						}
					} else {
						while (isCrossEdge(cube.getSlice(1).getEdge(dSliceIndex))) {
							cube.performAbsoluteMoves("d");
							catalogMoves("D");
						}
					}
				}
				/*
				 * If there is an unoriented cross edge in the bottom slice and
				 * no unoriented cross edges in the top slice
				 */
				else {
					dSliceIndex = getIndexOfCrossEdgeInSlice(1, 1);
					count = 0;

					if (dSliceIndex == 1)
						uSliceIndex = 3;
					else if (dSliceIndex == 3)
						uSliceIndex = 1;
					else
						uSliceIndex = dSliceIndex;

					try {
						/*
						 * Move oriented cross edges out of the way
						 */
						while ((count < 4) && (isCrossEdge(cube.getSlice(0).getEdge(uSliceIndex)))) {
							++count;
							dSliceIndex = (dSliceIndex + 1) % 4;

							if (dSliceIndex == 1)
								uSliceIndex = 3;
							else if (dSliceIndex == 3)
								uSliceIndex = 1;
							else
								uSliceIndex = dSliceIndex;

							cube.performAbsoluteMoves("d");
							catalogMoves("D");
						}
					}
					/*
					 * This exception is thrown if all cross edges are in the
					 * top layer, hence uSliceIndex will be negative
					 */
					catch (ArrayIndexOutOfBoundsException e) {
						if (edgesAreOpposite(cube.getEdge(0), cube.getEdge(2))) {
							cube.performAbsoluteMoves("R U2 R' U2 R");
							catalogMoves("R U2 R' U2 R");
						} else if (edgesAreOpposite(cube.getEdge(0), cube.getEdge(1))) {
							cube.performAbsoluteMoves("R' U' R U'");
							catalogMoves("R' U' R U'");
						} else {
							cube.performAbsoluteMoves("L U L' U");
							catalogMoves("L U L' U");
						}
					}
				}

				/*
				 * Perform the move required to get the edge in the top slice.
				 */
				switch (uSliceIndex) {
				case 0:
					cube.performAbsoluteMoves("B");
					catalogMoves("B");
					break;
				case 1:
					cube.performAbsoluteMoves("R");
					catalogMoves("R");
					break;
				case 2:
					cube.performAbsoluteMoves("F");
					catalogMoves("F");
					break;
				case 3:
					cube.performAbsoluteMoves("L");
					catalogMoves("L");
					break;
				default:
					break;
				}
			}
		}

		// Stores the y moves in the solution.
		LinkedList<String> temp = new LinkedList<>();
		LinkedList<String> catalogMoves = getCatalogMoves();

		for (int i = 0; i < catalogMoves.size(); ++i) {
			if (catalogMoves.get(i).contains("y"))
				temp.add(catalogMoves.get(i));
		}

		simplifyMoves(temp, SolveMaster.CANCELLATIONS);

		if (temp.size() > 0) {
			if (temp.get(0).contains("'")) {
				// The cross doesn't require any
				// rotations, so undo any
				// rotations performed.
				cube.rotate("y");
			} else if (temp.get(0).contains("2")) {
				cube.rotate("y2");
			} else {
				cube.rotate("y'");
			}
		}

		cube.rotate("z2");
		catalogMoves("z2");
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

		if ((colorOne = edgeOne.getStickers()[1]).equals(Color.white))
			colorOne = edgeOne.getStickers()[0];
		if ((colorTwo = edgeTwo.getStickers()[1]).equals(Color.white))
			colorOne = edgeTwo.getStickers()[0];

		int one = -1, two = -1;

		for (int i = 0; i < 4; ++i) {
			if (crossEdges[i].getStickers()[1].equals(colorOne))
				one = i;
			else if (crossEdges[i].getStickers()[1].equals(colorTwo))
				two = i;
		}

		return ((one + two) % 2 == 0);
	}

	/**
	 * @param sliceIndex
	 *            the index of the slice to be analysed
	 * @param orientation
	 *            the orientation to be found
	 * @return <b>true</b> if the specified slice contains a cross edge with the
	 *         specified orientation; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean sliceContainsCrossEdgeOriented(int sliceIndex, int orientation) {
		Slice slice = cube.getSlice(sliceIndex);
		Edge edge;

		for (int i = 0; i < 4; ++i) {
			edge = slice.getEdge(i);
			if ((edge.getOrientation() == orientation)
					&& ((edge.getStickers()[1] == Color.white || edge.getStickers()[1] == Color.white)))
				return true;
		}

		return false;
	}

	/**
	 * @param edge
	 *            the edge to be analysed
	 * @return <b>true</b> if the specified edge is a cross edge, i.e. the edge
	 *         contains a white sticker; <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isCrossEdge(Edge edge) {
		Color[] stickers = edge.getStickers();
		if ((stickers[0] == Color.white) || (stickers[1] == Color.white))
			return true;
		else
			return false;
	}

	/**
	 * @param relativeSolvedEdge
	 *            the secondary colour of a cross edge that is solved
	 *            (relatively) to other pieces
	 * @param workingEdge
	 *            the secondary colour of a cross edge that is not solved at all
	 * @return the expected offset between the two cross edges on a solved cube
	 */
	private/* public */int getExpectedOffset(Color relativeSolvedEdge, Color workingEdge) {
		int end = 0, start = 0;

		for (int i = 0; i < 4; ++i) {
			if (crossEdges[i].getSecondaryColor().equals(relativeSolvedEdge))
				start = i;
			else if (crossEdges[i].getSecondaryColor().equals(workingEdge))
				end = i;
		}

		if (end - start <= -2)
			return (end - start) + 4;
		else if (end - start == 3)
			return -1;
		else
			return end - start;
	}

	/**
	 * @param sliceIndex
	 *            the index of the slice whose edges are to be searched
	 * @param orientation
	 *            the orientation of the found cross edge must be of this
	 *            orientation
	 * @return the index of a cross edge in the specified slice with the
	 *         specified orientation
	 */
	private int getIndexOfCrossEdgeInSlice(int sliceIndex, int orientation) {
		Slice slice = cube.getSlice(sliceIndex);
		Color[] stickers;
		for (int i = 0; i < 4; ++i) {
			stickers = slice.getEdge(i).getStickers();
			if (stickers[orientation].equals(Color.white))
				return i;
		}

		return -2;
	}

	/*
	 * private int getNumCrossEdgesOriented() { int numOriented = 4; Edge[]
	 * edges = cube.getEdges(); Color[] stickers;
	 * 
	 * for (int i = 0; i < 12; ++i) { stickers = edges[i].getStickers(); if
	 * (stickers[0].equals(Color.white) || stickers[1].equals(Color.white))
	 * numOriented -= edges[i].getOrientation(); }
	 * 
	 * return numOriented; }
	 */

	/**
	 * Indicates whether or not the cross is solved. Solves AUF at the same
	 * time.
	 * 
	 * @return <b>true</b> if the cross is solved; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isCrossSolved() {
		boolean solved;

		for (int count = 0; count < 4; ++count) {
			solved = true;
			for (int i = 0; i < 4; ++i) {
				if (!isPieceSolved(crossEdges[i])) {
					solved = false;
					break;
				}
			}

			if (solved)
				return true;
			else { // Check AUF
				cube.performAbsoluteMoves("U");
				catalogMoves("U");
			}

		}
		return false;
	}

	/*
	 * private boolean uFaceChanged(Slice newSlice, Edge[] originalSliceEdges) {
	 * Edge currentEdge; boolean found = false;
	 * 
	 * for (int i = 0; i < 4; ++i) { found = false; currentEdge =
	 * originalSliceEdges[i];
	 * 
	 * if (isCrossEdge(currentEdge)) { for (int j = 0; j < 4; ++j) { if
	 * (newSlice.getEdge(j).compareTo(currentEdge) == 0) { found = true; break;
	 * } } if (!found) return true; else continue; }
	 * 
	 * currentEdge = newSlice.getEdge(i); if (isCrossEdge(currentEdge)) { for
	 * (int j = 0; j < 4; ++j) { if
	 * (originalSliceEdges[j].compareTo(currentEdge) == 0) { found = true;
	 * break; } } if (!found) return true; } }
	 * 
	 * return false; }
	 * 
	 * public String getSolutionExplanation() { String solutionExplanation =
	 * super.solutionExplanation; LinkedList<String> moves = getCatalogMoves();
	 * int size = moves.size(); Cube tempCube = new Cube(); int edgeIndex = 0,
	 * orientation = 0; Color[] stickers; String currentMove; Edge[] oldEdges =
	 * new Edge[4];
	 * 
	 * if (size == 0) return "";
	 * 
	 * for (int i = 0; i < 4; ++i) oldEdges[i] = new Edge();
	 * 
	 * tempCube.rotate("z2");
	 * 
	 * while (!tempCube.getSlice(4).getCentre().equals(originalCentre))
	 * tempCube.rotate("y");
	 * 
	 * tempCube.performAbsoluteMoves(getReverseStringMoves(moves));
	 * 
	 * for (int i = 0; i < size; ++i) { currentMove = moves.get(i);
	 * 
	 * for (int j = 0; j < 4; ++j)
	 * oldEdges[j].setStickers(Arrays.copyOf(tempCube.getEdge(j).getStickers(),
	 * 2));
	 * 
	 * if (!"xyz".contains(currentMove.substring(0, 1)))
	 * tempCube.performAbsoluteMoves(currentMove);
	 * 
	 * if (uFaceChanged(tempCube.getSlice(0), oldEdges)) { switch
	 * (moves.get(i).substring(0, 1)) { case "F": edgeIndex = 2; orientation =
	 * 1; break; case "B": edgeIndex = 0; orientation = 1; break; case "R":
	 * edgeIndex = 1; orientation = 0; break; case "L": edgeIndex = 3;
	 * orientation = 0; break; default: break; }
	 * 
	 * stickers = tempCube.getEdge(edgeIndex).getStickers();
	 * 
	 * solutionExplanation +=
	 * String.format("Insert the %s-%s edge into the U face%n",
	 * Cubie.getColorToWord(stickers[orientation]),
	 * Cubie.getColorToWord(stickers[(orientation + 1) % 2])); } }
	 * 
	 * return solutionExplanation; }
	 */

}
