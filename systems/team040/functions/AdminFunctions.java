/*
 * AdminFunctions.java
 * @author Matt Prestwich
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
	public static void createAccount(String username, char[] password, int level) {
		String digest = Hasher.generateDigest(password);
		String query = "INSERT INTO UserAccount VALUES (?, ?, ?);";

		try(Connection con = SQLFunctions.connectToDatabase();
		    PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, digest);
            pstmt.setInt(3, level);
            pstmt.executeUpdate();
            System.out.println("Added successfully.");
		}
		catch (SQLIntegrityConstraintViolationException ex) {
			System.out.println("User already exists.");
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
	}
	
	/**
	 * Function employed to update user passwords.
	 * @
	 */
	public static void changePassword(String username, char[] newPass) {
	    String query = "UPDATE UserAccount SET password = ? WHERE username = ?;";
	    String digest = Hasher.generateDigest(newPass);

		try(Connection con = SQLFunctions.connectToDatabase();
		    PreparedStatement pstmt = con.prepareStatement(query)) {

			pstmt.setString(1, digest);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			System.out.println("Updated successfully.");
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
				System.out.println("Removed successfully.");
			} else
				System.out.println("Cannot remove own account.");
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
			System.out.println("Department added successfully.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("Department already exists.");
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
			System.out.println("Department removed successfully.");
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
	public static void addDegree(String degreeCode, String degreeName) {
	    Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO Degree VALUES (?, ?)");
			pstmt.setString(1, degreeCode);
			pstmt.setString(2, degreeName);
			pstmt.executeUpdate();
			System.out.println("Degree course added successfully.");
		}
		catch (SQLIntegrityConstraintViolationException ex) {
			System.out.println("Degree course already exists.");
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
			System.out.println("Degree course removed successfully.");
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
			System.out.println("Department assigned successfully.");
		}
		catch (SQLIntegrityConstraintViolationException ex) {
			System.out.println("Degree already assigned to this department.");
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
			pstmt.setString(4, TimePeriod);
			pstmt.setString(5, ModuleTitle);
			pstmt.executeUpdate();
			System.out.println("Module added successfully.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("Module already exists.");
			SQLFunctions.closeAll(con, pstmt);
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}	
	
	/**
	 * Function employed to remove modules.
	 */
	public static void removeModule(String ModuleID) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM Module WHERE ModuleID = ?");
			pstmt.setString(1, ModuleID);
			pstmt.executeUpdate();
			System.out.println("Module removed successfully.");
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
			System.out.println("Module assigned successfully.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("Module already assigned to this degree.");
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
	public static void assignDegreeModules(String DegreeLevel, String DegreeCode, String Level) {
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
			System.out.println("Modules assigned successfully.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("Modules already assigned to this degree.");
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
}