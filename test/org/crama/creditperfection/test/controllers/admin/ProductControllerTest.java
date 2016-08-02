package org.crama.creditperfection.test.controllers.admin;

import static play.inject.Bindings.bind;

import org.crama.creditperfection.test.base.UnitTestBase;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controllers.admin.ProductController;
import play.inject.guice.GuiceApplicationBuilder;
import services.ProductService;

public class ProductControllerTest extends UnitTestBase {
	
	@Mock
	private ProductService productServiceMock;
	
	@InjectMocks
	private ProductController userController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);	
		
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(ProductService.class).toInstance(productServiceMock));
        						         						 
    }

}
