package repository;

import java.util.List;

import com.google.inject.ImplementedBy;

import models.User;
import repository.impl.UserRepositoryImpl;

@ImplementedBy(UserRepositoryImpl.class)
public interface UserRepository {

	User findByEmail(String email);
	
	List<User> getAll();

	User getById(long id);

}
