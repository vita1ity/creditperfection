package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;
import com.google.inject.Inject;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Global extends Controller {

    @Inject
    FormFactory formFactory;

    public Result index(){
        return ok(index.render());
    }

    public Result register(){
        User user = formFactory.form(User.class).bindFromRequest().get();
        user.active = true;
        user.save();
        flash("message", user.username + " registered successfully - please login");
        return redirect(routes.Global.index());
    }

    public Result login(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String username = form.get("username");
        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user!=null){
            session("username",username);
        } else {
            flash("message","invalid credentials");
        }
        return redirect(routes.Global.index());
    }

    public Result logout(){
        session().clear();
        return redirect(routes.Global.index());
    }
}
