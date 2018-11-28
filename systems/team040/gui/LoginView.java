package systems.team040.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This view is the login page for the application
 */
public class LoginView extends JPanel {
   private JTextField username;
   private JPasswordField password;
   private JButton login;

    LoginView() {
        super(new FlowLayout());

        username = new JTextField("username");
        password = new JPasswordField("password");
        login = new JButton("login");

        add(username);
        add(password);
        add(login);
    }

    public String getEnteredUsername() {
        return username.getText();
    }

    public char[] getEnteredPassword() {
        return password.getPassword();
    }

    public JButton getLogin() {
        return login;
    }
}
