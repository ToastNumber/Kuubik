package jCube;

import java.awt.Color;

/*
 * Initial orientation = White-top Green-front
 */

/**
 * @author Kelsey McKenna
 */
public class Cube {
	/**
	 * NUM_SLICES is used instead of 6 throughout for readability
	 */
	private static final int NUM_SLICES = 6;
	/**
	 * NUM_EDGES is used instead of 12 throughout for readability
	 */
	private static final int NUM_EDGES = 12;
	/**
	 * NUM_CORNERS is used instead of 8 throughout for readability
	 */
	private static final int NUM_CORNERS = 8;
	/**
	 * This two-dimensional array stores the indices of the slices involved in a
	 * corresponding rotation. The first element stores the slices involved in
	 * ‘x’ rotations, the second stores those involved in ‘y’ rotations, and the
	 * third stores those involve in ‘z’ rotations.
	 */
	private static int[][] rotationSlices = { { 0, 5, 1, 4 }, // x
			{ 2, 4, 3, 5 }, // y
			{ 0, 2, 1, 3 }, // z
	};
	/**
	 * The ith element of this array stores the cubie indices for the ith slice.
	 * The cubie indices indicate the index of a slice’s cubies in relation to
	 * the rest of the cube.
	 */
	private static int[][] cubieIndices = { { 0, 1, 2, 3, 0, 1, 2, 3 }, { 4, 5, 6, 7, 8, 9, 10, 11 },
			{ 2, 1, 4, 7, 1, 5, 11, 6 }, { 0, 3, 6, 5, 3, 7, 9, 4 }, { 3, 2, 7, 6, 2, 6, 10, 7 },
			{ 1, 0, 5, 4, 0, 4, 8, 5 }, };
	/**
	 * This stores the Edges of the cube.
	 */
	private Edge[] edges = new Edge[NUM_EDGES];
	/**
	 * This stores the corners of the cube.
	 */
	private Corner[] corners = new Corner[NUM_CORNERS];
	/**
	 * This stores the slices of the cube. U slice -> slices[0], D slice ->
	 * slices[1], R slice -> slices[2], L slice -> slices[3], F slice ->
	 * slices[4], B slice -> slices[5]
	 */
	private Slice[] slices = new Slice[NUM_SLICES];

	/**
	 * Constructor - sets up cube for display etc.
	 */
	public Cube() {
		resetCube();
	}

	/**
	 * Constructor - acts as a clone
	 * 
	 * @param cube
	 *            the cube to be cloned
	 */
	public Cube(Cube cube) {
		for (int i = 0; i < NUM_EDGES; ++i) {
			edges[i] = new Edge(cube.getEdge(i));
		}
		for (int i = 0; i < NUM_CORNERS; ++i) {
			corners[i] = new Corner(cube.getCorner(i));
		}
		for (int i = 0; i < NUM_SLICES; ++i) {
			slices[i] = new Slice(edges, corners);
		}
	}

	/**
	 * Resets the indices of the cubies to their original state
	 */
	private void resetCubieIndices() {
		for (int i = 0; i < 8; ++i)
			corners[i].setCubieIndex(i);

		for (int i = 0; i < 12; ++i)
			edges[i].setCubieIndex(i);

		for (int slice = 0; slice < NUM_SLICES; ++slice) {
			slices[slice].setCubieIndices(cubieIndices[slice]);
		}
	}

	/**
	 * @param index
	 *            the index of the slice to be returned
	 * @return the <b>index</b>th slice
	 */
	public Slice getSlice(int index) {
		return slices[index];
	}

	/**
	 * @param face
	 *            the name of the face which represents the slice to be returned
	 * @return the slice which is associated with the specified face
	 */
	public Slice getSlice(String face) {
		switch (face) {
		case "U":
			return slices[0];
		case "D":
			return slices[1];
		case "R":
			return slices[2];
		case "L":
			return slices[3];
		case "F":
			return slices[4];
		case "B":
			return slices[5];
		default:
			return null;
		}
	}

