package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import errors.ValidationError;
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
	
	public void update(AuthNetAccount account) {
		authRepository.update(account);
	}
	
	public List<ValidationError> validate(AuthNetAccount account) {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();

		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("name", "Please enter Account Name"), account.getName());
		fieldErrorMap.put(new ValidationError("loginId", "Please enter Login ID"), account.getLoginId());
		fieldErrorMap.put(new ValidationError("transactionKey", "Please enter Transaction Key"), account.getTransactionKey());
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		
		return null;
	}
	
}
