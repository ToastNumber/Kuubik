package jCube;

/**
 * @author Kelsey McKenna
 */
public class MemberCompetition {

	/**
	 * Stores the competition ID of the record
	 */
	private int competitionID;
	/**
	 * Stores the member ID of the record
	 */
	private int memberID;
	/**
	 * This stores the 5 times of the average of the record.
	 */
	private String[] times;

	/**
	 * Constructor - Assigns values to fields
	 * 
	 * @param competitionID
	 *            the ID of the competition
	 * @param memberID
	 *            the ID of the member
	 * @param times
	 *            an array of the 5 times recorded by the member in the
	 *            competition
	 */
	public MemberCompetition(int competitionID, int memberID, String[] times) {
		this.competitionID = competitionID;
		this.memberID = memberID;
		this.times = times;
	}

	/**
	 * Constructor - Assigns values to fields
	 * 
	 * @param competitionID
	 *            the ID of the competition
	 * @param memberID
	 *            the ID of the member
	 * @param time1
	 *            the first time of the average
	 * @param time2
	 *            the second time of the average
	 * @param time3
	 *            the third time of the average
	 * @param time4
	 *            the fourth time of the average
	 * @param time5
	 *            the fifth time of the average
	 */
	public MemberCompetition(int competitionID, int memberID, String time1, String time2, String time3, String time4,
			String time5) {
		this.competitionID = competitionID;
		this.memberID = memberID;

		times = new String[] { time1, time2, time3, time4, time5 };
	}

	/**
	 * @return the ID of the competition
	 */
	public int getCompetitionID() {
		return competitionID;
	}

	/**
	 * Assigns an ID to the competition
	 * 
	 * @param competitionID
	 *            the new ID of the competition
	 */
	public void setCompetitionID(int competitionID) {
		this.competitionID = competitionID;
	}

	/**
	 * @return the ID of the member
	 */
	public int getMemberID() {
		return memberID;
	}

	/**
	 * Assigns an ID to the member
	 * 
	 * @param memberID
	 *            the ID of the member
	 */
	public void setMemberID(int memberID) {
		this.memberID = memberID;
	}

	/**
	 * @return an array of Strings representing the times of the average
	 */
	public String[] getTimes() {
		return times;
	}

	/**
	 * Assigns the times of the average
	 * 
	 * @param times
	 *            the times of the average
	 */
	public void setTimes(String[] times) {
		this.times = times;
	}

	/**
	 * @return the calculated WCA average of the 5 times
	 */
	public double getAverage() {
		return Statistics.getAverageOf(5, times);
	}

	/**
	 * @return the calculated WCA average of the 5 times to 2 decimal places
	 */
	public double get2DPAverage() {
		return Double.valueOf(String.format("%.02f", getAverage()));
	}

	/**
	 * @return the 5 times of the average in a numerical representation
	 */
	public double[] getNumericTimeArray() {
		double[] timesToReturn = new double[5];

		for (int i = 0; i < 5; ++i)
			timesToReturn[i] = Solve.getFormattedStringToDouble(this.times[i]);

		return timesToReturn;
	}

	/**
	 * This method compares <b>this</b> average to another average. An average
	 * is better than another average if the 'average of 5' is faster, or the
	 * fastest time of <b>this</b> is faster than the fastest time of
	 * <b>other</b>. If the fastest times are the same, then the second fastest
	 * times are compared in the same way. If all times are the same, then
	 * <b>other</b> will be assumed to be the better average
	 * 
	 * @param other
	 *            the other MemberCompetition to which this MemberCompetition is
	 *            compared
	 * @return <b>true</b> if <b>this</b> is better than <b>other</b>; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isBetterThan(MemberCompetition other) {
		/*
		 * Stores the numerical representation of the average. For example, if
		 * the times of this average were 12.00, 13.00, 14.00, 15.00, 16.00,
		 * then 'thisAverage' would store 14.0
		 */
		double thisAverage = get2DPAverage();
		double otherAverage = other.get2DPAverage();

		// If one of the average is DNF, then set it to infinity
		if (thisAverage == -1)
			thisAverage = 1e10;
		if (otherAverage == -1)
			otherAverage = 1e10;

		if (thisAverage < otherAverage)
			return true;
		else if (otherAverage < thisAverage)
			return false;

		/*
		 * Stores the times of the average in their numerical representation.
		 * For example, if the times were {12.00, 1:10.50, 55.23, 2:00.95,
		 * 1:46.58} then the list would store {12.0, 70.5, 55.23, 120.95,
		 * 106.58}
		 */
		double[] list1 = this.getNumericTimeArray();
		double[] list2 = other.getNumericTimeArray();

		// If a time is DNF, then set it to infinity
		for (int i = 0; i < 5; ++i) {
			if (list1[i] == -1)
				list1[i] = 1e10;
			if (list2[i] == -1)
				list2[i] = 1e10;
		}

		Sorter.quickSort(list1);
		Sorter.quickSort(list2);

		/*
		 * i.e. if the fastest time of the first average is better than the
		 * second average, then return true, or vice-versa return false. If the
		 * fastest time of each is the same, the second times are compared etc.
		 * until they are different.
		 */
		for (int i = 0; i < 5; ++i) {
			if (list1[i] < list2[i])
				return true;
			else if (list2[i] < list1[i])
				return false;
		}

		/*
		 * If this point is reached, then the averages are exactly the same, so
		 * just return false.
		 */
		return false;
	}
}