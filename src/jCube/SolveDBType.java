package jCube;

/**
 * @author Kelsey McKenna
 */
public class SolveDBType extends Solve {

	/**
	 * The ID of the solve
	 */
	private int id;
	/**
	 * The date the solve was added
	 */
	private String dateAdded = "";

	/**
	 * Constructor - assigns values to fields
	 * 
	 * @param id
	 *            the ID for the solve
	 * @param time
	 *            the time for the solve
	 * @param penalty
	 *            the penalty for the solve
	 * @param comment
	 *            the comment for the solve
	 * @param scramble
	 *            the scramble for the solve
	 * @param solution
	 *            the solution for the solve
	 * @param dateAdded
	 *            the solve was added
	 */
	public SolveDBType(int id, String time, String penalty, String comment, String scramble, String solution,
			String dateAdded) {
		super(time, penalty, comment, scramble, solution);
		this.id = id;
		this.dateAdded = dateAdded;
	}

	/**
	 * @return ID of the solve
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return the date the solve was added
	 */
	public String getDateAdded() {
		return dateAdded;
	}

}