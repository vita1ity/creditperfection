package modules;

import com.google.inject.AbstractModule;

import controllers.StartupController;
import play.Logger;

public class Module extends AbstractModule {

	@Override
	protected void configure() {
		
		Logger.info("Binding on startup class...");
		
		bind(StartupController.class).asEagerSingleton();
		
	}

	
	
}
