/*
 * RegistrarFunctions.java
 * @author Matt Prestwich
 */

/**
 * A class containing all the functions available to registrar accounts.
 */

package systemsProject;

import java.sql.*;

public class RegistrarFunctions {
	// static class variables
		private static Connection con = null;
		private static Statement stmt = null;
		private static PreparedStatement pstmt = null;

	/**
	 * Function employed to add student details.
	 * @throws SQLException
	 */
	public static void addStudent(
			String StudentID, String Title, String Forenames, String Surname, String EmailAddress, String Username) throws SQLException {			
		try {
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(
					"INSERT INTO Student VALUES (?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, StudentID);
			pstmt.setString(2, Title);
			pstmt.setString(3, Forenames);
			pstmt.setString(4, Surname);
			pstmt.setString(5, EmailAddress);
			pstmt.setString(6, Username);
			pstmt.executeUpdate();
			System.out.println("Details added successfully.");
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to remove student details.
	 * @throws SQLException 
	 */
	public static void removeStudent(String StudentID) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(
					"DELETE FROM Student WHERE StudentID = ?");
			pstmt.setString(1, StudentID);
			pstmt.executeUpdate();
			System.out.println("Removed successfully.");
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to initially register students for study.
	 * @throws SQLException
	 */
	public static void registerStudent(String PeriodID, String DegreeLevel, String StudentID, String StartDate) throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(
					"INSERT INTO StudentPeriod VALUES (?)");
			pstmt.setString(1, PeriodID + StudentID);
			pstmt.setString(2, PeriodID);
			pstmt.setString(3, DegreeLevel);
			pstmt.setString(4, StudentID);
			pstmt.setString(5, StartDate);
			pstmt.executeUpdate();
			System.out.println("Removed successfully.");
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/*/** WIP
	 * Function employed to add optional modules on behalf of students.
	 * @throws SQLException
	 *
	public static void addOptionalModule() throws SQLException {
		try {
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(
					"SELECT * FROM DegreeModule WHERE DegreeLevel = ? AND Core = N");
			pstmt.setString(1, PeriodID + StudentID);
			pstmt.executeUpdate();
			System.out.println("Module added successfully.");
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}*/
	
	/**
	 * Function employed to verify credit totals.
	 * @throws SQLException 
	 */
	public static void verifyCreditTotal(String StudentID) throws SQLException {
		int creditTotal = 0;
		
		try {
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(
					"SELECT * FROM StudentPeriod WHERE StudentID = ?");
			pstmt.setString(1, StudentID);
			ResultSet student = pstmt.executeQuery();
			student.next();
			String DegreeLevel = student.getString(3);
			student.close();

			pstmt = con.prepareStatement(
					"SELECT * FROM DegreeModule WHERE DegreeLevel = ? AND CORE = 1");
			pstmt.setString(1, DegreeLevel);
			ResultSet modules = pstmt.executeQuery();
			
			while (modules.next()) {
				pstmt = con.prepareStatement(
						"SELECT * FROM Module WHERE ModuleID = ?");
				pstmt.setString(1, modules.getString(1));
				ResultSet module = pstmt.executeQuery();
				module.next();
				creditTotal += module.getInt(3);
				module.close();
			}
			modules.close();
			
			if (DegreeLevel.charAt(4) == 'U') {
				if (creditTotal == 120)
					System.out.println("Student fully registered.");
				else
					System.out.println("Student must take 120 credits of modules.");
			} else if (DegreeLevel.charAt(4) == 'P') {
				if (creditTotal == 180)
					System.out.println("Student fully registered");
				else
					System.out.println("Student must take 180 credits of modules.");
			}
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
}
