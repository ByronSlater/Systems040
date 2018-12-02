package systems.team040.application;

import systems.team040.functions.AccountType;

public class LoggedInUser {
    static LoggedInUser instance;
    private String username;
    private AccountType accountType;

    public static LoggedInUser getInstance() {
        return instance == null ? new LoggedInUser() : instance;
    }

    public static void login(String username, AccountType type) {
        LoggedInUser user = getInstance();
        user.accountType = type;
        user.username = username;
    }

    public static void logout() {
        instance = null;
    }

    private LoggedInUser() {}

    public String getUsername() {
        return username;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
