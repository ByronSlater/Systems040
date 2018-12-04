/*
 * StudentFunctions.java
 * @author Matt Prestwich
 */

/**
 * A class containing all the functions available to student accounts.
 */

package systems.team040.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentFunctions {
    public static String getIDFromUsername(Connection con, String username) throws SQLException {
        String query = "SELECT StudentID FROM Student WHERE Username = ?;";

        try(PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username);

            try(ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getString(1);
            }
        }
    }

    public static String getIDFromUsername(String username) throws SQLException {
        try(Connection con = SQLFunctions.connectToDatabase()) {
            return getIDFromUsername(con, username);
        }
    }
}
