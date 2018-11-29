/*
 * TeacherFunctions.java
 * @author Matt Prestwich
 * @author Byron Slater
 * @author James Taylor
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
	 * Function employed to calculate students' weighted mean grades.
	 * @throws SQLException
	 */
	public static double calculateWeightedMeanGrade(String StudentPeriod) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet grades = null;
	    double gradesSum = 0;
	    double weightedMean = 0;
	    
		try {
			con = SQLFunctions.connectToDatabase();
			
			pstmt = con.prepareStatement(
		    		"SELECT DegreeLevel.Level,DegreeCode FROM DegreeLevel JOIN StudentPeriod ON DegreeLevel.DegreeLevel = StudentPeriod.DegreeLevel WHERE StudentPeriod = ?");
		    pstmt.setString(1, StudentPeriod);
		    ResultSet rs = pstmt.executeQuery();
		    rs.next();
		    int level = rs.getInt(1);
		    String Degree = rs.getString(2);
		    rs.close();
			
			pstmt = con.prepareStatement(
					"SELECT * FROM Grades WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			grades = pstmt.executeQuery();
			
			while (grades.next()) {
				int creditValue = getCreditValue(con, grades.getString(2));
				if (grades.getObject(4) instanceof Integer) {
					if ((level >= 4) || (Degree.charAt(3) == 'P')) {
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
			
			pstmt = con.prepareStatement(
		    		"SELECT DegreeLevel.LevelFROM DegreeLevel JOIN StudentPeriod ON DegreeLevel.DegreeLevel = StudentPeriod.DegreeLevel WHERE StudentPeriod = ?");
		    pstmt.setString(1, StudentPeriod);
		    ResultSet rs = pstmt.executeQuery();
		    rs.next();
		    int level = rs.getInt(1);
		    rs.close();
		    pstmt.close();
			
			pstmt = con.prepareStatement(
					"SELECT * FROM StudentPeriod WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			ResultSet student = pstmt.executeQuery();
			student.next();
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
	
	/**
	 * Function employed to retrieve the next level of a degree.
	 * @throws SQLException 
	 */
	private static String getNextDegreeLevel(Connection con, String currentStudyPeriod) throws SQLException {
		String currentLevel = currentStudyPeriod.substring(0, 1);
		String degreeCode = currentStudyPeriod.substring(1);
		String nextLevel = "0";
		
		if (degreeCode.length() == 7) {
			if (getNumberOfLevels(con, degreeCode) == 4) {
				if (currentLevel == "P")
					nextLevel = "4";
				else if (Integer.parseInt(currentLevel) == 3)
					nextLevel = "P";
				else
					nextLevel = Integer.toString(Integer.parseInt(nextLevel) + 1);
			} else if (getNumberOfLevels(con, degreeCode) == 3) {
				if (currentLevel == "P")
					nextLevel = "3";
				else if (Integer.parseInt(currentLevel) == 2)
					nextLevel = "P";
				else
					nextLevel = Integer.toString(Integer.parseInt(nextLevel) + 1);
			}
		} else
			nextLevel = Integer.toString((Integer.parseInt(currentLevel) + 1));
		
		return nextLevel;
	}
	
	// Unsure where startDate comes from, passing it in externally for now.
	/**
	 * Function employed to register from, graduate from, or fail a period of study.
	 * @throws SQLException 
	 */
	public static void progressToNextPeriod(String currentStudentPeriod, String startDate) throws SQLException {
		Connection con = null;
	    PreparedStatement pstmt = null;
	    
		try {			
			con = SQLFunctions.connectToDatabase();
			pstmt = con.prepareStatement("SELECT * FROM StudentPeriod WHERE StudentPeriod = ?");
			pstmt.setString(1, currentStudentPeriod);
			ResultSet currentPeriod = pstmt.executeQuery();
			pstmt.close();
			
			String nextPeriodID = Integer.toString(((int) currentPeriod.getString(2).charAt(0)) + 1);
			
			if (currentPeriod.getString(3).substring(0,1) == "P" || calculateIfPassed(currentStudentPeriod)) {
				if (currentPeriod.getString(3).substring(0,1) == "P" || Integer.parseInt(currentPeriod.getString(3).substring(0,1)) < getNumberOfLevels(con, currentPeriod.getString(3).substring(1))) {
					// Progress to next level.
					pstmt = con.prepareStatement(
							"INSERT INTO StudentPeriod VALUES(?, ?, ?, ?, ?)");
					pstmt.setString(1, nextPeriodID + currentPeriod.getString(1).substring(1));
					pstmt.setString(2, nextPeriodID);
					pstmt.setString(3, getNextDegreeLevel(con, currentPeriod.getString(3)));
					pstmt.setString(4, currentPeriod.getString(4));
					pstmt.setString(5, startDate);
					pstmt.executeUpdate();
					pstmt.close();					
				} else {
					// Graduate.
					System.out.println("Student has graduated with a " + getDegreeClass(currentPeriod.getString(1).substring(1)));
				}
			} else {
				// Repeat current level, plus other special rules if failed.
				pstmt = con.prepareStatement(
						"INSERT INTO StudentPeriod VALUES(?, ?, ?, ?, ?)");
				pstmt.setString(1, nextPeriodID + currentPeriod.getString(1).substring(1));
				pstmt.setString(2, nextPeriodID);
				pstmt.setString(3, currentPeriod.getString(3));
				pstmt.setString(4, currentPeriod.getString(4));
				pstmt.setString(5, startDate);
				pstmt.executeUpdate();
				pstmt.close();
			}
		} finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to retrieve the total number of levels containing modules on a degree course.
	 * @throws SQLException 
	 */
	private static int getNumberOfLevels(Connection con, String DegreeCode) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement(
				"SELECT COUNT(*) FROM DegreeLevel WHERE DegreeCode = ? AND Level <> 'P'");
		pstmt.setString(1, DegreeCode);
		int level = pstmt.executeQuery().getInt(1);
		pstmt.close();
		return level;
	}
	
	/**
	 * Function employed to calculate the overall degree result.
	 * @throws SQLException 
	 */
	public static String getDegreeClass(String StudentID) throws SQLException {
		Connection con = null;
	    PreparedStatement pstmt = null;
	    double finalGrade = 0;
	    double finalMean = 0;
	    String finalDegreeClass = "";
	    
		try {
			con = SQLFunctions.connectToDatabase();
			
			pstmt = con.prepareStatement(
					"SELECT * FROM StudentPeriod WHERE StudentID = ?");
			pstmt.setString(1, StudentID);
			ResultSet StudentPeriods = pstmt.executeQuery();
			
			int degreeLength = getNumberOfLevels(con, StudentPeriods.getString(3).substring(1));
			
			// Calculates grade weightings.
			while (StudentPeriods.next()) {
				if (StudentPeriods.getString(3).substring(0, 1) == "P")
					continue;
				else if (Integer.parseInt(StudentPeriods.getString(3).substring(0,1)) == 1 && degreeLength == 1)
					finalGrade = calculateWeightedMeanGrade(StudentPeriods.getString(1));
				else if (Integer.parseInt(StudentPeriods.getString(3).substring(0,1)) == 1)
						continue;
				else if (Integer.parseInt(StudentPeriods.getString(3).substring(0,1)) == 2)
					finalGrade += calculateWeightedMeanGrade(StudentPeriods.getString(1));
				else
					finalGrade += (2 * calculateWeightedMeanGrade(StudentPeriods.getString(1)));
			}
	
			if (degreeLength == 1)
				finalMean = finalGrade;
			else if (degreeLength == 4)
				finalMean = (finalGrade / 5);
			else
				finalMean = (finalGrade / 3);
			
			// Determines final degree classifications.
			if (degreeLength == 1) {
				if (finalMean >= 69.5)
					finalDegreeClass = "Distinction";
				else if (finalMean >= 59.5)
					finalDegreeClass = "Merit";
				else if (finalMean >= 49.5)
					finalDegreeClass = "Pass";
				else
					finalDegreeClass = "Fail";
			} else if (degreeLength == 3) {
				if (finalMean >= 69.5)
					finalDegreeClass = "First Class";
				else if (finalMean >= 59.5)
					finalDegreeClass = "Upper Second";
				else if (finalMean >= 49.5)
					finalDegreeClass = "Lower Second";
				else if (finalMean >= 44.5)
					finalDegreeClass = "Third Class";
				else if (finalMean >= 39.5)
					finalDegreeClass = "Pass (Non-Honours)";
				else
					finalDegreeClass = "Fail";
			} else if (degreeLength == 4) {
				if (finalMean >= 69.5)
					finalDegreeClass = "First Class";
				else if (finalMean >= 59.5)
					finalDegreeClass = "Upper Second";
				else if (finalMean >= 49.5)
					finalDegreeClass = "Lower Second";
				else
					finalDegreeClass = "Fail";
			}
			
		} finally {
			SQLFunctions.closeAll(con, pstmt);
		}
		return finalDegreeClass;
	}
}
