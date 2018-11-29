package systems.team040.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Kinda janky but gives a way to generate inputforms easily enough
 */
public class InputPanel extends MyPanel {
    private HashMap<String, Supplier<String>> stringGetters;
    private HashMap<String, Supplier<Integer>> integerGetters;

    private ArrayList<MyTextField> needValidating;

    InputPanel(boolean hasBackButton) {
        super(hasBackButton);
        stringGetters = new HashMap<>();
        integerGetters = new HashMap<>();
        needValidating = new ArrayList<>();
    }

    <T extends JComponent> void addStringInput(
            String label, String key, T component, Function<T, String> func) {

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel(label), BorderLayout.PAGE_START);
        inputPanel.add(component, BorderLayout.CENTER);

        getCenterPanel().add(inputPanel);

        stringGetters.put(key, () -> func.apply(component));
    }

    void addStringInput(
            String label, String key, MyTextField component, Function<MyTextField, String> func) {

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel(label), BorderLayout.PAGE_START);
        inputPanel.add(component, BorderLayout.CENTER);

        getCenterPanel().add(inputPanel);

        stringGetters.put(key, () -> func.apply(component));
        needValidating.add(component);
    }

    <T extends JComponent> void addNumericInput(
            String label, String key, T component, Function<T, Integer> func) {

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel(label), BorderLayout.PAGE_START);
        inputPanel.add(component, BorderLayout.CENTER);

        getCenterPanel().add(inputPanel);
        integerGetters.put(key, () -> func.apply(component));
    }

    void createComboBox(String label, String key, ArrayList<String> list) {
        JComboBox<String> cbb = new JComboBox<>(list.toArray(new String[list.size()]));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel(label), BorderLayout.PAGE_START);
        inputPanel.add(cbb, BorderLayout.CENTER);

        getCenterPanel().add(inputPanel);
        stringGetters.put(key, () -> cbb.getSelectedItem().toString());
    }

    String getString(String key) {
        return stringGetters.get(key).get();
    }

    int getInteger(String key) {
        return integerGetters.get(key).get();
    }

    boolean isOkay() {
        for(MyTextField textField : needValidating) {
            if(!textField.isOkay()) {
                return false;
            }
        }

        return true;
    }
}
