package systems.team040.gui;

import javax.swing.*;
import java.awt.*;

public class TeacherView extends JPanel {
    private JTable studentsTable;
    private JButton logout;

    TeacherView() {
        super(new BorderLayout());
        logout = new JButton("Log out");
        logout.setPreferredSize(GUI.buttonSize);

        studentsTable = new JTable();

        JScrollPane scrollPane = new JScrollPane(studentsTable);

        add(scrollPane, BorderLayout.CENTER);
        add(logout, BorderLayout.PAGE_END);
    }

    public JTable getStudents() {
        return studentsTable;
    }

    public JButton getLogout() {
        return logout;
    }
}
