package systems.team040.gui.forms;

import systems.team040.gui.GUI;

import javax.swing.*;
import java.awt.*;

public class GradeStudentView extends JPanel {
    private JTable studentInfoTable;
    private JButton backButton;
    private JButton gradeButton;

    GradeStudentView() {
        super(new BorderLayout());
        studentInfoTable = new JTable(new Object[][] {}, new Object[] {"Name", "Oi"});

        JScrollPane scrollPane = new JScrollPane(studentInfoTable);
        backButton = new JButton("Back");
        backButton.setPreferredSize(GUI.buttonSize);

        gradeButton = new JButton("Grade");


        add(scrollPane, BorderLayout.PAGE_START);

        add(new JTextField("Hi"), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(gradeButton);
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.PAGE_END);
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getGradeButton() {
        return gradeButton;
    }

    public JTable getStudentInfoTable() {
        return studentInfoTable;
    }
}
