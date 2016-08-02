package modules;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;
import security.DeadboltHandlerImpl;
import security.HandlerCacheImpl;

import javax.inject.Singleton;

public class CustomDeadboltHook extends Module {
	@Override
    public Seq<Binding<?>> bindings(final Environment environment,
                                    final Configuration configuration) {
        return seq(bind(DeadboltHandler.class).to(DeadboltHandlerImpl.class).in(Singleton.class),
        		   bind(HandlerCache.class).to(HandlerCacheImpl.class).in(Singleton.class));
    }
}
