/*
 * SQLFunctions.java
 * @author Matt Prestwich
 */

/**
 * A class containing miscellaneous SQL functions.
 */

package systemsProject;

import java.sql.*;

public class SQLFunctions {
	
	public static Connection connectToDatabase() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://stusql.dcs.shef.ac.uk/team040", "team040", "c7a84239");
	}
	
	public static Boolean checkPrivilege(Connection con, int credID, int permission) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement("SELECT * FROM UserAccount WHERE id = ?");
		pstmt.setInt(1, credID);
		ResultSet cred = pstmt.executeQuery();

		while (cred.next()) {
			if (cred.getInt("accountType") == permission){
				cred.close();
				pstmt.close();
				return true;
			}
		}
		cred.close();
		pstmt.close();
		return false;
	}

	public static void defaultError(SQLException ex) {
		ex.printStackTrace();
	}
	
	public static void closeAll(Connection con, Statement stmt, PreparedStatement pstmt) throws SQLException {
		if (con != null)
			con.close();
		if (stmt != null)
			stmt.close();
		if (pstmt != null)
			pstmt.close();
	}
	
}
