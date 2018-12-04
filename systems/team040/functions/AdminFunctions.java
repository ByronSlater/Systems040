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
	public static void removeUser(String username) throws SQLException {
	    String query = "DELETE FROM UserAccount WHERE username = ?;";

		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt = con.prepareStatement(query)){

            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
	}

	/**
	 * Function employed to add departments.
	 */
	public static void addDepartment(String deptCode, String deptName) throws SQLException {
		String query = "INSERT INTO Department VALUES (?, ?);";

		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, deptCode);
			pstmt.setString(2, deptName);
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * Function employed to remove departments.
	 */
	public static void removeDepartment(String deptCode) throws SQLException {
		String query = "DELETE FROM Department WHERE Dept = ?;";

		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, deptCode);
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * Function employed to add degree courses.
	 */
	public static void addDegree(String degreeCode, String degreeName, int degreeLength) throws SQLException {
		String degreeQuery = "INSERT INTO Degree VALUES (?, ?);";
		String degreeLevelsQuery = "INSERT INTO DegreeLevel VALUES (?, ?, ?,?);";


		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt1 = con.prepareStatement(degreeQuery);
			PreparedStatement pstmt2 = con.prepareStatement(degreeLevelsQuery)) {

		    con.setAutoCommit(false);
			pstmt1.setString(1, degreeCode);
			pstmt1.setString(2, degreeName);
			pstmt1.executeUpdate();

			for(int i = 1; i <= degreeLength; i++){
				pstmt2.setString(1, i + degreeCode);
				pstmt2.setString(2, degreeCode);
				pstmt2.setString(3, Integer.toString(i));
				if (i == (degreeLength - 1))
					pstmt2.setBoolean(4, true);
				else
					pstmt2.setBoolean(4, false);
				pstmt2.executeUpdate();
			}

			if (degreeCode.length() == 7) {
			    pstmt2.setString(1, "Y" + degreeCode);
			    pstmt2.setString(2, degreeCode);
			    pstmt2.setString(3, "Y");
			    pstmt2.executeUpdate();
			}

			con.commit();
		}
	}

	/**
	 * Function employed to remove degree courses.
	 */
	public static void removeDegree(String degreeCode) throws SQLException {
		String query = "DELETE FROM Degree WHERE DegreeCode = ?";

		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, degreeCode);
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * Function employed to assign a department or departments to a degree course.
	 */
	public static void assignDegreeDepartment(String DegreeCode, String Dept, int isPrimary) throws SQLException {
		String query = "INSERT INTO DegreeDepartments VALUES (?, ?, ?)";

		try(Connection con = SQLFunctions.connectToDatabase();
            PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, DegreeCode);
			pstmt.setString(2, Dept);
			pstmt.setInt(3, isPrimary);
			pstmt.executeUpdate();
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
		String query = "DELETE FROM Module WHERE ModuleID = ?";
		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, ModuleID);
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * Function employed to assign modules to their degree courses.
	 */
	public static void assignModuleDegree(String ModuleID, String DegreeLevel, int Core) throws SQLException {
		String query = "INSERT INTO DegreeModule VALUES (?, ?, ?);";

		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, ModuleID);
			pstmt.setString(2, DegreeLevel);
			pstmt.setInt(3, Core);
			pstmt.executeUpdate();
		}
	}
}

