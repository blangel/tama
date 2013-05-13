package org.moxie.tama;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 11:00 AM
 */
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private final String user;

    private final String password;

    private final String smtpHost;

    private final String smtpPort;

    public EmailService(String user, String password, String smtpHost, String smtpPort) {
        this.user = user;
        this.password = password;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    public synchronized void email(List<String> results, String emailAddressesCSV) {
        if ((results == null) || results.isEmpty()) {
            return;
        }
        
        StringBuilder messageTxt = new StringBuilder();
        for (String result : results) {
            if (result == null) {
                continue;
            }
            messageTxt.append(result);
        }

        Session session = createSession();
        MimeMessage message = new MimeMessage(session);
        String matchesText = "<b>" + results.size() + " new match" +
                                               (results.size() > 1 ? "es" : "") + " found.</b>";
        String emailBody = matchesText + "<br/>" + messageTxt.toString();
        try {
            message.setSubject("new craigslist matches");
            Address[] parsed = InternetAddress.parse(emailAddressesCSV);
            message.setRecipients(Message.RecipientType.TO, parsed);
            message.setContent(emailBody, "text/html; charset=utf-8");
            Transport.send(message);
            LOG.info("Email sent [ to {} with {} results ].", emailAddressesCSV, results.size());
        } catch (MessagingException me) {
            LOG.error("Could not send email to %s", emailAddressesCSV);
            LOG.error(me.getMessage(), me);
        }
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.socketFactory.port", smtpPort);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", smtpPort);

        return Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
    }

}
