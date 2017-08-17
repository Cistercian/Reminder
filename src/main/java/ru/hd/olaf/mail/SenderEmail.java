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

    public static String sendEmail(String address){
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

        StringBuilder message = new StringBuilder();
        address = address.replace(",", ";");

        for (String addressTo: address.split(";")) {
            try {
                Session session = Session.getDefaultInstance(properties, authenticator);

                MimeMessage mail = new MimeMessage(session);

                // Set the from address
                mail.setFrom(new InternetAddress("scaneburg@rgsbank.ru"));
                // Set the to address
                mail.addRecipient(Message.RecipientType.TO, new InternetAddress(addressTo));
                // Set the subject
                mail.setSubject("Test");
                // Set the content
                mail.setText("Test");
                // Send message
                Transport.send(mail);

                message.append(String.format("%s : письмо отправлено", addressTo));
                logger.debug("Письмо отправлено");

            } catch (MessagingException e) {
                message.append(String.format("%s : Ошибка при отправке письма: %s", addressTo, e.getMessage()));
                logger.debug(String.format("Ошибка при отправке письма: %s", e.getMessage()));
            }
        }

        return message.toString();
    }
}
