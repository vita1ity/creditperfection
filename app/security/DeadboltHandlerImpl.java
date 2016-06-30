package security;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.models.Subject;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import services.UserService;
import views.html.accessFailed;

@Singleton
public class DeadboltHandlerImpl extends AbstractDeadboltHandler {
	
	private UserService userService;
	
	@Inject
	public DeadboltHandlerImpl(ExecutionContextProvider ecProvider, UserService userService) {
		super(ecProvider);
		this.userService = userService;
	}

	public CompletionStage<Optional<Result>> beforeAuthCheck(final Http.Context context) {
        return CompletableFuture.completedFuture(Optional.empty());
    }
	
	public CompletionStage<Optional<? extends Subject>> getSubject(final Http.Context context) {
        
		String email = context.session().get("email");
		
		Logger.info("UserService: " + userService);
		
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(userService.findByEmail(email)),
                                             (Executor) executionContextProvider.get());
    }

	public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context) {
		return CompletableFuture.completedFuture(Optional.empty());
    }

	@Override
    public CompletionStage<Result> onAuthFailure(final Http.Context context, final Optional<String> content) {
        
        return CompletableFuture.completedFuture(ok(accessFailed.render()));
    }
	

}
