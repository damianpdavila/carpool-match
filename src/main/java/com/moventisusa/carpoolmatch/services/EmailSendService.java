package com.moventisusa.carpoolmatch.services;

import com.moventisusa.carpoolmatch.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Service
public class EmailSendService {

    public static final String ESS_MATCH_EMAIL_FROM = "Car Pool Match <carpoolmatch@revoo.biz>";
    public static final String ESS_MATCH_EMAIL_SUBJECT = "You've got a Car Pool Match!";
    public static final String ESS_NOTIFY_EMAIL_FROM = "Car Pool Match <carpoolmatch@revoo.biz>";
    public static final String ESS_NOTIFY_EMAIL_SUBJECT = "You have new potential Car Pool Matches!";

    private static String separator = System.getProperty("line.separator");
    public static final String ESS_NOTIFY_EMAIL_MESSAGE = String.join(
            separator,
            "Hi %1$s,",
            " ",
            "There are some new potential matches for you!",
            " ",
            "Log in to the site and click on the 'Match' menu to see any new or updated matches.",
            " ",
            "Good luck!",
            " ",
            " ",
            "IMPORTANT:",
            "This email was sent by Car Pool Match from an unattended email address. Do not reply directly to this email as no one will receive it.",
            "If you need help or have questions, please log in to the site and click on the 'Support' menu."
            );

    private JavaMailSender mailSender;

    private static LocalDateTime lastDailyBroadcast = LocalDateTime.of(1964, Month.MARCH, 29, 00, 00);

    @Autowired
    public EmailSendService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send email to single recipient.
     * @param recipient Email, optionally formatted, of the recipient. Optional format is "Name <email address>"
     * @param sender  Email, optionally formatted, of the sender. Optional format is "Name <email address>"
     * @param subject  Subject line
     * @param message  Body of email.
     */
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
    /**
     * This method is used for broadcast messages(needs to be refactored and wrapped with sendBroadcast() or something similar).
     * Only send broadcast messages once per day to avoid inundating users with emails.
     * @param recipients User objects to be sent the message
     * @param sender Email, optionally formatted, of the sender. Optional format is "Name <email address>"
     * @param subject  Subject line
     * @param message  Body of email with formatting placeholder for name, e.g., at least 1 %s or %1$s notation
     */
    @Async("threadPoolTaskExecutor")
    public void prepareAndSendMultiple(List<User> recipients, String sender, String subject, String message) {

        if (! okToBroadcast()) return;

        for (User recipient: recipients) {

            try {
                String recipientEmailFormatted = String.join("", recipient.getFullName(), " <", recipient.getEmail(), ">" );
                String messageFormatted = String.format(message, recipient.getFullName());
                prepareAndSend(recipientEmailFormatted, sender, subject, messageFormatted);
                /* Throttle the sending to manage impact to email server */
                Thread.sleep(500L);

            } catch (MailException | InterruptedException me) {
                // runtime exception; compiler will not force you to handle it
                System.out.println(me.getLocalizedMessage());
            }
        }
    }
    private boolean okToBroadcast() {

        LocalDateTime nextBroadcastDayTime = lastDailyBroadcast.plusDays(1);

        if (LocalDateTime.now().isAfter(nextBroadcastDayTime)) {
            lastDailyBroadcast = LocalDateTime.now();
            return true;
        } else {
            return false;
        }
    }

}