package com.bolsadeideas.springboot.blogpost.app.services;

import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		
		mailSender.send(message);
		
	}

	public void sendHtmlEmail(String from, String subject, String body) throws MessagingException {
	    MimeMessage message = mailSender.createMimeMessage();

	    message.setFrom(new InternetAddress(from));
	    message.setRecipients(MimeMessage.RecipientType.TO, from);
	    message.setSubject(subject);

	    String htmlContent = body;
	    message.setContent(htmlContent, "text/html; charset=utf-8");

	    mailSender.send(message);
	}

//	public void sendEmailFromTemplate() throws MessagingException {
//	  MimeMessage message = mailSender.createMimeMessage();
//
//	  message.setFrom(new InternetAddress("sender@example.com"));
//	  message.setRecipients(MimeMessage.RecipientType.TO, "recipient@example.com");
//	  message.setSubject("Test email from my Springapplication");
//
//	  // Read the HTML template into a String variable
//	  String htmlTemplate = readFile("template.html");
//
//	  // Replace placeholders in the HTML template with dynamic values
//	  htmlTemplate = htmlTemplate.replace("${name}", "John Doe");
//	  htmlTemplate = htmlTemplate.replace("${message}", "Hello, this is a test email.");
//
//	  // Set the email's content to be the HTML template
//	  message.setContent(htmlTemplate, "text/html; charset=utf-8");
//
//	  mailSender.send(message);
//	}

}
