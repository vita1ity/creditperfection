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

import play.Configuration;

@Singleton
public class MailService {
	
	/*@Inject
    private DefaultApplication app;*/
	
	@Inject
	private Configuration conf;
	
	
    public void sendEmailToken(String email, String token){
    	
        final String url = conf.getString("app.test");
       
        final String username = conf.getString("email.username");
        final String subject = "Credit Perfection Registration";
        final String text = "Click the link below to verify your Credit Perfection account \n\n" + 
        		url + "/registertoken/" + token;
        
        sendEmail(username, email, subject, text);
        
    }

    public void sendEmailPassword(String email, String tempPass){
        final String username = conf.getString("email.username");
        
        final String subject = "Credit Perfection Password";
        final String text = "Please change your password after logging in\n\n" + "Password: " + tempPass;
        
        sendEmail(username, email, subject, text);
       
    }
    
    private void sendEmail(String from, String to, String subject, String body) {
		
    	final String username = conf.getString("email.username");
        final String password = conf.getString("email.password");
    	
    	Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
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
		    Transport.send(msg);
	    } catch (MessagingException e) {
	    	
	    	e.printStackTrace();
	    	
			
		}
	}
}
