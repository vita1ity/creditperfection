package repository.impl;

import java.util.List;

import javax.inject.Singleton;

import com.avaje.ebean.Model.Finder;
import com.avaje.ebean.PagedList;

import models.User;
import repository.UserRepository;

@Singleton
public class UserRepositoryImpl implements UserRepository {

	private Finder<Long, User> find = new Finder<Long, User>(User.class);
	
	@Override
	public User findByEmail(String email) {
		
		User user = find.where().eq("email", email).findUnique();
		return user;
		
	}
	
	@Override
	public List<User> getAll() {
		List<User> userList = find.all();
		return userList;
	}

	@Override
	public User getById(long id) {
		User user = find.byId(id);
		return user;
	}

	@Override
	public void update(User user) {
		user.updateUserInfo(user);
    	user.save();		
	}

	@Override
	public void save(User user) {
		user.save();
	}

	@Override
	public boolean delete(User user) {
		return user.delete();
		
	}

	@Override
	public PagedList<User> getUsersPage(int page, int pageSize) {
		
		PagedList<User> pagedList = find.findPagedList(page, pageSize);
		
		return pagedList;
	}

	
	
	
	
}
