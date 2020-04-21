package com.xerofinancials.importer.beans;

import java.util.ArrayList;
import java.util.List;

public class EmailNotificationRecipients {
    private List<String> errorRecipients = new ArrayList<>();
    private List<String> notificationRecipients = new ArrayList<>();
    private String user;
    private String password;

    public List<String> getErrorRecipients() {
        return errorRecipients;
    }

    public void setErrorRecipients(final List<String> errorRecipients) {
        this.errorRecipients = errorRecipients;
    }

    public List<String> getNotificationRecipients() {
        return notificationRecipients;
    }

    public void setNotificationRecipients(final List<String> notificationRecipients) {
        this.notificationRecipients = notificationRecipients;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
