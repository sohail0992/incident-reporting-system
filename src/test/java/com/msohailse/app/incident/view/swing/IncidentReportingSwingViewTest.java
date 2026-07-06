package com.msohailse.app.incident.view.swing;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.msohailse.app.incident.controller.IncidentController;
import com.msohailse.app.incident.controller.UserController;

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

}
