package jCube;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @author Kelsey McKenna
 */
public class TextFile {

	/**
	 * A constant used for readability and to prevent external argument errors
	 * that can occur when string parameters are used.
	 */
	public static final int READ = 0;
	/**
	 * A constant used for readability and to prevent external argument errors
	 * that can occur when string parameters are used.
	 */
	public static final int WRITE = 1;
	/**
	 * A constant used for readability and to prevent external argument errors
	 * that can occur when string parameters are used.
	 */
	public static final int WRITE_APPEND = 2;
	/**
	 * This stores the file path of the text file.
	 */
	private String filePath = null;
	/**
	 * This is used with brO to read the data in a file.
	 */
	private FileReader frO = null;
	/**
	 * This is used with frO to read the data in a file.
	 */
	private BufferedReader brO = null;
	/**
	 * This is used with pwO to read the data in a file.
	 */
	private FileWriter fwO = null;
	/**
	 * This is used with fwO to read the data in a file.
	 */
	private PrintWriter pwO = null;

	/**
	 * @param filePath
	 */
	/**
	 * Sets the file path of the text file
	 * 
	 * @param filePath
	 *            the file path of the file to be accessed
	 */
	public void setFilePath(String filePath) {
		close();
		this.filePath = filePath;
	}

	/**
	 * Closes the buffers accessing the file
	 */
	public void close() {
		try {
			if (frO != null) {
				frO.close();
				brO.close();
				frO = null;
				brO = null;
			}

			if (fwO != null) {
				fwO.close();
				pwO.close();
				fwO = null;
				pwO = null;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Changes the I/O method to the specified I/O method, it can be either
	 * read, write, or writing and appending.
	 * 
	 * @param io
	 *            the IO method
	 * @throws Exception
	 *             if the file cannot be found, opened etc.
	 */
	public void setIO(int io) throws Exception {
		close();

		switch (io) {
		case (READ):
			frO = new FileReader(filePath);
			brO = new BufferedReader(frO);
			break;

		case (WRITE):
			fwO = new FileWriter(filePath, false);
			pwO = new PrintWriter(fwO);
			break;

		case (WRITE_APPEND):
			fwO = new FileWriter(filePath, true);
			pwO = new PrintWriter(fwO);
			break;
		}
	}

	/**
	 * @return the number of lines in the text file
	 * @throws Exception
	 *             if the file cannot be accessed properly.
	 */
	public int getNumLines() throws Exception {
		return TextFile.getNumLines(this.filePath);
	}

	/**
	 * @param filePath
	 *            the path of the file whose number of lines will be counted.
	 * @return the number of lines in the specified file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public static int getNumLines(String filePath) throws Exception {
		FileReader fr2O = null;
		BufferedReader br2O = null;
		int numLines = 0;

		fr2O = new FileReader(filePath);
		br2O = new BufferedReader(fr2O);

		while (br2O.readLine() != null)
			numLines++;

		fr2O.close();
		br2O.close();

		return numLines;
	}

	/**
	 * @return the next letter read from the file.
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public String readLetter() throws Exception {
		String data = null;
		int charCode;

		if (frO != null) {
			if ((charCode = brO.read()) != -1)
				data = Character.toString((char) charCode);
		}

		return data;
	}

	/**
	 * @return the next line read in the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public String readLine() throws Exception {
		String data = null;

		if ((frO != null) && (brO != null)) {
			data = brO.readLine();
		}

		return data;
	}

	/**
	 * @return an array of strings containing the data of each line in the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public String[] readAllLines() throws Exception {
		int numLines = getNumLines();
		String[] lines = new String[numLines];

		for (int i = 0; i < numLines; ++i) {
			lines[i] = this.readLine();
		}

		return lines;
	}

	/**
	 * @param data
	 *            writes the specified data to the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public void writeLine(String data) throws Exception {
		writeLineGeneral(data);
	}

	/**
	 * @param data
	 *            writes the specified data to the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public void writeLine(char data) throws Exception {
		writeLineGeneral(Character.toString(data));
	}

	/**
	 * @param data
	 *            writes the specified data to the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public void writeLine(double data) throws Exception {
		writeLineGeneral(Double.toString(data));
	}

	/**
	 * @param data
	 *            writes the specified data to the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public void writeLine(long data) throws Exception {
		writeLineGeneral(Long.toString(data));
	}

	/**
	 * @param data
	 *            writes the specified data to the file and adds a new line
	 *            character.
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	private void writeLineGeneral(String data) throws Exception {
		pwO.printf("%s" + "%n", data);
	}

	/**
	 * @param data
	 *            writes the specified data to the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public void write(String data) throws Exception {
		writeGeneral(data);
	}

	/**
	 * @param data
	 *            writes the specified data to the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public void write(char data) throws Exception {
		writeGeneral(Character.toString(data));
	}

	/**
	 * @param data
	 *            writes the specified data to the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public void write(double data) throws Exception {
		writeGeneral(Double.toString(data));
	}

	/**
	 * @param data
	 *            writes the specified data to the file
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	public void write(long data) throws Exception {
		writeGeneral(Long.toString(data));
	}

	/**
	 * @param data
	 *            writes the specified data to the file.
	 * @throws Exception
	 *             if the file cannot be accessed properly
	 */
	private void writeGeneral(String data) throws Exception {
		pwO.printf("%s", data);
	}

}