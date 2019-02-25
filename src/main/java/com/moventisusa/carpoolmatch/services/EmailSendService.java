package com.moventisusa.carpoolmatch.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class EmailSendService {

    public static final String CPMATCH_EMAIL_FROM = "Car Pool Match <carpoolmatch@revoo.biz>";
    public static final String CPMATCH_EMAIL_SUBJECT = "You've got a Car Pool Match!";

    private JavaMailSender mailSender;

    @Autowired
    public EmailSendService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void prepareAndSend(String recipient, String sender, String subject, String message) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(sender);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(message);
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException me) {
            // runtime exception; compiler will not force you to handle it
            System.out.println(me.getLocalizedMessage());
            throw me;
        }
    }

}