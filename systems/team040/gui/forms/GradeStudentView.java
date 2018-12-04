package systems.team040.gui.forms;

import systems.team040.functions.SQLFunctions;
import systems.team040.gui.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class GradeStudentView extends MyPanel {
    private DefaultTableModel model;
    private JTable table;
    private String studentID;
    String query;

    public GradeStudentView(String studentID) {
        super(true);

        this.studentID = studentID;

        query = "" +
                "SELECT ModuleID AS 'Module' " +
                "     , Grades.StudentPeriod AS Period " +
                "     , Grade AS 'Initial Grade' " +
                "     , Resit AS 'Resit Grade' " +
                "  FROM Grades " +
                "  JOIN StudentPeriod" +
                "       ON StudentPeriod.StudentPeriod = Grades.StudentPeriod " +
                " WHERE StudentPeriod.StudentID = ?;";


        try {
            model = (DefaultTableModel) GUI.queryToTable(query, s -> s.setString(1, studentID));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Couldn't get grades: " + e.getMessage()
            );
            e.printStackTrace();
        }

        table = new JTable(model);


        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane);
    }

    public DefaultTableModel getModel() {
        model.fireTableDataChanged();
        return model;
    }
}
