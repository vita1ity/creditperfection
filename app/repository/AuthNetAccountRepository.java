package repository;

import java.util.List;

import com.google.inject.ImplementedBy;

import models.AuthNetAccount;
import repository.impl.AuthNetAccountRepositoryImpl;

@ImplementedBy(AuthNetAccountRepositoryImpl.class)
public interface AuthNetAccountRepository {

	AuthNetAccount getById(long id);

	List<AuthNetAccount> getAll();

	void update(AuthNetAccount account);

	void save(AuthNetAccount account);

	boolean delete(AuthNetAccount account);

	List<AuthNetAccount> getEnabled();

	AuthNetAccount getAccountByPriority(int priority);

}
