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
	 * Function employed to calculate students' weighted mean grades.
	 * @throws SQLException
	 */
	public static double calculateWeightedMeanGrade(String StudentPeriod) throws SQLException {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    int gradesSum = 0;
	    int totalModules = 0;
	    double weightedMean = 0;
	    
		try {
			con = SQLFunctions.connectToDatabase();
			
			pstmt = con.prepareStatement(
					"SELECT * FROM Grades WHERE StudentPeriod = ?");
			pstmt.setString(1, StudentPeriod);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				if ((Integer) rs.getInt(4) instanceof Integer) {
					if ((int) rs.getString(1).charAt(3) >= 4) {
						if (rs.getInt(4) > 50)
							gradesSum += 50;
						else
							gradesSum += rs.getInt(4);
					}
					else if (rs.getInt(4) > 40)
						gradesSum += 40;
					totalModules += 1;
				} else if ((Integer) rs.getInt(3) instanceof Integer) {
					gradesSum += rs.getInt(3);
					totalModules += 1; 
				}
			}
			rs.close();
			weightedMean = gradesSum / totalModules;
			System.out.println(weightedMean);
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
		return weightedMean;
	}
	
	// WIP
	/**
	 * Function employed to calculate whether a student has passed their period of study.
	 * @throws SQLException
	 */
	public static void calculateIfPassed(double weightedMean) throws SQLException {
	    
	}
}
