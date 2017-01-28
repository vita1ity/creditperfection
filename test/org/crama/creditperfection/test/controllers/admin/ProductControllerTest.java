package org.crama.creditperfection.test.controllers.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

import java.util.Arrays;
import java.util.List;

import org.crama.creditperfection.test.base.ControllerTestBase;
import org.crama.creditperfection.test.builders.ProductBuilder;
import org.crama.creditperfection.test.builders.ProductFormBuilder;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;

import controllers.admin.ProductController;
import forms.ProductForm;
import models.Product;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import services.ProductService;

public class ProductControllerTest extends ControllerTestBase {
	
	private static Product testProduct;
	private static ProductForm testProductForm;
	
	@Mock
	private ProductService productServiceMock;
	
	@InjectMocks
	private ProductController productController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);			
		
		testProduct = new ProductBuilder().id(55).build();
		testProductForm = new ProductFormBuilder().id("55").build();
		
		List<Product> allProducts = Arrays.asList(
			new ProductBuilder()
				.id(11)
				.name("testProduct1")
				.build(),
			new ProductBuilder()
				.id(12)
				.name("testProduct2")
				.build(),
				testProduct); 
		
		when(productServiceMock.getAll()).thenReturn(allProducts);
		when(productServiceMock.getById(testProduct.getId())).thenReturn(testProduct);
		when(productServiceMock.createProduct(Matchers.any())).thenReturn(testProduct);
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(ProductService.class).toInstance(productServiceMock));        						         						 
    }
	
	@Test
	public void testProducts()	{
		Result result = productController.products();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Add Product"));
		assertTrue(contentAsString(result).contains(testProduct.getName()));
		assertTrue(contentAsString(result).contains(productServiceMock.getAll().get(0).getName()));
		assertTrue(contentAsString(result).contains(productServiceMock.getAll().get(1).getName()));
	}
	
	@Test
	public void testAddProducts_Success() {
		JsonNode productFormJson = Json.toJson(testProductForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(productFormJson)
									.uri(controllers.admin.routes.ProductController.addProduct().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Product was created successfully");
	}
	
	@Test
	public void testAddProduct_Failure_ValidationErrors() {
		testProductForm.setName("Name");
		JsonNode productFormJson = Json.toJson(testProductForm);
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(productFormJson)
									.uri(controllers.admin.routes.ProductController.addProduct().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "name");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Product Name should contain at least 5 characers");
	}
	
	@Test
	public void testAddProduct_Failure_ParsingJson() {
		       
		JsonNode testProductJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testProductJson)
									.uri(controllers.admin.routes.ProductController.addProduct().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to Product");
	}
	
	@Test
	public void testEditProducts_Success() {
		JsonNode productFormJson = Json.toJson(testProductForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(productFormJson)
									.uri(controllers.admin.routes.ProductController.editProduct().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Product was edited successfully");
	}
	
	@Test
	public void testEditProduct_Failure_ValidationErrors() {
		testProductForm.setName("Name");
		JsonNode productFormJson = Json.toJson(testProductForm);
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(productFormJson)
									.uri(controllers.admin.routes.ProductController.editProduct().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "name");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Product Name should contain at least 5 characers");
	}
	
	@Test
	public void testEditProduct_Failure_ParsingJson() {
		       
		JsonNode testProductJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testProductJson)
									.uri(controllers.admin.routes.ProductController.editProduct().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to Product");
	}
	
	@Test
	public void testEditProduct_Failure_ProductNotFound() {
		       
		JsonNode testProductJson = Json.toJson(testProductForm);
		
		when(productServiceMock.getById(testProduct.getId())).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testProductJson)
									.uri(controllers.admin.routes.ProductController.editProduct().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Product with id 55 is not found");
	}
	
	@Test 
	public void testDeleteProduct_Success() {
		when(productServiceMock.delete(Mockito.any())).thenReturn(true);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testProduct.getId())))
				.uri(controllers.admin.routes.ProductController.deleteProduct().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Product was deleted successfully");
	}
	
	@Test 
	public void testDeleteProduct_Failure_ProductNotFound() {
		long id = 456;
		when(productServiceMock.getById(id)).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(id)))
				.uri(controllers.admin.routes.ProductController.deleteProduct().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Product with id " + id + " is not found");
	}
	
	@Test 
	public void testDeleteProduct_Failure() {
		when(productServiceMock.delete(Mockito.any())).thenReturn(false);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testProduct.getId())))
				.uri(controllers.admin.routes.ProductController.deleteProduct().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Error occured while deleting the Product");
	}

	
}
