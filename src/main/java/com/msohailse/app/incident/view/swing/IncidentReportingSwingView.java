package com.msohailse.app.incident.view.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.msohailse.app.incident.controller.IncidentController;
import com.msohailse.app.incident.controller.UserController;
import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.User;
import com.msohailse.app.incident.model.Severity;
import com.msohailse.app.incident.view.IncidentReportingView;

public class IncidentReportingSwingView extends JFrame implements IncidentReportingView {

	private static final long serialVersionUID = 1L;

	private static final String CARD_LOGIN         = "LOGIN";
	private static final String CARD_REGISTER      = "REGISTER";
	private static final String CARD_INCIDENT_LIST = "INCIDENT_LIST";
	private static final String CARD_ADD_INCIDENT  = "ADD_INCIDENT";

	private CardLayout cardLayout;
	private JPanel rootPanel;
	private JPanel loginPanel;
	private JPanel registerPanel;
	private JPanel incidentListPanel;
	private JPanel addIncidentPanel;

	// login panel
	private JTextField loginEmailTextBox;
	private JPasswordField loginPasswordTextBox;
	private JButton loginButton;
	private JLabel loginErrorLabel;

	// register panel
	private JTextField registerFirstNameTextBox;
	private JTextField registerLastNameTextBox;
	private JTextField registerEmailTextBox;
	private JPasswordField registerPasswordTextBox;
	private JButton registerButton;
	private JLabel registerErrorLabel;

	// main panel
	private JLabel welcomeLabel;
	private JButton addIncidentButton;
	private JList<Incident> incidentList;
	private DefaultListModel<Incident> incidentListModel;
	private JLabel mainErrorLabel;

	// incident creation panel
	private JTextField incidentTitleTextBox;
	private JTextField incidentDescriptionTextBox;
	private JTextField incidentTagTextField;
	private JComboBox<Severity> incidentSeverityComboBox;
	private JLabel incidentErrorLabel;

	private transient UserController userController;
	private transient IncidentController incidentController;
	private transient User loggedInUser;

	public IncidentReportingSwingView() {
		setTitle("Incident Reporting System");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 500);

		cardLayout = new CardLayout();
		rootPanel = new JPanel(cardLayout);
		setContentPane(rootPanel);

		loginPanel = buildLoginPanel();
		registerPanel = buildRegisterPanel();
		incidentListPanel = buildIncidentListPanel();
		addIncidentPanel = buildAddIncidentPanel();

		rootPanel.add(loginPanel, CARD_LOGIN);
		rootPanel.add(registerPanel, CARD_REGISTER);
		rootPanel.add(incidentListPanel, CARD_INCIDENT_LIST);
		rootPanel.add(addIncidentPanel, CARD_ADD_INCIDENT);

