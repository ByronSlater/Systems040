/*
 * RegistrarFunctions.java
 * @author Matt Prestwich
 * @author Byron Slater
 * @author James Taylor
 */

/**
 * A class containing all the functions available to registrar accounts.
 */

package systemsProject;

import java.sql.*;
import java.util.ArrayList;

public class RegistrarFunctions {
	/**
	 * Function employed to add student details.
	 */
	public static void addStudent(
			String StudentID, String Title, String Forenames,
			String Surname, String EmailAddress, String Username
	) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

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
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to remove student details.
     * TODO : needs to delete information from users table too
	 */
	public static void removeStudent(String StudentID) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
		    String query = "DELETE FROM STUDENT WHERE StudentId = ?;";
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, StudentID);
			pstmt.executeUpdate();
			System.out.println("Removed successfully.");
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to initially register students for study.
	 */
	public static void registerStudent(String PeriodID, String DegreeLevel, String StudentID, String StartDate) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
		    String query = "INSERT INTO StudentPeriod VALUES (?);";
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, PeriodID + StudentID);
			pstmt.setString(2, PeriodID);
			pstmt.setString(3, DegreeLevel);
			pstmt.setString(4, StudentID);
			pstmt.setString(5, StartDate);
			pstmt.executeUpdate();
			System.out.println("Removed successfully.");
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	

	/**
	 * Function employed to list optional modules for a student. An empty list indicates full credits.
	 * @throws SQLException
	 */
	public static ArrayList<String> listOptionalModule(String StudentID) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ArrayList<String> optionalModules = new ArrayList<String>();
		try {
			con = SQLFunctions.connectToDatabase();	
			pstmt = con.prepareStatement(
					"SELECT * MAX(PeriodID) FROM StudentPeriod WHERE StudentID = ?");
			pstmt.setString(1, StudentID);
			ResultSet period = pstmt.executeQuery();
			period.next();
			String StudentPeriod = period.getString(1);
			String DegreeLevel = period.getString(3);
			period.close();
			int creditTotal = verifyCreditTotal(StudentPeriod);
			int remainingCredits = 0;
			
			if (DegreeLevel.charAt(4) == 'U') 
				remainingCredits = 120 - creditTotal;
			else if (DegreeLevel.charAt(4) == 'P') 
				remainingCredits = 180 - creditTotal;	
			if (remainingCredits > 0) {
				pstmt = con.prepareStatement(
						"SELECT * FROM DegreeModule WHERE DegreeLevel = ? AND CORE = 0");
				pstmt.setString(1, DegreeLevel);
				ResultSet modules = pstmt.executeQuery();
				while (modules.next()) {
					if (modules.getInt(2) > remainingCredits)
						optionalModules.add(modules.getString(1));
				}
				modules.close();
			}
            return optionalModules;
		}
		catch (SQLException ex) {
			SQLFunctions.defaultError(ex, con, stmt, pstmt);
		}
		finally {
			SQLFunctions.closeAll(con, stmt, pstmt);
		}
	}
	
	/**
	 * Function employed to return the total number of credits for a students current degree course.
	 */
	public static int verifyCreditTotal(String StudentPeriod) throws SQLException {
		int creditTotal = 0;
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(
					"SELECT DegreeLevel FROM StudentPeriod WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			ResultSet student = pstmt.executeQuery();
			student.next();
			String DegreeLevel = student.getString(1);
			student.close();

			pstmt = con.prepareStatement(
					"SELECT * FROM DegreeModule WHERE DegreeLevel = ?");
			pstmt.setString(1, DegreeLevel);
			ResultSet modules = pstmt.executeQuery();
			
			while (modules.next()) {
				pstmt = con.prepareStatement(
						"SELECT ModuleID,Credits FROM Module WHERE ModuleID = ?");
				pstmt.setString(1, modules.getString(1));
				ResultSet module = pstmt.executeQuery();
				module.next();
				creditTotal += module.getInt(2);
				module.close();
			}
			modules.close();
			return creditTotal;

		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
}
