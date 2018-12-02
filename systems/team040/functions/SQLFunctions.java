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
import java.util.function.Function;

public class SQLFunctions {
	public static Connection connectToDatabase() throws SQLException {
		DriverManager.setLoginTimeout(10);
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

	/**
	 * Takes a query to execute and a function to apply to the recordset (almost always
	 * in the form rs -> rs.getString(1)) and creates a list of T
	 */
	public static <T> ArrayList<T> queryToList(String query, CheckedFunction<ResultSet, T> func) throws SQLException {
	    ArrayList<T> list = new ArrayList<>();
	    try(Connection con = connectToDatabase();
			Statement stmt = con.createStatement()) {

	    	ResultSet rs = stmt.executeQuery(query);
	    	while(rs.next()) {
	    	    try {
					list.add(func.apply(rs));
				} catch(SQLException e) {
	    	    	// Something goes wrong we just skip the row
					System.err.println("Error retrieving row");
					System.err.println(e.getCause().getMessage());
				}
			}
		}

	    return list;
	}
}
