package jCube;

import java.awt.Color;
import java.util.Arrays;

/**
 * @author Kelsey McKenna
 */
public class Cubie implements Comparable<Cubie> {
	/**
	 * Custom colour definition for orange
	 */
	public static final Color orange = new Color(255, 132, 10);
	/**
	 * This indicates the orientation of the cubie. 0 = Oriented, 1 =
	 * Flipped/Clockwise, -1 = Anticlockwise
	 */
	private int orientation = 0;
	/**
	 * This stores the stickers of the cubie.
	 */
	private Color[] stickers;
	/**
	 * This stores the index of the cubie on the cube in relation to the other
	 * pieces.
	 */
	private int cubieIndex;

	/**
	 * Constructor - empty
	 */
	public Cubie() {
	}

	/**
	 * Constructor - assigns stickers to the cubie
	 * 
	 * @param stickers
	 *            the stickers to be assigned to the cube
	 */
	public Cubie(Color[] stickers) {
		this.stickers = stickers;
	}

	/**
	 * Sets the stickers of the cubie to the <b>stickers</b> parameter
	 * 
	 * @param stickers
	 *            the stickers to be assigned to the cubie
	 */
	public void setStickers(Color[] stickers) {
		this.stickers = stickers;
	}

	/**
	 * Sets the cubieIndex of the cubie to the parameter
	 * 
	 * @param cubieIndex
	 *            the new cubie index
	 */
	public void setCubieIndex(int cubieIndex) {
		this.cubieIndex = cubieIndex;
	}

	/**
	 * Sets the orientation of the cubie to the parameter
	 * 
	 * @param orientation
	 *            the value to be assigned to the orientation
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * Sets the <b>index</b>th sticker of the cubie to <b>color</b>
	 * 
	 * @param index
	 *            the index of the sticker to be changed
	 * @param color
	 *            the new color for the <b>index</b>th sticker
	 */
	public void setSticker(int index, Color color) {
		Color[] stickers = Arrays.copyOf(getStickers(), getStickers().length);
		stickers[index] = color;
		setStickers(stickers);
	}

	/**
	 * @return the orientation of the cubie
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * @return an array of colors representing the stickers of the cubie
	 */
	public Color[] getStickers() {
		return stickers;
	}

	/**
	 * @return the cubie index associated with this cubie
	 */
	public int getCubieIndex() {
		return cubieIndex;
	}

	/**
	 * @param otherCubie
	 *            the cubie to which this cubie is compared
	 * @return <b>0</b> if this cubie represents the same cubie as
	 *         <b>otherCubie</b>; <br>
	 *         <b>-1</b> otherwise
	 */
	public int compareTo(Cubie otherCubie) {
		// Stores true if the current sticker of the current cubie is found on
		// otherCubie; false otherwise.
		boolean found;

		/*
		 * i.e. if this instance is a corner, and the other instance is an edge,
		 * then they are obviously not the same.
		 */
		if (otherCubie.getStickers().length != this.stickers.length)
			return -1;

		for (int i = 0; i < stickers.length; ++i) {
			found = false;
			for (int j = 0; j < stickers.length; ++j) {
				/*
				 * Each element of this object's stickers should be in the other
				 * object's stickers. If the element is not found in the other
				 * array, then the method will return false.
				 */
				if (this.getStickers()[i].equals(otherCubie.getStickers()[j])) {
					found = true;
					break;
				}
			}
			/*
			 * This means the one of the elements of this.stickers has not been
			 * found in otherCubie.stickers, so their stickers are not the same
			 */
			if (!found)
				return -1;
		}
		return 0;
	}

	/**
	 * @param otherCubie
	 *            the cubie to which this cubie is compared
	 * @return <b>0</b> if the cubies are the same and the stickers are in the
	 *         same order (but not necessarily positions, e.g. the
	 *         red-green-white corner will be shown to be the same as the
	 *         green-white-red corner; <br>
	 *         <b>-1</b> otherwise
	 */
	public int strictCompareTo(Cubie otherCubie) {
		// TODO Test this method to ensure that the new algorithm is valid

		int stickersLength = this.stickers.length;

		if (stickersLength != otherCubie.getStickers().length)
			return -1;

		/*
		 * Color[] otherStickersCopy = Arrays.copyOf(otherCubie.getStickers(),
		 * otherCubie.getStickers().length); boolean matching; Color current;
		 * 
		 * 
		 * for (int i = 0; i < length; ++i) { current = stickersCopy[0];
		 * matching = true;
		 * 
		 * for (int j = 0; j < length; ++j) { if
		 * (!stickersCopy[j].equals(this.stickers[j])) { matching = false;
		 * break; } }
		 * 
		 * if (matching) return 0;
		 * 
		 * for (int j = 0; j < length - 1; ++j) { // Cycle stickers
		 * stickersCopy[j] = stickersCopy[j + 1]; } stickersCopy[length - 1] =
		 * current;
		 * 
		 * } return -1;
		 */

		int i = 0;

		/*
		 * Find the index of a matching sticker
		 */
		while ((i < stickersLength) && (!otherCubie.getStickers()[0].equals(this.getStickers()[i]))) {
			++i;
		}

		/*
		 * If no matching sticker is found then i == stickersLength, so return
		 * -1
		 */
		if (i == stickersLength) {
			return -1;
		}

		/*
		 * Compare each element until will the expected offset. If any elements
		 * differ, then return -1.
		 */
		for (int j = i + 1; j < i + stickersLength; ++j) {
			if (!stickers[j % stickersLength]
					.equals(otherCubie.getStickers()[(j - i + stickersLength) % stickersLength])) {
				return -1;
			}
		}

		/*
		 * No contradictions have been found, so return 0
		 */
		return 0;
	}

	/**
	 * @param color
	 *            the colour to be analysed
	 * @return the english word used for the colour; <br>
	 *         <b>-1</b> otherwise
	 */
	public static String getColorToWord(Color color) {
		if (color.equals(Color.white))
			return "white";
		else if (color.equals(Color.yellow))
			return "yellow";
		else if (color.equals(Color.red))
			return "red";
		else if (color.equals(Cubie.orange))
			return "orange";
		else if (color.equals(Color.green))
			return "green";
		else if (color.equals(Color.blue))
			return "blue";

		return "-1";
	}

	/**
	 * @param color
	 *            the english word for the colour to be returned
	 * @return a Color object with the associated characteristics of
	 *         <b>color</b>
	 */
	public static Color getWordToColor(String color) {
		color = color.toLowerCase().substring(0, 1);

		switch (color) {
		case "w":
			return Color.white;
		case "y":
			return Color.yellow;
		case "r":
			return Color.red;
		case "o":
			return Cubie.orange;
		case "g":
			return Color.green;
		case "b":
			return Color.blue;
		default:
			System.err.printf("\"getWordToColor(String color)\" -> color = %s%n", color);
			return Color.black;
		}
	}

}