package systems.team040.functions;

import java.sql.*;

public class DBUpdates {
	
	public static void main (String[] args) throws SQLException {

		try(Connection con = DriverManager.getConnection("jdbc:mysql://stusql.dcs.shef.ac.uk/team040", "team040", "c7a84239");
			Statement stmt = con.createStatement()) {

			//stmt.executeUpdate("DELETE FROM Student WHERE StudentID='1'");
			//stmt.executeUpdate("INSERT INTO UserAccount VALUES (null, 'pass', '0')");
			//stmt.executeUpdate("INSERT INTO UserAccount VALUES (null, 'test', '1')");
			ResultSet res = stmt.executeQuery("SELECT * FROM UserAccount");
			while (res.next()) {
				//System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4) + " " + res.getString(5));
				System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3));
			}
			res.close();
			//stmt.executeUpdate("CREATE TABLE UserAccount (id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY, password VARCHAR(50), accountType INTEGER)");
			//stmt.executeUpdate("DROP TABLE UserAccount");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
