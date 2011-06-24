package org.moxie.tama;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 11:00 AM
 */
public class EmailService {

    protected final JavaMailSenderImpl javaMailSender;

    protected final String sendToEmail;

    public EmailService() {
        this("langelb@gmail.com");
    }

    public EmailService(String sendToEmail) {
        this.javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(465);
        javaMailSender.setProtocol("smtps");
        javaMailSender.setUsername("craigslist.delegate");
        javaMailSender.setPassword("cr41gsl1std3l3g4t3$");
        Properties properties = new Properties();
        properties.put("mail.smtps.auth", true);
        properties.put("mail.smtps.starttls.enable", true);
        properties.put("mail.smtps.debug", true);
        javaMailSender.setJavaMailProperties(properties);
        this.sendToEmail = sendToEmail;
    }

    public void email(List<String> results) {
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

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        String emailBody = messageTxt.toString();
        System.out.println("Emailing:\n" + emailBody);
        try {
            helper.setSubject(results.size() + " Craigslist Apt Matches Found!");
            helper.setTo(sendToEmail);
            helper.setText(emailBody, true);
            javaMailSender.send(message);
            System.out.println("Email sent.");
        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }

}
