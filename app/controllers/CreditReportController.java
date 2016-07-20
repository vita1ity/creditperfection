package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import services.UserService;

@Singleton
public class CreditReportController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
	private UserService userService;
	
	public Result processUrl() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		String url = form.get("url");
		
		return ok(views.html.report.render(url));
	}
	
	@Security.Authenticated(Secured.class)
	public Result reportPage() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		
    	//TODO get user report
		/*String memberId = session().get("memberId");
    	JSONResponse response = creditReportService.getReport(memberId);
		
    	if (response instanceof ErrorResponse) {
    		return badRequest(Json.toJson(response));
    	}
    	else {
    		//TODO
    		//process report
    		CreditReportSuccessResponse reportResponse = (CreditReportSuccessResponse)response;
    		String report = reportResponse.getReportUrl();
    		
    		//do something with report
    	}*/
		
		Logger.info("KBA Questions url: " + user.getKbaQuestions().getUrl());
		
        return ok(views.html.userReport.render(user));
    }
}
