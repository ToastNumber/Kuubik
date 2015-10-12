package jCube;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Kelsey McKenna
 */
public class Statistics {

	/**
	 * @author Kelsey McKenna
	 */
	private static class Average {
		/**
		 * This stores the name of the average, e.g. ‘Average of 5’
		 */
		String name;
		/**
		 * This stores the calculated numerical interpretation of the average.
		 */
		double average;
		/**
		 * This stores the number of times in the average, e.g. average of 5
		 * will have a length of 5.
		 */
		int length;
		/**
		 * This stores the times of the average in a formatted fashion; the
		 * fastest and slowest times are surrounded by brackets.
		 */
		String formattedTimes;
	}

	/**
	 * This variable shares the same memory location as the list in the main
	 * window. This means that statistics of recent times can be found.
	 */
	private LinkedList<Solve> times;
	/**
	 * Stores the calculated averages.
	 */
	private static Average[] averages;

	/**
	 * Constructor - assigns the times from which averages are calculated
	 * 
	 * @param times
	 *            the times to be used
	 */
	public Statistics(LinkedList<Solve> times) {
		this.times = times;
		averages = new Average[8];

		for (int i = 0; i < averages.length; ++i)
			averages[i] = new Average();
	}

	/**
	 * Sets the times to be used for averages
	 * 
	 * @param times
	 *            the times to be used for averages
	 */
	public void setTimes(LinkedList<Solve> times) {
		this.times = times;
	}

	/**
	 * Returns an average of the specified size. The times used are the most
	 * recent ones.
	 * 
	 * @param sizeOfAverage
	 *            the size of the average
	 * @return the average of the specified size
	 */
	public double getRecentAverageOf(int sizeOfAverage) {
		return getAverageOf(sizeOfAverage, times);
	}

	/**
	 * @return the overall mean of all times, ignoring DNFs
	 */
	public double getOverallMean() {
		return getOverallMean(times);
	}

	/**
	 * @return a string representing all calculated statistics in a formatted
	 *         fashion
	 */
	public String getRecentFormattedStandardStatistics() {
		return getFormattedStandardStatisticsString(times);
	}

	/**
	 * @return a array containing all calculated statistics in a formatted
	 *         fashion
	 */
	public String[] getRecentFormattedStatisticsArray() {
		return getFormattedStatisticsArray(times);
	}

	/**
	 * @param sizeOfAverage
	 *            must be 5 or over
	 * @param times
	 *            the times from which the average is calculated
	 * @return the average of the specified size.
	 */
	public static double getAverageOf(int sizeOfAverage, LinkedList<Solve> times) {
		// Stores the number of DNF times in the past 'sizeOfAverage' times.
		int numDNF = getNumDNF(sizeOfAverage, times);
		int size = times.size();

		if ((times.size() < sizeOfAverage) || (sizeOfAverage < 5) || (numDNF > 1))
			return -1;

		// Stores the raw numerical representation of the times.
		double[] raw = new double[sizeOfAverage];
		// Accumulates the sum of the times to be used in the mean, i.e. the
		// middle (x - 2) times.
		double sum = 0;
		// Stores the number of times to be used in the mean so that the mean
		// can be found.
		double factor = /* (x < 5) ? x : */sizeOfAverage - 2;
		// Stores the index of the first element to be used in the mean.
		int start;
		// Stores the index of the last element to be used in the mean.
		int end;

		start = 1;
		end = sizeOfAverage - 1;

		/*
		 * This allows the number of times specified to be processed.
		 */
		for (int i = 0; i < sizeOfAverage; ++i)
			raw[i] = times.get(size - sizeOfAverage + i).getNumericTime();

		/*
		 * This gets all DNFs (-1s) at the start and sorts other times.
		 */
		Sorter.quickSort(raw);

		/*
		 * If there is a DNF time, then the first element will represent the
		 * slowest time and the second element the fastest, so the last three
		 * elements need to be processed.
		 */
		if (numDNF > 0) {
			++start;
			++end;
		}

		for (int i = start; i < end; ++i) {
			sum += raw[i];
		}

		return (sum / factor);
	}

	/**
	 * @param sizeOfAverage
	 *            the size of the average (must be greater than or equal to 5)
	 * @param stringTimes
	 *            the times from which the average will be calculated
	 * @return the average of the specified times
	 */
	public static double getAverageOf(int sizeOfAverage, String[] stringTimes) {
		LinkedList<Solve> tempTimes = new LinkedList<>();

		for (int i = 0; i < stringTimes.length; ++i)
			tempTimes.add(new Solve(stringTimes[i], "", ""));

		return getAverageOf(sizeOfAverage, tempTimes);
	}

