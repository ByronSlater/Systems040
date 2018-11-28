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
			Connection con, String ModuleID) throws SQLException{
		ResultSet modules = null;
		int creditValue = 0;
		
		PreparedStatement pstmt = con.prepareStatement(
				"SELECT * FROM Module WHERE ModuleID = ?");
		pstmt.setString(1, ModuleID);
		modules = pstmt.executeQuery();				
		
		while (modules.next()) {
			creditValue = modules.getInt(3);
		}
		modules.close();
		pstmt.close();
		return creditValue;
	}
	
	/**
	 * Function employed to retrieve the current degree level.
	 * @throws SQLException 
	 */
	private static int getDegreeLevel(
			Connection con, String StudentPeriod) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement(
	    		"SELECT * FROM StudentPeriod WHERE StudentPeriod = ?");
	    pstmt.setString(1, StudentPeriod);
	    ResultSet rs = pstmt.executeQuery();
	    pstmt = con.prepareStatement(
	    		"SELECT * FROM DegreeLevel WHERE DegreeLevel = ?");
	    pstmt.setString(1, rs.getString(3));
	    ResultSet rs1 = pstmt.executeQuery();
	    int level = Integer.parseInt(rs1.getString(3));
	    rs.close();
	    rs1.close();
	    pstmt.close();
	    return level;
	}
	
	/**
	 * Function employed to calculate students' weighted mean grades.
	 * @throws SQLException
	 */
	public static double calculateWeightedMeanGrade(String StudentPeriod) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet grades = null;
	    int gradesSum = 0;
	    double weightedMean = 0;
	    int level = 0;
	    
		try {
			con = SQLFunctions.connectToDatabase();
			
			level = getDegreeLevel(con, StudentPeriod);
			
			pstmt = con.prepareStatement(
					"SELECT * FROM Grades WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			grades = pstmt.executeQuery();
			
			while (grades.next()) {
				int creditValue = getCreditValue(con, grades.getString(1));;
				if (grades.getObject(4) instanceof Integer) {
					if (level >= 4) {
						if (grades.getInt(4) >= 50)
							gradesSum += (50 * creditValue);
						else
							gradesSum += (grades.getInt(4) * creditValue);
					} else {
						if (grades.getInt(4) >= 40)
							gradesSum += (40 * creditValue);
						else
							gradesSum += (grades.getInt(4) * creditValue);
					}
				} else {
					gradesSum += (grades.getInt(3) * creditValue); 
				}
			}
			grades.close();
			
			if (level >= 4)
				weightedMean = gradesSum / 18000;
			else
				weightedMean = gradesSum / 12000;
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
	 */
	public static boolean calculateIfPassed(String StudentPeriod) throws SQLException {
		Connection con = null;
	    PreparedStatement pstmt = null;
	    Boolean allModulesPassed = true;
	    Boolean weightedMeanPass = false;
	    Boolean concededPass = false;
	    double weightedMean = 0;
	    
		try {
			con = SQLFunctions.connectToDatabase();
			
			int level = getDegreeLevel(con, StudentPeriod);
			
			pstmt = con.prepareStatement(
					"SELECT * FROM StudentPeriod WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			ResultSet student = pstmt.executeQuery();
			pstmt.close();
			
			pstmt = con.prepareStatement(
					"SELECT * FROM Grades WHERE StudentPeriod = ?");
			pstmt.setString(1, student.getString(1));
			ResultSet modules = pstmt.executeQuery();
			student.close();
			
			int creditsFailed = 0;
			while (modules.next()) {
				if (level >= 4) {
					if (modules.getInt(3) < (0.9 * 50))
						allModulesPassed = false;
					else if (modules.getInt(3) < 50)
						creditsFailed += getCreditValue(con, modules.getString(1));
				} else {
					if (modules.getInt(3) < (0.9 * 40))
						allModulesPassed = false;
					else if (modules.getInt(3) < 40)
						creditsFailed += getCreditValue(con, modules.getString(1));
				}
			}
			modules.close();
			
			if (level >= 4 && creditsFailed <= 15)
				concededPass = true;
			else if (creditsFailed <= 20)
				concededPass = true;
			
			weightedMean = calculateWeightedMeanGrade(StudentPeriod);
			if (level <= 3 && weightedMean >= 40)
				weightedMeanPass = true;
			else if (level >= 4 && weightedMean >= 50)
				weightedMeanPass = true;
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
		if ((allModulesPassed && weightedMeanPass) || (weightedMeanPass && concededPass))
			return true;
		else
			return false;
	}
}
