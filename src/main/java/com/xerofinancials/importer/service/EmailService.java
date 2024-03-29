package com.xerofinancials.importer.service;

import com.xerofinancials.importer.beans.EmailNotificationConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String NOTIFICATION_PREFIX = "Xero Financials Importer (${serverName}) : ";
    private final EmailNotificationConfigs emailNotificationConfigs;
    private final String subjectPrefix;

    public EmailService(final EmailNotificationConfigs emailNotificationConfigs) {
        this.emailNotificationConfigs = emailNotificationConfigs;
        this.subjectPrefix = getSubjectFromNotificationPrefix();
    }

    public void sendErrorEmail(String subject, List<String> messages) {
        try {
            final Session session = getSession();
            final Message message = getMessage(session, subject, messages, emailNotificationConfigs.getErrorRecipients());
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Error while sending email", e);
        }
    }

    public void sendNotificationEmail(String subject, List<String> messages) {
        try {
            final Session session = getSession();
            final Message message = getMessage(session, subject, messages, emailNotificationConfigs.getNotificationRecipients());
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Error while sending email", e);
        }
    }

    private Session getSession() {
        final Properties properties = new Properties();
        properties.put("mail.smtp.host", emailNotificationConfigs.getHost());
        properties.put("mail.smtp.port", emailNotificationConfigs.getPort());
        properties.put("mail.smtp.auth", "true");
        if (emailNotificationConfigs.getSocketFactoryPort() != null) {
            properties.put("mail.smtp.socketFactory.port", emailNotificationConfigs.getSocketFactoryPort());
        }
        if (emailNotificationConfigs.getSocketFactoryClass() != null) {
            properties.put("mail.smtp.socketFactory.class", emailNotificationConfigs.getSocketFactoryClass());
        }

        return Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        emailNotificationConfigs.getUser(),
                        emailNotificationConfigs.getPassword()
                );
            }
        });
    }

    private Message getMessage(Session session, String subject, List<String> messages, List<String> recipients) throws MessagingException {
        final Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailNotificationConfigs.getUser()));
        final InternetAddress[] parsedRecipients = InternetAddress.parse(String.join(",", recipients));
        message.setRecipients(Message.RecipientType.TO, parsedRecipients);
        message.setSubject(subjectPrefix + subject);
        message.setText(String.join("\r\n", messages));
        return message;
    }

    private String getSubjectFromNotificationPrefix() {
        final String serverName = emailNotificationConfigs.getServerName();
        return NOTIFICATION_PREFIX.replace("${serverName}", serverName != null && !serverName.isEmpty() ? serverName : "Unknown Server");
    }
}
