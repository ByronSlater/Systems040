package systems.team040.gui;

import systems.team040.functions.SQLFunctions;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AddStudentView extends JPanel {
    private JButton addStudentButton;
    private JButton backButton;
    private JTextField forename, surname, title, tutor;

    AddStudentView() {
        super(new BorderLayout());
        forename = new JTextField();
        forename.setPreferredSize(GUI.inputSize);
        surname = new JTextField();
        surname.setPreferredSize(GUI.inputSize);
        title = new JTextField();
        title.setPreferredSize(GUI.inputSize);
        tutor = new JTextField();
        tutor.setPreferredSize(GUI.inputSize);

        addStudentButton = new JButton("Add student");
        backButton = new JButton("Back");

        backButton.setPreferredSize(GUI.buttonSize);

        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(addStudentButton);
        southPanel.add(backButton);

        add(southPanel, BorderLayout.PAGE_END);

        JPanel middlePanel = new JPanel(new FlowLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Forename"), BorderLayout.PAGE_START);
        panel.add(forename, BorderLayout.CENTER);
        middlePanel.add(panel);

        panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Surname"), BorderLayout.PAGE_START);
        panel.add(surname, BorderLayout.CENTER);
        middlePanel.add(panel);

        panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Title"), BorderLayout.PAGE_START);
        panel.add(title, BorderLayout.CENTER);
        middlePanel.add(panel);

        panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Tutor"), BorderLayout.PAGE_START);
        panel.add(tutor, BorderLayout.CENTER);
        middlePanel.add(panel);

        JCheckBox checkBox = new JCheckBox();
        ArrayList<Object> degreeCodes = SQLFunctions.columnToList("DegreeCode", "Degreelevel");


        add(middlePanel, BorderLayout.CENTER);


    }
}
