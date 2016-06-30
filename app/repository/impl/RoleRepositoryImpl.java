package repository.impl;

import javax.inject.Singleton;

import com.avaje.ebean.Model.Finder;

import models.SecurityRole;
import repository.RoleRepository;

@Singleton
public class RoleRepositoryImpl implements RoleRepository {

	private Finder<Long, SecurityRole> find = new Finder<Long, SecurityRole>(SecurityRole.class);
    
	@Override
    public SecurityRole findByName(String name) {
		SecurityRole userRole = find.where().eq("name", name).findUnique();
		return userRole;
	}

	@Override
	public boolean checkIsEmpty() {
		return find.findRowCount() == 0;
	}
	
}
