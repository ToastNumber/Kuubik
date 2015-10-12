package jCube;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 * @author Kelsey McKenna
 */
public class Solve {

	/**
	 * This stores the time of the solve.
	 */
	private String time = "";
	/**
	 * This stores the comment for the solve.
	 */
	private String comment = "";
	/**
	 * This stores the penalty for the solve.
	 */
	private String penalty = "";
	/**
	 * This stores the scramble for the solve.
	 */
	private String scramble = "";
	/**
	 * This stores the solution for the solve.
	 */
	private String solution = "";
	/**
	 * This stores the move count for the solve.
	 */
	private int moveCount = 0;

	/**
	 * Constructor - assigns values to time, penalty, and comment fields
	 * 
	 * @param time
	 *            the time of the solve
	 * @param penalty
	 *            the penalty of the solve
	 * @param comment
	 *            the comment of the solve
	 */
	public Solve(String time, String penalty, String comment) {
		this.time = time;
		this.penalty = penalty;
		this.comment = comment;
	}

	/**
	 * Constructor - assigns values to time, penalty, comment, scramble, and
	 * solution fields
	 * 
	 * @param time
	 *            the time of the solve
	 * @param penalty
	 *            the penalty of the solve
	 * @param comment
	 *            the comment of the solve
	 * @param scramble
	 *            the scramble of the solve
	 * @param solution
	 *            the solution of the solve
	 */
	public Solve(String time, String penalty, String comment, String scramble, String solution) {
		this.time = time;
		this.penalty = penalty;
		this.comment = comment;
		this.scramble = scramble;
		this.solution = solution;
		this.moveCount = getMoveCount(solution);
	}

	/**
	 * Constructor - assigns values to time, penalty, comment, scramble, and
	 * solution fields
	 * 
	 * @param time
	 *            the time of the solve
	 * @param penalty
	 *            the penalty of the solve
	 * @param comment
	 *            the comment of the solve
	 * @param scramble
	 *            the scramble of the solve
	 * @param solution
	 *            the solution of the solve
	 */
	public Solve(String time, String penalty, String comment, String scramble, LinkedList<String> solution) {
		int size = solution.size();

		this.time = time;
		this.penalty = penalty;
		this.comment = comment;
		this.scramble = scramble;
		this.moveCount = size;

		for (int i = 0; i < size; ++i)
			this.solution += solution.get(i) + " ";
	}

	/**
	 * Sets the time of the solve.
	 * 
	 * @param time
	 *            the time of the solve in string format and formatted
	 *            representation, e.g. the input should be 1:02.00 rather than
	 *            62.0
	 */
	public void setStringTime(String time) {
		this.time = time;
	}

	/**
	 * Sets the time of the solve
	 * 
	 * @param time
	 *            the time of the solve as a real number
	 */
	public void setNumericTime(double time) {
		this.time = Double.toString(time);
	}

