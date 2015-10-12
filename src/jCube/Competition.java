package jCube;

import java.text.SimpleDateFormat;

/**
 * @author Kelsey McKenna
 */
public class Competition {

	/**
	 * The ID of the competition
	 */
	private int competitionID;
	/**
	 * The date of the competition
	 */
	private String date;

	/**
	 * Constructor to initialise fields
	 * 
	 * @param competitionID
	 *            the ID of the competition
	 * @param date
	 *            the date of the competition
	 */
	public Competition(int competitionID, String date) {
		this.setID(competitionID);
		this.setDate(date);
	}

	/**
	 * @return the ID of the competition
	 */
	public int getID() {
		return competitionID;
	}

	/**
	 * @param competitionID
	 *            the ID of the competition
	 */
	public void setID(int competitionID) {
		this.competitionID = competitionID;
	}

	/**
	 * @return the date of the competition
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date of the competition
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Returns a value indicating whether the argument is a valid date for a
	 * competition
	 * 
	 * @param dateString
	 *            the date to be analysed
	 * @return true if the argument is valid and is in the format dd/MM/yyyy;
	 *         false otherwise
	 */
	public static boolean isValidDate(String dateString) {
		try {
			/*
			 * This will throw a ParseException if dateString is not in the
			 * correct format. No assignment statement is needed; this statement
			 * just confirms that dateString is in the correct format to
			 * proceed.
			 */
			new SimpleDateFormat("dd/MM/yyyy").parse(dateString);

			// Stores an integer representation of the day in the month.
			int day;
			// Stores an integer representation of the month in the year.
			int month;
			// Stores an integer representation of the year.
			int year;

			day = Integer.valueOf(dateString.substring(0, dateString.indexOf("/")));
			dateString = dateString.substring(dateString.indexOf("/") + 1);

			month = Integer.valueOf(dateString.substring(0, dateString.indexOf("/")));
			year = Integer.valueOf(dateString.substring(dateString.indexOf("/") + 1));

			/*
			 * This means the method will return false if a month greater than
			 * '12' is indicated. It will also return false if the day indicated
			 * is greater than the number of days in the specified month.
			 */
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
	 *            the month of the date in question
	 * @param year
	 *            the year of the date in question
	 * @return the number of days in the month
	 */
	private static int getNumDaysInMonth(int month, int year) {
		switch (month) {
		case 2:
			/*
			 * This is a leap year checker. A year is a leap year if it is
			 * divisible by four and not divisible by 100 unless it is also
			 * divisible by 400.
			 */
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

}
