
package com.acnovate.audit_manager.service.impl;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String to, String password) {

        if (!isValidEmail(to)) {
            throw new IllegalArgumentException("Invalid email address: " + to);
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to the Audit Management Application!");
        message.setText(constructEmailBody(password));
        emailSender.send(message);
    }

    private String constructEmailBody(String password) {
        return String.format(
                "Dear User,\n\n" +
                        "Welcome to the Audit Management Application!\n\n" +
                        "Your account has been successfully created. Below are your login details:\n\n" +
                        "Password: %s\n\n" +
                        "Please keep your password secure. If you did not request this email, please ignore it.\n\n" +
                        "Best Regards,\n" +
                        "The Audit Management Team",
                password
        );
    }


    private boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }
}
