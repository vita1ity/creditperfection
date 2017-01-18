package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import errors.ValidationError;
import models.AuthNetAccount;
import play.Configuration;
import repository.AuthNetAccountRepository;

@Singleton
public class AuthNetAccountService {

	@Inject
	private AuthNetAccountRepository authRepository;
	
	@Inject
	private Configuration conf;
	
	public AuthNetAccount getById(long id) {
		return authRepository.getById(id);
	}
	
	public List<AuthNetAccount> getAll() {
		return authRepository.getAll();
	}
	
	public void update(AuthNetAccount account) {
		authRepository.update(account);
	}
	
	public List<ValidationError> validate(AuthNetAccount account) {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();

		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("name", "Please enter Account Name"), account.getName());
		fieldErrorMap.put(new ValidationError("loginId", "Please enter Login ID"), account.getLoginId());
		fieldErrorMap.put(new ValidationError("transactionKey", "Please enter Transaction Key"), account.getTransactionKey());
		if (account.getPriority() == null) {
			fieldErrorMap.put(new ValidationError("priority", "Please enter account priority"), null);
		}
		else {
			fieldErrorMap.put(new ValidationError("priority", "Please enter account priority"), Integer.toString(account.getPriority()));
		}
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		//check priority valid
		if (account.getPriority() != null) {
			List<AuthNetAccount> allAccounts = authRepository.getAll();
			int priority = account.getPriority();
			if (priority <= 0 || priority > allAccounts.size() + 1) {
				errors.add(new ValidationError("priority", "Account priority should be a number from 1 to N (where N is a number of accounts)"));
			}
			for (AuthNetAccount a: allAccounts) {
				if (a.getId() != account.getId() && a.getPriority() == priority) {
					errors.add(new ValidationError("priority", "Account with id: " + a.getId() + " already has a priority " + priority + ". "
							+ "Please change its priority first"));
					break;
				}
			}
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		
		return null;
	}

	public void save(AuthNetAccount account) {
		authRepository.save(account);
		
	}

	public boolean delete(AuthNetAccount account) {
		return authRepository.delete(account);		
	}

	public List<AuthNetAccount> getEnabled() {
		
		return authRepository.getEnabled();
	}

	public void updateInfo(AuthNetAccount accountDB, AuthNetAccount account) {
		accountDB.setDescription(account.getDescription());
		accountDB.setIsEnabled(account.getIsEnabled());
		accountDB.setLoginId(account.getLoginId());
		accountDB.setName(account.getName());
		accountDB.setTransactionKey(account.getTransactionKey());
		accountDB.setPriority(account.getPriority());
	}
	
	public AuthNetAccount getDefaultAccount() {
		
		String loginId = conf.getString("authorise.net.login.id");
		String transactionKey = conf.getString("authorise.net.transaction.key");

		AuthNetAccount defaultAccount = new AuthNetAccount(loginId, transactionKey);

		return defaultAccount;
		
	}
	
	public AuthNetAccount getAccountByPriority(int priority) {
		AuthNetAccount account = authRepository.getAccountByPriority(priority);
		while (!account.getIsEnabled()) {
			account = authRepository.getAccountByPriority(++priority);
		}
		return account;
		
	}
	
	
}
