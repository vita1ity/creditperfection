package org.crama.creditperfection.test.builders;

import models.SecurityRole;

public class SecurityRoleBuilder {

	private int id = 1;
	
	private String name = "user";
	
	public SecurityRoleBuilder id(int id) {
		this.id = id;
		return this;
	}
	
	public SecurityRoleBuilder name(String name) {
		this.name = name;
		return this;
	}

	public SecurityRole build() {
		SecurityRole role = new SecurityRole(id, name);
		return role;
	}
	
	
}
