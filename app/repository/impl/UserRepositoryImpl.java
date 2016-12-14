package repository.impl;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Singleton;

import com.avaje.ebean.Model.Finder;
import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;

import forms.UserSearchForm;
import models.User;
import models.enums.UserStateFilter;
import play.Logger;
import repository.UserRepository;
import utils.StringConverter;

@Singleton
public class UserRepositoryImpl implements UserRepository {

	private Finder<Long, User> find = new Finder<Long, User>(User.class);
	
	@Override
	public User findByEmail(String email) {
		
		User user = find.where().eq("email", email).findUnique();
		return user;
		
	}
	
	@Override
	public User findByEmailOnlyActive(String email) {
		
		User user = find.where().and(Expr.eq("email", email), Expr.eq("active", true)).findUnique();
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
	

	@Override
	public PagedList<User> searchByName(String query, int currentPage, int pageSize) {
		
		Logger.info("SEARCH BY NAME: ");
		
		String queryStr = "WHERE MATCH (first_name,last_name,email) AGAINST ('" + query + "' IN NATURAL LANGUAGE MODE)";
	    PagedList<User> resultUsers = find.setQuery(queryStr).findPagedList(currentPage, pageSize);
		
	    
	    
		return resultUsers;
	}

	@Override
	public PagedList<User> preciseSearch(UserSearchForm searchForm, int currentPage, int pageSize) {

		Logger.info("PRECISE SEARCH: ");
		
		StringBuilder query = new StringBuilder("where ");
		Field[] allFields = UserSearchForm.class.getDeclaredFields();
	    for (Field field : allFields) {
	    	
	    	field.setAccessible(true);
	    	Object fieldValue = null;
			try {
				fieldValue = field.get(searchForm);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	    	
	    	if (fieldValue != null && !fieldValue.equals("")) {
	    		
	    		String fieldName = field.getName();
		    	String convertedField = StringConverter.convertToDBForm(fieldName);
		    	
		    	if (convertedField.equals("active")) {
		    		UserStateFilter filter = (UserStateFilter)fieldValue;
		    		//only active
		    		if (filter.getValue() == 1) {
		    			query.append(convertedField + " = '1' and");
		    		}
		    		//only inactive
		    		else if (filter.getValue() == 2) {
		    			query.append(convertedField + " = '0' and");
		    		}
		    	}
		    	else if (convertedField.equals("id")) {
		    		query.append(convertedField + " = " + fieldValue.toString() + " and ");
		    	}
		    	else {
		    		query.append(convertedField + " like '%" + fieldValue.toString() + "%' and ");
		    	
		    	}
	    	}
	    	
	    	
	    }
	    
	    if (query.toString().contains("and")) {
	    	query.delete(query.length() - 4, query.length());
	    }
    	Logger.info(query.toString());
	    
	    PagedList<User> resultUsers = find.setQuery(query.toString()).findPagedList(currentPage, pageSize);
		
		return resultUsers;
	}

	
	
	
	
}
