package org.crama.creditperfection.test.base;

import static play.inject.Bindings.bind;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.crama.creditperfection.test.builders.UserBuilder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;
import controllers.StartupController;
import models.SecurityRole;
import models.User;
import play.Application;
import play.Mode;
import play.api.mvc.RequestHeader;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;
import security.DeadboltHandlerImpl;
import security.HandlerCacheImpl;


public abstract class ControllerTestBase extends WithApplication {
		
	@Mock
	private StartupController startupControllerMock;
		
	public ControllerTestBase()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = new GuiceApplicationBuilder();
        return builder
            .disable(play.db.DBModule.class)
            .disable(play.api.db.DBModule.class)
            .disable(play.db.ebean.EbeanModule.class)
            .disable(play.api.db.evolutions.EvolutionsModule.class)
            .bindings(bind(play.api.db.DBApi.class).to(DummyScalaDBApi.class))
            .bindings(bind(play.api.db.Database.class).to(DummyScalaDatabase.class))
            .bindings(bind(play.db.Database.class).to(DummyJavaDatabase.class))
            .bindings(bind(play.db.DBApi.class).to(DummyJavaDBApi.class))
            .bindings(bind(DeadboltHandlerImpl.class).to(DummyDeadboltHandlerImpl.class))
            .overrides(bind(StartupController.class).toInstance(startupControllerMock))
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