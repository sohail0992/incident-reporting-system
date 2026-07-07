package com.msohailse.app.incident.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.msohailse.app.incident.TransactionCode;
import com.msohailse.app.incident.TransactionManager;
import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.Severity;
import com.msohailse.app.incident.model.Tag;
import com.msohailse.app.incident.model.User;
import com.msohailse.app.incident.repository.IncidentReportingRepository;
import com.msohailse.app.incident.view.IncidentReportingView;

public class IncidentControllerTest {

	private static final String TITLE = "Smoke detected";
	private static final String DESCRIPTION = "Second floor near server room";
	private static final Severity SEVERITY = Severity.HIGH;
	private static final String TAG_TITLE = "fire";

	@InjectMocks
	private IncidentController incidentController;

	@Mock
	private TransactionManager transactionManager;

	@Mock
	private IncidentReportingRepository incidentReportingRepository;

	@Mock
	private IncidentReportingView view;

	private AutoCloseable closeable;
	private User loggedInUser;

	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		when(transactionManager.doInTransaction(any()))
			.thenAnswer(
				answer((TransactionCode<?> code) -> code.apply(incidentReportingRepository)));

		loggedInUser = new User();
		loggedInUser.setFirstName("John");
		loggedInUser.setLastName("Doe");
		loggedInUser.setEmail("john@example.com");
		loggedInUser.setPassword("SecurePass1");
	}

	@After
	public void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	public void testReportIncidentWhenTagExistsReusesTagWithoutSaving() {
		Tag existingTag = new Tag(TAG_TITLE);

		when(incidentReportingRepository.findTagByTitle(TAG_TITLE)).thenReturn(existingTag);

		incidentController.reportIncident(TITLE, DESCRIPTION, SEVERITY, TAG_TITLE, loggedInUser);

		verify(incidentReportingRepository).findTagByTitle(TAG_TITLE);
		verify(incidentReportingRepository, never()).save(any(Tag.class));
	}

	@Test
	public void testReportIncidentWhenTagNotFoundCreatesAndSavesTag() {
		when(incidentReportingRepository.findTagByTitle(TAG_TITLE)).thenReturn(null);

		incidentController.reportIncident(TITLE, DESCRIPTION, SEVERITY, TAG_TITLE, loggedInUser);

		InOrder inOrder = inOrder(incidentReportingRepository);
		inOrder.verify(incidentReportingRepository).findTagByTitle(TAG_TITLE);
		inOrder.verify(incidentReportingRepository).save(any(Tag.class));
		inOrder.verify(incidentReportingRepository).save(any(Incident.class));
	}

	@Test
	public void testReportIncidentSavesIncidentWithCorrectFields() {
		Tag existingTag = new Tag(TAG_TITLE);
		when(incidentReportingRepository.findTagByTitle(TAG_TITLE)).thenReturn(existingTag);

		incidentController.reportIncident(TITLE, DESCRIPTION, SEVERITY, TAG_TITLE, loggedInUser);

		ArgumentCaptor<Incident> captor = ArgumentCaptor.forClass(Incident.class);
		verify(incidentReportingRepository).save(captor.capture());
		Incident saved = captor.getValue();
		assertThat(saved.getTitle()).isEqualTo(TITLE);
		assertThat(saved.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(saved.getSeverity()).isEqualTo(SEVERITY);
		assertThat(saved.getReportedBy()).isEqualTo(loggedInUser);
		assertThat(saved.getTag()).isEqualTo(existingTag);
	}

	@Test
	public void testReportIncidentCallsIncidentAddedOnView() {
		Tag existingTag = new Tag(TAG_TITLE);
		when(incidentReportingRepository.findTagByTitle(TAG_TITLE)).thenReturn(existingTag);

		incidentController.reportIncident(TITLE, DESCRIPTION, SEVERITY, TAG_TITLE, loggedInUser);

		verify(view).incidentAdded(any(Incident.class));
		verify(transactionManager, times(1)).doInTransaction(any());
	}

	@Test
	public void testReportIncidentUsesExactlyOneTransaction() {
		when(incidentReportingRepository.findTagByTitle(TAG_TITLE)).thenReturn(null);

		incidentController.reportIncident(TITLE, DESCRIPTION, SEVERITY, TAG_TITLE, loggedInUser);

		verify(transactionManager, times(1)).doInTransaction(any());
	}

	@Test
	public void testReportIncidentWhenTagNotFoundSavesTagWithCorrectTitle() {
		when(incidentReportingRepository.findTagByTitle(TAG_TITLE)).thenReturn(null);

		incidentController.reportIncident(TITLE, DESCRIPTION, SEVERITY, TAG_TITLE, loggedInUser);

		ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
		verify(incidentReportingRepository).save(captor.capture());
		assertThat(captor.getValue().getTagTitle()).isEqualTo(TAG_TITLE);
	}

	@Test
	public void testReportIncidentDoesNotCallViewError() {
		Tag existingTag = new Tag(TAG_TITLE);
		when(incidentReportingRepository.findTagByTitle(TAG_TITLE)).thenReturn(existingTag);

		incidentController.reportIncident(TITLE, DESCRIPTION, SEVERITY, TAG_TITLE, loggedInUser);

		verify(view, never()).showError(any());
	}
}
