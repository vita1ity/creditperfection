package controllers;

import com.google.inject.Inject;
import models.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Private extends Controller {

    @Inject
    FormFactory formFactory;


}
