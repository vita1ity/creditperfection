package org.crama.creditperfection.test.base;

import java.sql.Connection;

import javax.sql.DataSource;

import play.api.db.Database;
import scala.Function1;

public class DummyScalaDatabase implements Database {

	@Override
	public DataSource dataSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection getConnection(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String url() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> A withConnection(Function1<Connection, A> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> A withConnection(boolean arg0, Function1<Connection, A> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> A withTransaction(Function1<Connection, A> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
