package com.xerofinancials.importer.configs;

import com.xerofinancials.importer.beans.EmailNotificationRecipients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Bean
    public EmailNotificationRecipients emailNotificationRecipients(
            @Value("${email.error.recipient}") String errorRecipient,
            @Value("${email.notification.recipient}") String notificationRecipient,
            @Value("${email.user}") String user,
            @Value("${email.password}") String password
    ) {
        final EmailNotificationRecipients emailNotificationRecipients = new EmailNotificationRecipients();
        emailNotificationRecipients.getErrorRecipients().add(errorRecipient);
        emailNotificationRecipients.getNotificationRecipients().add(notificationRecipient);
        emailNotificationRecipients.setUser(user);
        emailNotificationRecipients.setPassword(password);
        return emailNotificationRecipients;
    }
}
