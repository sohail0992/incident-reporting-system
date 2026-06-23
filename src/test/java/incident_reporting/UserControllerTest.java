package incident_reporting;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
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

public class UserControllerTest {

	private static final String EMAIL = "john@example.com";
	private static final String PASSWORD = "SecurePass1";
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Doe";

	@InjectMocks
	private UserController userController;

	@Mock
	private UserRepository userRepository;

	@Mock
	private IncidentReportingView view;

	private AutoCloseable closeable;

	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	private User buildUser() {
		User user = new User();
		user.setFirstName(FIRST_NAME);
		user.setLastName(LAST_NAME);
		user.setEmail(EMAIL);
		user.setPassword(PASSWORD);
		return user;
	}

	@After
	public void tearDown() throws Exception {
		closeable.close();
	}

	// login

	@Test
	public void testLoginWhenUserNotFoundShowsError() {
		when(userRepository.findByEmail(EMAIL)).thenReturn(null);

		userController.login(EMAIL, PASSWORD);

		verify(userRepository).findByEmail(EMAIL);
		verify(view).showLoginError("User not found");
		verifyNoMoreInteractions(userRepository);
		verifyNoMoreInteractions(view);
	}

	@Test
	public void testLoginWhenPasswordWrongShowsError() {
		User user = buildUser();
		when(userRepository.findByEmail(EMAIL)).thenReturn(user);

		userController.login(EMAIL, "WrongPass99");

		verify(userRepository).findByEmail(EMAIL);
		verify(view).showLoginError("Invalid password");
		verifyNoMoreInteractions(userRepository);
		verifyNoMoreInteractions(view);
	}

	@Test
	public void testLoginWhenValidCallsOnLoginSuccess() {
		User user = buildUser();
		when(userRepository.findByEmail(EMAIL)).thenReturn(user);

		userController.login(EMAIL, PASSWORD);

		verify(view).onLoginSuccess(user);
	}

	@Test
	public void testLoginWhenValidVerifiesOrder() {
		User user = buildUser();
		when(userRepository.findByEmail(EMAIL)).thenReturn(user);

		userController.login(EMAIL, PASSWORD);

		InOrder inOrder = inOrder(userRepository, view);
		inOrder.verify(userRepository).findByEmail(EMAIL);
		inOrder.verify(view).onLoginSuccess(user);
		inOrder.verifyNoMoreInteractions();
	}

	// register

	@Test
	public void testRegisterWhenEmailAlreadyExistsShowsError() {
		when(userRepository.findByEmail(EMAIL)).thenReturn(buildUser());

		userController.registerUser(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

		verify(userRepository).findByEmail(EMAIL);
		verify(view).showRegistrationError("Email already registered");
		verifyNoMoreInteractions(userRepository);
		verifyNoMoreInteractions(view);
	}

	@Test
	public void testRegisterWhenValidSavesUser() {
		when(userRepository.findByEmail(EMAIL)).thenReturn(null);

		userController.registerUser(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

		InOrder inOrder = inOrder(userRepository);
		inOrder.verify(userRepository).findByEmail(EMAIL);
		inOrder.verify(userRepository).save(org.mockito.ArgumentMatchers.any(User.class));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testRegisterWhenValidDoesNotCallViewError() {
		when(userRepository.findByEmail(EMAIL)).thenReturn(null);

		userController.registerUser(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

		verifyNoMoreInteractions(view);
	}

	@Test
	public void testRegisterWhenInvalidEmailThrowsFromUserEntity() {
		when(userRepository.findByEmail("notanemail")).thenReturn(null);

		assertThatThrownBy(() -> userController.registerUser(FIRST_NAME, LAST_NAME, "notanemail", PASSWORD))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid email format");
	}

}
