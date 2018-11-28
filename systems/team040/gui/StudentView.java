package systems.team040.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

public class StudentView extends JPanel {
    private JTable infoTable;
    private JButton logout;

    StudentView() {
        super(new FlowLayout());

        logout = new JButton("Logout");
        infoTable = new JTable();

        add(infoTable);
        add(logout);
    }

    public JTable getInfoTable() {
        return infoTable;
    }

    public JButton getLogout() {
        return logout;
    }
}
