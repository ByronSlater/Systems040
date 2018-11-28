package systems.team040.gui;

import systems.team040.functions.Hasher;
import systems.team040.functions.SQLFunctions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.swing.*;

public class AppController {
    private JFrame frame;
    private Container contentPane;
    private UserType currentUser;

    AppController() {
        currentUser = null;

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        contentPane = frame.getContentPane();

        frame.setResizable(false);
        frame.setSize(1000, 600);
        frame.setLocation(
                GUI.screenSize.width / 2 - 500,
                GUI.screenSize.height / 2 - 300
        );

        frame.setVisible(true);

        changeView(createLoginScreen());
    }

    void changeView(JPanel newPanel) {
        contentPane.removeAll();
        contentPane.add(newPanel);
        frame.revalidate();
        frame.repaint();
    }

    void showGradingView() {
        GradeStudentView view = new GradeStudentView();
        changeView(view);
        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTeacherSwitchboard();
            }
        });
    }

    void showTeacherSwitchboard() {

    }

    JPanel createAddStudentView() {
        AddStudentView view = new AddStudentView();

        return view;
    }

    JPanel createLoginScreen() {
        LoginView view = new LoginView();

        view.getLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryLogin(view.getEnteredUsername(), view.getEnteredPassword());
            }
        });

        return view;
    }

    void tryLogin(String username, char[] password) {
        switch(username.toLowerCase()) {
            case "admin":
                changeView(createAdminSwitchboard());
                break;
            case "student":
                changeView(createStudentView(null));
                break;
            case "teacher":
                changeView(createTeacherView());
                break;
            case "registrar":
                changeView(createRegistrarSwitchboard());
                break;
            default:
                tryProperLogin(username, password);

        }
    }

    private void tryProperLogin(String username, char[] password) {
        String query = "SELECT Password FROM UserAccount WHERE Username = ?;";

        try(Connection con = SQLFunctions.connectToDatabase();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()) {
                // no password found, username invalid, do nothing
                if(!rs.next()) {
                    System.out.println("invalid usename");
                    return;
                }
                String stored = rs.getString(1);

                if(Hasher.validatePassword(password, stored)) {
                    System.out.println("Real account entered");
                } else {
                    System.out.println("wrong password");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    void logout() {
        currentUser = null;
        changeView(createLoginScreen());
    }

    JPanel createTeacherView() {
        TeacherView view = new TeacherView();

        view.getLogout().addActionListener(e -> logout());

        return view;
    }

    JPanel createAdminSwitchboard() {
        String[] buttonTitles = {
                "View/Edit users",
                "View/Edit modules",
                "View/Edit degrees",
                "View/Edit departments",
                "Link modules"
        };

        Supplier<JPanel>[] funcs = new Supplier[] {
                this::viewUsers,
                this::viewModules,
                this::viewDegrees,
                this::viewDepartments,
                this::linkModules
        };

        return createGenericSwitchboard(buttonTitles, funcs);
    }

    JPanel viewUsers() {
        String query = "SELECT * FROM UserAccount;";

        return createInfoPanel(query, this::createAdminSwitchboard);
    }

    JPanel viewModules() {
        String query = "SELECT * FROM Module;";

        return createInfoPanel(query, this::createAdminSwitchboard);
    }

    JPanel viewDegrees() {
        String query = "SELECT * FROM Degree;";

        return createInfoPanel(query, this::createAdminSwitchboard);
    }

    JPanel viewDepartments() {
        String query = "SELECT * FROM Department;";

        return createInfoPanel(query, this::createAdminSwitchboard);
    }

    JPanel linkModules() {
        String query = "SELECT * FROM Users;";

        return createInfoPanel(query, this::createAdminSwitchboard);
    }

    JPanel createRegistrarSwitchboard() {
        String[] buttonTitles = {
                "Nothin"
        };
        Supplier<JPanel>[] funcs = new Supplier[] {
                this::createRegistrarSwitchboard
        };

        return createGenericSwitchboard(buttonTitles, funcs);
    }

    JPanel createInfoPanel(String query, Supplier<JPanel> backButtonFunc) {
        JScrollPane scrollPane = new JScrollPane(new JTable(GUI.queryToTable(query)));
        JPanel infoPanel = new JPanel(new BorderLayout());

        infoPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");

        backButton.addActionListener(e -> changeView(backButtonFunc.get()));
        buttonsPanel.add(backButton);

        infoPanel.add(buttonsPanel, BorderLayout.PAGE_END);

        return infoPanel;
    }

    JPanel createStudentView(String regNo) {
        StudentView view = new StudentView();
        view.getLogout().addActionListener(e -> logout());
        return view;
    }

    public static void main(String[] args) {
        new AppController();
    }

    /**
     * Generic function to create a switchboard that just has a list
     * of buttons that take you to various JPanels, provided as suppliers in the
     * function
     *
     * We can give every switchboard a logout button at the bottom because
     * we know that they only show up directly after logging in
     */
    JPanel createGenericSwitchboard(String[] buttonTitles, Supplier<JPanel>[] funcs) {
        // Switchboard is the whole thing, buttonpanel is the panel
        // with the switch buttons on
        JPanel switchboard = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Create each button using the titles and link them to an action that
        // creates the view
        JButton[] buttons = new JButton[buttonTitles.length];
        for(int i = 0; i < buttonTitles.length; ++i) {
            buttons[i] = new JButton(buttonTitles[i]);

            Supplier<JPanel> func = funcs[i];

            buttons[i].addActionListener(e -> changeView(func.get()));

            buttonPanel.add(buttons[i]);
        }

        switchboard.add(buttonPanel, BorderLayout.CENTER);

        JPanel logoutPanel = new JPanel(new FlowLayout());
        JButton logoutButton = new JButton("Log out");

        logoutButton.addActionListener(e -> changeView(createLoginScreen()));
        logoutPanel.add(logoutButton);
        switchboard.add(logoutPanel, BorderLayout.PAGE_END);

        return switchboard;
    }
}
