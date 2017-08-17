package ru.hd.olaf.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hd.olaf.util.LogUtil;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
public class SenderEmail {

    private static final Logger logger = LoggerFactory.getLogger(SenderEmail.class);

    public static String sendEmail(String address, InputStream inputStream, Map<String, String> settings) throws IOException {
        logger.debug(LogUtil.getMethodName());

        Properties properties = getConnectProperties(settings);

        final String username = settings.get("smtpLogin");
        final String password = settings.get("smtpPassword");
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        StringBuilder message = new StringBuilder();
        address = address.replace(",", ";");

        try {
            Session session = Session.getDefaultInstance(properties, authenticator);

            MimeMessage mail = new MimeMessage(session);

            mail.setFrom(new InternetAddress(settings.get("smtpSender")));
            mail.setSubject(settings.get("smtpTitle"));

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(
                    "Данное письмо сформированно автоматически, пожалуйста, не отвечайте на него. ");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(inputStream, "application/vnd.ms-excel")));
            messageBodyPart.setFileName("overdue.xls");
            multipart.addBodyPart(messageBodyPart);

            mail.setContent(multipart);

            // Send message
            for (String addressTo : address.split(";")) {
                try {
                    mail.setRecipient(Message.RecipientType.TO, new InternetAddress(addressTo));
                    Transport.send(mail);

                    message.append(String.format("%s : письмо отправлено", addressTo));
                    logger.debug("Письмо отправлено");
                } catch (MessagingException e) {
                    message.append(String.format("Ошибка при отправке письма: %s", e.getMessage()));
                    logger.debug(String.format("Ошибка при отправке письма: %s", e.getMessage()));
                }
            }
        } catch (MessagingException e) {
            message.append(String.format("Ошибка при генерации письма: %s", e.getMessage()));
            logger.debug(String.format("Ошибка при генерации письма: %s", e.getMessage()));
        }

        return message.toString();
    }

    private static Properties getConnectProperties(Map<String, String> settings){
        Properties properties = new Properties();

        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", settings.get("smtpHost"));
        properties.put("mail.smtp.port", settings.get("smtpPort"));
        properties.put("mail.smtp.auth", "true");

        return properties;
    }
}
