package jCube;

import static java.awt.Color.blue;
import static java.awt.Color.green;
import static java.awt.Color.red;
import static java.awt.Color.white;
import static java.awt.Color.yellow;

import java.awt.Color;
import java.util.Arrays;

/**
 * @author Kelsey McKenna
 */
public class Edge extends Cubie {
	/**
	 * The initial edges of a solved cube with white on top and green on front.
	 * The <b>i</b>th element represents the sticker for the cubie with
	 * cubieIndex <b>i</b>
	 */
	private static final Color[][] INITIAL_EDGES = { { white, blue }, { white, red }, { white, green },
			{ white, orange }, { blue, orange }, { blue, red }, { green, red }, { green, orange }, { yellow, blue },
			{ yellow, orange }, { yellow, green }, { yellow, red } };

	/**
	 * Constructor - blank
	 */
	public Edge() {
	}

	/**
	 * Constructor - assigns colors to the stickers of the Cubie
	 * 
	 * @param stickers
	 *            the colors to be assigned to the stickers of the Edge
	 */
	public Edge(Color[] stickers) {
		super(stickers);
	}

	/**
	 * Constructor - assigns colors to the stickers of the Cubie
	 * 
	 * @param one
	 *            the first sticker colour
	 * @param two
	 *            the second sticker colour
	 */
	public Edge(Color one, Color two) {
		Color[] stickers = { one, two };
		super.setStickers(stickers);
	}

	/**
	 * Constructor - acts as a clone
	 * 
	 * @param edge
	 *            the edge to be cloned
	 */
	public Edge(Edge edge) {
		super(edge.getStickers());
		setOrientation(edge.getOrientation());
	}

	/**
	 * @return the initial edges of a solved cube with white on top and green on
	 *         front
	 */
	public static Color[][] getAllInitialStickers() {
		return Arrays.copyOf(INITIAL_EDGES, 12);
	}

	/**
	 * @param index
	 *            the index of the edge whose stickers to be returned
	 * @return the <b>index</b>th Edge's stickers
	 */
	public static Color[] getInitialStickers(int index) {
		return Arrays.copyOf(INITIAL_EDGES[index], 2);
	}

	/**
	 * @param zero
	 *            the first sticker of the Edge
	 * @param one
	 *            the second sticker of the Edge
	 */
	public void setStickers(Color zero, Color one) {
		Color[] stickers = { zero, one };
		super.setStickers(stickers);
	}

	/**
	 * Flip the positions of the stickers so that the first sticker in place of
	 * the second sticker and vice-versa.
	 */
	private void flipStickers() {
		Color[] stickersCopy = Arrays.copyOf(super.getStickers(), 2);
		setStickers(stickersCopy[1], stickersCopy[0]);
	}

	/**
	 * Flips the edge so that its appearance (<b>stickers</b>) and properties
	 * (<b>orientation</b>) change
	 */
	public void flip() {
		flipStickers();
		flipOrientation();
	}

	/**
	 * Change the orientation in the following mapping: <br>
	 * 0 -> 1 <br>
	 * 1 -> 0
	 */
	public void flipOrientation() {
		super.setOrientation((super.getOrientation() + 1) % 2);
	}

	/**
	 * Returns the secondary colour of an edge, i.e. the colour that is not
	 * white or yellow. If the edge does not contain white or yellow, then the
	 * first sticker will be returned.
	 * 
	 * @return <b>secondary colour</b> of the edge if the Edge has a yellow or
	 *         white sticker; <br>
	 *         <b>first sticker</b> otherwise
	 */
	public Color getSecondaryColor() {
		Color[] stickers = getStickers();

		if ((stickers[0].equals(Color.white)) || (stickers[0].equals(Color.yellow)))
			return stickers[1];
		else
			return stickers[0];
	}

}
