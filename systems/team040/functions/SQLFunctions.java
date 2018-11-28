/*
 * SQLFunctions.java
 * @author Matt Prestwich
 */

/**
 * A class containing miscellaneous SQL functions.
 */

package systems.team040.functions;

import java.lang.AutoCloseable;
import java.sql.*;
import java.util.ArrayList;

public class SQLFunctions {
	
	public static Connection connectToDatabase() throws SQLException {
		return DriverManager.getConnection(
				"jdbc:mysql://stusql.dcs.shef.ac.uk/team040",
				"team040", "c7a84239"
		);
	}
	

	/**
	 * Just tries to call close on a param list of closeables handed to it
	 */
	public static void closeAll(AutoCloseable... closeables) {
	    for(AutoCloseable c : closeables) {
	    	try {
	    		if(c != null) { c.close(); }
	    	} catch (Exception e) {
				System.out.println("Errored trying to close stuff, bad sign");
	    		e.printStackTrace();
			}
		}
	}

	public static ArrayList<Object> columnToList(String table, String column) {
		String query = "SELECT ? FROM ?;";
		ArrayList<Object> ret = new ArrayList<>();
		try(Connection conn = connectToDatabase();
		    PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, column);
			pstmt.setString(2, table);

			try(ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()) {
					ret.add(rs.getObject(1));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ret;
	}
}