	/**
	 * Performs the specified move on the cube
	 * 
	 * @param m
	 *            the move to be performed in WCA notation
	 */
	private void performAbsoluteMove(String m) {
		m = m.toLowerCase();

		switch (m) {
		case "u":
			performMove('j');
			break;
		case "u'":
			performMove('f');
			break;
		case "u2":
			performMove('j');
			performMove('j');
			break;
		case "d":
			performMove('s');
			break;
		case "d'":
			performMove('l');
			break;
		case "d2":
			performMove('l');
			performMove('l');
			break;
		case "r":
			performMove('i');
			break;
		case "r'":
			performMove('k');
			break;
		case "r2":
			performMove('k');
			performMove('k');
			break;
		case "l":
			performMove('d');
			break;
		case "l'":
			performMove('e');
			break;
		case "l2":
			performMove('e');
			performMove('e');
			break;
		case "f":
			performMove('h');
			break;
		case "f'":
			performMove('g');
			break;
		case "f2":
			performMove('g');
			performMove('g');
			break;
		case "b":
			performMove('w');
			break;
		case "b'":
			performMove('o');
			break;
		case "b2":
			performMove('o');
			performMove('o');
			break;
		case "rw":
			performMove('u');
			break;
		case "rw'":
			performMove('m');
			break;
		case "lw":
			performMove('v');
			break;
		case "lw'":
			performMove('r');
			break;
		case "m":
			performMove('x');
			break;
		case "m'":
			performMove('.');
			break;
		case "m2":
			performMove('.');
			performMove('.');
			break;
		default:
			rotate(m);
			break;
		}
	}

	/**
	 * Performs the specified moves on the cube
	 * 
	 * @param moves
	 *            the moves to be performed in WCA notation
	 */
	public void performAbsoluteMoves(String moves) {
		if (moves == null)
			return;

		moves = moves.trim();
		if (moves.equals(""))
			return;

		String currentMove;
		int i = 0;
		int indexOfSpace;
		String remainingMoves;

		while (i < moves.length()) {
			remainingMoves = moves.substring(i);
			indexOfSpace = remainingMoves.indexOf(" ");

			if (indexOfSpace == -1)
				indexOfSpace = remainingMoves.length();

			currentMove = remainingMoves.substring(0, indexOfSpace).trim();
			// if ("xyz".contains(currentMove.substring(0, 1)))
			// rotate(currentMove);
			// else

			performAbsoluteMove(currentMove); // perform absolute move handles
												// rotations

			i += indexOfSpace + 1;
		}
	}

	/**
	 * Performs the specified move on the cube
	 * 
	 * @param m
	 *            the move to be performed represented by a key on keyboard
	 *            (i.e. not WCA notation)
	 */
	public void performMove(char m) {
		String move = Character.toString(m).toLowerCase();
		switch (move) {
		case "j": // U
			slices[0].performMove(1);
			updateCubies(0);
			break;
		case "f": // U'
			slices[0].performMove(-1);
			updateCubies(0);
			break;
		case "i": // R
			slices[2].performMove(1);
			slices[2].twistAllCorners(1);
			updateCubies(2);
			break;
		case "k": // R'
			slices[2].performMove(-1);
			slices[2].twistAllCorners(-1);
			updateCubies(2);
			break;
		case "d": // L
			slices[3].performMove(1);
			slices[3].twistAllCorners(1);
			updateCubies(3);
			break;
		case "e": // L'
			slices[3].performMove(-1);
			slices[3].twistAllCorners(-1);
			updateCubies(3);
			break;
		case "s": // D
			slices[1].performMove(1);
			updateCubies(1);
			break;
		case "l": // D'
			slices[1].performMove(-1);
			updateCubies(1);
			break;
		case "h": // F
			slices[4].performMove(1);
			slices[4].flipAllEdges();
			slices[4].twistAllCorners(1);
			updateCubies(4);
			break;
		case "g": // F'
			slices[4].performMove(-1);
			slices[4].flipAllEdges();
			slices[4].twistAllCorners(-1);
			updateCubies(4);
			break;
		case "w": // B
			slices[5].performMove(1);
			slices[5].flipAllEdges();
			slices[5].twistAllCorners(1);
			updateCubies(5);
			break;
		case "o": // B'
			slices[5].performMove(-1);
			slices[5].flipAllEdges();
			slices[5].twistAllCorners(-1);
			updateCubies(5);
			break;
		case "u": // Rw
			this.performMove('d');
			this.rotate("x");
			break;
		case "m": // Rw'
			performMove('e');
			rotate("x'");
			break;
		case "r": // Lw'
			performMove('k');
			rotate("x");
			break;
		case "v": // Lw
			performMove('i');
			rotate("x'");
			break;
		case "x": // M
			performMove('i');
			performMove('e');
			rotate("x'");
			break;
		case ".": // M'
			performMove('k');
			performMove('d');
			rotate("x");
			break;
		case ";":
			rotate("y");
			break;
		case "a":
			rotate("y'");
			break;
		case "y":
			rotate("x");
			break;
		case "n":
			rotate("x'");
			break;
		case "p":
			rotate("z");
			break;
		case "q":
			rotate("z'");
			break;
		default:
			break;
		}

		updateAll();
	}

