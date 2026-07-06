package com.msohailse.app.incident.view.swing;

import static org.mockito.Mockito.verify;

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
import com.msohailse.app.incident.model.User;

@RunWith(GUITestRunner.class)
public class IncidentReportingSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private IncidentReportingSwingView view;

	@Mock private UserController userController;
	@Mock private IncidentController incidentController;

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

	@Test @GUITest
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

	// ---------------------------------------------------------------
	// Login panel
	// ---------------------------------------------------------------

	@Test @GUITest
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

	@Test @GUITest
	public void testUserLoggedInShowsMainPanelWithWelcomeMessage() {
		User user = new User(1, "M", "Sohail", "msohail@test.com", "Password1");
		GuiActionRunner.execute(() -> view.userLoggedIn(user));
		window.label("welcomeLabel").requireText("Welcome, M!");
	}

}
