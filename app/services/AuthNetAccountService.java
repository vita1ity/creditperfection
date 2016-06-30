package services;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.AuthNetAccount;
import repository.AuthNetAccountRepository;

@Singleton
public class AuthNetAccountService {

	@Inject
	private AuthNetAccountRepository authRepository;
	
	public AuthNetAccount getById(long id) {
		return authRepository.getById(id);
	}
	
	public List<AuthNetAccount> getAll() {
		return authRepository.getAll();
	}
	
}
