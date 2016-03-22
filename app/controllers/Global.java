package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;
import javax.inject.Inject;
import io.netty.util.concurrent.Promise;
import models.User;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import play.mvc.*;
import play.libs.ws.*;
import play.libs.F.*;

public class Global extends Controller {

    @Inject
    FormFactory formFactory;
    @Inject
    WSClient ws;

    public Result index(){
        return ok(index.render());
    }

    public Result register() throws Exception {
//        User user = formFactory.form(User.class).bindFromRequest().get();
//        user.token = Tokener.randomString(48);
//        user.save();
//        sendEmailToken(user.email,user.token);
//        flash("message", "Email verification sent");
        if (authorizePayment() && sendPost()){
            flash("message","Registered successfully");

        }else{
            flash("message","Registration failed");
        }
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
            flash("message", "Account verified, please login");
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
            sendEmailPassword(user.email, user.password);
        }
        else {
            flash("message","Could not find account with that email");
        }
        return redirect(routes.Global.index());
    }

    private boolean authorizePayment(){
        return true;
    }

    private boolean sendPost() throws Exception {
        DynamicForm form = formFactory.form().bindFromRequest();
        String url = "https://idcs.idandcredit.com/modal/portal/enroll.php?";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", USER_AGENT);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("partnerCode", "CRDPRF"));
        urlParameters.add(new BasicNameValuePair("partnerPass", "kYmfR5@23"));
        urlParameters.add(new BasicNameValuePair("packageId", "474"));
        urlParameters.add(new BasicNameValuePair("branding", "CRDPRF"));
        urlParameters.add(new BasicNameValuePair("memberId", form.get("email")));
        urlParameters.add(new BasicNameValuePair("firstname", form.get("first_name")));
        urlParameters.add(new BasicNameValuePair("lastname", form.get("last_name")));
        urlParameters.add(new BasicNameValuePair("address", form.get("address")));
        urlParameters.add(new BasicNameValuePair("city", form.get("city")));
        urlParameters.add(new BasicNameValuePair("state", form.get("state")));
        urlParameters.add(new BasicNameValuePair("zip", form.get("zip")));
        urlParameters.add(new BasicNameValuePair("phone", form.get("phone")));
        urlParameters.add(new BasicNameValuePair("email", form.get("email")));
        urlParameters.add(new BasicNameValuePair("password", form.get("password")));
        urlParameters.add(new BasicNameValuePair("action", "CreateDashEnrollment"));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println(result.toString());
        return true;
    }

    private boolean createDashEnrollment() {
        DynamicForm form = formFactory.form().bindFromRequest();

        try {
            String url = "https://idcs.idandcredit.com/modal/portal/enroll.php?";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            StringBuilder builder = new StringBuilder();
            builder.append("partnerCode=CRDPRF");
            builder.append("&partnerPass=kYmfR5@23");
            builder.append("&packageId=474");
            builder.append("&branding=CRDPRF");
            builder.append("&memberId=" + form.get("email"));
            builder.append("&firstname" + form.get("first_name"));
            builder.append("&lastname" + form.get("last_name"));
            builder.append("&address" + form.get("address"));
            builder.append("&city" + form.get("city"));
            builder.append("&state" + form.get("state"));
            builder.append("&zip" + form.get("zip"));
            builder.append("&phone" + form.get("phone"));
            builder.append("&email" + form.get("email"));
            builder.append("&password" + form.get("password"));
            builder.append("&action" + "CreateDashEnrollment");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(builder.toString());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            Logger.error("\nSending 'POST' request to URL : " + url);
            Logger.error("Post parameters : " + builder.toString());
            Logger.error("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            Logger.error(response.toString());
        }catch(Exception e){
            Logger.error(e.toString());
        }
        return true;
    }

}
