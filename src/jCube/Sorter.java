package jCube;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kelsey McKenna
 */
public class Sorter {

	/**
	 * Checks that the list is not null or empty then sorts the list with the
	 * fastest time first and the slowest time last.
	 * 
	 * @param list
	 *            the list to be sorted
	 * @param start
	 *            the index of the first element to be sorted
	 * @param end
	 *            the index of the last element to be sorted
	 */
	public static void sortByTime(SolveDBType[] list, int start, int end) {
		/*
		 * If the list is null, then it cannot be sorted, so return; If the
		 * list's length is less than 2, then it is already sorted, so return.
		 */
		if ((list == null) || (list.length <= 1))
			return;

		sBT(list, start, end);
	}

	/**
	 * Sorts a list with the fastest time first and the slowest time last.
	 * 
	 * @param list
	 *            the list to be sorted
	 * @param start
	 *            the index of the first element to be sorted
	 * @param end
	 *            the index of the last element to be sorted
	 */
	public static void sBT(SolveDBType[] list, int start, int end) {
		double pivot = Solve.getFormattedStringToDouble(list[((start + end) / 2)].getStringTime());

		// This points to the element on the left of the pivot
		int i = start;
		// This points to the element on the right of the pivot
		int j = end;

		// Stores the numeric representation of the time currently being
		// examined
		double current = 0;

		if (pivot == -1)
			pivot = 1e10;

		while (i <= j) {
			/*
			 * This converts the ith element to its numerical value, e.g.
			 * 1:10.50 becomes 70.5. If the element is -1, then set it to
			 * infinity (1e10), otherwise take its numerical value. This results
			 * in the list being sorted with -1s at the end rather than at the
			 * start because -1 represents DNF which represents infinity.
			 */
			while (((current = Solve.getFormattedStringToDouble(list[i].getStringTime())) == -1 ? 1e10 : current) < pivot)
				++i;

			while (((current = Solve.getFormattedStringToDouble(list[j].getStringTime())) == -1 ? 1e10 : current) > pivot)
				--j;

			if (i <= j) {
				swap(list, i, j);
				++i;
				--j;
			}
		}

		if (i < end)
			sBT(list, i, end);

		if (start < j)
			sBT(list, start, j);
	}

	/**
	 * Sorts the list with the earliest date first and the latest date last.
	 * 
	 * @param list
	 *            the list to be sorted
	 * @param start
	 *            the index of the first element to be sorted
	 * @param end
	 *            the index of the last element to be sorted
	 * @throws ParseException
	 *             if one of the dates cannot be parsed, i.e. the format is
	 *             wrong
	 */
	public static void sortByDateAdded(SolveDBType[] list, int start, int end) throws ParseException {
		Date pivot = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(list[((start + end) / 2)].getDateAdded());
		int i = start, j = end;

		while (i <= j) {
			while (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(list[i].getDateAdded()).before(pivot))
				++i;

			while (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(list[j].getDateAdded()).after(pivot))
				--j;

			if (i <= j) {
				swap(list, i, j);
				++i;
				--j;
			}
		}

		if (i < end)
			sortByDateAdded(list, i, end);

		if (start < j)
			sortByDateAdded(list, start, j);
	}

	/**
	 * @param list
	 *            the list to be sorted
	 * @param start
	 *            the index of the first element to be sorted
	 * @param end
	 *            the index of the last element to be sorted
	 * @throws Exception
	 *             if one of the elements has not been instantiated etc.
	 */
	public static void sortBySolveID(SolveDBType[] list, int start, int end) throws Exception {
		int pivot = list[((start + end) / 2)].getID();
		int i = start, j = end;

		while (i <= j) {
			while (list[i].getID() < pivot)
				++i;

			while (list[j].getID() > pivot)
				--j;

			if (i <= j) {
				swap(list, i, j);
				++i;
				--j;
			}
		}

		if (i < end)
			sortBySolveID(list, i, end);

		if (start < j)
			sortBySolveID(list, start, j);
	}

