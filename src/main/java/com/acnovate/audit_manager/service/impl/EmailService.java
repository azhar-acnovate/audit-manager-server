
package com.acnovate.audit_manager.service.impl;

import java.io.File;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	protected final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender emailSender;

	// TODO rename method sentEmail to sentEmailForDefaultPassword
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

	public void sendEmailWithAttachment(String to, String subject, String body, File attachment)
			throws MessagingException {

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to.split(","));
		helper.setSubject(subject);
		helper.setText(body, true);

		if (attachment != null && attachment.exists()) {
			helper.addAttachment(attachment.getName(), attachment);
		}

		emailSender.send(message);

		logger.info("Mail sent successfully");
	}

	// TODO rename method constructEmailBody to constructDefaultPasswordBody
	private String constructEmailBody(String password) {
		return String.format("Dear User,\n\n" + "Welcome to the Audit Management Application!\n\n"
				+ "Your account has been successfully created. Below are your login details:\n\n" + "Password: %s\n\n"
				+ "Please keep your password secure. If you did not request this email, please ignore it.\n\n"
				+ "Best Regards,\n" + "The Audit Management Team", password);
	}

	private boolean isValidEmail(String email) {
		EmailValidator validator = EmailValidator.getInstance();
		return validator.isValid(email);
	}
}
