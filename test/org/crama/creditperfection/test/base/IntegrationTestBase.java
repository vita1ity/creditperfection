package org.crama.creditperfection.test.base;

import java.util.Collections;

import org.mockito.Mockito;

import play.Application;
import play.Mode;
import play.api.mvc.RequestHeader;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;


public abstract class IntegrationTestBase extends WithApplication {
	private static final String databaseUrl = "jdbc:mysql://localhost/creditperfection-test";
	
	public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = new GuiceApplicationBuilder();
        return builder
        		.configure("db.default.url", databaseUrl)
                .in(Mode.TEST);                
    }
	
	@Override
    protected Application provideApplication()
    {
		return getBuilder().build();
    }

    @Override
    public void startPlay()
    {
        super.startPlay();
        Http.Context.current.set(new Http.Context(1L,
                                                  Mockito.mock(RequestHeader.class),
                                                  Mockito.mock(Http.Request.class),
                                                  Collections.<String, String>emptyMap(),
                                                  Collections.<String, String>emptyMap(),
                                                  Collections.<String, Object>emptyMap()));        
	     
    }


    public Http.Context context()
    {
        return Http.Context.current.get();
    }
 
}
