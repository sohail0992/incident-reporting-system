package com.msohailse.app.incident.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.msohailse.app.incident.TransactionCode;
import com.msohailse.app.incident.TransactionManager;
import com.msohailse.app.incident.model.User;
import com.msohailse.app.incident.repository.IncidentReportingRepository;
import com.msohailse.app.incident.view.IncidentReportingView;

public class UserControllerTest {

	private static final String EMAIL = "john@example.com";
	private static final String PASSWORD = "SecurePass1";
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Doe";

	@InjectMocks
	private UserController userController;

	@Mock
	private TransactionManager transactionManager;

	@Mock
	private IncidentReportingRepository incidentReportingRepository;

	@Mock
	private IncidentReportingView view;

	private AutoCloseable closeable;

	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		when(transactionManager.doInTransaction(any()))
			.thenAnswer(
				answer((TransactionCode<?> code) -> code.apply(incidentReportingRepository)));
	}

	@After
	public void tearDown() throws Exception {
		closeable.close();
	}

	private User buildUser() {
		User user = new User();
		user.setFirstName(FIRST_NAME);
		user.setLastName(LAST_NAME);
		user.setEmail(EMAIL);
		user.setPassword(PASSWORD);
		return user;
	}

	@Test
	public void testLoginWhenUserNotFoundShowsError() {
		when(incidentReportingRepository.findUserByEmail(EMAIL)).thenReturn(null);

		userController.login(EMAIL, PASSWORD);

		verify(incidentReportingRepository).findUserByEmail(EMAIL);
		verify(view).showError("User not found");
		verify(transactionManager, times(1)).doInTransaction(any());
		verifyNoMoreInteractions(incidentReportingRepository);
		verifyNoMoreInteractions(view);
	}

	@Test
	public void testLoginWhenPasswordWrongShowsError() {
		User user = buildUser();
		when(incidentReportingRepository.findUserByEmail(EMAIL)).thenReturn(user);

		userController.login(EMAIL, "WrongPass99");

		verify(incidentReportingRepository).findUserByEmail(EMAIL);
		verify(view).showError("Invalid password");
		verify(transactionManager, times(1)).doInTransaction(any());
		verifyNoMoreInteractions(incidentReportingRepository);
		verifyNoMoreInteractions(view);
	}

	@Test
	public void testLoginWhenValidCallsOnLoginSuccess() {
		User user = buildUser();
		when(incidentReportingRepository.findUserByEmail(EMAIL)).thenReturn(user);

		userController.login(EMAIL, PASSWORD);

		verify(view).userLoggedIn(user);
		verify(transactionManager, times(1)).doInTransaction(any());
	}

	@Test
	public void testLoginWhenValidVerifiesOrder() {
		User user = buildUser();
		when(incidentReportingRepository.findUserByEmail(EMAIL)).thenReturn(user);

		userController.login(EMAIL, PASSWORD);

		InOrder inOrder = inOrder(incidentReportingRepository, view);
		inOrder.verify(incidentReportingRepository).findUserByEmail(EMAIL);
		inOrder.verify(view).userLoggedIn(user);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testRegisterWhenEmailAlreadyExistsShowsError() {
		when(incidentReportingRepository.findUserByEmail(EMAIL)).thenReturn(buildUser());

		userController.registerUser(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

		verify(incidentReportingRepository).findUserByEmail(EMAIL);
		verify(view).showError("Email already registered");
		verify(transactionManager, times(1)).doInTransaction(any());
		verifyNoMoreInteractions(incidentReportingRepository);
		verifyNoMoreInteractions(view);
	}

	@Test
	public void testRegisterWhenValidSavesUser() {
		when(incidentReportingRepository.findUserByEmail(EMAIL)).thenReturn(null);

		userController.registerUser(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

		InOrder inOrder = inOrder(incidentReportingRepository);
		inOrder.verify(incidentReportingRepository).findUserByEmail(EMAIL);
		inOrder.verify(incidentReportingRepository).save(any(User.class));
		inOrder.verifyNoMoreInteractions();
		verify(transactionManager, times(1)).doInTransaction(any());
	}

	@Test
	public void testRegisterWhenValidDoesNotCallViewError() {
		when(incidentReportingRepository.findUserByEmail(EMAIL)).thenReturn(null);

		userController.registerUser(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

		verifyNoMoreInteractions(view);
	}

	@Test
	public void testRegisterWhenInvalidEmailThrowsFromUserEntity() {
		when(incidentReportingRepository.findUserByEmail("notanemail")).thenReturn(null);

		assertThatThrownBy(() -> userController.registerUser(FIRST_NAME, LAST_NAME, "notanemail", PASSWORD))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid email format");
	}
}
