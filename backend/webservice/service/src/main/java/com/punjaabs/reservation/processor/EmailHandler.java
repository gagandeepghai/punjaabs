package com.punjaabs.reservation.processor;

import java.util.logging.Logger;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import com.punjaabs.reservation.request_response.ReservationRequest;

public class EmailHandler{
	private static Logger log = Logger.getLogger(EmailHandler.class.getName());
	private static final String GMAIL_SMTP_PORT = "587";
	private static final String MAIL_SMTP_PORT = "mail.smtp.port";
	private static final String SMTP_GMAIL_COM = "smtp.gmail.com";
	private static final String MAIL_SMTP_HOST = "mail.smtp.host";
	private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	private static final String FROM_EMAIL = "punjaabsindianrestaurant@gmail.com";
	private static final String FROM_PASSWORD = "djdrerchef";
	
	private static String RESERVATION_EMAIL_SUBJECT = "Punjaabs reservation confirmation";
	private static String RESERVATION_EMAIL_BODY = "Hello USER_NAME! \n \n" +
						"Thank you for choosing to dine with us at Punj aab's restaurant. You are all set. Your reservation for RESERVATION_TIME today is confirmed. " +
						"\n\nYour reservation confirmation number for party size [PARTY_SIZE] is: RESERVATION_CONFIRMATION_NUMBER"+
						"\n\nWe look forward to serving you. Get ready to get pampered."+
						"\n\nSincerely,"+
						"\nPunjaabs Resraurant";
	
	public static void sendEmailForReservationConfirmation(ReservationRequest request, Long reservationId, String numberOfPeople) throws Exception {
		log.info("Sending email with reservation confirmation: " +request.getUserEmail());
		String userEmail = request.getUserEmail();
		
		MimeMessage message = createInitMimeMessage();
		try{
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
			message.setSubject(RESERVATION_EMAIL_SUBJECT);
			
			String reservationConfirmationText = RESERVATION_EMAIL_BODY;
			
			reservationConfirmationText = reservationConfirmationText.replace("USER_NAME", request.getUserName());
			reservationConfirmationText = reservationConfirmationText.replace("RESERVATION_TIME", request.getReservationTime());
			reservationConfirmationText = reservationConfirmationText.replace("PARTY_SIZE", numberOfPeople);
			reservationConfirmationText = reservationConfirmationText.replace("RESERVATION_CONFIRMATION_NUMBER", reservationId.toString());
			
			message.setText(reservationConfirmationText);
			log.info("Sending email from: " +FROM_EMAIL);
			log.info("Sending email to: " +userEmail);
			Transport.send(message);
			log.info("Sent message successfully");
		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	private static MimeMessage createInitMimeMessage()
			throws MessagingException, AddressException {
		Properties properties = System.getProperties();
		properties.put(MAIL_SMTP_AUTH, "true");
		properties.put(MAIL_SMTP_STARTTLS_ENABLE, "true");
		properties.put(MAIL_SMTP_HOST, SMTP_GMAIL_COM);
		properties.put(MAIL_SMTP_PORT, GMAIL_SMTP_PORT);
		
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
			}
		});
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(FROM_EMAIL));
		return message;
	}
}