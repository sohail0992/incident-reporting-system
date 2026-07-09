package com.msohailse.app.incident.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.msohailse.app.incident.controller.IncidentController;
import com.msohailse.app.incident.controller.UserController;
import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.Severity;
import com.msohailse.app.incident.model.Tag;
import com.msohailse.app.incident.model.User;

@RunWith(GUITestRunner.class)
public class IncidentReportingSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private IncidentReportingSwingView view;

	@Mock
	private UserController userController;
	@Mock
	private IncidentController incidentController;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			view = new IncidentReportingSwingView();
			view.setUserController(userController);
			view.setIncidentController(incidentController);
			return view;
		});
		window = new FrameFixture(robot(), view);
		window.show();
		GuiActionRunner.execute(() -> {
			view.toFront();
			view.requestFocusInWindow();
		});
		robot().waitForIdle();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	// ---------------------------------------------------------------
	// Register panel
	// ---------------------------------------------------------------

	@Test
	@GUITest
	public void testRegisterPanelControlsInitialStates() {
		GuiActionRunner.execute(() -> view.showRegisterPanel());
		window.label(JLabelMatcher.withText("First Name").andShowing());
		window.textBox("registerFirstNameTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("Last Name").andShowing());
		window.textBox("registerLastNameTextBox").requireEnabled();
		window.textBox("registerEmailTextBox").requireEnabled();
		window.textBox("registerPasswordTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Register")).requireDisabled();
		window.label("registerErrorLabel").requireText(" ");
	}

	@Test
	public void testShowErrorShouldShowMessageInRegisterErrorLabel() {
		GuiActionRunner.execute(() -> view.showRegisterPanel());
		GuiActionRunner.execute(() -> view.showError("Email already taken"));
		window.label("registerErrorLabel").requireText("Email already taken");
	}

	@Test
	public void testRegisterButtonShouldDelegateToUserControllerRegisterUser() {
		GuiActionRunner.execute(() -> view.showRegisterPanel());
		window.textBox("registerFirstNameTextBox").enterText("M");
		window.textBox("registerLastNameTextBox").enterText("Sohail");
		window.textBox("registerEmailTextBox").enterText("msohail.se@gmail.com");
		window.textBox("registerPasswordTextBox").enterText("Password1");
		window.button("registerButton").click();
		verify(userController).registerUser("M", "Sohail", "msohail.se@gmail.com", "Password1");
	}

	@Test
	public void testRegisterButtonDisabledWhenOnlyFirstNameIsMissing() {
		GuiActionRunner.execute(() -> view.showRegisterPanel());
		// first name left empty
		window.textBox("registerLastNameTextBox").enterText("Sohail");
		window.textBox("registerEmailTextBox").enterText("msohail.se@gmail.com");
		window.textBox("registerPasswordTextBox").enterText("Password1");
		window.button(JButtonMatcher.withText("Register")).requireDisabled();
	}

	// ---------------------------------------------------------------
	// Login panel
	// ---------------------------------------------------------------

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Email").andShowing());
		window.textBox("loginEmailTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("Password").andShowing());
		window.textBox("loginPasswordTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Login")).requireDisabled();
		window.label("loginErrorLabel").requireText(" ");
	}

	@Test
	public void testWhenEitherFieldIsEmptyThenLoginButtonShouldBeDisabled() {
		window.textBox("loginEmailTextBox").enterText("msohail@test.com");
		// password left empty
		window.button(JButtonMatcher.withText("Login")).requireDisabled();
	}

	@Test
	public void testWhenEmailAndPasswordAreValidThenLoginButtonShouldBeEnabled() {
		window.textBox("loginEmailTextBox").enterText("msohail@test.com");
		window.textBox("loginPasswordTextBox").enterText("Password1");
		window.button(JButtonMatcher.withText("Login")).requireEnabled();
	}

	@Test
	public void testWhenEmailIsEmptyButPasswordIsFilledThenLoginButtonShouldBeDisabled() {
		// email left empty
		window.textBox("loginPasswordTextBox").enterText("Password1");
		window.button(JButtonMatcher.withText("Login")).requireDisabled();
	}

	@Test
	public void testShowErrorShouldShowMessageInLoginErrorLabel() {
		GuiActionRunner.execute(() -> view.showError("Invalid credentials"));
		window.label("loginErrorLabel").requireText("Invalid credentials");
	}

	@Test
	public void testLoginButtonShouldDelegateToUserControllerLogin() {
		window.textBox("loginEmailTextBox").enterText("msohail@test.com");
		window.textBox("loginPasswordTextBox").enterText("Password1");
		window.button("loginButton").click();
		verify(userController).login("msohail@test.com", "Password1");
	}

	@Test
	@GUITest
	public void testUserLoggedInShowsMainPanelWithWelcomeMessage() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		GuiActionRunner.execute(() -> view.userLoggedIn(user));
		window.label("welcomeLabel").requireText("Welcome, M!");
	}

	// ---------------------------------------------------------------
	// Incident list panel
	// ---------------------------------------------------------------

	@Test
	@GUITest
	public void testShowAllIncidentsShouldPopulateList() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		Tag tag = new Tag(1, "infra", "infrastructure issues");
		Incident i1 = new Incident(1, "Server down", "DB offline", Severity.HIGH, user, tag);
		Incident i2 = new Incident(2, "Slow query", "Index missing", Severity.LOW, user, tag);
		GuiActionRunner.execute(() -> view.userLoggedIn(user));
		GuiActionRunner.execute(() -> view.showAllIncidents(Arrays.asList(i1, i2)));
		String[] contents = window.list("incidentList").contents();
		assertThat(contents).containsExactly(i1.toString(), i2.toString());
	}

	@Test
	@GUITest
	public void testIncidentAddedShouldAddToList() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		Tag tag = new Tag(1, "infra", "infrastructure issues");
		Incident i1 = new Incident(1, "Server down", "DB offline", Severity.HIGH, user, tag);
		GuiActionRunner.execute(() -> view.userLoggedIn(user));
		GuiActionRunner.execute(() -> view.incidentAdded(i1));
		String[] contents = window.list("incidentList").contents();
		assertThat(contents).containsExactly(i1.toString());
		window.label("welcomeLabel").requireText("Welcome, M!");
	}

	@Test
	@GUITest
	public void testIncidentRemovedShouldRemoveFromList() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		Tag tag = new Tag(1, "infra", "infrastructure issues");
		Incident i1 = new Incident(1, "Server down", "DB offline", Severity.HIGH, user, tag);
		Incident i2 = new Incident(2, "Slow query", "Index missing", Severity.LOW, user, tag);
		GuiActionRunner.execute(() -> view.userLoggedIn(user));
		GuiActionRunner.execute(() -> view.showAllIncidents(Arrays.asList(i1, i2)));
		GuiActionRunner.execute(() -> view.incidentRemoved(i1));
		String[] contents = window.list("incidentList").contents();
		assertThat(contents).containsExactly(i2.toString());
	}

	// ---------------------------------------------------------------
	// Add incident panel
	// ---------------------------------------------------------------

	@Test
	@GUITest
	public void testAddIncidentPanelControlsInitialStates() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		GuiActionRunner.execute(() -> {
			view.userLoggedIn(user);
			view.showAddIncidentPanel();
		});
		window.textBox("incidentTitleTextBox").requireEnabled();
		window.textBox("incidentDescriptionTextBox").requireEnabled();
		window.comboBox("incidentSeverityComboBox").requireEnabled();
		window.textBox("incidentTagTextField").requireEnabled();
		window.label("incidentErrorLabel").requireText(" ");
	}

	@Test
	public void testSubmitIncidentWithEmptyTagShouldShowError() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		GuiActionRunner.execute(() -> {
			view.userLoggedIn(user);
			view.showAddIncidentPanel();
		});
		window.textBox("incidentTitleTextBox").enterText("Server down");
		window.textBox("incidentDescriptionTextBox").enterText("DB offline");
		window.button("submitIncidentButton").click();
		window.label("incidentErrorLabel").requireText("Tag cannot be empty");
	}

	@Test
	public void testSubmitIncidentButtonShouldDelegateToIncidentController() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		GuiActionRunner.execute(() -> {
			view.userLoggedIn(user);
			view.showAddIncidentPanel();
		});
		window.textBox("incidentTitleTextBox").enterText("Server down");
		window.textBox("incidentDescriptionTextBox").enterText("DB offline");
		window.comboBox("incidentSeverityComboBox").selectItem(Pattern.compile("HIGH"));
		window.textBox("incidentTagTextField").enterText("infra");
		window.button("submitIncidentButton").click();
		verify(incidentController, timeout(1000)).reportIncident("Server down", "DB offline", Severity.HIGH, "infra",
				user);
	}

	@Test
	public void testBackButtonShouldReturnToIncidentListPanel() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		GuiActionRunner.execute(() -> {
			view.userLoggedIn(user);
			view.showAddIncidentPanel();
		});
		window.button("incidentBackButton").click();
		window.label("welcomeLabel").requireText("Welcome, M!");
	}

}
