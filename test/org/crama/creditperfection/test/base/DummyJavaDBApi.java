package org.crama.creditperfection.test.base;

import java.util.List;

import play.db.DBApi;
import play.db.Database;

public class DummyJavaDBApi implements DBApi {

	@Override
	public Database getDatabase(String arg0) {
		return new DummyJavaDatabase();
	}

	@Override
	public List<Database> getDatabases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
