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
		
		AdminFunctions.addModule("COM1003", "Java", "COM", 20, 0);
		
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
			ResultSet res = stmt.executeQuery("SELECT * FROM Degree");
			while (res.next()) {
				System.out.println(res.getString(1) + " " + res.getString(2));
			}
			res.close();//*/
			
			//stmt.executeUpdate("CREATE TABLE UserAccount (Username VARCHAR(10) PRIMARY KEY, Password VARCHAR(80) NOT NULL, AccountType INTEGER)");
			//stmt.executeUpdate("CREATE TABLE Degree (DegreeCode VARCHAR(10) PRIMARY KEY, DegreeName VARCHAR(50))");
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally {
			if (con != null) con.close();
		}
		
	}
	
}