		cardLayout.show(rootPanel, CARD_LOGIN);
	}

	public void setUserController(UserController userController) {
		this.userController = userController;
	}

	public void setIncidentController(IncidentController incidentController) {
		this.incidentController = incidentController;
	}

	public DefaultListModel<Incident> getIncidentListModel() {
		return incidentListModel;
	}

	// ---------------------------------------------------------------
	// IncidentReportingView interface
	// ---------------------------------------------------------------

	@Override
	public void showError(String message) {
		loginErrorLabel.setText(message);
		registerErrorLabel.setText(message);
	}

	@Override
	public void userLoggedIn(User user) {
		this.loggedInUser = user;
		welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");
		loginErrorLabel.setText(" ");
		cardLayout.show(rootPanel, CARD_INCIDENT_LIST);
	}

	@Override
	public void showAllIncidents(List<Incident> is) {
		incidentListModel.clear();
		for (Incident i : is) {
			incidentListModel.addElement(i);
		}
	}

	@Override
	public void incidentAdded(Incident incident) {
		incidentListModel.addElement(incident);
		incidentTitleTextBox.setText("");
		incidentDescriptionTextBox.setText("");
		incidentTagTextField.setText("");
		incidentErrorLabel.setText(" ");
		cardLayout.show(rootPanel, CARD_INCIDENT_LIST);
	}

	@Override
	public void incidentRemoved(Incident incident) {
		incidentListModel.removeElement(incident);
	}

	@Override
	public void userRegistered(User user) {
		registerErrorLabel.setText(" ");
		cardLayout.show(rootPanel, CARD_LOGIN);
	}

	// package-private for tests
	void showRegisterPanel() {
		cardLayout.show(rootPanel, CARD_REGISTER);
	}

	void showAddIncidentPanel() {
		cardLayout.show(rootPanel, CARD_ADD_INCIDENT);
	}

	// ---------------------------------------------------------------
	// Panel builders
	// ---------------------------------------------------------------

	private JPanel buildIncidentListPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(8, 8, 8, 8);
		c.weightx = 1.0;
		c.gridx = 0;

		welcomeLabel = new JLabel("Welcome!");
		welcomeLabel.setName("welcomeLabel");
		c.gridy = 0;
		panel.add(welcomeLabel, c);

		addIncidentButton = new JButton("+ Add Incident");
		addIncidentButton.setName("addIncidentButton");
		addIncidentButton.addActionListener(e -> cardLayout.show(rootPanel, CARD_ADD_INCIDENT));
		c.gridy = 1;
		panel.add(addIncidentButton, c);

		incidentListModel = new DefaultListModel<>();
		incidentList = new JList<>(incidentListModel);
		incidentList.setName("incidentList");
		JScrollPane scrollPane = new JScrollPane(incidentList);
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		panel.add(scrollPane, c);

		mainErrorLabel = new JLabel(" ");
		mainErrorLabel.setName("mainErrorLabel");
		mainErrorLabel.setForeground(Color.RED);
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		panel.add(mainErrorLabel, c);
		return panel;
	}

	private JPanel buildLoginPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(8, 8, 8, 8);
		c.weightx = 1.0;

		JPanel loginSection = new JPanel(new GridBagLayout());
		loginSection.setBorder(new TitledBorder("Login"));

		GridBagConstraints lc = new GridBagConstraints();
		lc.fill = GridBagConstraints.HORIZONTAL;
		lc.insets = new Insets(8, 6, 8, 6);

		lc.gridx = 0; lc.gridy = 0; lc.weightx = 0;
		loginSection.add(new JLabel("Email"), lc);
		lc.gridx = 1; lc.weightx = 1.0;
		loginEmailTextBox = new JTextField(20);
		loginEmailTextBox.setName("loginEmailTextBox");
		loginEmailTextBox.setText("");
		loginSection.add(loginEmailTextBox, lc);

		lc.gridx = 0; lc.gridy = 1; lc.weightx = 0;
		loginSection.add(new JLabel("Password"), lc);
		lc.gridx = 1; lc.weightx = 1.0;
		loginPasswordTextBox = new JPasswordField(20);
		loginPasswordTextBox.setName("loginPasswordTextBox");
		loginPasswordTextBox.setText("");
		loginSection.add(loginPasswordTextBox, lc);

		lc.gridx = 0; lc.gridy = 2; lc.gridwidth = 2;
		loginButton = new JButton("Login");
		loginButton.setName("loginButton");
		loginButton.setEnabled(false);
		loginButton.addActionListener(e -> {
			loginErrorLabel.setText(" ");
			userController.login(
				loginEmailTextBox.getText().trim(),
				new String(loginPasswordTextBox.getPassword())
			);
		});
		loginSection.add(loginButton, lc);
		c.gridx = 0; c.gridy = 0;
		panel.add(loginSection, c);

		loginErrorLabel = new JLabel(" ");
		loginErrorLabel.setName("loginErrorLabel");
		loginErrorLabel.setForeground(Color.RED);
		c.gridx = 0; c.gridy = 1;
		panel.add(loginErrorLabel, c);

		JButton switchToRegister = new JButton("Don't have an account? Register");
		switchToRegister.setName("switchToRegisterButton");
		switchToRegister.setBorderPainted(false);
		switchToRegister.setContentAreaFilled(false);
		switchToRegister.addActionListener(e -> {
			loginErrorLabel.setText(" ");
			cardLayout.show(rootPanel, CARD_REGISTER);
		});
		c.gridx = 0; c.gridy = 2;
		panel.add(switchToRegister, c);

		KeyAdapter loginEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				loginButton.setEnabled(
					!loginEmailTextBox.getText().trim().isEmpty() &&
					loginPasswordTextBox.getPassword().length > 0
				);
			}
		};
		loginEmailTextBox.addKeyListener(loginEnabler);
		loginPasswordTextBox.addKeyListener(loginEnabler);
		return panel;
	}

	private JPanel buildRegisterPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(8, 8, 8, 8);
		c.weightx = 1.0;

		JPanel registerSection = new JPanel(new GridBagLayout());
		registerSection.setBorder(new TitledBorder("Register"));

		GridBagConstraints rc = new GridBagConstraints();
		rc.fill = GridBagConstraints.HORIZONTAL;
		rc.insets = new Insets(8, 6, 8, 6);

		rc.gridx = 0; rc.gridy = 0; rc.weightx = 0;
		registerSection.add(new JLabel("First Name"), rc);
		rc.gridx = 1; rc.weightx = 1.0;
		registerFirstNameTextBox = new JTextField(20);
		registerFirstNameTextBox.setName("registerFirstNameTextBox");
		registerSection.add(registerFirstNameTextBox, rc);

		rc.gridx = 0; rc.gridy = 1; rc.weightx = 0;
		registerSection.add(new JLabel("Last Name"), rc);
		rc.gridx = 1; rc.weightx = 1.0;
		registerLastNameTextBox = new JTextField(20);
		registerLastNameTextBox.setName("registerLastNameTextBox");
		registerSection.add(registerLastNameTextBox, rc);

		rc.gridx = 0; rc.gridy = 2; rc.weightx = 0;
		registerSection.add(new JLabel("Email"), rc);
		rc.gridx = 1; rc.weightx = 1.0;
		registerEmailTextBox = new JTextField(20);
		registerEmailTextBox.setName("registerEmailTextBox");
		registerSection.add(registerEmailTextBox, rc);

		rc.gridx = 0; rc.gridy = 3; rc.weightx = 0;
		registerSection.add(new JLabel("Password"), rc);
		rc.gridx = 1; rc.weightx = 1.0;
		registerPasswordTextBox = new JPasswordField(20);
		registerPasswordTextBox.setName("registerPasswordTextBox");
		registerSection.add(registerPasswordTextBox, rc);

		rc.gridx = 0; rc.gridy = 4; rc.gridwidth = 2;
		registerButton = new JButton("Register");
		registerButton.setName("registerButton");
		registerButton.setEnabled(false);
		registerButton.addActionListener(e -> {
			registerErrorLabel.setText(" ");
			userController.registerUser(
				registerFirstNameTextBox.getText().trim(),
				registerLastNameTextBox.getText().trim(),
				registerEmailTextBox.getText().trim(),
				new String(registerPasswordTextBox.getPassword())
			);
		});
		registerSection.add(registerButton, rc);

		c.gridx = 0; c.gridy = 0;
		panel.add(registerSection, c);

		registerErrorLabel = new JLabel(" ");
		registerErrorLabel.setName("registerErrorLabel");
		registerErrorLabel.setForeground(Color.RED);
		c.gridx = 0; c.gridy = 1;
		panel.add(registerErrorLabel, c);

		JButton switchToLogin = new JButton("Already have an account? Sign In");
		switchToLogin.setName("switchToLoginButton");
		switchToLogin.setBorderPainted(false);
		switchToLogin.setContentAreaFilled(false);
		switchToLogin.addActionListener(e -> {
			registerErrorLabel.setText(" ");
			cardLayout.show(rootPanel, CARD_LOGIN);
		});
		c.gridx = 0; c.gridy = 2;
		panel.add(switchToLogin, c);

		KeyAdapter registerEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				registerButton.setEnabled(
					!registerFirstNameTextBox.getText().trim().isEmpty() &&
					!registerLastNameTextBox.getText().trim().isEmpty() &&
					!registerEmailTextBox.getText().trim().isEmpty() &&
					registerPasswordTextBox.getPassword().length > 0
				);
			}
		};
		registerFirstNameTextBox.addKeyListener(registerEnabler);
		registerLastNameTextBox.addKeyListener(registerEnabler);
		registerEmailTextBox.addKeyListener(registerEnabler);
		registerPasswordTextBox.addKeyListener(registerEnabler);

		return panel;
	}

	private JPanel buildAddIncidentPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(8, 8, 8, 8);
		c.weightx = 1.0;
		c.gridx = 0;

		JPanel incidentSection = new JPanel(new GridBagLayout());
		incidentSection.setBorder(new TitledBorder("Create New Incident"));

		GridBagConstraints ic = new GridBagConstraints();
		ic.fill = GridBagConstraints.HORIZONTAL;
		ic.insets = new Insets(8, 6, 8, 6);

		ic.gridx = 0; ic.gridy = 0; ic.weightx = 0;
		incidentSection.add(new JLabel("Title"), ic);
		ic.gridx = 1; ic.weightx = 1.0;
		incidentTitleTextBox = new JTextField(20);
		incidentTitleTextBox.setName("incidentTitleTextBox");
		incidentSection.add(incidentTitleTextBox, ic);

		ic.gridx = 0; ic.gridy = 1; ic.weightx = 0;
		incidentSection.add(new JLabel("Description"), ic);
		ic.gridx = 1; ic.weightx = 1.0;
		incidentDescriptionTextBox = new JTextField(20);
		incidentDescriptionTextBox.setName("incidentDescriptionTextBox");
		incidentSection.add(incidentDescriptionTextBox, ic);

		ic.gridx = 0; ic.gridy = 2; ic.weightx = 0;
		incidentSection.add(new JLabel("Severity"), ic);
		ic.gridx = 1; ic.weightx = 1.0;
		incidentSeverityComboBox = new JComboBox<>(Severity.values());
		incidentSeverityComboBox.setName("incidentSeverityComboBox");
		incidentSection.add(incidentSeverityComboBox, ic);

		ic.gridx = 0; ic.gridy = 3; ic.weightx = 0;
		incidentSection.add(new JLabel("Tag"), ic);
		ic.gridx = 1; ic.weightx = 1.0;
		incidentTagTextField = new JTextField(20);
		incidentTagTextField.setName("incidentTagTextField");
		incidentSection.add(incidentTagTextField, ic);

		ic.gridx = 0; ic.gridy = 4; ic.gridwidth = 2; ic.weightx = 1.0;
		JButton submitIncidentButton = new JButton("Submit");
		submitIncidentButton.setName("submitIncidentButton");
		submitIncidentButton.addActionListener(e -> {
			String tagTitle = incidentTagTextField.getText().trim();
			if (tagTitle.isEmpty()) {
				incidentErrorLabel.setText("Tag cannot be empty");
				return;
			}
			incidentController.reportIncident(
				incidentTitleTextBox.getText().trim(),
				incidentDescriptionTextBox.getText().trim(),
				(Severity) incidentSeverityComboBox.getSelectedItem(),
				tagTitle,
				loggedInUser
			);
		});
		incidentSection.add(submitIncidentButton, ic);

		ic.gridy = 5;
		JButton backButton = new JButton("Back");
		backButton.setName("incidentBackButton");
		backButton.addActionListener(e -> cardLayout.show(rootPanel, CARD_INCIDENT_LIST));
		incidentSection.add(backButton, ic);

		ic.gridy = 6;
		incidentErrorLabel = new JLabel(" ");
		incidentErrorLabel.setName("incidentErrorLabel");
		incidentErrorLabel.setForeground(Color.RED);
		incidentSection.add(incidentErrorLabel, ic);

		c.gridy = 0;
		panel.add(incidentSection, c);

		c.gridy = 1;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new JPanel(), c);

		return panel;
	}
}
