package services;

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
	
	/*@Inject
    private DefaultApplication app;*/
	
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
        final String text = "User " + user.firstName + " " + user.lastName + " "
        		+ "has submitted request about cancellation of his subscription.\n"
        		+ "Please confirm the request in the admin area";
        
        sendEmail(user.email, to, subject, text);
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
        /*props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");*/
        
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