	/**
	 * @param times
	 *            the times from which the overall mean will be calculated
	 * @return the overall mean of all specified times, ignoring DNFs
	 */
	public static double getOverallMean(LinkedList<Solve> times) {
		int size = times.size();
		int numDNF = getNumDNF(size, times);
		double result = 0, current;

		if ((size == 0) || (numDNF == size))
			return -1;

		for (int i = 0; i < size; ++i) {
			current = times.get(i).getNumericTime();
			if (current != -1)
				result += current;
		}

		return (result) / ((double) size - getNumDNF(size, times));
	}

	/**
	 * Returns a formatted average (i.e. all times are separated by commas and
	 * the fastest and slowest times are surrounded with brackets) of the
	 * specified size as the string
	 * 
	 * @param sizeOfAverage
	 *            the size of the average to be returned
	 * @param times
	 *            the times from which the average is calculated
	 * @return a formatted string of the average in the form
	 *         "[average] [time_1], [time_2], ..., [time_n]"
	 */
	public static String getFormattedAverage(int sizeOfAverage, LinkedList<Solve> times) {
		int size = times.size();
		// Stores the times in order of their numerical value.
		double[] orderedTimes = new double[sizeOfAverage];
		// Stores a copy of the times in the average.
		double[] timesCopy = new double[sizeOfAverage];
		/*
		 * The first element indicates whether or not the slowest time has been
		 * found and enclosed in brackets; The second element indicates whether
		 * or no the fastest time has been found and enclosed in brackets.
		 */
		boolean[] found = { false, false };
		String formattedStatistics = "";

		if (size < sizeOfAverage)
			return "";

		for (int i = 0; i < sizeOfAverage; ++i)
			orderedTimes[i] = times.get(size - sizeOfAverage + i).getNumericTime();

		timesCopy = Arrays.copyOf(orderedTimes, orderedTimes.length);
		Sorter.quickSort(orderedTimes);
		sortByDNF(orderedTimes);

		for (int i = 0; i < timesCopy.length; ++i) {
			/*
			 * If the current time is the slowest time the average, and the
			 * slowest time has not already been identified, then put brackets
			 * around it.
			 */
			if ((!found[1]) && (timesCopy[i] == orderedTimes[orderedTimes.length - 1])) {
				found[1] = true;
				formattedStatistics += String.format("(%s)",
						(timesCopy[i] == -1) ? "DNF" : Solve.getSecondsToFormattedString(timesCopy[i]));
			}
			/*
			 * If the current time is the fastest time the average, and the
			 * fastest time has not already been identified, then put brackets
			 * around it.
			 */
			else if ((timesCopy[i] == orderedTimes[0]) && (!found[0])) {
				found[0] = true;
				formattedStatistics += String.format("(%s)",
						(timesCopy[i] == -1) ? "DNF" : Solve.getSecondsToFormattedString(timesCopy[i]));
			} else
				formattedStatistics += String.format("%s",
						(timesCopy[i] == -1) ? "DNF" : Solve.getSecondsToFormattedString(timesCopy[i]));

			formattedStatistics += ", ";
		}
		/*
		 * Remove the last comma
		 */
		if (formattedStatistics.contains(","))
			formattedStatistics = formattedStatistics.substring(0, formattedStatistics.lastIndexOf(","));

		return formattedStatistics;
	}

	/**
	 * Sorts the specified list so that '-1' elements are at the end
	 * 
	 * @param list
	 *            the list to be sorted
	 */
	private static void sortByDNF(double[] list) {
		// Stores the index of the element whose whose value will become list[j]
		int i = 0;
		// Stores the index of the element being examined.
		int j = 0;

		/*
		 * Pushes all non-DNF times to start of array.
		 */
		while ((i < list.length) && (j < list.length)) {
			if (list[j] != -1) {
				list[i] = list[j];
				++i;
			}

			++j;
		}

		/*
		 * All non-DNF times are before i, so set all elements after to -1
		 */
		for (; i < list.length; ++i) {
			list[i] = -1;
		}
	}

