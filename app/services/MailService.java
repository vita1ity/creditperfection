package services;

import java.time.LocalDate;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import models.User;
import play.Configuration;
import play.Logger;

@Singleton
public class MailService {
	
	
	@Inject
	private Configuration conf;
	
	
    public void sendEmailToken(String email, String token){
    	
        final String url = conf.getString("app.host");
        //final String url = conf.getString("app.localhost");
       
        final String from = conf.getString("email.support");
        final String subject = "Credit Perfection Registration";
        final String text = "Click the link below to verify your Credit Perfection account \n\n" + 
        		url + "/registertoken/" + token;
        
        sendEmail(from, email, subject, text);
        
    }

    public void sendEmailPassword(String email, String tempPass){
        final String from = conf.getString("email.support");
        
        final String subject = "Credit Perfection Password";
        final String text = "Please change your password after logging in\n\n" + "Password: " + tempPass;
        
        sendEmail(from, email, subject, text);
       
    }
    
   public void sendCancelSubscriptionNotification(User user) {
    	final String to = conf.getString("email.support");
    	
        final String subject = "Subscription Cancellation Notification";
        final String text = "User " + user.getFirstName() + " " + user.getLastName() + " with id: " + user.getId()
        		+ " has submitted request about cancellation of his subscription.\n"
        		+ "Please confirm the request in the admin area";
        
        sendEmail(to, to, subject, text);
	}
    
   public void sendTransactionFailedNotification(User user) {
	    final String from = conf.getString("email.support");
   	
        final String subject = "Subscription Cannot be Renewed";
        final String text = "Your subscription at the service https://secure.creditperfection.org/ cannot be renewed due to transaction failure.\n\n "
       		+ "Please verify your credit card information at the service. Your subscription will be resumed ones we are able to process the transaction.\n\n "
       		+ "You can change your credit card information and try to run the transaction again by login in the site.";
        sendEmail(from, user.getEmail(), subject, text);
	}
   
   
    public void sendFreeWeekTrialAccepted(User user, LocalDate endDate) {
	    final String from = conf.getString("email.support");
  	
       final String subject = "One week for FREE";
       final String text = "Congradulations! You have accepted an offer for a FREE week report at the service https://secure.creditperfection.org/.\n\n "
      		+ "The offer will expire on: " + endDate;
		sendEmail(from, user.getEmail(), subject, text);
	}
   
    
    public void sendFreeMonthTrialAccepted(User user, LocalDate endDate) {
		
    	final String from = conf.getString("email.support");
      	
        final String subject = "One month for FREE";
        final String text = "Congradulations! You have accepted an offer for a FREE month report at the service https://secure.creditperfection.org/.\n\n "
       		+ "The offer will expire on: " + endDate;
 		sendEmail(from, user.getEmail(), subject, text);
		
	}
    
	public void sendYearDiscountAccepted(User user, double amount, LocalDate endDate) {
		
    	final String from = conf.getString("email.support");
      	
        final String subject = "Discount for the WHOLE year";
        final String text = "Congradulations! You have beed subscribed for a year discount at the service https://secure.creditperfection.org/. "
        		+ "Starting from the next month you will pay only " + amount + "$ / month\n\n "
       		+ "The offer will expire on: " + endDate;
 		sendEmail(from, user.getEmail(), subject, text);
		
	}
	
	
	public void sendFreeWeekOffer(User user, LocalDate renewFailedDate) {
		
		final String url = conf.getString("app.host");
		//final String url = conf.getString("app.localhost");
		
		final String from = conf.getString("email.support");
      	
        final String subject = "Additional FREE Week Trial Offer";
        final String text = "We would like to inform you that your subscription was failed to renew on " + renewFailedDate +
        		 ".\n In order to use the service you need to confirm credit card details and try to run transaction again. \n\n" + 
        		"Our customers are important for us and we would like to propose you another FREE week, so you can try all the benefits of our service. " + 
                "Don't miss this wonderful opportunity!\n\n " + 
                "Please click on the following link to accept the offer and proceed with subscription renewal: " + url + "/discount/apply/free-week-mail";
 		sendEmail(from, user.getEmail(), subject, text);
	}

	public void sendFreeMonthOffer(User user, LocalDate renewFailedDate) {

		final String url = conf.getString("app.host");
		//final String url = conf.getString("app.localhost");
		
		final String from = conf.getString("email.support");
      	
        final String subject = "FREE Month Service Usage Offer";
        final String text = "We would like to inform you that your subscription was failed to renew on " + renewFailedDate +
        		 ".\n In order to use the service you need to confirm credit card details and try to run transaction again. \n\n" + 
        		"We really want you to stay with us so we would like to propose you one month of FREE usage of our service. " +
                "Whole month credit card reports absolutely FREE of charge!\n\n " + 
                "Please click on the following link to accept the offer and proceed with subscription renewal: " + url + "/discount/apply/free-month-mail";
 		sendEmail(from, user.getEmail(), subject, text);
 		
	}

	public void sendYearDiscountOffer(User user, LocalDate renewFailedDate) {
		
		final String url = conf.getString("app.host");
		//final String url = conf.getString("app.localhost");
		
		final String from = conf.getString("email.support");
      	
        final String subject = "A year service ONLY for 10$ a month";
        final String text = "We would like to inform you that your subscription was failed to renew on " + renewFailedDate +
        		 ".\n In order to use the service you need to confirm credit card details and try to run transaction again. \n\n" + 
        		"We are ready to offer you whole year service usage ONLY for 10$ a month! " + 
        		"Please think twice. This is our last proposition.\n\n " + 
                "Please click on the following link to accept the offer and proceed with subscription renewal: " + url + "/discount/apply/year-discount-mail";
 		sendEmail(from, user.getEmail(), subject, text);
	}

	
	
    
    private void sendEmail(String from, String to, String subject, String body) {
		
    	final String username = conf.getString("email.username");
        final String password = conf.getString("email.password");
    	final String host = conf.getString("email.host");
    	final String port = conf.getString("email.port");
        
    	Properties props = new Properties();
    	props.put("mail.smtp.host", host);
    	props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
       
        
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
		
		
		//create and send message
		MimeMessage msg = new MimeMessage(session);
	    try {
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		    msg.setFrom(new InternetAddress(from));
		    msg.setSubject(subject);
		    msg.setText(body);
		    
		    Logger.info("To: " + to);
		    Logger.info("From: " + from);
		    Logger.info("Subject: " + subject);
		    Logger.info("Body: " + body);
		    
		    Transport.send(msg);
		    
		    Logger.info("Message sent");
		    
	    } catch (MessagingException e) {
	    	
	    	e.printStackTrace();
	    	
			
		}
	}

	

	

	
}
