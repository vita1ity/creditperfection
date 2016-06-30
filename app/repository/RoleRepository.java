package repository;

import com.google.inject.ImplementedBy;

import models.SecurityRole;
import repository.impl.RoleRepositoryImpl;

@ImplementedBy(RoleRepositoryImpl.class)
public interface RoleRepository {

	SecurityRole findByName(String name);

	boolean checkIsEmpty();
	
}
