/*
 * AdminFunctions.java
 * @author Matt Prestwich
 */

/**
 * A class containing all the functions available to admin accounts.
 */

package systemsProject;

import java.sql.*;

public class AdminFunctions {
	// static class variables
	private static Connection con = null;
	private static Statement stmt = null;
	private static PreparedStatement pstmt = null;
	
	/**
	 * Function employed to create user accounts and set their privileges.
	 * @throws SQLException
	 */
	public static void createAccount(String username, String password, int level) throws SQLException {			
		try {
			con = SQLFunctions.connectToDatabase();			
			if (level < 4) {
				pstmt = con.prepareStatement(
						"INSERT INTO UserAccount VALUES (?, ?, ?)");
				pstmt.setString(1, username);
				pstmt.setString(2, password);
				pstmt.setInt(3, level);
				pstmt.executeUpdate();
				System.out.println("Added successfully.");
			} else
				System.out.println("Invalid permission set.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("User already exists.");
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to update user passwords.
	 * @throws SQLException 
	 */
	public static void changePassword(String username, String newPass) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(
					"UPDATE UserAccount SET password = ? WHERE username = ?");
			pstmt.setString(1, newPass);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			System.out.println("Updated successfully.");
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to remove user accounts.
	 * @throws SQLException 
	 */
	public static void removeUser(String selfUsername, String username) throws SQLException {
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
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}

	/**
	 * Function employed to add departments.
	 */
	public static void addDepartment(String deptCode, String deptName) throws SQLException {
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
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}	
	
	/**
	 * Function employed to remove departments.
	 */
	public static void removeDepartment(String deptCode) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM Department WHERE Dept = ?");
			pstmt.setString(1, deptCode);
			pstmt.executeUpdate();
			System.out.println("Department removed successfully.");
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to add degree courses.
	 */
	public static void addDegree(String degreeCode, String degreeName) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO Degree VALUES (?, ?)");
			pstmt.setString(1, degreeCode);
			pstmt.setString(2, degreeName);
			pstmt.executeUpdate();
			System.out.println("Degree course added successfully.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("Degree course already exists.");
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}	
	
	/**
	 * Function employed to remove degree courses.
	 */
	public static void removeDegree(String degreeCode) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM Degree WHERE DegreeCode = ?");
			pstmt.setString(1, degreeCode);
			pstmt.executeUpdate();
			System.out.println("Degree course removed successfully.");
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to assign a department or departments to a degree course.
	 */
	public static void assignDegreeDepartment(String DegreeCode, String Dept, int isPrimary) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO DegreeDepartments VALUES (?, ?, ?)");
			pstmt.setString(1, DegreeCode);
			pstmt.setString(2, Dept);
			pstmt.setInt(3, isPrimary);
			pstmt.executeUpdate();
			System.out.println("Department assigned successfully.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("Degree already assigned to this department.");
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to add modules.
	 */
	public static void addModule(String ModuleID, String Dept, int Credits, String TimePeriod, String ModuleTitle) throws SQLException {
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
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}	
	
	/**
	 * Function employed to remove modules.
	 */
	public static void removeModule(String ModuleID) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"DELETE FROM Module WHERE ModuleID = ?");
			pstmt.setString(1, ModuleID);
			pstmt.executeUpdate();
			System.out.println("Module removed successfully.");
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	// Not yet tested functions below this point.
	/**
	 * Function employed to assign modules to their degree courses.
	 */
	public static void assignModuleDegree(String ModuleID, String DegreeLevel, String Core) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO DegreeModule VALUES (?, ?, ?)");
			pstmt.setString(1, ModuleID);
			pstmt.setString(2, DegreeLevel);
			pstmt.setString(3, Core);
			pstmt.executeUpdate();
			System.out.println("Module assigned successfully.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("Module already assigned to this degree.");
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to assign degree courses their modules. 
	 */
	public static void assignDegreeModules(String DegreeLevel, String DegreeCode, String Level) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement(
					"INSERT INTO DegreeLevel VALUES (?, ?, ?)");
			pstmt.setString(1, DegreeLevel);
			pstmt.setString(2, DegreeCode);
			pstmt.setString(3, Level);
			pstmt.executeUpdate();
			System.out.println("Modules assigned successfully.");
		}
		catch (SQLIntegrityConstraintViolationException iCV) {
			System.out.println("Modules already assigned to this degree.");
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
}