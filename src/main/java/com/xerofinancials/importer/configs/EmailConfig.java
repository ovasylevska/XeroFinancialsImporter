package com.xerofinancials.importer.configs;

import com.xerofinancials.importer.beans.EmailNotificationConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Bean
    public EmailNotificationConfigs emailNotificationRecipients(
            @Value("${email.error.recipient}") String errorRecipient,
            @Value("${email.notification.recipient}") String notificationRecipient,
            @Value("${email.user}") String user,
            @Value("${email.password}") String password,
            @Value("${email.host}") String host,
            @Value("${email.port}") Integer port,
            @Value("${email.socketFactory.port}") Integer socketFactoryPort,
            @Value("${email.socketFactory.class}") String socketFactoryClass,
            @Value("${spring.application.server}") String applicationServer
    ) {
        final EmailNotificationConfigs emailNotificationConfigs = new EmailNotificationConfigs();
        emailNotificationConfigs.getErrorRecipients().add(errorRecipient);
        emailNotificationConfigs.getNotificationRecipients().add(notificationRecipient);
        emailNotificationConfigs.setUser(user);
        emailNotificationConfigs.setPassword(password);
        emailNotificationConfigs.setHost(host);
        emailNotificationConfigs.setPort(port);
        emailNotificationConfigs.setSocketFactoryPort(socketFactoryPort);
        emailNotificationConfigs.setSocketFactoryClass(socketFactoryClass);
        emailNotificationConfigs.setServerName(applicationServer);
        return emailNotificationConfigs;
    }
}
