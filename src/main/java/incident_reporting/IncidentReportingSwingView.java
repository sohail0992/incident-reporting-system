package incident_reporting;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
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

public class IncidentReportingSwingView extends JFrame implements IncidentReportingView {

	private static final long serialVersionUID = 1L;

	private static final String CARD_LOGIN = "LOGIN";
	private static final String CARD_MAIN = "MAIN";

	private CardLayout cardLayout;
	private JPanel rootPanel;

	// login panel
	private JTextField loginEmailTextBox;
	private JPasswordField loginPasswordTextBox;
	private JButton loginButton;
	private JTextField registerFirstNameTextBox;
	private JTextField registerLastNameTextBox;
	private JTextField registerEmailTextBox;
	private JPasswordField registerPasswordTextBox;
	private JButton registerButton;
	private JLabel loginErrorLabel;

	// main panel
	private JLabel welcomeLabel;
	private JButton addIncidentButton;
	private JList<Incident> incidentList;
	private DefaultListModel<Incident> incidentListModel;
	private JLabel mainErrorLabel;

	private transient UserController userController;

	public IncidentReportingSwingView() {
		setTitle("Incident Reporting System");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 520);

		cardLayout = new CardLayout();
		rootPanel = new JPanel(cardLayout);
		setContentPane(rootPanel);

		rootPanel.add(buildLoginPanel(), CARD_LOGIN);
		rootPanel.add(buildMainPanel(), CARD_MAIN);

