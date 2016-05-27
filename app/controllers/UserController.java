package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.User;
import models.json.CreditReportSuccessResponse;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.CreditReportService;

@Singleton
@Security.Authenticated(Secured.class)
public class UserController extends Controller {
	
	@Inject
	private CreditReportService creditReportService;
	
	public Result userPage() {
		
		String email = session().get("email");
		User user = User.findByEmail(email);
		
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
		
		Logger.info("KBA Questions url: " + user.kbaQuestions.url);
		
        return ok(views.html.user.render(user));
    }
	
}
