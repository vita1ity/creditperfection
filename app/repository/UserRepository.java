package repository;

import java.util.List;

import com.google.inject.ImplementedBy;

import models.SecurityRole;
import models.User;
import repository.impl.UserRepositoryImpl;

@ImplementedBy(UserRepositoryImpl.class)
public interface UserRepository {

	User findByEmail(String email);
	
	List<User> getAll();

	User getById(long id);
	
	void save(User user);
	
	void update(User user);

	boolean delete(User user);

}
