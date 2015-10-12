package jCube;

import java.awt.Color;
import java.util.LinkedList;

/**
 * @author Kelsey McKenna
 */
public class LinearSearch {

	/**
	 * Suppresses default constructor, ensuring non-instantiability.
	 */
	private LinearSearch() {
	}

	/**
	 * Searches an array of Colors for a specified Color
	 * 
	 * @param list
	 *            the list to be searched
	 * @param element
	 *            the element to be found
	 * @return the index of <b>element</b> in <b>list</b>. <br>
	 *         If the index is 2, then <b>-1</b> is returned. <br>
	 *         If the element cannot be found then <b>-2</b> is returned.
	 */
	public static int linearSearchCornerOrientation(Color[] list, Color element) {
		for (int i = 0; i < list.length; ++i) {
			if (list[i].equals(element))
				return (i == 2) ? -1 : i;
		}
		return -2; // Not found
	}

	/**
	 * Searches an array of Colors for a specified Color
	 * 
	 * @param list
	 *            the list to be searched
	 * @param element
	 *            the element to be found
	 * @return the index of <b>element</b> in <b>list</b> if the element can be
	 *         found; <br>
	 *         <b>-1</b> otherwise
	 */
	public static int linearSearch(Color[] list, Color element) {
		for (int i = 0; i < list.length; ++i) {
			if (list[i].equals(element))
				return i;
		}

		return -1; // Not found
	}

	/**
	 * Searches an array of Strings for a specified String
	 * 
	 * @param list
	 *            the list to be searched
	 * @param element
	 *            the element to be found
	 * @return the index of <b>element</b> in <b>list</b> if the element can be
	 *         found; <br>
	 *         <b>-1</b> otherwise
	 */
	public static int linearSearch(String[] list, String element) {
		for (int i = 0; i < list.length; ++i) {
			if (list[i].equals(element))
				return i;
		}

		return -1;// Not found
	}

	/**
	 * Searches a linked list of strings for a specified element
	 * 
	 * @param list
	 *            the list to be searched
	 * @param element
	 *            the element to be found
	 * @return the index of <b>element</b> in <b>list</b>
	 */
	public static int linearSearch(LinkedList<String> list, String element) {
		for (int i = 0; i < list.size(); ++i) {
			if (list.get(i).equals(element))
				return i;
		}

		return -1; // Not found
	}

	/**
	 * Searches an array of Strings for a String that starts with the specified
	 * element, e.g. "Hello" starts with "He"
	 * 
	 * @param list
	 *            the list to be searched
	 * @param element
	 *            the element to be found
	 * @return the index of <b>element</b> in <b>list</b>
	 */
	public static int linearSearchStartsWith(String[] list, String element) {
		for (int i = 0; i < list.length; ++i) {
			if (list[i].startsWith(element))
				return i;
		}

		return -1; // Not found
	}

	/**
	 * Searches an array of Integers to see if it contains a specified element
	 * (index is not important)
	 * 
	 * @param list
	 *            the list to be searched
	 * @param element
	 *            to be found
	 * @return <b>true</b> if the list contains the specified element; <br>
	 *         <b>false</b> otherwise
	 */
	public static boolean linearSearchContains(Integer[] list, Integer element) {
		for (int i = 0; i < list.length; ++i) {
			if (list[i].equals(element))
				return true;
		}

		return false;
	}

}
