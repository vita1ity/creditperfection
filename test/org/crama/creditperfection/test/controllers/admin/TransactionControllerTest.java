package org.crama.creditperfection.test.controllers.admin;

import org.crama.creditperfection.test.base.UnitTestBase;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import controllers.admin.TransactionController;
import services.MailService;
import services.RoleService;
import services.UserService;

public class TransactionControllerTest extends UnitTestBase {
	
	@Mock
	private UserService userServiceMock;
	
	@Mock
	private MailService mailServiceMock;
	
	@Mock
	private RoleService roleServiceMock;
	
	@InjectMocks
	private TransactionController userController;

}
