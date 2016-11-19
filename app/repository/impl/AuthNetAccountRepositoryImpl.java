package repository.impl;

import java.util.List;

import javax.inject.Singleton;

import com.avaje.ebean.Model.Finder;

import models.AuthNetAccount;
import repository.AuthNetAccountRepository;

@Singleton
public class AuthNetAccountRepositoryImpl implements AuthNetAccountRepository {

	private Finder<Long, AuthNetAccount> find = new Finder<Long, AuthNetAccount>(AuthNetAccount.class);
	
	@Override
	public AuthNetAccount getById(long id) {
		AuthNetAccount account = find.byId(id);
		return account;
	}

	@Override
	public List<AuthNetAccount> getAll() {
		List<AuthNetAccount> accountList = find.all();
		return accountList;
	}

	@Override
	public void update(AuthNetAccount account) {
		
		account.update();
		
	}

	@Override
	public void save(AuthNetAccount account) {
		account.save();
		
	}

	@Override
	public boolean delete(AuthNetAccount account) {
		return account.delete();		
	}

	@Override
	public List<AuthNetAccount> getEnabled() {
		List<AuthNetAccount> accountList = find.where().eq("isEnabled", true).findList();
		return accountList;
	}

	
	
}
