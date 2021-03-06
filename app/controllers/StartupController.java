package controllers;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import com.avaje.ebean.Ebean;

import akka.actor.ActorSystem;
import models.Product;
import models.SecurityRole;
import play.Logger;
import play.libs.Yaml;
import play.mvc.Controller;
import scala.concurrent.duration.Duration;
import scheduler.CreditCardChargeJob;
import services.ProductService;
import services.RoleService;
import services.SubscriptionService;
import services.TransactionService;
import services.UserService;

@Singleton
public class StartupController {

	private ActorSystem system;
	
	private CreditCardChargeJob chargeJob;
	
	private ProductService productService;
	
	private RoleService roleService;
	
	private UserService userService;
	
	private SubscriptionService subscriptionService;
	
	private TransactionService transactionService;
	
	@Inject
	public StartupController(ActorSystem system, CreditCardChargeJob chargeJob, 
			ProductService productService, RoleService roleService, UserService userService, SubscriptionService subscriptionService, TransactionService transactionService) {
		
		Logger.info("Application startup...");
		
		this.system = system;
		this.chargeJob = chargeJob;
		this.productService = productService;
		this.roleService = roleService;
		this.userService = userService;
		this.subscriptionService = subscriptionService;
		this.transactionService = transactionService;
		
		//this.userService.generateUsers(300);
		//this.subscriptionService.generateSubscriptions(250);
		//this.transactionService.generateTransactions(250);
		
		fillDatabase();
		
		createCreditCardChargeJob();
		
		
	}
	
	@SuppressWarnings("unchecked")
	private void fillDatabase() {

        if (productService.checkIsEmpty()) {
        	InputStream is = this.getClass().getClassLoader().getResourceAsStream("initial-products.yml");
        	Ebean.saveAll((List<Product>)Yaml.load(is, this.getClass().getClassLoader()));
            //Ebean.saveAll((List<Product>)Yaml.load("initial-products.yml"));
            Logger.info("Database filled with products");
        }
        if (roleService.checkIsEmpty()) {
        	InputStream is = this.getClass().getClassLoader().getResourceAsStream("initial-roles.yml");
        	Ebean.saveAll((List<SecurityRole>)Yaml.load(is, this.getClass().getClassLoader()));
            //Ebean.saveAll((List<SecurityRole>)Yaml.load("initial-roles.yml"));
            Logger.info("Database filled with roles");
        }
        
	}
	
	private void createCreditCardChargeJob() {
		Logger.info("ActorSystem: " + system);
        
        //run credit card charging job
        system.scheduler().scheduleOnce(
                Duration.create(0, TimeUnit.MILLISECONDS),
                chargeJob, system.dispatcher()
        );

       system.scheduler().schedule(
                Duration.create(nextExecutionInSeconds(0, 0), TimeUnit.SECONDS),
                Duration.create(24, TimeUnit.HOURS),
                chargeJob, system.dispatcher()
        );
        
	}


    private static int nextExecutionInSeconds(int hour, int minute){
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }

    private static DateTime nextExecution(int hour, int minute){
        DateTime next = new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        
        if (next.isBeforeNow()) {
        	next = next.plusHours(24);
        }
        
        Logger.info("next execution: " + next);
        
        return next;
    }
	
}