		cardLayout.show(rootPanel, CARD_LOGIN);
	}

	public void setUserController(UserController userController) {
		this.userController = userController;
	}

	// ---------------------------------------------------------------
	// IncidentReportingView interface
	// ---------------------------------------------------------------

	@Override
	public void showLoginError(String message) {
		loginErrorLabel.setText(message);
	}

	@Override
	public void showRegistrationError(String message) {
		loginErrorLabel.setText(message);
	}

	@Override
	public void onLoginSuccess(User user) {
		welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");
		loginErrorLabel.setText("");
		cardLayout.show(rootPanel, CARD_MAIN);
	}

	@Override
	public void showIncidents(List<Incident> incidents) {
		incidentListModel.clear();
		for (Incident i : incidents) {
			incidentListModel.addElement(i);
		}
	}

	// ---------------------------------------------------------------
	// Panel builders
	// ---------------------------------------------------------------

	private JPanel buildLoginPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4, 4, 4, 4);

		// --- Login section ---
		JPanel loginSection = new JPanel(new GridBagLayout());
		loginSection.setBorder(new TitledBorder("Login"));

		GridBagConstraints lc = new GridBagConstraints();
		lc.fill = GridBagConstraints.HORIZONTAL;
		lc.insets = new Insets(3, 3, 3, 3);

		lc.gridx = 0; lc.gridy = 0;
		loginSection.add(new JLabel("Email"), lc);
		lc.gridx = 1;
		loginEmailTextBox = new JTextField();
		loginEmailTextBox.setName("loginEmailTextBox");
		loginSection.add(loginEmailTextBox, lc);

		lc.gridx = 0; lc.gridy = 1;
		loginSection.add(new JLabel("Password"), lc);
		lc.gridx = 1;
		loginPasswordTextBox = new JPasswordField();
		loginPasswordTextBox.setName("loginPasswordTextBox");
		loginSection.add(loginPasswordTextBox, lc);

		lc.gridx = 0; lc.gridy = 2; lc.gridwidth = 2;
		loginButton = new JButton("Login");
		loginButton.setName("loginButton");
		loginButton.setEnabled(false);
		loginSection.add(loginButton, lc);

		c.gridx = 0; c.gridy = 0;
		panel.add(loginSection, c);

		// --- Register section ---
		JPanel registerSection = new JPanel(new GridBagLayout());
		registerSection.setBorder(new TitledBorder("Register"));

		GridBagConstraints rc = new GridBagConstraints();
		rc.fill = GridBagConstraints.HORIZONTAL;
		rc.insets = new Insets(3, 3, 3, 3);

		rc.gridx = 0; rc.gridy = 0;
		registerSection.add(new JLabel("First Name"), rc);
		rc.gridx = 1;
		registerFirstNameTextBox = new JTextField();
		registerFirstNameTextBox.setName("registerFirstNameTextBox");
		registerSection.add(registerFirstNameTextBox, rc);

		rc.gridx = 0; rc.gridy = 1;
		registerSection.add(new JLabel("Last Name"), rc);
		rc.gridx = 1;
		registerLastNameTextBox = new JTextField();
		registerLastNameTextBox.setName("registerLastNameTextBox");
		registerSection.add(registerLastNameTextBox, rc);

		rc.gridx = 0; rc.gridy = 2;
		registerSection.add(new JLabel("Email"), rc);
		rc.gridx = 1;
		registerEmailTextBox = new JTextField();
		registerEmailTextBox.setName("registerEmailTextBox");
		registerSection.add(registerEmailTextBox, rc);

		rc.gridx = 0; rc.gridy = 3;
		registerSection.add(new JLabel("Password"), rc);
		rc.gridx = 1;
		registerPasswordTextBox = new JPasswordField();
		registerPasswordTextBox.setName("registerPasswordTextBox");
		registerSection.add(registerPasswordTextBox, rc);

		rc.gridx = 0; rc.gridy = 4; rc.gridwidth = 2;
		registerButton = new JButton("Register");
		registerButton.setName("registerButton");
		registerButton.setEnabled(false);
		registerSection.add(registerButton, rc);

		c.gridx = 0; c.gridy = 1;
		panel.add(registerSection, c);

		// --- shared error label ---
		loginErrorLabel = new JLabel(" ");
		loginErrorLabel.setName("loginErrorLabel");
		loginErrorLabel.setForeground(java.awt.Color.RED);
		c.gridx = 0; c.gridy = 2;
		panel.add(loginErrorLabel, c);

		// --- wire key listeners ---
		wireLoginEnableDisable();
		wireRegisterEnableDisable();
		wireLoginButton();
		wireRegisterButton();

		return panel;
	}

	private JPanel buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4, 4, 4, 4);

		welcomeLabel = new JLabel("Welcome!");
		welcomeLabel.setName("welcomeLabel");
		c.gridx = 0; c.gridy = 0;
		panel.add(welcomeLabel, c);

		addIncidentButton = new JButton("+ Add Incident");
		addIncidentButton.setName("addIncidentButton");
		c.gridx = 0; c.gridy = 1;
		panel.add(addIncidentButton, c);

		incidentListModel = new DefaultListModel<>();
		incidentList = new JList<>(incidentListModel);
		incidentList.setName("incidentList");
		JScrollPane scrollPane = new JScrollPane(incidentList);
		c.gridx = 0; c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		panel.add(scrollPane, c);

		mainErrorLabel = new JLabel(" ");
		mainErrorLabel.setName("mainErrorLabel");
		mainErrorLabel.setForeground(java.awt.Color.RED);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.gridx = 0; c.gridy = 3;
		panel.add(mainErrorLabel, c);

		return panel;
	}

	// ---------------------------------------------------------------
	// Key listeners for button enable/disable
	// ---------------------------------------------------------------

	private void wireLoginEnableDisable() {
		javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
			public void insertUpdate(javax.swing.event.DocumentEvent e) { updateLoginButton(); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { updateLoginButton(); }
			public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLoginButton(); }
		};
		loginEmailTextBox.getDocument().addDocumentListener(dl);
		loginPasswordTextBox.getDocument().addDocumentListener(dl);
	}

	private void updateLoginButton() {
		loginButton.setEnabled(
			!loginEmailTextBox.getText().trim().isEmpty() &&
			loginPasswordTextBox.getPassword().length > 0
		);
	}

	private void wireRegisterEnableDisable() {
		javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
			public void insertUpdate(javax.swing.event.DocumentEvent e) { updateRegisterButton(); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { updateRegisterButton(); }
			public void changedUpdate(javax.swing.event.DocumentEvent e) { updateRegisterButton(); }
		};
		registerFirstNameTextBox.getDocument().addDocumentListener(dl);
		registerLastNameTextBox.getDocument().addDocumentListener(dl);
		registerEmailTextBox.getDocument().addDocumentListener(dl);
		registerPasswordTextBox.getDocument().addDocumentListener(dl);
	}

	private void updateRegisterButton() {
		registerButton.setEnabled(
			!registerFirstNameTextBox.getText().trim().isEmpty() &&
			!registerLastNameTextBox.getText().trim().isEmpty() &&
			!registerEmailTextBox.getText().trim().isEmpty() &&
			registerPasswordTextBox.getPassword().length > 0
		);
	}

	private void wireLoginButton() {
		loginButton.addActionListener(e -> {
			loginErrorLabel.setText(" ");
			userController.login(
				loginEmailTextBox.getText().trim(),
				new String(loginPasswordTextBox.getPassword())
			);
		});
	}

	private void wireRegisterButton() {
		registerButton.addActionListener(e -> {
			loginErrorLabel.setText(" ");
			userController.registerUser(
				registerFirstNameTextBox.getText().trim(),
				registerLastNameTextBox.getText().trim(),
				registerEmailTextBox.getText().trim(),
				new String(registerPasswordTextBox.getPassword())
			);
		});
	}

}