	/**
	 * Performs the specified rotation on the cube
	 * 
	 * @param rotation
	 *            the rotation to be performed - this should one of the
	 *            following: <br>
	 *            {x, x', y, y', z, z'}
	 */
	public void rotate(String rotation) {
		/*
		 * If the argument is null, empty, or does not start with 'x', 'y', or
		 * 'z', then nothing can be done, so just return.
		 */
		if ((rotation == null) || (rotation.trim().length() == 0) || (!"xyz".contains(rotation.substring(0, 1))))
			return;

		if (rotation.equals("z")) {
			rot("y");
			rot("x'");
			rot("y'");
		} else if (rotation.equals("z'")) {
			rot("y'");
			rot("x'");
			rot("y");
		} else {
			if (rotation.contains("2")) {
				for (int i = 0; i < 2; ++i)
					rotate(rotation.substring(0, 1));
			} else {
				rot(rotation);
			}
		}
	}

	/**
	 * Performs the specified rotation on the cube
	 * 
	 * @param rotation
	 *            the rotation to be performed - must be "x" or "y" without "2"s
	 */
	private void rot(String rotation) {
		// Stores the indices of the slices affected by the rotation.
		int[] sliceIndices = rotationSlices[((int) rotation.toCharArray()[0] - (int) 'x')];
		// Stores a Slice temporarily so that the cycle of Slices can be
		// completed.
		Slice tempSlice = slices[sliceIndices[0]];
		// Stores the index of the next slice to be changed.
		int nextIndex;
		// Stores the direction in which the slices should be cycled.
		int direction = (rotation.contains("'") ? -1 : 1);
		// Stores the index of the element in slicesIndices that stores the
		// index of the last slice that should be changed.
		int end = (4 + direction) % 4;

		/**
		 * This cycles around the slices with the indices specified in
		 * sliceIndices, e.g. <br>
		 * 0 -> 5 <br>
		 * 5 -> 1 <br>
		 * 1 -> 4 <br>
		 * 4 -> 0 <br>
		 * would be the cycle order for an "x" rotation. <br>
		 */
		for (int i = 0; i != end; i = (i - direction + 4) % 4) {
			nextIndex = (i - direction + 4) % 4;
			slices[sliceIndices[i]] = slices[sliceIndices[nextIndex]];
			performRotationMaintenance(rotation, sliceIndices[i]);
			updateCubies(sliceIndices[i]);
		}

		/**
		 * This is the final step of the cycle. This is similar to declaring a
		 * 'temp' variable when performing a swap, e.g. <br>
		 * temp <- a <br>
		 * a <- b <br>
		 * b <- temp
		 */
		slices[sliceIndices[end]] = tempSlice;
		performRotationMaintenance(rotation, sliceIndices[end]);
		updateCubies(sliceIndices[end]);

		/*
		 * This performs the necessary operations to ensure that the
		 * corners/edges are twisted/flipped correctly after a particular
		 * orientation.
		 */
		switch (rotation) {
		case "x'":
		case "x":
			slices[0].twistAllCorners(1);
			updateCubies(0);
			slices[1].twistAllCorners(-1);
			updateCubies(1);
			break;
		case "y":
		case "y'":
			for (int i = 0; i < 4; ++i) {
				slices[sliceIndices[i]].flipEdge(1);
				updateCubies(sliceIndices[i]);
			}

			break;
		default:
			break;
		}

		if (rotation.equals("x'")) {
			slices[0].performMove(2);
			updateCubies(0);
			slices[1].performMove(2);
			updateCubies(1);
		}
		/*
		 * Update all cubies in each slice to the cubies in this instance of
		 * Cube.
		 */
		updateAll();
	}

