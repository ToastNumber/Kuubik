package jCube;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Kelsey McKenna
 */
public class MemberCompetitionDatabaseConnection {
	/**
	 * The try/catch block is invoked if the table does not exist or the query
	 * is invalid
	 * 
	 * @param query
	 *            the SQLite query to be performed on 'memberCompetition' table
	 * @return an array of MemberCompetitions representing the result of the
	 *         specified query
	 * @throws SQLException
	 *             if the query is invalid
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 */
	public static MemberCompetition[] executeQuery(String query) throws SQLException, ClassNotFoundException {
		MemberCompetition[] memberCompetitions = null;

		try {
			memberCompetitions = executeSafeQuery(query);
		} catch (SQLException e) {
			initTable();
			memberCompetitions = executeSafeQuery(query);
		}

		return memberCompetitions;
	}

	/**
	 * @param query
	 *            the SQLite query to be performed on 'memberCompetition' table
	 * @return an array of MemberCompetitions representing the result of the
	 *         specified query
	 * @throws ClassNotFoundException
	 *             if SQLite classes are missing
	 * @throws SQLException
	 *             if the table does not exist or the query is invalid
	 */
	private static MemberCompetition[] executeSafeQuery(String query) throws ClassNotFoundException, SQLException { // table
																													// initialised
																													// etc.
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement statement;
		ResultSet rs;
		MemberCompetition[] memberCompetitions;
		int numRecords = 0;

		connection = DriverManager.getConnection("jdbc:sqlite:res/cube.db");
		statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.

		rs = statement.executeQuery(query);

		while (rs.next())
			++numRecords;

		rs.close();
		rs = statement.executeQuery(query);

		memberCompetitions = new MemberCompetition[numRecords];

		for (int i = 0; i < numRecords; ++i) {
			rs.next();
			memberCompetitions[i] = new MemberCompetition(rs.getInt("competitionID"), rs.getInt("memberID"),
					rs.getString("time1"), rs.getString("time2"), rs.getString("time3"), rs.getString("time4"),
					rs.getString("time5"));
		}

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
		}

		return memberCompetitions;
	}

	/**
	 * Executes the specified update on the 'memberCompetition' table
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

			statement.executeUpdate("DROP TABLE IF EXISTS memberCompetition");
			statement.executeUpdate("CREATE TABLE memberCompetition(" + "competitionID INTEGER, "
					+ "memberID INTEGER, " + "time1 TEXT, " + "time2 TEXT, " + "time3 TEXT, " + "time4 TEXT, "
					+ "time5 TEXT, " + "FOREIGN KEY (competitionID) REFERENCES competition(competitionID), "
					+ "FOREIGN KEY (memberID) REFERENCES member(memberID)" + ");");

		} catch (SQLException e) {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e2) {
			}
		}
	}

}