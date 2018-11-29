package systems.team040.gui;

import systems.team040.functions.AccountType;
import systems.team040.functions.Hasher;
import systems.team040.functions.RegistrarFunctions;
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
import javax.swing.border.Border;

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

    void changeView(JPanel newPanel) {
        contentPane.removeAll();
        contentPane.add(newPanel);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Creates the login screen
     */
    JPanel createLoginScreen() {
        LoginView view = new LoginView();

        view.addButton("Login").addActionListener(new ActionListener() {
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
                changeView(registrarHome());
                break;
            default:
                tryProperLogin(username, password);

        }
    }

    private void tryProperLogin(String username, char[] password) {
        String query = "SELECT Password, AccountType FROM UserAccount WHERE Username = ?;";

        try(Connection con = SQLFunctions.connectToDatabase();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()) {
                // no password found, username invalid, do nothing
                if(!rs.next()) {
                    System.out.println("invalid usename");
                    return;
                }

                String stored = rs.getString("Password");

                if(Hasher.validatePassword(password, stored)) {
                    System.out.println("Real account entered");
                    AccountType at = AccountType.fromInt(rs.getInt("AccountType"));
                    switch(at) {
                        case Registrar:
                            changeView(registrarHome());
                            break;
                        case Teacher:
                            changeView(createTeacherView());
                            break;
                        case Student:
                            changeView(createStudentView("0123"));
                            break;
                        case Admin:
                            changeView(createAdminSwitchboard());
                            break;
                    }
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
        String query = "SELECT * FROM STUDENTS";
        MyPanel view = createInfoPanel(query, false);

        view.addButton("Log out").addActionListener(e -> logout());
        view.addButton("View Grades").addActionListener(e -> changeView(viewGrades()));

        return view;
    }

    MyPanel viewGrades() {
        String query = "SELECT * FROM GRADES;";
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

    MyPanel addUser() {
        InputPanel view = new AddStudentView();
        view.getBackButton().addActionListener(e -> changeView(viewUsers()));
        view.addButton("Add").addActionListener(e -> {
            if(!view.isOkay()) {
                JOptionPane.showMessageDialog(null, "Invalid Inputs");
                return;
            }
            RegistrarFunctions.addStudent(
                    view.getString("title"),
                    view.getString("forename"),
                    view.getString("surname"),
                    view.getString("tutor"),
                    view.getString("degree"),
                    "10-05-1995"
            );

            changeView(viewUsers());
        });
        return view;
    }

    MyPanel deleteUser() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewUsers()));
        view.addButton("Delete").addActionListener(e -> System.out.println("not done yet"));
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
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewModules()));
        view.addButton("Add").addActionListener(e -> System.out.println("not done yet"));
        return view;
    }

    MyPanel deleteModule() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewModules()));
        view.addButton("Delete").addActionListener(e -> System.out.println("not done yet"));
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
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewDegrees()));
        view.addButton("Add").addActionListener(e -> System.out.println("not done yet"));
        return view;
    }

    MyPanel deleteDegree() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewDegrees()));
        view.addButton("Delete").addActionListener(e -> System.out.println("not done yet"));
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
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewDepartments()));
        view.addButton("Add").addActionListener(e -> System.out.println("not done yet"));
        return view;
    }

    MyPanel deleteDepartment() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(viewDepartments()));
        view.addButton("Delete").addActionListener(e -> System.out.println("not done yet"));
        return view;
    }
    MyPanel linkModules() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(createAdminSwitchboard()));
        return view;
    }

    MyPanel registrarHome() {
        String query = "SELECT * FROM STUDENTS;";
        MyPanel view = createInfoPanel(query, false);
        view.addButton("Log out").addActionListener(e -> logout());
        view.addButton("View individual student").addActionListener(
                e -> changeView(selectStudent())
        );
        view.addButton("Add student");
        view.addButton("Delete student");

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
        view.getBackButton().addActionListener(e -> changeView(viewUsers()));
        view.addButton("Add").addActionListener(e -> {
            RegistrarFunctions.addStudent(
                    view.getString("title"),
                    view.getString("forename"),
                    view.getString("surname"),
                    view.getString("tutor"),
                    view.getString("degree"),
                    "10-05-1995"
            );
        });
        return view;
    }

    MyPanel deleteStudent() {
        MyPanel view = new MyPanel(true);
        view.getBackButton().addActionListener(e -> changeView(registrarHome()));
        view.addButton("Delete").addActionListener(e -> System.out.println("not implemented"));

        return view;

    }

    MyPanel createInfoPanel(String query, boolean hasBackButton) {
        MyPanel view = new MyPanel(hasBackButton);

        JScrollPane scrollPane = new JScrollPane(new JTable(GUI.queryToTable(query)));
        JPanel infoPanel = new JPanel(new BorderLayout());

        view.add(scrollPane, BorderLayout.CENTER);

        return view;
    }

    JPanel createStudentView(String regNo) {
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
