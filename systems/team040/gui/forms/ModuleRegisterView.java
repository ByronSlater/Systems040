package systems.team040.gui.forms;

import systems.team040.functions.SQLFunctions;
import systems.team040.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ModuleRegisterView extends MyPanel {
    private String studentID;
    private JComboBox<String> moduleSelector;
    private String latestStudentPeriod;
    private String degreeLevel;

    public ModuleRegisterView(String studentID) {
        super(true);

        centerPanel.setLayout(new BorderLayout());
        String query;

        try(Connection con = SQLFunctions.connectToDatabase()) {
            query = "SELECT StudentPeriod, DegreeLevel " +
                    "  FROM StudentPeriod " +
                    " WHERE StudentID = ?" +
                    " ORDER BY StudentPeriod DESC; ";

            try(PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setString(1, studentID);

                try(ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                    latestStudentPeriod = rs.getString(1);
                    degreeLevel = rs.getString(2);
                }
            }

            query = "" +
                    "SELECT ModuleID " +
                    "  FROM DegreeModule " +
                    " WHERE DegreeLevel = ?" +
                    "       AND isCore = 0;";
            ArrayList<String> availModules = SQLFunctions.queryToList(
                    con, query, rs -> rs.getString(1), s -> s.setString(1, degreeLevel)
            );

            query = "" +
                    "SELECT Module.* " +
                    "  FROM Grades " +
                    "  JOIN Module" +
                    "    ON Grades.ModuleID = Module.ModuleID" +
                    " WHERE Grades.StudentPeriod = ?; ";

            centerPanel.add(new JScrollPane(
                    new JTable(GUI.queryToTable(query, s -> s.setString(1, latestStudentPeriod)))
            ), BorderLayout.CENTER);

            moduleSelector = new JComboBox<>();

            for (String availModule : availModules) {
                moduleSelector.addItem(availModule);
            }

            centerPanel.add(moduleSelector, BorderLayout.PAGE_END);


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public String getSelectedModule() {
        return (String) moduleSelector.getSelectedItem();
    }

    public String getDegreeLevel() {
        return degreeLevel;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getLatestStudentPeriod() {
        return latestStudentPeriod;
    }
}
