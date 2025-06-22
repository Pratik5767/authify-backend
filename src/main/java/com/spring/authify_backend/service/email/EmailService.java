package com.spring.authify_backend.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;

	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;

	@Override
	public void sendWelcomeEmail(String toEmail, String name) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Welcome to our platform");
		message.setText("Hello " + name + ", \n\nThanks for registering with us!\n\nRegards, \nAuthify Team");
		javaMailSender.send(message);
	}

//	@Override
//	public void sendResetOtpEmail(String toEmail, String otp) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setFrom(fromEmail);
//		message.setTo(toEmail);
//		message.setSubject("Password Reset OTP");
//		message.setText("Your OTP from resetting your password is: " + otp
//				+ ".\n\nUse this otp to proceed with resetting your password");
//		javaMailSender.send(message);
//	}
//
//	@Override
//	public void sendOtpEmail(String toEmail, String otp) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setFrom(fromEmail);
//		message.setTo(toEmail);
//		message.setSubject("Account Verification OTP");
//		message.setText("Your OTP is " + otp + " verify your account using this OTP.");
//		javaMailSender.send(message);
//	}

	public void sendResetOtpEmail(String toEmail, String otp) throws MessagingException {
		Context context = new Context();
		context.setVariable("email", toEmail);
		context.setVariable("otp", otp);

		String process = templateEngine.process("password-reset-email", context);
		MimeMessage minMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(minMessage);

		helper.setFrom(fromEmail);
		helper.setTo(toEmail);
		helper.setSubject("Forgot your Password?");
		helper.setText(process, true);

		javaMailSender.send(minMessage);
	}

	@Override
	public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
		Context context = new Context();
		context.setVariable("email", toEmail);
		context.setVariable("otp", otp);

		String process = templateEngine.process("verify-email", context);
		MimeMessage minMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(minMessage);

		helper.setFrom(fromEmail);
		helper.setTo(toEmail);
		helper.setSubject("Account Verification OTP");
		helper.setText(process, true);

		javaMailSender.send(minMessage);
	}
}