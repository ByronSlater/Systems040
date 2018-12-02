package systems.team040.gui.forms;

import javax.swing.*;
import java.awt.*;

/**
 * This view is the login page for the application
 */
public class LoginView extends MyPanel {
   public JTextField username;
   private JPasswordField password;
   private JButton login;

    public LoginView() {
        super(false);

        username = new JTextField("username");
        password = new JPasswordField("password");
        login = new JButton("login");

        username.setPreferredSize(new Dimension(200, 24));
        password.setPreferredSize(new Dimension(200, 24));

        centerPanel.add(username);
        centerPanel.add(password);
    }

    public String getEnteredUsername() {
        return username.getText();
    }

    public char[] getEnteredPassword() {
        return password.getPassword();
    }
}