	/*
	 * private void rot(String move) { Edge tempEdge; Color tempCentre;
	 * 
	 * switch(move) { case "x": tempEdge = edges[0]; tempCentre =
	 * slices[0].getCentre();
	 * 
	 * edges[0] = edges[2]; edges[2] = edges[10]; edges[10] = edges[8]; edges[8]
	 * = tempEdge;
	 * 
	 * slices[0].setCentre(slices[4].getCentre());
	 * slices[4].setCentre(slices[1].getCentre());
	 * slices[1].setCentre(slices[5].getCentre());
	 * slices[5].setCentre(tempCentre);
	 * 
	 * performAbsoluteMoves("R"); performAbsoluteMoves("L'");
	 * 
	 * edges[0].flip(); edges[2].flip(); edges[8].flip(); edges[10].flip();
	 * break; case "x'": tempEdge = edges[0]; tempCentre =
	 * slices[0].getCentre();
	 * 
	 * edges[0] = edges[8]; edges[8] = edges[10]; edges[10] = edges[2]; edges[2]
	 * = tempEdge;
	 * 
	 * slices[0].setCentre(slices[5].getCentre());
	 * slices[5].setCentre(slices[1].getCentre());
	 * slices[1].setCentre(slices[4].getCentre());
	 * slices[4].setCentre(tempCentre);
	 * 
	 * performAbsoluteMoves("R'"); performAbsoluteMoves("L");
	 * 
	 * edges[0].flip(); edges[2].flip(); edges[8].flip(); edges[10].flip();
	 * break; case "y": tempEdge = edges[4]; tempCentre = slices[4].getCentre();
	 * 
	 * edges[4] = edges[7]; edges[7] = edges[6]; edges[6] = edges[5]; edges[5] =
	 * tempEdge;
	 * 
	 * slices[4].setCentre(slices[2].getCentre());
	 * slices[2].setCentre(slices[5].getCentre());
	 * slices[5].setCentre(slices[3].getCentre());
	 * slices[3].setCentre(tempCentre);
	 * 
	 * performAbsoluteMoves("U"); performAbsoluteMoves("D'");
	 * 
	 * edges[4].flip(); edges[5].flip(); edges[6].flip(); edges[7].flip();
	 * break; case "y'": tempEdge = edges[4]; tempCentre =
	 * slices[4].getCentre();
	 * 
	 * edges[4] = edges[5]; edges[5] = edges[6]; edges[6] = edges[7]; edges[7] =
	 * tempEdge;
	 * 
	 * slices[4].setCentre(slices[3].getCentre());
	 * slices[3].setCentre(slices[5].getCentre());
	 * slices[5].setCentre(slices[2].getCentre());
	 * slices[2].setCentre(tempCentre);
	 * 
	 * performAbsoluteMoves("U'"); performAbsoluteMoves("D");
	 * 
	 * edges[4].flip(); edges[5].flip(); edges[6].flip(); edges[7].flip();
	 * break; }
	 * 
	 * if (move.contains("x")) { for (int i = 0; i < 12; ++i) { if ((i == 0) ||
	 * (i == 2) || (i == 8) || (i == 10)) continue;
	 * 
	 * if (isTestLEdge(edges[i])) edges[i].flipOrientation(); } }
	 * 
	 * else { for (int i = 0; i < 4; ++i) { if (isMLEdge(edges[i]))
	 * edges[i].flipOrientation(); }
	 * 
	 * for (int i = 8; i < 12; ++i) { if (isMLEdge(edges[i]))
	 * edges[i].flipOrientation(); } }
	 * 
	 * 
	 * updateAll(); }
	 */

	/*
	 * @SuppressWarnings("unused") private boolean isMLEdge(Edge edge) { Color[]
	 * stickers = edge.getStickers();
	 * 
	 * if ((!stickers[0].equals(slices[0].getCentre())) &&
	 * (!stickers[1].equals(slices[0].getCentre())) &&
	 * (!stickers[0].equals(slices[1].getCentre())) &&
	 * (!stickers[1].equals(slices[1].getCentre()))) return true; else return
	 * false; }
	 */
	/*
	 * @SuppressWarnings("unused") private boolean isTestLEdge(Edge edge) {
	 * Color[] stickers = edge.getStickers();
	 * 
	 * if ((!stickers[0].equals(slices[2].getCentre())) &&
	 * (!stickers[1].equals(slices[2].getCentre())) &&
	 * (!stickers[0].equals(slices[3].getCentre())) &&
	 * (!stickers[1].equals(slices[3].getCentre()))) return true; else return
	 * false; }
	 */

