package systems.team040.gui;

import org.omg.PortableServer.POA;
import systems.team040.functions.SQLFunctions;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddStudentView extends InputPanel {
    AddStudentView() {
        super(true);

        ArrayList<String> possibleTitles = new ArrayList<>();
        possibleTitles.add("Mr");
        possibleTitles.add("Mrs");

        ArrayList<String> degrees =
                SQLFunctions.columnToList("Degree", "DegreeCode")
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.toCollection(ArrayList::new));

        addStringInput("Forename", "forename", new MyTextField(".+"), JTextComponent::getText);
        addStringInput("Surname", "surname", new MyTextField(".+"), JTextComponent::getText);
        createComboBox("Title", "title", possibleTitles);
        addStringInput("Tutor", "tutor", new MyTextField(".+"), JTextComponent::getText);
        createComboBox("Degree", "degree", degrees);
    }
}
