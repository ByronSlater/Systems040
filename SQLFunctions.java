/*
 * SQLFunctions.java
 * @author Matt Prestwich
 * @author Byron Slater
 */

/**
 * A class containing miscellaneous SQL functions.
 */

package systemsProject;

import java.lang.AutoCloseable;
import java.sql.*;

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
	    		if(c != null) c.close();
	    	} catch (Exception e) {
				System.out.println("Errored trying to close stuff, bad sign");
	    		e.printStackTrace();
			}
		}
	}	
}