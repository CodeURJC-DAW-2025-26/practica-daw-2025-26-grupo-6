package com.grupo6daw.lcdd_daw.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    
    @Autowired
    private JavaMailSender emailSender;

    public void send(String to, String subject, String htmlBody) throws MessagingException {
        send(to, subject, htmlBody, null);
    }

    public void send(String to, String subject, String htmlBody, Map<String, String> inlines) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        
        helper.setSubject(subject);

        String html =   "<!DOCTYPE html>" +
                        "<html lang=\"es\">" +
                            "<head>" +
                                "<meta charset=\"UTF-8\">" +
                                "<style>" +
                                    "h1,h2,h3,h4,h5,h6 {" +
                                        "color: #2c1607;" +
                                    "}" +
                                    "p {" +
                                        "color: #342c26;" +
                                    "}" +
                                    "span, a {" +
                                        "color: #890f00" +
                                    "}" +
                                "</style>" +
                            "</head>" +
                            "<body style='text-align: center;'>" +
                                htmlBody +
                            "</body>" +
                        "</html>";

        helper.setText(html, true);
        
        if (inlines != null) {
            for (Map.Entry<String, String> entry : inlines.entrySet()) {
                String contentId = entry.getKey();      // id in the html (cid:ID)
                String path = entry.getValue();         // path of the file in resources
                
                helper.addInline(contentId, new ClassPathResource(path));
            }
        }

        emailSender.send(message);
    }
}
