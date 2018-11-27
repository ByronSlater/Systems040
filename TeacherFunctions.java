/*
 * TeacherFunctions.java
 * @author Matt Prestwich
 * @author Byron Slater
 */

/**
 * A class containing all the functions available to teacher accounts.
 */

package systemsProject;

import java.sql.*;

public class TeacherFunctions {
	/**
	 * Function employed to add or update student grades.
	 * @throws SQLException
	 */
	public static void addGrade(String ModuleID, String StudentPeriod, int Grade) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			
			pstmt = con.prepareStatement(
					"INSERT INTO Grades VALUES (?, ?, ?, null)");
			pstmt.setString(1, StudentPeriod);
			pstmt.setString(2, ModuleID);
			pstmt.setInt(3, Grade);
			pstmt.executeUpdate();
			pstmt.close();
			System.out.println("Grade added successfully.");
		}
		catch (SQLIntegrityConstraintViolationException ex) {
			pstmt = con.prepareStatement(
					"UPDATE Grades SET Grade = ? WHERE StudentPeriod = ? AND ModuleID = ?");
			pstmt.setInt(1, Grade);
			pstmt.setString(2, StudentPeriod);
			pstmt.setString(3, ModuleID);
			pstmt.executeUpdate();
			System.out.println("Grade added successfully.");
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	// UNTESTED BELOW THIS POINT DUE TO TOO MANY CONNECTIONS - REQUIRES TESTING BEFORE IMPLEMENTATION!
	/**
	 * Function employed to add or update student resit grades.
	 * @throws SQLException
	 */
	public static void addResitGrade(String ModuleID, String StudentPeriod, int ResitGrade) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();
			
			pstmt = con.prepareStatement(
					"UPDATE Grades SET Resit = ? WHERE ModuleID = ? AND StudentPeriod = ?");
			pstmt.setInt(1, ResitGrade);
			pstmt.setString(2, ModuleID);
			pstmt.setString(3, StudentPeriod);
			pstmt.executeUpdate();
			System.out.println("Resit grade added successfully.");
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to retrieve module credit values.
	 * @throws SQLException 
	 */
	private static int getCreditValue(
			Connection con, PreparedStatement pstmt, String ModuleID) throws SQLException{
		ResultSet modules = null;
		int creditValue = 0;
		
		pstmt = con.prepareStatement(
				"SELECT * FROM Module WHERE ModuleID = ?");
		pstmt.setString(1, ModuleID);
		modules = pstmt.executeQuery();				
		
		while (modules.next()) {
			creditValue = modules.getInt(3);
		}
		return creditValue;
	}
	
	/**
	 * Function employed to calculate students' weighted mean grades.
	 * @throws SQLException
	 */
	public static double calculateWeightedMeanGrade(String StudentPeriod) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet grades = null;
	    double gradesSum = 0;
	    int totalModules = 0;
	    double weightedMean = 0;
	    int creditValue = 0;
	    int level = Integer.parseInt(StudentPeriod.substring(0,1));
	    
		try {
			con = SQLFunctions.connectToDatabase();
			
			pstmt = con.prepareStatement(
					"SELECT * FROM Grades WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			grades = pstmt.executeQuery();
			
			while (grades.next()) {
				if (level >= 4) {
					if (grades.getObject(4) instanceof Integer) {
							creditValue = getCreditValue(con, pstmt, grades.getString(1));
							gradesSum += (50 * (creditValue / 160));
					}
				}
			}
			grades.close();
			//weightedMean = 
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
		return weightedMean;
	}
	
	/**
	 * Function employed to calculate whether a student has passed their period of study.
	 * @throws SQLException
	 *
	public static boolean calculateIfPassed(String StudentPeriod) throws SQLException {
		Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet student = null;
	    ResultSet modules = null;
	    Boolean allModulesPassed = false;
	    Boolean weightedMeanPass = false;
	    Boolean concededPass = false;
	    String currentLevel = null;
	    double weightedMean = 0;
	    int level = 0;
	    
		try {
			con = SQLFunctions.connectToDatabase();
			
			pstmt = con.prepareStatement(
					"SELECT * FROM StudentPeriod WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			student = pstmt.executeQuery();
			
			// Retrieves student information.
			while (student.next()) {
				pstmt = con.prepareStatement(
						"SELECT * FROM DegreeLevel WHERE DegreeCode = ?");
				pstmt.setString(1, student.getString(3).substring(1));
				currentLevel = student.getString(2).substring(0,1);
				modules = pstmt.executeQuery();
			}
			student.close();
			
			while (modules.next()) {
				if (modules.getString(1).substring(0, 1) == "P")
					continue;
				else
					level += 1;
			}
			modules.close();
			
			pstmt = con.prepareStatement(
					"SELECT * FROM GRADES WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			
			weightedMean = calculateWeightedMeanGrade(StudentPeriod);
			if (level == 3 && weightedMean >= 40)
				weightedMeanPass = true;
			else if (level == 4 && weightedMean >= 50)
				weightedMeanPass = true;
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
		if ((allModulesPassed && weightedMeanPass) || concededPass)
			return true;
		else
			return false;
	}*/
}
