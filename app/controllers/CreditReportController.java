package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

@Singleton
public class CreditReportController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	public Result processUrl() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		return ok(views.html.report.render(form.get("url")));
	}

	
}
