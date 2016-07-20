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
import security.Secured;
import services.CreditReportService;

@Singleton
@Security.Authenticated(Secured.class)
public class UserController extends Controller {
	
	@Inject
	private CreditReportService creditReportService;
	
	
	
}
