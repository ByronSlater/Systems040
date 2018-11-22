package systemsProject;

import java.sql.*;

public class SystemTestDrive {

	public static void main(String[] args) throws SQLException {
		
		//AdminFunctions.createAccount("222222222", "test", 3);
		//AdminFunctions.changePassword("222222222", "new");
		//AdminFunctions.removeUser("111111111", "222222222");
		
		//AdminFunctions.addDepartment("POL", "Politics");
		//AdminFunctions.removeDepartment("MAT");
		
		//AdminFunctions.addDegree("G405", "b");
		//AdminFunctions.removeDegree("G404");
		//AdminFunctions.assignDegreeDepartment("G404", "MAS", 1);
		
		//AdminFunctions.addModule("COM1008", "COM", 20, "Y", "Devices");
		//AdminFunctions.removeModule("COM1002");
		//AdminFunctions.assignDegreeModules("1COMU01", "G402", "1");
		//AdminFunctions.assignModuleDegree("COM1008", "1COMU01", "N");
/*----------------------------------------------------------------*/

		
		
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
			ResultSet res = stmt.executeQuery("SELECT * FROM DegreeModule");
			while (res.next()) {
				//System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4) + " " + res.getString(5));
				//System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4));
				//System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3));
				//System.out.println(res.getString(1) + " " + res.getString(2));
				//System.out.println(res.getString(1));
			}
			res.close();//*/
			
			//stmt.executeUpdate("CREATE TABLE UserAccount (Username VARCHAR(10) PRIMARY KEY, Password VARCHAR(80) NOT NULL, AccountType INTEGER)");
			//stmt.executeUpdate("CREATE TABLE Degree (DegreeCode VARCHAR(10) PRIMARY KEY, DegreeName VARCHAR(50))");
			//stmt.executeUpdate("CREATE TABLE DegreeDepartments (DegreeCode VARCHAR(10) NOT NULL, Dept CHAR(3) NOT NULL, IsPrimary INTEGER NOT NULL, PRIMARY KEY (DegreeCode, Dept))");
			//stmt.executeUpdate("CREATE TABLE Module (ModuleID CHAR(7) NOT NULL PRIMARY KEY, Dept CHAR(3) NOT NULL, Credits INT(3) NOT NULL, TimePeriod CHAR (1) NOT NULL, ModuleTitle VARCHAR (50) NOT NULL)");		
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally {
			if (con != null) con.close();
		}
		
	}
	
}
