/*
 * AdminFunctions.java
 * @author Matt Prestwich
 * @author James Taylor
 */

/**
 * A class containing all the functions available to admin accounts.
 */
package systems.team040.functions;

import java.sql.*;

public class AdminFunctions {
	/**
	 * Function employed to create user accounts and set their privileges.
	 * @throws SQLException
	 */
	public static void createAccount(String username, char[] password, int level) throws SQLException {
		String digest = Hasher.generateDigest(password);
		String query = "INSERT INTO UserAccount VALUES (?, ?, ?);";

		try(Connection con = SQLFunctions.connectToDatabase();
		    PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, digest);
            pstmt.setInt(3, level);
            pstmt.executeUpdate();
		}
	}
	
	/**
	 * Function employed to update user passwords.
	 * @
	 */
	public static void changePassword(String username, char[] newPass) throws SQLException {
	    String query = "UPDATE UserAccount SET password = ? WHERE username = ?;";
	    String digest = Hasher.generateDigest(newPass);

		try(Connection con = SQLFunctions.connectToDatabase();
		    PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, digest);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * Function employed to remove user accounts.
	 * @
	 */
	public static void removeUser(String selfUsername, String username) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();			
			if (selfUsername != username) {
				pstmt = con.prepareStatement(
						"DELETE FROM UserAccount WHERE username = ?");
				pstmt.setString(1, username);
				pstmt.executeUpdate();
			}
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}

	/**
	 * Function employed to add departments.
	 */
	public static void addDepartment(String deptCode, String deptName) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO Department VALUES (?, ?)");
			pstmt.setString(1, deptCode);
			pstmt.setString(2, deptName);
			pstmt.executeUpdate();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}	
	
	/**
	 * Function employed to remove departments.
	 */
	public static void removeDepartment(String deptCode) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM Department WHERE Dept = ?");
			pstmt.setString(1, deptCode);
			pstmt.executeUpdate();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to add degree courses.
	 */
	public static void addDegree(String degreeCode, String degreeName, int degreeLength) throws SQLException {
	    Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO Degree VALUES (?, ?)");
			pstmt.setString(1, degreeCode);
			pstmt.setString(2, degreeName);
			pstmt.executeUpdate();	
			
			
			for(int i=1; i<=degreeLength; i++){
				assignDegreeLevels(i + degreeCode, degreeCode, Integer.toString(i));
			}
			if (degreeCode.length() == 7) {
				assignDegreeLevels("Y" + degreeCode, degreeCode, "Y");
			}
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}	
	
	/**
	 * Function employed to remove degree courses.
	 */
	public static void removeDegree(String degreeCode) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
		    String query = "DELETE FROM Degree WHERE DegreeCode = ?";
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, degreeCode);
			pstmt.executeUpdate();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to assign a department or departments to a degree course.
	 */
	public static void assignDegreeDepartment(String DegreeCode, String Dept, int isPrimary) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			String query = "INSERT INTO DegreeDepartments VALUES (?, ?, ?)";
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, DegreeCode);
			pstmt.setString(2, Dept);
			pstmt.setInt(3, isPrimary);
			pstmt.executeUpdate();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to add modules.
	 */
	public static void addModule(
			String moduleID, String dept, int credits, String timePeriod, String moduleTitle
	) throws SQLException {
		String query = "INSERT INTO Module VALUES (?, ?, ?, ?, ?);";
		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, moduleID);
			pstmt.setString(2, dept);
			pstmt.setInt(3, credits);
			pstmt.setString(4, timePeriod); //Time period is the CHAR A/S/U/Y (Autumn,Spring,Summer,Year)
			pstmt.setString(5, moduleTitle);
			pstmt.executeUpdate();
			System.out.println("Module added successfully.");
		}
	}	
	
	/**
	 * Function employed to remove modules.
	 */
	public static void removeModule(String ModuleID) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM Module WHERE ModuleID = ?");
			pstmt.setString(1, ModuleID);
			pstmt.executeUpdate();
			System.out.println("Module removed successfully.");
		} finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to assign modules to their degree courses.
	 */
	public static void assignModuleDegree(String ModuleID, String DegreeLevel, int Core) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO DegreeModule VALUES (?, ?, ?)");
			pstmt.setString(1, ModuleID);
			pstmt.setString(2, DegreeLevel);
			pstmt.setInt(3, Core);
			pstmt.executeUpdate();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to assign degree courses their modules. 
	 */
	public static void assignDegreeLevels(String DegreeLevel, String DegreeCode, String Level) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
		    String query = "INSERT INTO DegreeLevel VALUES (?, ?, ?)";

			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, DegreeLevel);
			pstmt.setString(2, DegreeCode);
			pstmt.setString(3, Level);
			pstmt.executeUpdate();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
}
