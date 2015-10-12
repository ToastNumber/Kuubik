package jCube;

import java.awt.Color;
import java.util.Arrays;

/*
 * 4 corners and 4 edges per slice
 * Corners are indexed like this:
 * 			0 1
 * 			2 3
 * Edges are indexed like this
 * 			  0
 * 			3   1
 * 			  2
 * 	
 */

/**
 * @author Kelsey McKenna
 */
public class Slice {
	/**
	 * This variable stores the number ‘4’ and is used for readability in code.
	 */
	private static final int NUM_EDGES = 4; // Readability
	/**
	 * This variable stores the number ‘4’ and is used for readability in code.
	 */
	private static final int NUM_CORNERS = 4; // Readability

	/**
	 * This array stores the Edges in the slice.
	 */
	private Edge[] edges = new Edge[NUM_EDGES];
	/**
	 * This arrays stores Corners in the slice.
	 */
	private Corner[] corners = new Corner[NUM_CORNERS];
	/**
	 * This array stores the colour of the centre of the slice.
	 */
	private Color centre;
	/**
	 * This array stores the indices of the cubies in relation to the other
	 * pieces of the cubie. The first four elements are corner indices; the last
	 * four are edge indices.
	 */
	private int[] cubieIndices;

	/**
	 * Constructor - assigns cubie indices to pieces
	 * 
	 * @param cubieIndices
	 *            the first four elements should be corner indices and the next
	 *            four should be edges indices.
	 */
	public Slice(int[] cubieIndices) {
		this.cubieIndices = cubieIndices;

		for (int i = 0; i < 4; ++i) {
			edges[i] = new Edge();
			corners[i] = new Corner();
			corners[i].setCubieIndex(cubieIndices[i]);
			edges[i].setCubieIndex(cubieIndices[i + 4]);
		}
	}

	/**
	 * Constructor - assigns Edges and Corners to the slice
	 * 
	 * @param edges
	 *            the Edges of the slice
	 * @param corners
	 *            the Corners of the slice
	 */
	public Slice(Edge[] edges, Corner[] corners) {
		this.edges = edges;
		this.setCorners(corners);
	}

	/**
	 * Sets the cubie indices of the pieces
	 * 
	 * @param cubieIndices
	 *            the cubie indices to be assigned to the pieces. The first four
	 *            elements should be for the Corners and the next four should be
	 *            for the Edges.
	 */
	public void setCubieIndices(int[] cubieIndices) {
		for (int i = 0; i < 4; ++i) {
			corners[i].setCubieIndex(cubieIndices[i]);
			edges[i].setCubieIndex(cubieIndices[i + 4]);
		}
	}

	/**
	 * Sets the Edges of the slice to the specified Edges
	 * 
	 * @param edges
	 *            the Edges to be assigned to the slice
	 */
	public void setEdges(Edge[] edges) {
		this.edges = edges;
	}

	/**
	 * Sets the Corners of the slice to the specified Corners
	 * 
	 * @param corners
	 *            the Corners to be assigned to the slice
	 */
	public void setCorners(Corner[] corners) {
		this.corners = corners;
	}

	/**
	 * Sets the Edge at the specified index to the specified Edge
	 * 
	 * @param index
	 *            the index of the Edge to be changed
	 * @param edge
	 *            the Edge to be assigned to the specified index
	 */
	public void setEdge(int index, Edge edge) {
		edges[index] = edge;
	}

	/**
	 * Sets the Corner at the specified index to the specified Corner
	 * 
	 * @param index
	 *            the index of the Corner to be changed
	 * @param corner
	 *            the Corner to be assigned to the specified index
	 */
	public void setCorner(int index, Corner corner) {
		corners[index] = corner;
	}

	/**
	 * Sets the centre to the specified Color
	 * 
	 * @param centre
	 *            the Color to be set as the centre
	 */
	public void setCentre(Color centre) {
		this.centre = centre;
	}

	/**
	 * @return an array containing the Edges of the slice
	 */
	public Edge[] getEdges() {
		return edges;
	}

	/**
	 * @return an array containing the Corners of the slice
	 */
	public Corner[] getCorners() {
		return corners;
	}

	/**
	 * @param index
	 *            the index of the Edge to be returned
	 * @return the <b>index</b>th Edge
	 */
	public Edge getEdge(int index) {
		return edges[index];
	}

	/**
	 * @param index
	 *            the index of the Corner to be returned
	 * @return the <b>index</b>th Corner
	 */
	public Corner getCorner(int index) {
		return corners[index];
	}

	/**
	 * @return the centre Color
	 */
	public Color getCentre() {
		return centre;
	}

