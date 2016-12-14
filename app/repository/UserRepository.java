package repository;

import java.util.List;

import com.avaje.ebean.PagedList;
import com.google.inject.ImplementedBy;

import forms.UserSearchForm;
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

	PagedList<User> getUsersPage(int page, int pageSize);

	PagedList<User> searchByName(String query, int currentPage, int pageSize);

	PagedList<User> preciseSearch(UserSearchForm searchForm, int currentPage, int pageSize);

	User findByEmailOnlyActive(String email);

}
