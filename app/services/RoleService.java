package services;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.SecurityRole;
import repository.RoleRepository;

@Singleton
public class RoleService {

	@Inject
	private RoleRepository roleRepository;
	
	public SecurityRole findByName(String name) {
		return roleRepository.findByName(name);
	}

	public boolean checkIsEmpty() {
		
		return roleRepository.checkIsEmpty();
	}
	
}