	/**
	 * @return an array containing the indices for all cubies
	 */
	public int[] getCubieIndices() {
		return this.cubieIndices;
	}

	/**
	 * Performs a move on the slice in the specified direction <br>
	 * 1 - Clockwise 2 - 180 degree -1 - Anticlockwise
	 * 
	 * This method handles an argument of '2'
	 * 
	 * @param direction
	 *            the direction in which to move the slice
	 */
	public void performMove(int direction) {
		int tempDirection = (direction == 2) ? 1 : direction;

		for (int i = 0; i < (int) Math.abs(direction); ++i)
			pMove(tempDirection);
	}

	/**
	 * Performs a move on the slice in the specified direction <br>
	 * 1 - Clockwise -1 - Anticlockwise
	 * 
	 * @param direction
	 *            the direction in which to move the slice
	 */
	private void pMove(int direction) {
		// Stores a copy of the stickers of the last edge in the swap-cycle.
		Color[] tempEdgeStickers = Arrays.copyOf(edges[0].getStickers(), 2);
		// Stores a copy of the stickers of the last corner in the swap-cycle.
		Color[] tempCornerStickers = Arrays.copyOf(corners[0].getStickers(), 3);
		// Stores a copy of the orientation of the last edge in the swap-cycle.
		int tempEOrientation = edges[0].getOrientation();
		// Stores a copy of the orientation of the last corner in the
		// swap-cycle.
		int tempCOrientation = corners[0].getOrientation();
		// Stores index of the next cubie in the swap cycle.
		int nextIndex = 0, end = (4 + direction) % 4;

		// Cycles around the stickers in the specified direction.
		for (int i = 0; i != end; i = (i - direction + 4) % 4) {
			nextIndex = (i - direction + 4) % 4;
			edges[i].setStickers(edges[nextIndex].getStickers());
			edges[i].setOrientation(edges[nextIndex].getOrientation());
			corners[i].setStickers(corners[nextIndex].getStickers());
			corners[i].setOrientation(corners[nextIndex].getOrientation());
		}

		// Completes the cycle
		edges[end].setStickers(tempEdgeStickers);
		edges[end].setOrientation(tempEOrientation);
		corners[end].setStickers(tempCornerStickers);
		corners[end].setOrientation(tempCOrientation);
	}

	/**
	 * Flips all edges so that the stickers and saved orientation changes for
	 * each edge
	 */
	public void flipAllEdges() {
		for (int i = 0; i < 4; ++i) {
			edges[i].flip();
		}
	}

	/**
	 * Flips the stickers and orientation of the Edge at the specified index
	 * 
	 * @param index
	 *            the index of the Edge to be flipped
	 */
	public void flipEdge(int index) {
		this.getEdge(index).flip();
	}

	/**
	 * This method should be called after performing a move so that the corners
	 * are twisted correctly.
	 * 
	 * @param direction
	 *            the direction of the move that was performed.
	 */
	public void twistAllCorners(int direction) {
		for (int i = 0; i < 4; ++i) {
			if (i % 2 == 0)
				corners[i].twist(-1);
			else
				corners[i].twist(1);
		}
	}

	/**
	 * Twists (rotates stickers and changes orientation) the Corner at the
	 * specified index in the specified direction
	 * 
	 * @param index
	 *            the index of the Corner to be twisted
	 * @param direction
	 *            the direction in which to rotate the Corner
	 */
	public void twistCorner(int index, int direction) {
		corners[index].twist(direction);
	}

	/**
	 * Swaps the Edges at the specified indices
	 * 
	 * @param i
	 *            the index of the first Edge to be swapped.
	 * @param j
	 *            the index of the second Edge to be swapped.
	 */
	public void swapEdges(int i, int j) {
		Edge temp = getEdge(i);
		setEdge(i, getEdge(j));
		setEdge(j, temp);
	}

	/**
	 * Swaps the Corners at the specified indices
	 * 
	 * @param i
	 *            the index of the first Corner to be swapped.
	 * @param j
	 *            the index of the second Corner to be swapped.
	 */
	public void swapCorners(int i, int j) {
		Corner temp = getCorner(i);
		setCorner(i, getCorner(j));
		setCorner(j, temp);
	}

	/**
	 * @param cubie
	 *            the cubie to be searched for
	 * @return <b>true</b> if the slice contains the specified Cubie; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean contains(Cubie cubie) {
		for (int i = 0; i < 4; ++i) {
			if (edges[i].compareTo(cubie) == 0)
				return true;
			else if (corners[i].compareTo(cubie) == 0)
				return true;
		}

		return false;
	}

}
