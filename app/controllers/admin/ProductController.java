package controllers.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import forms.ProductForm;
import models.Product;
import models.json.MessageResponse;
import models.json.ObjectCreatedResponse;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.ProductService;

@Singleton
@Restrict(@Group("admin"))
public class ProductController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
	private ProductService productService;
	
	public Result products() {
		
		List<Product> allProducts = productService.getAll();
		return ok(views.html.adminProducts.render(allProducts));
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result addProduct() {
				
		JsonNode json = request().body().asJson();
		
		ProductForm productForm = null;
		try {
			productForm = Json.fromJson(json, ProductForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to Product.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Product")));
		}    	 
	    	
	    List<ValidationError> errors = productForm.validate();	    	
	    if (errors != null) {	    	
	    	return badRequest(Json.toJson(errors));
	    }
	    
	    Product product = productService.createProduct(productForm);
        productService.save(product);
        
	    return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "Product was created successfully", product.getId())));    
	
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result editProduct() {
		
		JsonNode json = request().body().asJson();
		
		ProductForm productForm = null;
		try {
			productForm = Json.fromJson(json, ProductForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to Product.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Product")));
		}
	    	
		List<ValidationError> errors = productForm.validate();		
		if (errors != null) {			
			return badRequest(Json.toJson(errors));
		}
		
		Product product = productService.createProduct(productForm);
		Product productDB = productService.getById(product.getId());
		if (productDB == null) {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Product with id " + product.getId() + " is not found")));
		}
		
		productService.update(product);
			    
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Product was edited successfully")));
		
	}
	
	public Result deleteProduct() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		Product product = productService.getById(id);
		if (product == null) {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Product with id " + id + " is not found")));
		}
		
		boolean deleted = productService.delete(product);
		
		if (deleted) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "Product was deleted successfully")));
		}
		else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Error occured while deleting the Product")));
		}
		
	}


}
