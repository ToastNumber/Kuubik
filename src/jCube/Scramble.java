package jCube;

/**
 * @author Kelsey McKenna
 */
public class Scramble {

	/**
	 * This array stores all possible types of moves and their corresponding
	 * rotation directions.
	 */
	private static final String[] moves = { "RX", "LX", "UY", "DY", "FZ", "BZ" };
	/**
	 * This stores the three different directions that can be attributed to a
	 * move.
	 */
	private static final String[] directions = { "", "'", "2" };

	/**
	 * @return a randomly generated 25-move scramble
	 */
	public static String generateScramble() {
		String s = "";

		String penultimateFace = "  ";
		String lFace = "  ";

		for (int i = 0; i < 25; ++i) {
			String newFace = getRandomFace(penultimateFace, lFace);
			s += newFace.charAt(0) + getRandomDirection() + " ";
			penultimateFace = lFace;
			lFace = newFace;
		}

		return s;
	}

	/**
	 * @return a random element from the directions array
	 */
	private static String getRandomDirection() {
		return directions[(int) (Math.random() * 3)];
	}

	/**
	 * @param penultimate
	 *            the penultimate move in the scramble
	 * @param last
	 *            the last move in the scramble
	 * @return a random face whose direction of motion is in the same axis
	 *         compared with the penultimate and last moves. For example, R L F
	 *         would be an acceptable series of moves because the directions of
	 *         motion of these moves is x x z. U D U' would not be acceptable
	 *         because the directions of motion of these moves is y y y, which
	 *         are all the same so a new move must be generated.
	 */
	private static String getRandomFace(String penultimate, String last) {
		String toReturn = moves[(int) (Math.random() * moves.length)];

		if (last.equals(toReturn) || isSameAxis(penultimate, last, toReturn))
			return getRandomFace(penultimate, last);

		return toReturn;
	}

	/**
	 * @param a
	 *            the first move with its associated rotation direction
	 * @param b
	 *            the second move with its associated rotation direction
	 * @param c
	 *            the third move with its associated rotation direction
	 * @return <b>true</b> if the three moves are in the same axis; <br>
	 *         <b>false</b> otherwise
	 */
	private static boolean isSameAxis(String a, String b, String c) {
		return a.charAt(1) == b.charAt(1) && b.charAt(1) == c.charAt(1);
	}

}