	/**
	 * Sets the penalty of the solve
	 * 
	 * @param penalty
	 *            the penalty of the solve
	 */
	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}

	/**
	 * Sets the comment for the solve
	 * 
	 * @param comment
	 *            the comment for the solve
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param scramble
	 *            the scramble for the solve
	 */
	public void setScramble(String scramble) {
		this.scramble = scramble;
	}

	/**
	 * @param solution
	 *            the solution for the solve
	 */
	public void setSolution(String solution) {
		this.solution = solution;
	}

	/**
	 * @param moveCount
	 *            the move count for the solve
	 */
	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}

	/**
	 * @return the time in a string representation
	 */
	public String getStringTime() {
		return time;
	}

	/**
	 * @return a numeric representation of the solve's time
	 */
	public double getNumericTime() {
		return getFormattedStringToDouble(time);
	}

	/**
	 * @return the penalty for the solve
	 */
	public String getPenalty() {
		return this.penalty;
	}

	/**
	 * @return the comment for the solve
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the scramble for the solve
	 */
	public String getScramble() {
		return scramble;
	}

	/**
	 * @return the solution for the solve
	 */
	public String getSolution() {
		return solution;
	}

	/**
	 * @return the move count for the solve
	 */
	public int getMoveCount() {
		return moveCount;
	}

	/**
	 * Determines whether the specified time is in a valid. All valid formats
	 * are: MM:SS.sss <br>
	 * MM:SS. <br>
	 * MM:S.sss <br>
	 * MM:S. <br>
	 * M:SS.sss <br>
	 * M:SS. <br>
	 * M:S.sss <br>
	 * M:S. <br>
	 * SS.sss <br>
	 * SS. <br>
	 * S.sss <br>
	 * S. <br>
	 * DNF <br>
	 * 
	 * @param time
	 *            the time to be analysed
	 * @return <b>true</b> if the specified time is valid; <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isValidTime(String time) {
		if (time == null)
			return false;

		if (time.equals("DNF"))
			return true;
		else if (!time.matches("(\\d{1,2}:)?\\d{1,2}\\.\\d*"))
			return false;
		// Times cannot be greater than or equal to one hour
		else if (getFormattedStringToDouble(time) > 3599.59)
			return false;
		else
			return true;

	}

	/**
	 * Pads a time-string with leading and trailing zeros where appropriate. The
	 * argument must be in the form X:M.ss or similar so that it can be padded
	 * correctly.
	 * 
	 * @param time
	 *            the time-string to be padded
	 * @return the padded time-string
	 */
	public static String getPaddedTime(String time) {
		// Stores the resulting padded time-string to be returned.
		String paddedTime = "";
		// Stores the index of the ':' character in the 'time'.
		int indexOfColon = 0;
		// Stores the index of the '.' character in the 'time'.
		int indexOfPeriod = 0;
		// Iterates over each character in 'time'.
		int j = 0;

		if ((indexOfColon = time.indexOf(":")) != -1) {
			// Finds the index of the first non-zero character
			while (time.substring(j, j + 1).equals("0"))
				++j;

			if (j < indexOfColon)
				paddedTime += time.substring(j, indexOfColon + 1);
		}

		indexOfPeriod = time.indexOf(".");

		if (indexOfColon != -1) {
			/*
			 * Adds leading zeros as required after the colon
			 */
			for (int i = 0; i < 3 - indexOfPeriod + indexOfColon; ++i) {
				paddedTime += "0";
			}
		}

		paddedTime += time.substring(indexOfColon + 1);

		/*
		 * Adds trailing zeros so that there are two digits after decimal point
		 */
		int end = 3 - time.length() + indexOfPeriod;
		for (int i = 0; i < end; ++i)
			paddedTime += "0";

		return paddedTime;
	}

	/**
	 * Returns the numerical value represented by the formatted time-string
	 * 
	 * @param time
	 *            the formatted time-string from which the numerical time is to
	 *            be calculated
	 * @return the numerical value represented by <b>time</b>
	 */
	public static double getFormattedStringToDouble(String time) {
		if (time.equalsIgnoreCase("DNF"))
			return -1;

		// Stores the the index of the ':' character in 'time'.
		int index = 0;
		// Stores the resulting numerical representation of 'time'.
		double result = 0;

		/*
		 * If the formatted string contains a minute component, then multiply
		 * this part by 60 to get the number of seconds.
		 */
		if ((index = time.indexOf(":")) != -1)
			result += 60 * Integer.valueOf("0" + time.substring(0, index));

		result += Double.parseDouble("0" + time.substring(index + 1));

		return result;
	}

	/**
	 * Returns a formatted time-string representing the same number of seconds
	 * as the argument.
	 * 
	 * @param seconds
	 *            the number of seconds of the time
	 * @return a formatted time-string representting the same number of seconds
	 *         as the argument.
	 */
	public static String getSecondsToFormattedString(double seconds) {
		String formattedString = "";

		formattedString = Integer.toString((int) seconds / 60);

		seconds = seconds % 60;
		formattedString += ":" + String.format("%.02f", seconds);

		return getPaddedTime(formattedString);
	}

	/**
	 * @param moves
	 *            the sequence of moves whose length is to be calculated.
	 * @return the number of moves represented in <b>moves</b>
	 */
	public static int getMoveCount(String moves) {
		int length = moves.trim().length();
		int num = (length == 0) ? 0 : 1;

		for (int i = 0; i < length; ++i) {
			if (moves.substring(i, i + 1).equals(" "))
				++num;
		}

		return num;
	}

	/**
	 * @param dateString
	 *            the date to be analysed
	 * @return <b>true</b> if the date is valid and in the form yyyy-MM-dd
	 *         HH:mm:ss <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isValidDate(String dateString) {
		try {
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
			if (!dateString.matches("\\d{4,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}"))
				return false;
			else
				return isValidDateCheck(dateString.substring(0, dateString.indexOf(" ")))
						&& isValidTimeCheck(dateString.substring(dateString.indexOf(" ") + 1));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param dateString
	 *            the date to be analysed
	 * @return <b>true</b> if the date is in the form yyyy-MM-dd and is valid; <br>
	 *         <b>false</b> otherwise
	 */
	private static boolean isValidDateCheck(String dateString) {
		try {
			// new SimpleDateFormat("yyyy-MM-dd").parse(dateString);

			int day, month, year;

			year = Integer.valueOf(dateString.substring(0, dateString.indexOf("-")));
			dateString = dateString.substring(dateString.indexOf("-") + 1);

			month = Integer.valueOf(dateString.substring(0, dateString.indexOf("-")));
			day = Integer.valueOf(dateString.substring(dateString.indexOf("-") + 1));

			if ((month > 12) || (getNumDaysInMonth(month, year) < day))
				return false;
			else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param month
	 *            the month whose number of days is to be found
	 * @param year
	 *            the year of the date
	 * @return the number of days in the month
	 */
	private static int getNumDaysInMonth(int month, int year) {
		switch (month) {
		case 2:
			if ((year % 4 == 0) && ((year % 100 == 0) ? (year % 400 == 0) : true)) {
				return 29;
			} else {
				return 28;
			}
		case 4:
		case 6:
		case 7:
		case 11:
			return 30;
		default:
			return 31;
		}
	}

	/**
	 * @param timeString
	 *            the time to be analysed
	 * @return <b>true</b> if the time is valid; <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean isValidTimeCheck(String timeString) {
		String one, two, three;

		one = timeString.substring(0, timeString.indexOf(":"));
		timeString = timeString.substring(timeString.indexOf(":") + 1);

		two = timeString.substring(0, timeString.indexOf(":"));
		timeString = timeString.substring(timeString.indexOf(":") + 1);

		three = timeString;

		return (Integer.valueOf(one) < 24) && (Integer.valueOf(two) < 60) && (Integer.valueOf(three) < 60);
	}

}
