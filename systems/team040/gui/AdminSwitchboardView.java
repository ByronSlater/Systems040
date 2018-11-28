package systems.team040.gui;

import javax.swing.*;
import java.awt.*;

/**
 * View is the switchboard for the admins to choose what functions they want to
 * look at
 */
public class AdminSwitchboardView extends JPanel {
    private JButton users;
    private JButton degrees;
    private JButton depts;
    private JButton modules;
    private JButton moduleLinks;
    private JButton logout;

    AdminSwitchboardView() {
        super(new FlowLayout());

        users = new JButton("Users");
        add(users);

        degrees = new JButton("Degrees");
        add(degrees);

        depts = new JButton("Departments");
        add(depts);

        modules = new JButton("Modules");
        add(modules);

        moduleLinks = new JButton("Link Module");
        add(moduleLinks);

        logout = new JButton("Logout");
        add(logout);
    }

    public JButton getUsers() {
        return users;
    }

    public JButton getDegrees() {
        return degrees;
    }

    public JButton getDepts() {
        return depts;
    }

    public JButton getModules() {
        return modules;
    }

    public JButton getModuleLinks() {
        return moduleLinks;
    }

    public JButton getLogout() {
        return logout;
    }
}
