package jCube;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kelsey McKenna
 */
public class SolveDatabaseConnection {
	/**
	 * The try/catch block is invoked if the table does not exist or the query
	 * is invalid
	 * 
	 * @param query
	 *            the SQLite query to be performed on 'solve' table
	 * @return an array of Solves representing the result of the specified query
	 * @throws SQLException
	 *             if the query is invalid
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 */
	public static SolveDBType[] executeQuery(String query) throws SQLException, ClassNotFoundException {
		SolveDBType[] solves = null;

		try {
			solves = executeSafeQuery(query);
		} catch (SQLException e) {
			System.err.print(e.getMessage());
			try {
				initTable();
				solves = executeSafeQuery(query);
			} catch (SQLException exc) {
				System.err.print(exc.getMessage());
			}

			throw new SQLException();
		}

		return solves;
	}

	/**
	 * This method will only be called once the table exists
	 * 
	 * @param query
	 *            the SQLite query to be performed on 'solve' table
	 * @return an array of Solves representing the result of the specified query
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist or the query is invalid
	 */
	private static SolveDBType[] executeSafeQuery(String query) throws ClassNotFoundException, SQLException { // table
																												// initialised
																												// etc.
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement statement;
		ResultSet rs;
		SolveDBType[] solves;
		int numRecords = 0;

		connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
		statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.

		rs = statement.executeQuery(query);

		while (rs.next())
			++numRecords;

		rs.close();
		rs = statement.executeQuery(query);

		solves = new SolveDBType[numRecords];

		for (int i = 0; i < numRecords; ++i) {
			rs.next();

			solves[i] = new SolveDBType(rs.getInt("solveID"), rs.getString("solveTime"), rs.getString("penalty"),
					rs.getString("comment"), rs.getString("scramble"), rs.getString("solution"),
					rs.getString("dateAdded"));
		}

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			System.err.print(e.getMessage());
		}

		return solves;
	}

	/**
	 * Executes the specified update on the 'solve' table
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
			System.err.println(e2.getMessage());
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
			statement.executeUpdate("DROP TABLE IF EXISTS solve");
			statement.executeUpdate("CREATE TABLE solve(" + "solveID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "solveTime TEXT," + "penalty TEXT," + "comment TEXT," + "scramble TEXT," + "solution TEXT,"
					+ "dateAdded TEXT" + ");");

		} catch (SQLException e) {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e2) {
				System.err.println(e.getMessage());
			}
		}
	}

	/*
	 * private static void resetIDs() throws ClassNotFoundException {
	 * Class.forName("org.sqlite.JDBC"); Connection connection = null; Statement
	 * statement = null; ResultSet rs;
	 * 
	 * try { connection =
	 * DriverManager.getConnection("jdbc:sqlite:res/cube.db"); statement =
	 * connection.createStatement(); statement.setQueryTimeout(30); // set
	 * timeout to 30 sec.
	 * 
	 * rs = statement.executeQuery("SELECT * FROM solve;");
	 * 
	 * statement = connection.createStatement();
	 * statement.executeUpdate("DROP TABLE IF EXISTS solveCopy");
	 * statement.executeUpdate( "CREATE TABLE solveCopy(" +
	 * "solveID INTEGER PRIMARY KEY AUTOINCREMENT," + "solveTime TEXT NOT NULL,"
	 * + "penalty TEXT," + "comment TEXT," + "scramble TEXT," + "solution TEXT,"
	 * + "dateAdded TEXT" + ");" );
	 * 
	 * 
	 * while (rs.next()) { String solveTime = rs.getString("solveTime"); String
	 * penalty = rs.getString("penalty"); String comment =
	 * rs.getString("comment"), scramble = rs.getString("scramble"); String
	 * solution = rs.getString("solution"), dateAdded =
	 * rs.getString("dateAdded");
	 * 
	 * statement.executeUpdate(String.format(
	 * "INSERT INTO solveCopy(solveTime, penalty, comment, scramble, solution, dateAdded) "
	 * + "VALUES (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");", solveTime,
	 * penalty, comment, scramble, solution, dateAdded )); }
	 * 
	 * statement.executeUpdate("DROP TABLE solve");
	 * statement.executeUpdate("ALTER TABLE solveCopy RENAME TO solve");
	 * 
	 * } catch (SQLException e) { System.err.println(e.getMessage()); try { if
	 * (connection != null) connection.close(); } catch (SQLException e2) { } }
	 * }
	 */

	/**
	 * @return the current time in the format yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentTimeInSQLFormat() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

}