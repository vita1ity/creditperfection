package controllers;

import javax.inject.Singleton;

import play.mvc.Result;
import play.mvc.Controller;

@Singleton
public class PaymentController extends Controller {

	public Result paymentPage() {
		return ok(views.html.paymentPage.render());
	}
	
}
