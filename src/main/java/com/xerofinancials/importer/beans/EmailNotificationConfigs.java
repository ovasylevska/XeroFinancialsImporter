package com.xerofinancials.importer.beans;

import java.util.ArrayList;
import java.util.List;

public class EmailNotificationConfigs {
    private List<String> errorRecipients = new ArrayList<>();
    private List<String> notificationRecipients = new ArrayList<>();
    private String user;
    private String password;
    private String host;
    private Integer port;
    private String socketFactoryClass;
    private Integer socketFactoryPort;
    private String serverName;

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

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }


    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    public void setSocketFactoryClass(final String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
    }

    public Integer getSocketFactoryPort() {
        return socketFactoryPort;
    }

    public void setSocketFactoryPort(final Integer socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
}
