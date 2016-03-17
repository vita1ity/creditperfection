package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;
import com.google.inject.Inject;
import models.User;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Tokener;
import views.html.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Global extends Controller {

    @Inject
    FormFactory formFactory;

    public Result index(){
        return ok(index.render());
    }

    public Result register(){
        User user = formFactory.form(User.class).bindFromRequest().get();
        user.token = Tokener.randomString(48);
        user.save();
        sendEmailToken(user.email,user.token);
        flash("message", "Email verification sent");
        return redirect(routes.Global.index());
    }

    public Result login(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        if (user!=null){
            if (user.password.equals(form.get("password")) && user.active == true){
                session("email",email);
                session("name",user.first_name);
            }else{
                sendEmailToken(user.email,user.token);
                flash("message","Account not verified - Email verification sent");
            }
        } else {
            flash("message","invalid credentials");
        }
        return redirect(routes.Global.index());
    }

    public Result logout(){
        session().clear();
        return redirect(routes.Global.index());
    }

    private void sendEmailToken(String email, String token){
        final String username = Play.application().configuration().getString("email.username");
        final String password = Play.application().configuration().getString("email.password");
        final String url = Play.application().configuration().getString("app.test");
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
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Credit Perfection Registration");
            String text = "Click the link below to verify your Credit Perfection account \n\n" + url + "/registertoken/" + token;
            message.setText(text);
            Transport.send(message);
        } catch (MessagingException e) {
            Logger.error("invalid login credentials");
        }
    }

    private void sendEmailPassword(String email, String tempPass){
        final String username = Play.application().configuration().getString("email.username");
        final String password = Play.application().configuration().getString("email.password");
        final String url = Play.application().configuration().getString("app.test");
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
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Credit Perfection Password");
            String text = "Please change your password after logging in\n\n" + "Password: " + tempPass;
            message.setText(text);
            Transport.send(message);
        } catch (MessagingException e) {
            Logger.error("invalid login credentials");
        }
    }

    public Result registerToken(String token){
        User user = Ebean.find(User.class).where().eq("token", token).findUnique();
        if (user!=null){
            user.active = true;
            user.update();
            flash("message","Account verified, please login");
        }else{
            flash("error", "Account not verified, please try again");
        }
        return redirect(routes.Global.index());
    }

    public Result forgotPassword(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email",email).findUnique();
        if (user!=null){
            flash("message", "Password sent to email");
            sendEmailPassword(user.email,user.password);
        }
        else {
            flash("message","Could not find account with that email");
        }
        return redirect(routes.Global.index());
    }
}
