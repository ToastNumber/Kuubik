package jCube;

/**
 * @author Kelsey McKenna
 */
public class Algorithm {
	/**
	 * The ID of the algorithm in the database
	 */
	private int algorithmID;
	/**
	 * The moves of the algorithm
	 */
	private String moveSequence;
	/**
	 * The comment associated with the algorithm. This is usally a name, such as
	 * 'Sledgehammer'
	 */
	private String comment;

	/**
	 * Constructor allowing the fields of the class to be initialised
	 * 
	 * @param algorithmID
	 *            an integer representing the algorithmID of the algorithm
	 * @param moveSequence
	 *            a string holding the moves in the algorithm
	 * @param comment
	 *            a string holding the comment for the algorithm - this should
	 *            usually be a name such as 'sledgehammer'
	 */
	public Algorithm(int algorithmID, String moveSequence, String comment) {
		this.algorithmID = algorithmID;
		this.moveSequence = moveSequence;
		this.comment = comment;
	}

	/**
	 * @return an integer representing the ID of the algorithm
	 */
	public int getAlgorithmID() {
		return algorithmID;
	}

	/**
	 * @param algorithmID
	 *            an integer representing the ID of the algorithm
	 */
	public void setAlgorithmID(int algorithmID) {
		this.algorithmID = algorithmID;
	}

	/**
	 * @return a string containing the moves of the algorithm
	 */
	public String getMoveSequence() {
		return moveSequence;
	}

	/**
	 * @param moveSequence
	 *            a string containing the moves of the algorithm
	 */
	public void setMoveSequence(String moveSequence) {
		this.moveSequence = moveSequence;
	}

	/**
	 * @return a string containing the comment for the algorithm
	 */
	public String getComment() {
		return "" + comment;
	}

	/**
	 * @param comment
	 *            a string containing the comment for the algorithm
	 */
	public void setComment(String comment) {
		this.comment = "" + comment;
	}

}