	/**
	 * @param list
	 *            the list in which elements will be swapped
	 * @param i
	 *            the index of the first element to be swapped
	 * @param j
	 *            the index of the second element to be swapped
	 */
	private static void swap(SolveDBType[] list, int i, int j) {
		SolveDBType temp = list[i];
		list[i] = list[j];
		list[j] = temp;
	}

	/**
	 * Reverses the order of the specified array, <br>
	 * e.g. {1, 7, 4} would be come {4, 7, 1}
	 * 
	 * @param list
	 *            the array to be reversed
	 */
	public static void reverseArray(SolveDBType[] list) {
		for (int i = 0; i < list.length / 2; ++i)
			swap(list, i, list.length - 1 - i);
	}

	/**
	 * Checks that the list is not null or empty then sorts the list with the
	 * best average first and the worst average last.
	 * 
	 * @param list
	 *            the list to be sorted
	 * @param start
	 *            the index of the first element to be sorted
	 * @param end
	 *            the index of the last element to be sorted
	 */
	public static void sortByAverageThenTime(MemberCompetition[] list, int start, int end) {
		if ((list == null) || (list.length <= 1))
			return;

		sBA(list, start, end);
	}

	/**
	 * Sorts the list with the best average first and the worst average last.
	 * 
	 * @param list
	 *            the list to be sorted
	 * @param start
	 *            the index of the first element to be sorted
	 * @param end
	 *            the index of the last element to be sorted
	 */
	public static void sBA(MemberCompetition[] list, int start, int end) {
		MemberCompetition pivot = list[((start + end) / 2)];
		int i = start, j = end;

		while (i <= j) {
			while (list[i].isBetterThan(pivot))
				++i;

			while (pivot.isBetterThan(list[j]))
				--j;

			if (i <= j) {
				swap(list, i, j);
				++i;
				--j;
			}
		}

		if (i < end)
			sBA(list, i, end);

		if (start < j)
			sBA(list, start, j);
	}

	/**
	 * @param list
	 *            the list in which the elements will be swapped
	 * @param i
	 *            the index of the first element to be swapped
	 * @param j
	 *            the index of the second element to be swapped
	 */
	private static void swap(MemberCompetition[] list, int i, int j) {
		MemberCompetition temp = list[i];
		list[i] = list[j];
		list[j] = temp;
	}

	/**
	 * Sorts the specified list into ascending order. The argument is checked to
	 * see if it is null or has fewer than 2 elements.
	 * 
	 * @param list
	 *            the list to sort
	 */
	public static void quickSort(double[] list) {
		/*
		 * If the list is null, then it cannot be sorted, so return; If the
		 * list's length is less than 2, then it is already sorted, so return.
		 */
		if ((list == null) || (list.length <= 1))
			return;

		qSort(list, 0, list.length - 1);
	}

	/**
	 * Sorts the specified list in to ascending order.
	 * 
	 * @param list
	 *            the list to be sorted
	 * @param start
	 *            the index of the first element to be sorted
	 * @param end
	 *            the index of the last element to be sorted
	 */
	private static void qSort(double[] list, int start, int end) {
		double pivot = list[(start + end) / 2];
		// This points to the element on the left of the pivot
		int i = start;
		// This points to the element on the right of the pivot
		int j = end;

		while (i <= j) {
			while (list[i] < pivot)
				++i;

			while (list[j] > pivot)
				--j;

			if (i <= j) {
				swap(list, i, j);
				++i;
				--j;
			}
		}

		if (i < end)
			qSort(list, i, end);

		if (start < j)
			qSort(list, start, j);
	}

	/**
	 * Swaps the elements at the specified indices in the specified list
	 * 
	 * @param list
	 *            the list in which elements will be swapped
	 * @param i
	 *            the index of the first element to be swapped
	 * @param j
	 *            the index of the last element to be swapped
	 */
	private static void swap(double[] list, int i, int j) {
		double temp = list[i];
		list[i] = list[j];
		list[j] = temp;
	}

}
