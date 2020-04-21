package com.xerofinancials.importer.service;

import com.xerofinancials.importer.beans.EmailNotificationRecipients;
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
    private static final String NOTIFICATION_PREFIX = "Xero Financials Importer: ";
    private final EmailNotificationRecipients emailNotificationRecipients;

    public EmailService(final EmailNotificationRecipients emailNotificationRecipients) {
        this.emailNotificationRecipients = emailNotificationRecipients;
    }

    public void sendErrorEmail(String subject, String text) {
        try {
            final Session session = getSession();
            final Message message = getMessage(session, subject, text, emailNotificationRecipients.getErrorRecipients());
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Error while sending email", e);
        }
    }

    public void sendNotificationEmail(String subject, String text) {
        try {
            final Session session = getSession();
            final Message message = getMessage(session, subject, text, emailNotificationRecipients.getNotificationRecipients());
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Error while sending email", e);
        }
    }

    private Session getSession() {
        final Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        return Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        emailNotificationRecipients.getUser(),
                        emailNotificationRecipients.getPassword()
                );
            }
        });
    }

    private Message getMessage(Session session, String subject, String text, List<String> recipients) throws MessagingException {
        final Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailNotificationRecipients.getUser()));
        final InternetAddress[] parsedRecipients = InternetAddress.parse(String.join(",", recipients));
        message.setRecipients(Message.RecipientType.TO, parsedRecipients);
        message.setSubject(NOTIFICATION_PREFIX + subject);
        message.setText(text);
        return message;
    }
}
