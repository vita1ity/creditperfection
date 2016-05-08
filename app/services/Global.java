package services;
import java.util.List;

import com.avaje.ebean.Ebean;

import models.Product;
import models.Role;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

public class Global extends GlobalSettings {
	@SuppressWarnings("unchecked")
	@Override
	    public void onStart(Application app) {
	    	System.out.println("Application startup...");
	    	
	        if (Product.find.findRowCount() == 0) {
	            Ebean.saveAll((List<Product>)Yaml.load("initial-products.yml"));
	        }
	        if (Role.find.findRowCount() == 0) {
	            Ebean.saveAll((List<Role>)Yaml.load("initial-roles.yml"));
	        }
	    }
}
