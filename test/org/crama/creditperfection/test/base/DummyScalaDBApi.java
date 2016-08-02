package org.crama.creditperfection.test.base;

import play.api.db.DBApi;
import play.api.db.Database;
import scala.collection.Seq;

public class DummyScalaDBApi implements DBApi {

	@Override
	public Database database(String arg0) {
		return new DummyScalaDatabase();
	}

	@Override
	public Seq<Database> databases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
