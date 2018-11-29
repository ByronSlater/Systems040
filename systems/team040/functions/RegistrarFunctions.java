
/*
 * RegistrarFunctions.java
 * @author Matt Prestwich
 * @author Byron Slater
 * @author James Taylor
 */

/**
 * A class containing all the functions available to registrar accounts.
 */

package systems.team040.functions;

import java.sql.*;
import java.util.ArrayList;

public class RegistrarFunctions {
	/**
	 * Function employed to add student a student to the Student table with given details. Also takes a degree and add registers them for that degree.
	 * Will generate a unique Userid and Username.
	 */
	public static void addStudent(
			String Title, String Forenames,
			String Surname, String Tutor, String Degree, String StartDate
	) {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    String Username = null;
	    String StudentID = null;
		try {
			con = SQLFunctions.connectToDatabase();
			//Generation of a unique StudentID which is one higher than the previous maximum
			pstmt = con.prepareStatement(
					"SELECT MAX(StudentID) FROM Student");
			ResultSet maxID = pstmt.executeQuery();
			maxID.next();
			if (maxID.getString(1) != null) {
				StudentID = Integer.toString((Integer.parseInt(maxID.getString(1)) + 1));
			} else {
				StudentID = "000000001";}
			//Generation of a unique username by checking for usernames of the same form and incrementing the end value by 1 in duplicate cases.
			pstmt = con.prepareStatement(
					"SELECT Forename,Surname FROM Student WHERE Surname = ?");
			pstmt.setString(1, Surname);
			ResultSet students = pstmt.executeQuery();
			Username = Forenames.charAt(0) + Surname + 1; 
			if (students != null) {
				while (students.next()) {
					if (students.getString(1).charAt(0) == Forenames.charAt(0)) {
						pstmt = con.prepareStatement(
								"SELECT MAX(UserAccount.Username) FROM UserAccount JOIN Student ON UserAccount.Username = Student.Username WHERE Student.Forename = ? AND Student.Surname = ?");
						pstmt.setString(1, students.getString(1));
						pstmt.setString(2, students.getString(2));
						ResultSet nameFormat = pstmt.executeQuery();
						nameFormat.next();
						int value = 1+Character.getNumericValue(nameFormat.getString(1).charAt(nameFormat.getString(1).length()-1));
						Username = Forenames.charAt(0) + Surname + value;
						break;
					}
				}	
			}
			students.close();
			//Creates the account in the account database with a random password
			AdminFunctions.createAccount(Username, Student.generateRandomPassword(), 3);
			//Adds the student into the student database
			pstmt = con.prepareStatement(
					"INSERT INTO Student VALUES (?, ?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, StudentID);
			pstmt.setString(2, Title);
			pstmt.setString(3, Forenames);
			pstmt.setString(4, Surname);
			pstmt.setString(5, Username + "@Sheffield.ac.uk");
			pstmt.setString(6, Tutor);
			pstmt.setString(7, Username);
			pstmt.executeUpdate();
			//Gives the student a starting student study period with value A
			registerStudent(("A"), ("1" + Degree), StudentID, StartDate);
			//Assigns the user to all the core modules for their degree
			pstmt = con.prepareStatement(
					"SELECT * FROM DegreeModule WHERE DegreeLevel = ? AND isCore = 1");
			pstmt.setString(1, ("1" + Degree));
			ResultSet modules = pstmt.executeQuery();
			while (modules.next()) {
				addModule(modules.getString(1),("A"+StudentID));
			}
			modules.close();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	/**
	 * Function employed to remove student details. Takes the student username and deletes them for the UserAccount table. 
	 * This cascades and deletes the user from the system.
	 */
	public static void removeStudent(String Username) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
		    String query = "DELETE FROM UserAccount WHERE Username = ?;";
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, Username);
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
	 * Function employed to register a student for a study period.
	 */
	public static void registerStudent(String PeriodID, String DegreeLevel, String StudentID, String StartDate) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

		try {
			con = SQLFunctions.connectToDatabase();			
			pstmt = con.prepareStatement(
					"INSERT INTO StudentPeriod VALUES (?,?,?,?,?);");
			pstmt.setString(1, PeriodID + StudentID);
			pstmt.setString(2, PeriodID);
			pstmt.setString(3, DegreeLevel);
			pstmt.setString(4, StudentID);
			pstmt.setString(5, StartDate);
			pstmt.executeUpdate();
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
	}
	
	
	
    public static void addModule(String Module, String StudentPeriod){
    	Connection con = null;
		PreparedStatement pstmt = null;
    	try {
    		con = SQLFunctions.connectToDatabase();	
    		pstmt = con.prepareStatement(
    				"INSERT INTO Grades VALUES (?,?,null,null)");
    		pstmt.setString(1, StudentPeriod);
    		pstmt.setString(2, Module);
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
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
		return optionalModules;
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
		}
		catch (SQLException ex) {
		    ex.printStackTrace();
		}
		finally {
			SQLFunctions.closeAll(con, pstmt);
		}
		return creditTotal;
	}
}
