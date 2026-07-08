package com.msohailse.app.incident.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.PostgreSQLContainer;

import com.msohailse.app.incident.model.User;

@RunWith(GUITestRunner.class)
public class IncidentReportingSwingAppE2E extends AssertJSwingJUnitTestCase {

	@ClassRule
	public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	private static final String USER_EMAIL = "e2euser@example.com";
	private static final String USER_PASSWORD = "E2ePassword1";

	private EntityManagerFactory emf;

	private FrameFixture window;

	@Override
	protected void onSetUp() {
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", postgres.getJdbcUrl());
		properties.put("javax.persistence.jdbc.user", postgres.getUsername());
		properties.put("javax.persistence.jdbc.password", postgres.getPassword());
		emf = Persistence.createEntityManagerFactory("incident_reporting", properties);

		// We still need direct access to the database to create the context for
		// this test: seed one real user so we can test login, without testing
		// registration again here (already covered by unit/IT/UI tests).
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		User user = new User();
		user.setFirstName("End");
		user.setLastName("ToEnd");
		user.setEmail(USER_EMAIL);
		user.setPassword(USER_PASSWORD);
		em.persist(user);
		em.getTransaction().commit();
		em.close();

		application("com.msohailse.app.incident.app.swing.IncidentReportingSwingApp")
			.withArgs(
				"--db-host=" + postgres.getHost(),
				"--db-port=" + postgres.getFirstMappedPort(),
				"--db-name=" + postgres.getDatabaseName(),
				"--db-user=" + postgres.getUsername(),
				"--db-password=" + postgres.getPassword()
			)
			.start();

		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Incident Reporting System".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Override
	protected void onTearDown() {
		if (emf != null) {
			emf.close();
		}
	}

	@Test @GUITest
	public void testLoginAndReportIncidentEndToEnd() {
		window.textBox("loginEmailTextBox").enterText(USER_EMAIL);
		window.textBox("loginPasswordTextBox").enterText(USER_PASSWORD);
		window.button(JButtonMatcher.withText("Login")).click();

		// login now runs on a background thread before calling back into the
		// view, so wait for the incident list panel to actually appear rather
		// than assuming it's ready as soon as the click returns
		pause(new Condition("incident list panel to be showing") {
			@Override
			public boolean test() {
				try {
					return window.list().target().isShowing();
				} catch (ComponentLookupException notShowingYet) {
					return false;
				}
			}
		}, timeout(5000));
		assertThat(window.list().contents()).isEmpty();

		window.button(JButtonMatcher.withText("+ Add Incident")).click();
		window.textBox("incidentTitleTextBox").enterText("Server room overheating");
		window.textBox("incidentDescriptionTextBox").enterText("Cooling system failure detected");
		window.comboBox("incidentSeverityComboBox").selectItem(Pattern.compile("HIGH"));
		window.textBox("incidentTagTextField").enterText("fire");
		window.button(JButtonMatcher.withText("Submit")).click();

		// same reasoning: reportIncident also runs on a background thread
		// before the new incident reaches the list, so wait for it to appear
		pause(new Condition("new incident to appear in the list") {
			@Override
			public boolean test() {
				try {
					return window.list().contents().length == 1;
				} catch (ComponentLookupException notShowingYet) {
					return false;
				}
			}
		}, timeout(5000));

		// after submitting, the app must have navigated back to the incident
		// list and the new incident must be shown with all its real data,
		// including the severity actually selected in the combo box and the
		// tag that the app created on the fly since it didn't exist yet in
		// the seeded database
		assertThat(window.list().contents())
			.hasSize(1)
			.anySatisfy(e -> assertThat(e)
				.contains("HIGH", "Server room overheating", "Cooling system failure detected", "fire"));
	}
}
