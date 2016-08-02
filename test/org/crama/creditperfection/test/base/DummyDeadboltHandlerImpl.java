package org.crama.creditperfection.test.base;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import org.crama.creditperfection.test.builders.UserBuilder;

import com.google.inject.Inject;

import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.models.Subject;
import models.SecurityRole;
import models.User;
import play.mvc.Http;
import security.DeadboltHandlerImpl;
import services.UserService;

public class DummyDeadboltHandlerImpl extends DeadboltHandlerImpl {
	private static final String TEST_EMAIL = "test@gmail.com";
	private static User testUser;
	
	@Inject
	public DummyDeadboltHandlerImpl(ExecutionContextProvider ecProvider, UserService userService) {
		super(ecProvider, userService);
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
				 new SecurityRole(2, "admin"));
		testUser = new UserBuilder().email(TEST_EMAIL).id(1).roles(roles).build();
	}

	@Override
	public CompletionStage<Optional<? extends Subject>> getSubject(final Http.Context context) {
        
		return CompletableFuture.supplyAsync(() -> Optional.ofNullable(testUser),
                                             (Executor) executionContextProvider.get());
    }

}
