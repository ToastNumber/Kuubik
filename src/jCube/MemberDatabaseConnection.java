package jCube;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Kelsey McKenna
 */
public class MemberDatabaseConnection {

	/**
	 * The try/catch block is invoked if the table does not exist or the query
	 * is invalid
	 * 
	 * @param query
	 *            the SQLite query to be performed on 'member' table
	 * @return an array of Members representing the result of the specified
	 *         query
	 * @throws SQLException
	 *             if the query is invalid
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 */
	public static Member[] executeQuery(String query) throws SQLException, ClassNotFoundException {
		Member[] members = null;

		try {
			members = executeSafeQuery(query);
		} catch (SQLException e) {
			initTable();
			members = executeSafeQuery(query);
		}

		return members;
	}

	/**
	 * This method will only be called once the table exists
	 * 
	 * @param query
	 *            the SQLite query to be performed on 'Member' table
	 * @return an array of Members representing the result of the specified
	 *         query
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist or the query is invalid
	 */
	private static Member[] executeSafeQuery(String query) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement statement;
		ResultSet rs;
		Member[] members;
		int numRecords = 0;

		connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
		statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.

		rs = statement.executeQuery(query);

		while (rs.next())
			++numRecords;

		rs.close();
		rs = statement.executeQuery(query);

		members = new Member[numRecords];

		for (int i = 0; i < numRecords; ++i) {
			rs.next();
			members[i] = new Member(rs.getInt("memberID"), rs.getString("forenames"), rs.getString("surname"),
					rs.getString("gender"), rs.getString("dateOfBirth"), rs.getString("email"),
					rs.getString("formClass"));
		}

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
		}

		return members;
	}

	/**
	 * Executes the specified update on the 'member' table
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

			statement.executeUpdate("DROP TABLE IF EXISTS member");
			statement.executeUpdate("CREATE TABLE member(" + "memberID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "forenames TEXT," + "surname TEXT," + "gender TEXT," + "dateOfBirth TEXT," + "email TEXT, "
					+ "formClass TEXT" + ");");

		} catch (SQLException e) {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e2) {
			}
		}
	}

	/**
	 * Resets the IDs in the 'member' table so that they are continuous, e.g. if
	 * the IDs were 1, 2, 5, 6, 7, 12, 13 then they would be reset to 1, 2, 3,
	 * 4, 5, 6, 7
	 * 
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist etc.
	 */
	public static void resetIDs() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement statement = null;
		ResultSet rs;

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			rs = statement.executeQuery("SELECT * FROM member;");

			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE memberCopy(" + "memberID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "forenames TEXT," + "surname TEXT," + "gender TEXT," + "dateOfBirth TEXT," + "email TEXT, "
					+ "formClass TEXT" + ");");

			while (rs.next()) {
				statement.executeUpdate(String.format(
						"INSERT INTO memberCopy(forenames, surname, gender, dateOfBirth, email, formClass) "
								+ "VALUES (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");",
						rs.getString("forenames"), rs.getString("surname"), rs.getString("gender"),
						rs.getString("dateOfBirth"), rs.getString("email"), rs.getString("formClass")));
			}

			statement.executeUpdate("DROP TABLE member");
			statement.executeUpdate("ALTER TABLE memberCopy RENAME TO member");

			/*
			 * Set up other tables etc.
			 */
		} catch (SQLException e) {
			if (connection != null)
				connection.close();

			throw new SQLException();
		}
	}

}