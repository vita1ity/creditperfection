package org.crama.creditperfection.test.base;

import java.sql.Connection;

import javax.sql.DataSource;

import play.db.ConnectionCallable;
import play.db.ConnectionRunnable;
import play.db.Database;

public class DummyJavaDatabase implements Database {

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
	public DataSource getDataSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void withConnection(ConnectionRunnable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <A> A withConnection(ConnectionCallable<A> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void withConnection(boolean arg0, ConnectionRunnable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <A> A withConnection(boolean arg0, ConnectionCallable<A> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void withTransaction(ConnectionRunnable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <A> A withTransaction(ConnectionCallable<A> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
