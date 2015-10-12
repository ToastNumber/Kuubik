package jCube;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Kelsey McKenna
 */
public class AlgorithmDatabaseConnection {

	/**
	 * Stores the SQL update-strings required to insert the preset algorithms
	 * into the 'algorithm' table
	 */
	public static final String[] PRESET_ALGORITHM_UPDATES = {
			String.format("INSERT INTO algorithm(moveSequence, comment) " + "VALUES(\"%s\", \"%s\")", "R U R' U'",
					"Fast Move (uneditable)"),
			String.format("INSERT INTO algorithm(moveSequence, comment) " + "VALUES(\"%s\", \"%s\")", "R' F R F'",
					"Sledgehammer (uneditable)"),
			String.format("INSERT INTO algorithm(moveSequence, comment) " + "VALUES(\"%s\", \"%s\")",
					"R U R' U R U2 R'", "Sune (uneditable)"),

	};

	/**
	 * The try/catch block is invoked if the table does not exist or the query
	 * is invalid
	 * 
	 * @param query
	 *            the SQLite query to be performed on 'algorithm' table
	 * @return an array of Algorithms representing the result of the specified
	 *         query
	 * @throws SQLException
	 *             if the query is invalid
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 */
	public static Algorithm[] executeQuery(String query) throws SQLException, ClassNotFoundException {
		Algorithm[] algorithms = null;

		try {
			algorithms = executeSafeQuery(query);
		} catch (SQLException e) {
			initTable();
			algorithms = executeSafeQuery(query);
		}

		return algorithms;
	}

	/**
	 * This method will only be called once the table exists
	 * 
	 * @param query
	 *            the SQLite query to be performed on 'algorithm' table
	 * @return an array of Algorithms representing the result of the specified
	 *         query
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist or the query is invalid
	 */
	private static Algorithm[] executeSafeQuery(String query) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement statement;
		ResultSet rs;
		Algorithm[] algorithms;
		int numRecords = 0;

		connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
		statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.

		rs = statement.executeQuery(query);

		while (rs.next())
			++numRecords;

		rs.close();
		rs = statement.executeQuery(query);

		algorithms = new Algorithm[numRecords];

		for (int i = 0; i < numRecords; ++i) {
			rs.next();
			algorithms[i] = new Algorithm(rs.getInt("algorithmID"), rs.getString("moveSequence"), ""
					+ rs.getString("comment"));
		}

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
		}

		return algorithms;
	}

	/**
	 * Executes the specified update on the 'algorithm' table
	 * 
	 * @param update
	 *            the update to be performed on the table
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the query is invalid
	 */
	public static void executeUpdate(String update) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement statement = null;

		connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
		statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.

		statement.executeUpdate(update);

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e2) {
		}
	}

	/**
	 * Initialises the table in the database. This method will be called if the
	 * executeQuery(...) method cannot find the table in the database.
	 * 
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 */
	private static void initTable() throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement statement = null;

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			statement.executeUpdate("DROP TABLE IF EXISTS algorithm");
			statement.executeUpdate("CREATE TABLE algorithm(" + "algorithmID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "moveSequence TEXT," + "comment TEXT" + ");");
			for (int i = 0; i < PRESET_ALGORITHM_UPDATES.length; ++i)
				statement.execute(PRESET_ALGORITHM_UPDATES[i]);

		} catch (SQLException e) {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e2) {
			}
		}
	}

	/**
	 * Resets the IDs in the 'algorithm' table so that they are continuous, e.g.
	 * if the IDs were 1, 2, 5, 6, 7, 12, 13 then they would be reset to 1, 2,
	 * 3, 4, 5, 6, 7
	 * 
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist etc.
	 */
	public static void resetIDs() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		// This is used to establish a connection with the database.
		Connection connection = null;
		// This is used to perform updates and queries on the database
		Statement statement = null;
		// This stores the records returned from a query.
		ResultSet rs;

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			rs = statement.executeQuery("SELECT * FROM algorithm;");

			statement = connection.createStatement();
			statement.executeUpdate("DROP TABLE IF EXISTS algorithmCopy");
			statement.executeUpdate("CREATE TABLE algorithmCopy(" + "algorithmID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "moveSequence TEXT," + "comment TEXT" + ");");

			while (rs.next()) {
				String moveSequence = rs.getString("moveSequence"), comment = rs.getString("comment");
				statement.executeUpdate(String.format(
						"INSERT INTO algorithmCopy(moveSequence, comment) VALUES (\"%s\", \"%s\");", moveSequence,
						comment));
			}

			statement.executeUpdate("DROP TABLE algorithm");
			statement.executeUpdate("ALTER TABLE algorithmCopy RENAME TO algorithm");
		} catch (SQLException e) {
			System.out.println(e.getMessage());

			if (connection != null)
				connection.close();

			throw new SQLException();
		}
	}

}