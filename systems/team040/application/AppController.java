package systems.team040.application;

import jdk.nashorn.internal.scripts.JO;
import systems.team040.functions.*;
import systems.team040.gui.GUI;
import systems.team040.gui.components.MyTextField;
import systems.team040.gui.forms.*;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public class AppController {
    private JFrame frame;
    private Container contentPane;
    private UserType currentUser;

    AppController() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    private void changeView(JPanel newPanel) {
        contentPane.removeAll();
        contentPane.add(newPanel);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Creates the login screen
     */
    private JPanel createLoginScreen() {
        LoginView view = new LoginView();

        view.addButton("Login").addActionListener(e -> {
            JPanel nextPage = tryLogin(view.getEnteredUsername(), view.getEnteredPassword());
            if(nextPage == null) {
                JOptionPane.showMessageDialog(null, "Invalid credentials input");
            } else {
                changeView(nextPage);
            }
        });

        return view;
    }

    /**
     * Debug function that lets us login to different account types
     * by providing the appropriate username, ignoring password.
     * delegates to actual login function if not used
     */
    private JPanel tryLogin(String username, char[] password) {
        switch(username.toLowerCase()) {
            case "admin":
                return createAdminSwitchboard();
            case "student":
                return viewStudents(null);
            case "teacher":
                return createTeacherView();
            case "registrar":
                return registrarHome();
            default:
                return tryProperLogin(username, password);
        }
    }

    /**
     * checks the given password against what we pull off the database,
     * returns null if there is no match or returns the homepage
     * of the given user if a match is found
     */
    private JPanel tryProperLogin(String username, char[] password) {
        String query = "SELECT Password, AccountType FROM UserAccount WHERE Username = ?;";

        try(Connection con = SQLFunctions.connectToDatabase();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()) {
                // no password found, username invalid, do nothing
                if(!rs.next()) {
                    return null;
                }

                String stored = rs.getString("Password");

                if(Hasher.validatePassword(password, stored)) {
                    System.out.println("Real account entered");
                    AccountType at = AccountType.fromInt(rs.getInt("AccountType"));

                    LoggedInUser.login(username, at);

                    switch(at) {
                        case Registrar:
                            return registrarHome();
                        case Teacher:
                            return createTeacherView();
                        case Student:
                            return viewStudents("123");
                        case Admin:
                            return createAdminSwitchboard();
                    }
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * logs out and removes loggedinuser variable
     */
    private void logout() {
        currentUser = null;
        changeView(createLoginScreen());
    }

    JPanel createTeacherView() {
        String query = "SELECT * FROM Student";
        MyPanel view = createInfoPanel(query, false);

        System.out.println("yo");

        view.addButton("Log out").addActionListener(e -> logout());
        view.addButton("View Grades").addActionListener(e -> changeView(viewGrades()));

        return view;
    }

    MyPanel viewGrades() {
        String query = "SELECT * FROM Grades;";
        MyPanel view = createInfoPanel(query, true);

        view.getBackButton().addActionListener(e -> changeView(createTeacherView()));
        view.addButton("Set grades").addActionListener(e -> changeView(addGrade()));

        return view;
    }

    MyPanel addGrade() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewGrades()));
        view.addButton("Set grade").addActionListener(e -> System.out.println("Not impleented"));

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

    MyPanel viewUsers() {
        String query = "SELECT * FROM UserAccount;";
        MyPanel view = createInfoPanel(query, true);

        view.addButton("Add User").addActionListener(e -> changeView(addUser()));
        view.addButton("Delete User").addActionListener(e -> changeView(deleteUser()));
        view.getBackButton().addActionListener(e -> changeView(createAdminSwitchboard()));

        return view;
    }

    void displayErrors(InputPanel panel) {
        JOptionPane.showMessageDialog(null, panel.getErrorMessage());
    }

    MyPanel addUser() {
        AddUserView view = new AddUserView(true);
        view.getBackButton().addActionListener(e -> changeView(viewUsers()));
        view.addButton("Add").addActionListener(e -> {
            if(!view.isOkay()) {
                displayErrors(view);
                return;
            }

            try {
                AdminFunctions.createAccount(
                        view.getString("username"),
                        view.getPassword(),
                        view.getAccountType()
                );
                changeView(viewUsers());
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Couldn't create account: " + e1.getCause().getMessage()
                );
            }

        });
        return view;
    }

    MyPanel deleteUser() {
        InputPanel view = new InputPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewUsers()));

        try {
            ArrayList<String> usernames = SQLFunctions.queryToList(
                    "SELECT Username FROM UserAccount",
                    rs -> rs.getString(1)
            );
            view.addComboBox("Username", "username", usernames);
        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null,
                    "Couldn't fetch usernames: " + e.getCause().getMessage()
            );
        }


        view.addButton("Delete").addActionListener(e -> {
            try {
                AdminFunctions.removeUser("", view.getString("username"));
                changeView(viewUsers());
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(
                        null,
                        "Couldn't remove user: " + e1.getCause().getMessage()
                );
            }
        });
        return view;
    }

    MyPanel viewModules() {
        String query = "SELECT * FROM Module;";
        MyPanel view = createInfoPanel(query, true);

        view.getBackButton().addActionListener(e -> changeView(createAdminSwitchboard()));
        view.addButton("Add module").addActionListener(e -> changeView(addModule()));
        view.addButton("Delete module").addActionListener(e -> changeView(deleteModule()));

        return view;
    }

    MyPanel addModule() {
        AddModuleView view = new AddModuleView(true);
        view.getBackButton().addActionListener(e -> changeView(viewModules()));
        view.addButton("Add").addActionListener(
                e -> {
                    try {
                        AdminFunctions.addModule(
                                view.getString("dept") + view.getString("moduleid"),
                                view.getString("dept"),
                                view.getInteger("credits"),
                                view.getString("timeperiod"),
                                view.getString("moduletitle")
                        );

                        changeView(viewModules());
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                                null,
                                view.getString("dept") + view.getString("moduleid")
                                + " is already a module on our system. Please choose another number."
                        );

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                                null,
                                "Error saving to DB: " + ex.getCause().getMessage()
                        );
                    }
                }
        );
        return view;
    }

    MyPanel deleteModule() {
        InputPanel view = new InputPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewModules()));

        try {
            ArrayList<String> moduleIDs = SQLFunctions.queryToList(
                    "SELECT ModuleID FROM Module;",
                    rs -> rs.getString("ModuleID")
            );

            view.addComboBox("Module ID", "moduleid", moduleIDs);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Couldn't get module IDs: " + e.getCause().getMessage()
            );
            e.printStackTrace();
        }

        view.addButton("Delete").addActionListener(e -> {
            try {
                AdminFunctions.removeModule(view.getString("moduleid"));
                changeView(viewModules());
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Error deleting module: " + e1.getCause().getMessage()
                );
            }
        });
        return view;
    }

    MyPanel viewDegrees() {
        String query = "SELECT * FROM Degree;";
        MyPanel view = createInfoPanel(query, true);

        view.getBackButton().addActionListener(e -> changeView(createAdminSwitchboard()));
        view.addButton("Add degree").addActionListener(e -> changeView(addDegree()));
        view.addButton("Delete degree").addActionListener(e -> changeView(deleteDegree()));
        return view;
    }

    MyPanel addDegree() {
        InputPanel view = new InputPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewDegrees()));

        view.addStringInput(
                "Degree Code",
                "dcode",
                new MyTextField(".{1,7}"),
                JTextComponent::getText
        );

        view.addStringInput(
                "Degree Name",
                "dname",
                new MyTextField(".+"),
                JTextComponent::getText
        );

        view.addNumericInput(
                "Degree Length",
                "dlen",
                new MyTextField("\\d+"),
                mtf -> Integer.parseInt(mtf.getText())
        );

        view.addButton("Add").addActionListener(e -> {
            String dcode = view.getString("dcode");
            String dname = view.getString("dname");
            int dlen = view.getInteger("dlwn");
            try {
                AdminFunctions.addDegree(dcode, dname, dlen);
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Couldn't save degree: " + e1.getCause().getMessage()
                );
            }
        });
        return view;
    }

    MyPanel deleteDegree() {
        InputPanel view = new InputPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewDegrees()));

        try {
            ArrayList<String> degreeCodes =
                    SQLFunctions.queryToList(
                            "SELECT DegreeCode FROM Degree;",
                            rs -> rs.getString(1)
                    );

            view.addComboBox("Degree Code", "dcode", degreeCodes);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Couldn't get degree codes: " + e.getCause().getMessage()
            );
        }

        view.addButton("Delete").addActionListener(e -> {
            String dcode = view.getString("dcode");
            try {
                AdminFunctions.removeDegree(dcode);
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Couldn't delete module: " + e1.getCause().getMessage()
                );
            }
        });
        return view;
    }
    MyPanel viewDepartments() {
        String query = "SELECT * FROM Department;";
        MyPanel view = createInfoPanel(query, true);

        view.getBackButton().addActionListener(e -> changeView(createAdminSwitchboard()));
        view.addButton("Add department").addActionListener(e -> changeView(addDepartment()));
        view.addButton("Delete department").addActionListener(e -> changeView(deleteDepartment()));
        return view;
    }

    MyPanel addDepartment() {
        InputPanel view = new InputPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewDepartments()));

        view.addStringInput(
                "Dept Code", "dcode",
                new MyTextField("[A-Z]{3}"), JTextComponent::getText
        );

        view.addStringInput(
                "Dept Name", "dname",
                new MyTextField(".+"), JTextComponent::getText
        );

        view.addButton("Add").addActionListener(e -> {
            String dcode = view.getString("dcode");
            String dname = view.getString("dname");
            try {
                AdminFunctions.addDepartment(dcode, dname);
            } catch (SQLIntegrityConstraintViolationException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Department already exists with code \"" + dcode + "\""
                );
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Couldn't create department: " + e1.getCause().getMessage()
                );
            }
        });
        return view;
    }

    MyPanel deleteDepartment() {
        InputPanel view = new InputPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewDepartments()));

        try {
            ArrayList<String> departments = SQLFunctions.queryToList(
                    "SELECT Dept FROM Department;",
                    rs -> rs.getString(1)
            );

            view.addComboBox("Department Code", "dept", departments);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null
                    "Couldn't get departments: " + e.getCause().getMessage()
            );
        }


        view.addButton("Delete").addActionListener(e -> {
            String dept = view.getString("dept");
            try {
                AdminFunctions.removeDepartment(dept);
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Couldn't delete department: " + e1.getCause().getMessage()
                );
            }
        });
        return view;
    }
    MyPanel linkModules() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(createAdminSwitchboard()));
        return view;
    }

    MyPanel registrarHome() {
        String query = "SELECT * FROM Student;";
        MyPanel view = createInfoPanel(query, false);
        view.addButton("Log out").addActionListener(e -> logout());
        view.addButton("View individual student").addActionListener(
                e -> changeView(selectStudent())
        );
        view.addButton("Add student").addActionListener(e -> changeView(addStudent()));
        view.addButton("Delete student").addActionListener(e -> changeView(deleteStudent()));

        return view;
    }

    MyPanel selectStudent() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(registrarHome()));
        view.addButton("Select").addActionListener(e -> System.out.println("not implemented"));

        return view;
    }

    MyPanel addStudent() {
        InputPanel view = new AddStudentView();
        view.getBackButton().addActionListener(evt -> changeView(registrarHome()));
        view.addButton("Add").addActionListener(evt -> {
            try {
                RegistrarFunctions.addStudent(
                        view.getString("title"),
                        view.getString("forename"),
                        view.getString("surname"),
                        view.getString("tutor"),
                        view.getString("degree"),
                        view.getString("startdate")
                );

                changeView(registrarHome());
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error: User not created");
            }
        });
        return view;
    }

    private MyPanel deleteStudent() {
        InputPanel view = new InputPanel(true);
        view.getBackButton().addActionListener(e -> changeView(registrarHome()));

        try {
            ArrayList<String> usernames = SQLFunctions.queryToList(
                    "SELECT StudentID FROM Student;",
                    rs -> rs.getString(1)
            );

            view.addComboBox("Username", "username", usernames);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Couldn't get usernames: " + e.getCause().getMessage()
            );
        }
        view.addButton("Delete").addActionListener(
                e -> RegistrarFunctions.removeStudent(view.getString("username"))
        );

        return view;
    }

    private MyPanel createInfoPanel(String query, boolean hasBackButton) {
        MyPanel view = new MyPanel(hasBackButton);
        JScrollPane scrollPane = null;
        try {
            scrollPane = new JScrollPane(new JTable(GUI.queryToTable(query)));
            view.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
            JTextArea errorLabel = new JTextArea(e.getCause().getMessage());
            errorLabel.setLineWrap(true);
            errorLabel.setOpaque(false);
            errorLabel.setWrapStyleWord(true);

            view.add(errorLabel, BorderLayout.CENTER);
        }


        return view;
    }

    private JPanel viewStudents(String regNo) {
        // TODO - write a good query here
        String query = "SELECT * FROM STUDENTS;";
        MyPanel view = createInfoPanel(query, false);
        view.addButton("Log out").addActionListener(e -> logout());
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

        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return switchboard;
    }
}