	/**
	 * @param sizeOfAverage
	 *            the size of the average to be calculated
	 * @param averageIndex
	 *            the index of the element in <b>averages</b> that contains the
	 *            properties for the average.
	 * @param times
	 *            the times from which the average will be calculated
	 * @return the best average of the specified size.
	 */
	public static double getBestAverageOf(int sizeOfAverage, int averageIndex, LinkedList<Solve> times) {
		if (sizeOfAverage < 1)
			return -1;

		LinkedList<Solve> currentTimes = new LinkedList<>();
		int size = times.size();
		double bestAverage = 1e10; // infinite
		double currentAverage;
		double bestAverageBestTime = 0;

		averages[averageIndex].formattedTimes = "";

		for (int i = 0; i <= size - sizeOfAverage; ++i) {
			currentTimes.clear();
			for (int j = i; j < i + sizeOfAverage; ++j) {
				currentTimes.add(times.get(j));
			}
			if ((currentAverage = getAverageOf(sizeOfAverage, currentTimes)) <= bestAverage) {
				if (currentAverage == bestAverage) {
					if (getFastestTimeInPrevious(sizeOfAverage, currentTimes) < bestAverageBestTime) {
						bestAverage = currentAverage;
						bestAverageBestTime = getFastestTimeInPrevious(sizeOfAverage, currentTimes);
						averages[averageIndex].formattedTimes = getFormattedAverage(sizeOfAverage, currentTimes);
					}
				} else {
					bestAverage = currentAverage;
					bestAverageBestTime = getFastestTimeInPrevious(sizeOfAverage, currentTimes);
					averages[averageIndex].formattedTimes = getFormattedAverage(sizeOfAverage, currentTimes);
				}
			}
		}

		return (bestAverage == 1e10) ? -1 : bestAverage;
	}

	/**
	 * @param times
	 *            the times from which the formatted statistics will be
	 *            generated.
	 * @return a string containing the all calculated statistics in a formatted
	 *         fashion.
	 */
	public static String getFormattedStandardStatisticsString(LinkedList<Solve> times) {
		String formattedStatistics = "";
		double overallMean = getOverallMean(times);
		int size = times.size();

		averages[0].name = "Current Average of 5";
		averages[1].name = "Current Average of 12";
		averages[2].name = "Current Average of 50";
		averages[3].name = "Current Average of 100";
		averages[4].name = "Best Average of 5";
		averages[5].name = "Best Average of 12";
		averages[6].name = "Best Average of 50";
		averages[7].name = "Best Average of 100";

		averages[0].length = 5;
		averages[1].length = 12;
		averages[2].length = 50;
		averages[3].length = 100;
		averages[4].length = 5;
		averages[5].length = 12;
		averages[6].length = 50;
		averages[7].length = 100;

		for (int i = 0; i < averages.length / 2; ++i) {
			averages[i].average = getAverageOf(averages[i].length, times);
			formattedStatistics += String.format("%s:   \t", averages[i].name);
			formattedStatistics += (averages[i].average == -1) ? "DNF" : String.format("%s\t%s", Solve
					.getSecondsToFormattedString(averages[i].average),
					(averages[i].length <= 12) ? getFormattedAverage(averages[i].length, times) : "");
			formattedStatistics += String.format("%n");
		}

		formattedStatistics += String.format("%n%nOverall Mean (%d/%d):\t", size - getNumDNF(size, times), size)
				+ ((overallMean == -1) ? "DNF" : String.format("%s", Solve.getSecondsToFormattedString(overallMean)));
		formattedStatistics += String.format("%n%n");

		for (int i = averages.length / 2; i < averages.length; ++i) {
			averages[i].average = getBestAverageOf(averages[i].length, i, times);
			formattedStatistics += String.format("%s:   \t", averages[i].name);
			formattedStatistics += (averages[i].average == -1) ? "DNF" : String.format("%s\t%s", Solve
					.getSecondsToFormattedString(averages[i].average),
					(averages[i].length <= 12) ? averages[i].formattedTimes : "");
			formattedStatistics += String.format("%n");
		}

		return formattedStatistics;
	}

