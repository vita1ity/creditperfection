package security;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import be.objectify.deadbolt.java.ConfigKeys;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.cache.HandlerCache;
import services.UserService;

@Singleton
public class HandlerCacheImpl implements HandlerCache {
	
    private final DeadboltHandler defaultHandler;
    private final Map<String, DeadboltHandler> handlers = new HashMap<>();
    
    @Inject
    public HandlerCacheImpl(final ExecutionContextProvider ecProvider, UserService userService) {
        defaultHandler = new DeadboltHandlerImpl(ecProvider, userService);
        
        handlers.put(ConfigKeys.DEFAULT_HANDLER_KEY, defaultHandler);
    }
    

    @Override
    public DeadboltHandler apply(final String key) {
        return handlers.get(key);
    }

    @Override
    public DeadboltHandler get() {
        return defaultHandler;
    }
}