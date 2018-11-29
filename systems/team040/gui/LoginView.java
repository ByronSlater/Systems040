package systems.team040.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This view is the login page for the application
 */
class LoginView extends MyPanel {
   private JTextField username;
   private JPasswordField password;
   private JButton login;

    LoginView() {
        super(false);

        username = new JTextField("username");
        password = new JPasswordField("password");
        login = new JButton("login");

        username.setPreferredSize(new Dimension(200, 24));
        password.setPreferredSize(new Dimension(200, 24));

        getCenterPanel().add(username);
        getCenterPanel().add(password);
    }

    String getEnteredUsername() {
        return username.getText();
    }

    char[] getEnteredPassword() {
        return password.getPassword();
    }
}
