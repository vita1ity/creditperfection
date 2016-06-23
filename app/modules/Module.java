package modules;

import com.google.inject.AbstractModule;

import controllers.StartupController;

public class Module extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(StartupController.class).asEagerSingleton();
		
	}

	
	
}
