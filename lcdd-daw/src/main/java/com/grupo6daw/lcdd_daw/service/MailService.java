package com.grupo6daw.lcdd_daw.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    
    @Autowired
    private JavaMailSender emailSender;

    Logger logger = LoggerFactory.getLogger(MailService.class);

    private void send(String to, String subject, String htmlBody) throws MessagingException {
        send(to, subject, htmlBody, null);
    }

    private void send(String to, String subject, String htmlBody, Map<String, String> inlines) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        
        helper.setSubject(subject);

        helper.setText(htmlBody, true);
        
        if (inlines != null) {
            for (Map.Entry<String, String> entry : inlines.entrySet()) {
                String contentId = entry.getKey();      // id in the html (cid:ID)
                String path = entry.getValue();         // path of the file in resources
                
                helper.addInline(contentId, new ClassPathResource(path));
            }
        }

        emailSender.send(message);
    }
    
    @Async
    public void sendRegisterEmail(User user) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/register_email.html");
            
            String htmlBody = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            htmlBody = htmlBody
                .replace("{{nickname}}", user.getUserNickname())
                .replace("{{pageUrl}}", "https://localhost:8443");

            Map<String, String> inlines = new HashMap<>();
            inlines.put("logo", "static/img/logo.jpg");
            
            send(user.getUserEmail(), "Registro LCDD", htmlBody, inlines);
        } catch (MessagingException | IOException e) {
            logger.error("No se pudo enviar el correo de registro a: " + user.getUserEmail(), e);
        }
    }
}
