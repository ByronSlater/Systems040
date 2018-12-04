/*
 * TeacherFunctions.java
 * @author Matt Prestwich
 * @author Byron Slater
 * @author James Taylor
 */

/**
 * A class containing all the functions available to teacher accounts.
 */
package systems.team040.functions;

import com.mysql.cj.protocol.Resultset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherFunctions {
	/**
	 * Function employed to add or update student grades.
	 * @throws SQLException
	 */
	public static void addGrade(
			String moduleID, String studentPeriod, int grade, boolean resit
	) throws SQLException {

	    String query = resit
				? "UPDATE TABLE Grades SET Resit = ? WHERE StudentPeriod = ? AND ModuleID = ?;"
				: "UPDATE TABLE Grades SET Grade = ? Where StudentPeriod = ? AND ModuleID = ?;";

		try(Connection con = SQLFunctions.connectToDatabase();
			PreparedStatement pstmt = con.prepareStatement(query)) {

		    pstmt.setInt(1, grade);
		    pstmt.setString(2, studentPeriod);
		    pstmt.setString(3, moduleID);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Function employed to calculate students' weighted mean grades.
	 * @throws SQLException
	 */
	public static float calculateWeightedMeanGrade(Connection con, String studentPeriod, int level) throws SQLException {
	    float weightedMean;
	    String query;
	    
        // this query sums up all the best of their grades and multiplies each of them by the credit
        // value of the module they scored that grade in, so when we divide through by the total credits
        // available we get the % they achieved
        query = "SELECT SUM(GREATEST(grade, resit) * credits)" +
                "  FROM (" +
                "		SELECT COALESCE(Grade, 0) as grade, LEAST(COALESCE(Resit, 0), 40) as resit, Credits" +
                "  		  FROM Grades" +
                "  	      JOIN Module" +
                "              ON Grades.ModuleID = Module.ModuleID" +
                "        WHERE StudentPeriod = ?" +
                ") as t1;";

        try(PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, studentPeriod);

            try(ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                weightedMean = level == 4
                        ? (float)rs.getInt(1) / 18000
                        : (float)rs.getInt(1) / 12000;

            }
        }
		return weightedMean;
	}

	public static int getLevel(Connection con, String studentPeriod) throws SQLException {
		int level;

		String query = "" +
				"SELECT Level" +
				"  FROM DegreeLevel" +
				"  JOIN StudentPeriod" +
				"       ON DegreeLevel.DegreeLevel = StudentPeriod.DegreeLevel" +
				" WHERE StudentPeriod = ?";

		try(PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setString(1, studentPeriod);
			try(ResultSet rs = pstmt.executeQuery()) {
				rs.next();
				level = rs.getInt("Level");

			}
		}

		return level;
	}

	public static boolean calculateIfPassed(String studentPeriod) throws SQLException {
	    try(Connection con = SQLFunctions.connectToDatabase()) {
	    	return calculateIfPassed(con, studentPeriod);
		}
	}

	/**
	 * Function employed to calculate whether a student has passed their period of study.
	 * @throws SQLException
	 */
	public static boolean calculateIfPassed(Connection con, String studentPeriod) throws SQLException {
	    String query;
	    int passPercent, level, failableCredits, creditsTaken;

        level = getLevel(con, studentPeriod);

        // set constants based on level of study
        if(level == 4) {
            passPercent = 50;
            failableCredits = 15;
            creditsTaken = 180;
        } else {
            passPercent = 40;
            failableCredits = 20;
            creditsTaken = 120;
        }

        // This nifty little query calculates how many modules have been failed, with any that hold more than
        // the maximum number of failable credits counting as 2 (and thus failing the degree) and, at the same time
        // calculates the weighted mean.
        query = "" +
                "SELECT SUM(BestGrade * Credits) / ? AS WeightedMean											" +
                "     , SUM(FailedCost * IF(BestGrade < ?, IF(BestGrade < ?, 2, 1), 0)) AS FailedModules		" +
                "  FROM (																						" +
                "		SELECT GREATEST(COALESCE(Grade, 0)														" +
                " 		     , LEAST(COALESCE(Resit, 0), ?)) as BestGrade										" +
                "		     , Credits																			" +
                "		     , IF(Credits > ?, 2, 1) as FailedCost												" +
                "		  FROM Grades																			" +
                "		  JOIN Module																			" +
                " 		       ON Grades.ModuleID = Module.ModuleID												" +
                "		 WHERE Grades.StudentPeriod = ?															" +
                ") as t1;																						";


        try(PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, creditsTaken);
            pstmt.setInt(2, passPercent);
            pstmt.setInt(3, passPercent - 10);
            pstmt.setInt(4, passPercent);
            pstmt.setInt(5, failableCredits);
            pstmt.setString(6, studentPeriod);

            try(ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                float weightedMean = rs.getFloat("WeightedMean");
                int failedModules = rs.getInt("FailedModules");

                return !(weightedMean < passPercent) && failedModules <= 1;
            }
		}
	}

	public enum ProgressReturn {
		Failed, Progressed, NotGraded;
	}

	/**
	 * Function employed to register from, graduate from, or fail a period of study.
     *
	 * Could/should potentially be split into more modular functions, e.g. one for registering a student
	 * for a new year and one for registering a student for resitting a year but we don't need to use the code
	 * elsewhere so it's all in this big beast
	 * @throws SQLException
	 */
	public static ProgressReturn progressToNextPeriod(String studentPeriod) throws SQLException {
		char currentPeriodID, nextPeriodID;
		String degreeCode, degreeLevel;
		String query;
		String currentDate, nextDate;
		String studentID;
		String nextDegreeLevel;
		int level;


		try(Connection con = SQLFunctions.connectToDatabase()) {
		    con.setAutoCommit(false);

		    // First we'll check if there are any modules they haven't received a grade for and quit out
			query = "" +
					"SELECT COUNT(*) " +
					"  FROM Grades " +
					" WHERE StudentPeriod = ? " +
					"       AND Grade IS NULL " +
					"       AND Resit IS NULL; ";

			try(PreparedStatement pstmt = con.prepareStatement(query)) {
				pstmt.setString(1, studentPeriod);
				try(ResultSet rs = pstmt.executeQuery()) {
					rs.next();
					if(rs.getInt(1) > 0) {
						return ProgressReturn.NotGraded;
					}
				}
			}

			// get information on next studentperiod etc.
			query = "" +
					"SELECT *" +
					"  FROM StudentPeriod" +
					"  JOIN DegreeLevel" +
					"       ON StudentPeriod.DegreeLevel = DegreeLevel.DegreeLevel" +
					" WHERE StudentPeriod = ?;";
			try(PreparedStatement pstmt = con.prepareStatement(query)) {
				pstmt.setString(1, studentPeriod);

				// get all the information about the current degree level
				try (ResultSet rs = pstmt.executeQuery()) {
					rs.next();

					level = rs.getInt("Level");
					currentPeriodID = rs.getString("PeriodID").charAt(0);
					nextPeriodID = (char) (currentPeriodID + 1);
					currentDate = rs.getString("StartDate");
					degreeCode = rs.getString("DegreeCode");
					studentID = rs.getString("StudentID");
					degreeLevel = rs.getString("DegreeLevel");
				}
			}

			// get the next startdate
			query = "SELECT StartDate FROM TermDates WHERE StartDate > ? ORDER BY StartDate;";
			try(PreparedStatement pstmt = con.prepareStatement(query)) {
				pstmt.setString(1, currentDate);

				try(ResultSet rs = pstmt.executeQuery()) {
					rs.next();

					nextDate = rs.getString("StartDate");
				}
			}

			// if we passed we set up the next year
			if(calculateIfPassed(con, studentPeriod)) {

				// get the next degreeLevel
				query = "SELECT DegreeLevel FROM DegreeLevel WHERE Level > ? AND DegreeCode = ?";
				try(PreparedStatement pstmt = con.prepareStatement(query)) {
					pstmt.setInt(1, level);
					pstmt.setString(2, degreeCode);

					try(ResultSet rs = pstmt.executeQuery()) {
					    // assume we have already checked that this is not the last level of the degree
						rs.next();
                        nextDegreeLevel = rs.getString("DegreeLevel");
					}
				}


				// create the new studentperiod
				query = "INSERT INTO StudentPeriod(StudentPeriod, PeriodID, DegreeLevel, StudentID, StartDate) " +
						"VALUES (?, ?, ?, ?, ?);";
				try(PreparedStatement pstmt = con.prepareStatement(query)) {
					pstmt.setString(1, nextPeriodID + studentID);
					pstmt.setString(2, String.valueOf(nextPeriodID));
					pstmt.setString(3, nextDegreeLevel);
					pstmt.setString(4, studentID);
					pstmt.setString(5, nextDate);

					pstmt.executeUpdate();
				}

				// add the new grades
				query = "" +
						"INSERT INTO Grades (StudentPeriod, ModuleID)" +
                        "SELECT ?, ModuleID " +
                        "  FROM DegreeModule " +
                        " WHERE DegreeLevel = ? AND isCore = 1;";

				try(PreparedStatement pstmt = con.prepareStatement(query)) {
					pstmt.setString(1, nextPeriodID + studentID);
					pstmt.setString(2, nextDegreeLevel);
					pstmt.executeUpdate();
				}

				con.commit();
				return ProgressReturn.Progressed;

			} else {
				// we didn't pass so we add a new studentperiod at the same level and carry forward grades

				// create new studentPeriod at same level
				query = "" +
						"INSERT INTO StudentPeriod(StudentPeriod, PeriodID, DegreeLevel, StudentID, StartDate)" +
						"VALUES (?, ?, ?, ?, ?);";

				try(PreparedStatement pstmt = con.prepareStatement(query)) {
					pstmt.setString(1, nextPeriodID + studentID);
					pstmt.setString(2, String.valueOf(nextPeriodID));
					pstmt.setString(3, degreeLevel);
					pstmt.setString(4, studentID);
					pstmt.setString(5, nextDate);

					pstmt.executeUpdate();
				}

				int passGrade = level == 4 ? 50 : 40;

				// insert old grade if it passed or null if it didn't
				query = "" +
						"INSERT INTO Grades (StudentPeriod, Grade, ModuleID) " +
						"SELECT StudentPeriod, IF(BestGrade >= ?, BestGrade, NULL), ModuleID " +
						"  FROM (" +
                        "		SELECT ? AS StudentPeriod " +
						"		     , GREATEST(COALESCE(Grade, 0),LEAST(COALESCE(Resit, 0), ?)) AS BestGrade " +
						"		     , ModuleID AS ModuleID" +
						"		  FROM Grades " +
						"		 WHERE StudentPeriod = ? " +
						") as t1;";

				try(PreparedStatement pstmt = con.prepareStatement(query)) {
					pstmt.setInt(1, passGrade);
					pstmt.setString(2, nextPeriodID + studentID);
					pstmt.setInt(3, passGrade);
					pstmt.setString(4, studentPeriod);

					pstmt.executeUpdate();
				}

				con.commit();
				return ProgressReturn.Failed;
			}
		}
	}
}
