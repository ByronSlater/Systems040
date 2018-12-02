package systems.team040.gui.forms;

import com.mysql.cj.xdevapi.SqlDataResult;
import systems.team040.functions.SQLFunctions;
import systems.team040.gui.components.MyTextField;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AddStudentView extends InputPanel {
    public AddStudentView() {
        super(true);

        ArrayList<String> possibleTitles = new ArrayList<>(Arrays.asList("Mr", "Mrs"));

        try {
            ArrayList<String> degrees = SQLFunctions.queryToList(
                    "SELECT DegreeCode FROM Degree;",
                    rs -> rs.getString(1)
            );
            ArrayList<String> startDates = SQLFunctions.queryToList(
                    "SELECT StartDate FROM TermDates;",
                    rs -> rs.getString(1)
            );

            addComboBox("Start Date", "startdate", startDates);
            addComboBox("Degree", "degree", degrees);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error fetching from DB: " + e.getCause().getMessage()
            );
        }

        addComboBox("Title", "title", possibleTitles);

        addStringInput("Forename", "forename", new MyTextField(".+"), JTextComponent::getText);
        addStringInput("Surname", "surname", new MyTextField(".+"), JTextComponent::getText);
        addStringInput("Tutor", "tutor", new MyTextField(".+"), JTextComponent::getText);
    }
}
