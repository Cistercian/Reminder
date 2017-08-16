package ru.hd.olaf.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hd.olaf.util.LogUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public class SenderEmail {

    private static final Logger logger = LoggerFactory.getLogger(SenderEmail.class);

    public static void sendEmail(){
        logger.debug(LogUtil.getMethodName());

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "mskmail.msk.russb.org");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");

        final String username = "e-burg\\scaneburg";
        final String password = "Ry@85enK!";
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        Transport transport = null;

        try {
            Session session = Session.getDefaultInstance(properties, authenticator);

            MimeMessage message = new MimeMessage(session);

            // Set the from address
            message.setFrom(new InternetAddress("scaneburg@rgsbank.ru"));
            // Set the to address
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("d.v.hozyashev@rgsbank.ru"));
            // Set the subject
            message.setSubject("Test");
            // Set the content
            message.setText("Test");
            // Send message
            Transport.send(message);

            logger.debug("Письмо отправлено");
        } catch (MessagingException e) {
            logger.debug(String.format("Ошибка при отправке письма: %s", e.getMessage()));
        }

    }
}