	/**
	 * Returns each the formatted string for each average in an array
	 * 
	 * @param times
	 *            the times from which the statistics will be generated
	 * @return an array containing strings which represent the statistics in a
	 *         formatted fashion
	 */
	public static String[] getFormattedStatisticsArray(LinkedList<Solve> times) {
		String[] formattedStatistics = new String[(averages.length * 3) + 3];
		double overallMean = getOverallMean(times);
		int size = times.size(), fIndex = 0;

		averages[0].name = "Current Average of 5";
		averages[1].name = "Current Average of 12";
		averages[2].name = "Current Average of 50";
		averages[3].name = "Current Average of 100";
		averages[4].name = "Best Average of 5";
		averages[5].name = "Best Average of 12";
		averages[6].name = "Best Average of 50";
		averages[7].name = "Best Average of 100";

		averages[0].length = 5;
		averages[1].length = 12;
		averages[2].length = 50;
		averages[3].length = 100;
		averages[4].length = 5;
		averages[5].length = 12;
		averages[6].length = 50;
		averages[7].length = 100;

		for (int i = averages.length / 2; i < averages.length; ++i) {
			averages[i].average = getBestAverageOf(averages[i].length, i, times);
			formattedStatistics[fIndex++] = averages[i].name;
			formattedStatistics[fIndex++] = (averages[i].average == -1) ? "DNF" : Solve
					.getSecondsToFormattedString(averages[i].average);
			formattedStatistics[fIndex++] = averages[i].formattedTimes;
		}

		for (int i = 0; i < averages.length / 2; ++i) {
			averages[i].average = getAverageOf(averages[i].length, times);
			formattedStatistics[fIndex++] = averages[i].name;
			formattedStatistics[fIndex++] = (averages[i].average == -1) ? "DNF" : Solve
					.getSecondsToFormattedString(averages[i].average);
			formattedStatistics[fIndex++] = getFormattedAverage(averages[i].length, times);
		}

		formattedStatistics[fIndex++] = String.format("Overall Mean (%d/%d)", size - getNumDNF(size, times), size);
		formattedStatistics[fIndex++] = (overallMean == -1) ? "DNF" : Solve.getSecondsToFormattedString(overallMean);

		String formattedOverallAverage = "";
		double current;

		for (int i = 0; i < size; ++i) {
			current = times.get(i).getNumericTime();
			formattedOverallAverage += String.format("%s, ",
					(current == -1) ? "DNF" : Solve.getSecondsToFormattedString(current));
		}

		if (formattedOverallAverage.contains(","))
			formattedOverallAverage = formattedOverallAverage.substring(0, formattedOverallAverage.lastIndexOf(","));

		formattedStatistics[fIndex] = formattedOverallAverage;

		return formattedStatistics;
	}

	/**
	 * @param sizeOfAverage
	 *            the size of the average currently being calculated
	 * @param times
	 *            the times from which the number of DNFs will be determined
	 * @return the number of 'DNF'/-1 times in the past <b>sizeOfAverage</b>
	 *         times
	 */
	public static int getNumDNF(int sizeOfAverage, LinkedList<Solve> times) {
		int num = 0, size = times.size();
		if ((size == 0) || (size < sizeOfAverage))
			return 0;

		for (int i = 0; i < sizeOfAverage; ++i) {
			if (times.get(size - sizeOfAverage + i).getNumericTime() == -1)
				++num;
		}

		return num;
	}

	/*
	 * public double getFastestTimeInPrevious(int x) { return
	 * getFastestTimeInPrevious(x, times); } public double
	 * getSlowestTimeInPrevious(int x) { return getFastestTimeInPrevious(x,
	 * times); }
	 */

	/**
	 * Returns the fastest time in the previous <b>sizeOfAverage</b> times
	 * 
	 * @param sizeOfAverage
	 *            the size of the average being calculated
	 * @param times
	 *            the times from which the fastest time will be determined
	 * @return the fastest time in the previous <b>sizeOfAverage</b> times
	 */
	public static double getFastestTimeInPrevious(int sizeOfAverage, LinkedList<Solve> times) {
		if ((times.size() < sizeOfAverage) || (sizeOfAverage < 1))
			return -1;

		double best = times.getLast().getNumericTime();
		int start = times.size() - 1, end = times.size() - sizeOfAverage;
		double current;

		for (int i = start; i >= end; --i) {
			current = times.get(i).getNumericTime();

			best = Math.min(best, current);
		}

		return best;
	}

	/**
	 * Returns the slowest time in the previous <b>sizeOfAverage</b> times
	 * 
	 * @param sizeOfAverage
	 *            the size of the average being calculated
	 * @param times
	 *            the times from which the slowest time will be determined
	 * @return the slowest time in the previous <b>sizeOfAverage</b> times
	 */
	public static double getSlowestTimeInPrevious(int sizeOfAverage, LinkedList<Solve> times) {
		if ((times.size() < sizeOfAverage) || (sizeOfAverage < 1))
			return -1;

		double slowest = times.getLast().getNumericTime();
		int start = times.size() - 1, end = times.size() - sizeOfAverage;
		double current;

		for (int i = start; i >= end; --i) {
			current = times.get(i).getNumericTime();

			slowest = Math.max(slowest, current);
		}

		return slowest;
	}

}
