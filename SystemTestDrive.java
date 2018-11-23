package systemsProject;

import java.sql.*;

public class SystemTestDrive {

	public static void main(String[] args) throws SQLException {
		
		/**
		 * Admin function testing.
		 */
		
		//AdminFunctions.createAccount("sma16msp", "password", 3);
		//AdminFunctions.changePassword("222222222", "new");
		//AdminFunctions.removeUser("111111111", "160175686");
		
		//AdminFunctions.addDepartment("POL", "Politics");
		//AdminFunctions.removeDepartment("MAT");
		
		//AdminFunctions.addDegree("G405", "b");
		//AdminFunctions.removeDegree("G404");
		//AdminFunctions.assignDegreeDepartment("G404", "MAS", 1);
		
		//AdminFunctions.addModule("COM1002", "COM", 20, "Y", "Foundations of Computer Science");
		//AdminFunctions.removeModule("COM1002");
		//AdminFunctions.assignDegreeModules("1COMU01", "G402", "1");
		//AdminFunctions.assignModuleDegree("COM1009", "1COMU01", 1);
/*----------------------------------------------------------------*/
		/**
		 * Registrar function testing.
		 */
		
		//RegistrarFunctions.addStudent("160175686", "Mr", "Matthew Steven", "Prestwich", "msprestwich1@sheffield.ac.uk", "sma16msp");
		//RegistrarFunctions.removeStudent("160175686");
		
		//RegistrarFunctions.registerStudent("A", "1COMU01", "160175686", "17/09/2017");
		
		//RegistrarFunctions.verifyCreditTotal("170163233");		
/*----------------------------------------------------------------*/
		/**
		 * Teacher function testing.
		 */
		
		//TeacherFunctions.addGrade("COM1006", "A170163233", 35);
		TeacherFunctions.addResitGrade("COM1006", "A170163233", 75);
/*----------------------------------------------------------------*/
		System.out.println("--------------------------");
		Connection con = null;	
		try {
			con = DriverManager.getConnection("jdbc:mysql://stusql.dcs.shef.ac.uk/team040", "team040", "c7a84239");
			Statement stmt = con.createStatement();
			
			/*ResultSet rs = stmt.executeQuery("SELECT * FROM Department");
			ResultSetMetaData rsmd = rs.getMetaData();
			String name = rsmd.getColumnName(2);
			System.out.println(name);*/
			
			//*
			ResultSet res = stmt.executeQuery("SELECT * FROM Grades");
			while (res.next()) {
				//System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4) + " " + res.getString(5) + " " + res.getString(6));
				//System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4) + " " + res.getString(5));
				System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4));
				//System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3));
				//System.out.println(res.getString(1) + " " + res.getString(2));
				//System.out.println(res.getString(1));
			}
			res.close();//*/
			
			//stmt.executeUpdate("CREATE TABLE UserAccount (Username VARCHAR(10) PRIMARY KEY, Password VARCHAR(80) NOT NULL, AccountType INTEGER)");
			//stmt.executeUpdate("CREATE TABLE Degree (DegreeCode VARCHAR(10) PRIMARY KEY, DegreeName VARCHAR(50))");
			//stmt.executeUpdate("CREATE TABLE DegreeDepartments (DegreeCode VARCHAR(10) NOT NULL, Dept CHAR(3) NOT NULL, IsPrimary INTEGER NOT NULL, PRIMARY KEY (DegreeCode, Dept))");
			//stmt.executeUpdate("CREATE TABLE Module (ModuleID CHAR(7) NOT NULL PRIMARY KEY, Dept CHAR(3) NOT NULL, Credits INT(3) NOT NULL, TimePeriod CHAR (1) NOT NULL, ModuleTitle VARCHAR (50) NOT NULL)");		
			//stmt.executeUpdate("CREATE TABLE Student (StudentID CHAR(9) NOT NULL PRIMARY KEY, Title VARCHAR(10) NOT NULL, Forename VARCHAR(20) NOT NULL, Surname VARCHAR(20) NOT NULL, EmailAddress VARCHAR(30) NOT NULL, Username VARCHAR (50) NOT NULL)");
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally {
			if (con != null) con.close();
		}
		
	}
	
}
