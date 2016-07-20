package org.crama.creditperfection.tests.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.crama.creditperfection.tests.builders.SecurityRoleBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import models.SecurityRole;
import repository.RoleRepository;
import services.RoleService;

public class RoleServiceTest {

	@Inject
	@InjectMocks
	private RoleService roleService;
	
	@Mock
	private RoleRepository roleRepositoryMock;
	
	@Before
	public void setUpFindByName() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testFindByName() {
		
		SecurityRole testRole = new SecurityRoleBuilder().build();
		
		when(roleRepositoryMock.findByName("user")).thenReturn(testRole);
		
		SecurityRole role = roleService.findByName("user");
		assertTrue(role.getId() == 1);
		assertTrue(role.getName().equals("user"));
		
		verify(roleRepositoryMock, times(1)).findByName("user");
		
	}
	
	@Before
	public void setUpCkeckIsEmpty_True() {
	
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCheckIsEmpty_True() {
		
		when(roleRepositoryMock.checkIsEmpty()).thenReturn(true);
		
		boolean isEmpty = roleService.checkIsEmpty();
		
		assertTrue(isEmpty == true);
		
		verify(roleRepositoryMock, times(1)).checkIsEmpty();
		
	}
	
	@Before
	public void setUpCkeckIsEmpty_False() {
	
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCheckIsEmpty_False() {
		
		when(roleRepositoryMock.checkIsEmpty()).thenReturn(false);
		
		boolean isEmpty = roleService.checkIsEmpty();
		
		assertTrue(isEmpty == false);
		
		verify(roleRepositoryMock, times(1)).checkIsEmpty();
		
	}
	
}
