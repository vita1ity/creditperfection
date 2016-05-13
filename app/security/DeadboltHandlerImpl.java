package security;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.models.Subject;
import models.User;
import play.mvc.Http;
import play.mvc.Result;
import views.html.accessFailed;

public class DeadboltHandlerImpl extends AbstractDeadboltHandler {

	public DeadboltHandlerImpl(ExecutionContextProvider ecProvider) {
		super(ecProvider);
	}

	public CompletionStage<Optional<Result>> beforeAuthCheck(final Http.Context context) {
        return CompletableFuture.completedFuture(Optional.empty());
    }
	
	public CompletionStage<Optional<? extends Subject>> getSubject(final Http.Context context) {
        
		String email = context.session().get("email");
		
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(User.findByEmail(email)),
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