	/**
	 * Fixes the the cubies after an x rotation
	 * 
	 * @param rotation
	 *            the rotation to be performed
	 * @param i
	 *            the index of the slice affected
	 */
	private void performRotationMaintenance(String rotation, int i) {
		if (rotation.equals("x")) {
			if (i >= 4) {
				slices[i].swapEdges(1, 3);
				slices[i].flipEdge(0);
				slices[i].flipEdge(2);
			}
		} else if (rotation.equals("x'")) {
			if (i <= 1) {
				slices[i].flipEdge(0);
				slices[i].flipEdge(2);
			}
		}
	}

	/**
	 * This method acts as an interface between the cubies of each slice and the
	 * all the cubies in this class. This means that when one slice is changed,
	 * you can change any corresponding slices using the 'updateAll()' method
	 * 
	 * @param sliceIndex
	 *            the slice from which the updates should be taken
	 */
	public void updateCubies(int sliceIndex) {
		Edge cEdge;
		Corner cCorner;

		for (int cubieIndex = 0; cubieIndex < 4; ++cubieIndex) {
			cCorner = slices[sliceIndex].getCorner(cubieIndex);
			cEdge = slices[sliceIndex].getEdge(cubieIndex);

			this.corners[cubieIndices[sliceIndex][cubieIndex]] = cCorner;
			this.edges[cubieIndices[sliceIndex][cubieIndex + 4]] = cEdge;
		}

	}

	/**
	 * Updates the cubies in each slice based on the cubies in this instance of
	 * Cube.
	 */
	public void updateAll() {
		for (int sliceIndex = 0; sliceIndex < NUM_SLICES; ++sliceIndex) {
			for (int cubieIndex = 0; cubieIndex < 4; ++cubieIndex) {
				slices[sliceIndex].setCorner(cubieIndex, corners[cubieIndices[sliceIndex][cubieIndex]]);
				slices[sliceIndex].setEdge(cubieIndex, edges[(cubieIndices[sliceIndex][cubieIndex + 4])]);
			}
		}
		resetCubieIndices();
	}

	/**
	 * @return the edges of the cube
	 */
	public Edge[] getEdges() {
		return edges;
	}

	/**
	 * @return the corners of the cube
	 */
	public Corner[] getCorners() {
		return corners;
	}

	/**
	 * @return the slices of the cube
	 */
	public Slice[] getSlices() {
		return slices;
	}

	/**
	 * @param index
	 *            the index of the edge to be returned
	 * @return the <b>index</b>th edge
	 */
	public Edge getEdge(int index) {
		return edges[index];
	}

	/**
	 * @param index
	 *            the index of the corner to be returned
	 * @return the <b>index</b>th corner
	 */
	public Corner getCorner(int index) {
		return corners[index];
	}

	/**
	 * Resets the cube to its initial solved state with white on top and green
	 * on front.
	 */
	public void resetCube() {
		for (int i = 0; i < NUM_EDGES; ++i)
			edges[i] = new Edge();

		for (int i = 0; i < NUM_CORNERS; ++i)
			corners[i] = new Corner();

		for (int i = 0; i < NUM_SLICES; ++i)
			slices[i] = new Slice(cubieIndices[i]);

		slices[0].setCentre(Color.white);
		slices[1].setCentre(Color.yellow);
		slices[2].setCentre(Color.red);
		slices[3].setCentre(Cubie.orange);
		slices[4].setCentre(Color.green);
		slices[5].setCentre(Color.blue);

		for (int i = 0; i < 8; ++i) { // Give cube initial colors
			edges[i].setStickers(Edge.getInitialStickers(i));
			edges[i].setCubieIndex(i);
			corners[i].setStickers(Corner.getInitialStickers(i));
			corners[i].setCubieIndex(i);
		}

		for (int i = 8; i < 12; ++i) {
			edges[i].setStickers(Edge.getInitialStickers(i));
			edges[i].setCubieIndex(i);
		}

		updateAll();
	}

}
