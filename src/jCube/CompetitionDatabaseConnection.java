package jCube;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Kelsey McKenna
 */
public class CompetitionDatabaseConnection {

	/**
	 * The try/catch block is invoked if the table does not exist or the query
	 * is invalid
	 * 
	 * @param query
	 *            the SQLite query to be performed on 'competition' table
	 * @return an array of Competitions representing the result of the specified
	 *         query
	 * @throws SQLException
	 *             if the query is invalid
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 */
	public static Competition[] executeQuery(String query) throws SQLException, ClassNotFoundException {
		Competition[] competitions = null;

		try {
			competitions = executeSafeQuery(query);
		} catch (SQLException e) {
			initTable();
			competitions = executeSafeQuery(query);
		}

		return competitions;
	}

	/**
	 * @param query
	 *            the SQLite query to be performed on 'competition' table
	 * @return an array of Competitions representing the result of the specified
	 *         query
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist or the query is invalid
	 */
	private static Competition[] executeSafeQuery(String query) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement statement;
		ResultSet rs;
		Competition[] competitions;
		int numRecords = 0;

		connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
		statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.

		rs = statement.executeQuery(query);

		while (rs.next())
			++numRecords;

		rs.close();
		rs = statement.executeQuery(query);

		competitions = new Competition[numRecords];

		for (int i = 0; i < numRecords; ++i) {
			rs.next();
			competitions[i] = new Competition(rs.getInt("competitionID"), rs.getString("competitionDate"));
		}

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
		}

		return competitions;
	}

	/**
	 * Executes the specified update on the 'competition' table
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
			statement.executeUpdate("DROP TABLE IF EXISTS competition");
			statement.executeUpdate("CREATE TABLE competition(" + "competitionID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "competitionDate TEXT" + ");");

		} catch (SQLException e) {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e2) {
			}
		}
	}

}