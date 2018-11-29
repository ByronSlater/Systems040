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
	 * Function employed to create user accounts and set their privileges. Updates the user account database with a new user/password/account type.
	 * @throws SQLException
	 */
	public static void createAccount(String username, char[] password, int level) {
		String digest = Hasher.generateDigest(password);
		String query = "INSERT INTO UserAccount VALUES (?, ?, ?);";

		try(Connection con = SQLFunctions.connectToDatabase();
		    PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, digest);
            pstmt.setInt(3, level);
            pstmt.executeUpdate();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
	}
	
	/**
	 * Function employed to update user passwords. Finds a user ID and changes the
	 * @
	 */
	public static void changePassword(String username, char[] newPass) {
	    String digest = Hasher.generateDigest(newPass);
		try {
			Connection con = SQLFunctions.connectToDatabase();
		    PreparedStatement pstmt = con.prepareStatement(
		    		"UPDATE UserAccount SET password = ? WHERE username = ?;"); 
			pstmt.setString(1, digest);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
	}
	
	/**
	 * Function employed to remove user accounts.
	 * @
	 */
	public static void removeUser(String selfUsername, String username) {
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
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}

	/**
	 * Function employed to add departments.
	 */
	public static void addDepartment(String deptCode, String deptName) {
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
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}	
	
	/**
	 * Function employed to remove departments.
	 */
	public static void removeDepartment(String deptCode) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM Department WHERE Dept = ?");
			pstmt.setString(1, deptCode);
			pstmt.executeUpdate();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to add degree courses.
	 */
	public static void addDegree(String degreeCode, String degreeName, int degreeLength) {
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
				assignDegreeLevels(Integer.toString(i) + degreeCode, degreeCode, Integer.toString(i));
			}
			if (degreeCode.length() == 7) {
				assignDegreeLevels("Y" + degreeCode, degreeCode, "Y");
			}
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}	
	
	/**
	 * Function employed to remove degree courses.
	 */
	public static void removeDegree(String degreeCode) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
		    String query = "DELETE FROM Degree WHERE DegreeCode = ?";
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, degreeCode);
			pstmt.executeUpdate();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to assign a department or departments to a degree course.
	 */
	public static void assignDegreeDepartment(String DegreeCode, String Dept, int isPrimary) {
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
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to add modules.
	 */
	public static void addModule(String ModuleID, String Dept, int Credits, String TimePeriod, String ModuleTitle) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO Module VALUES (?, ?, ?, ?, ?)");
			pstmt.setString(1, ModuleID);
			pstmt.setString(2, Dept);
			pstmt.setInt(3, Credits);
			pstmt.setString(4, TimePeriod); //Time period is the CHAR A/S/U/Y (Autumn,Spring,Summer,Year)
			pstmt.setString(5, ModuleTitle);
			pstmt.executeUpdate();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}	
	
	/**
	 * Function employed to fully delete modules and all data attached such as student grades.
	 */
	public static void fullDeleteModule(String ModuleID) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM Module WHERE ModuleID = ?");
			pstmt.setString(1, ModuleID);
			pstmt.executeUpdate();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	
	/**
	 * Function employed to soft delete a module by removing all current links to degrees so the module cannot be added to any future students.
	 */
	public static void removeModule(String ModuleID) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM DegreeModule WHERE ModuleID = ?");
			pstmt.setString(1, ModuleID);
			pstmt.executeUpdate();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to assign modules to their degree courses.
	 */
	public static void assignModuleDegree(String ModuleID, String DegreeLevel, int Core) {
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
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to assign degree courses their modules. 
	 */
	public static void assignDegreeLevels(String DegreeLevel, String DegreeCode, String Level) {
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
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
}
