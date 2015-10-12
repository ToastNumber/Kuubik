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
public class Corner extends Cubie {

	/**
	 * Stores the initial permutation of the corners on a solved cube with white
	 * and green centres on top and front respectively.
	 */
	private static final Color[][] INITIAL_CORNERS = { { white, orange, blue }, { white, blue, red },
			{ white, red, green }, { white, green, orange }, { yellow, red, blue }, { yellow, blue, orange },
			{ yellow, orange, green }, { yellow, green, red } };

	/**
	 * Constructor
	 */
	public Corner() {
	}

	/**
	 * @param stickers
	 *            a three-element array containing the colors for the Corner
	 */
	public Corner(Color[] stickers) {
		super(stickers);
	}

	/**
	 * @param zero
	 *            the first sticker on the Corner
	 * @param one
	 *            the second sticker on the Corner
	 * @param two
	 *            the third sticker on the Corner
	 */
	public Corner(Color zero, Color one, Color two) {
		setStickers(zero, one, two);
	}

	/**
	 * Constructor - acts as a clone
	 * 
	 * @param corner
	 *            the corner to be copied
	 */
	public Corner(Corner corner) {
		super(corner.getStickers());
		setOrientation(corner.getOrientation());
	}

	/**
	 * @return the INITIAL_CORNERS array
	 */
	public static Color[][] getAllInitialStickers() {
		return Arrays.copyOf(INITIAL_CORNERS, 8);
	}

	/**
	 * Returns the stickers for the i-th corner
	 * 
	 * @param index
	 *            the index of the corner whose stickers are to be returned
	 * @return the stickers of the specified corner
	 */
	public static Color[] getInitialStickers(int index) {
		/*
		 * for (int i = 0; i < 3; ++i) {
		 * System.out.print(Cubie.getColorToWord(INITIAL_CORNERS[index][i])); }
		 */

		return Arrays.copyOf(INITIAL_CORNERS[index], 3);
	}

	/**
	 * Sets the three stickers of the Corner
	 * 
	 * @param zero
	 *            the first sticker
	 * @param one
	 *            the second sticker
	 * @param two
	 *            the third sticker
	 */
	public void setStickers(Color zero, Color one, Color two) {
		Color[] stickers = { zero, one, two };
		super.setStickers(stickers);
	}

	/**
	 * Changes the saved orientation and stickers accordingly
	 * 
	 * @param direction
	 *            the direction in which to twist the corner. <br>
	 *            <b>1</b> if clockwise; <br>
	 *            <b>-1</b> if anti-clockwise
	 */
	public void twist(int direction) {
		Color temp;
		Color[] stickersCopy = super.getStickers();

		temp = stickersCopy[0];

		if (direction > 0) {
			stickersCopy[0] = stickersCopy[2];
			stickersCopy[2] = stickersCopy[1];
			stickersCopy[1] = temp;
		} else {
			stickersCopy[0] = stickersCopy[1];
			stickersCopy[1] = stickersCopy[2];
			stickersCopy[2] = temp;
		}

		setOrientation(((getOrientation() + direction + 4) % 3) - 1);
	}

}
