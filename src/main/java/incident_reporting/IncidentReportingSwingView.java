package incident_reporting;

import java.awt.CardLayout;
import java.awt.Color;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class IncidentReportingSwingView extends JFrame implements IncidentReportingView {

	private static final long serialVersionUID = 1L;

	// names for the three screens managed by CardLayout
	private static final String CARD_LOGIN    = "LOGIN";
	private static final String CARD_REGISTER = "REGISTER";
	private static final String CARD_MAIN     = "MAIN";
	private static final String CARD_INCIDENT = "INCIDENT";

	private CardLayout cardLayout;
	private JPanel rootPanel;

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

	private transient UserController userController;

	public IncidentReportingSwingView() {
		setTitle("Incident Reporting System");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 360);

		// CardLayout lets us swap between login, register, and main screens
		// without opening a new window
		cardLayout = new CardLayout();
		rootPanel = new JPanel(cardLayout);
		setContentPane(rootPanel);

		rootPanel.add(buildLoginPanel(), CARD_LOGIN);
		rootPanel.add(buildRegisterPanel(), CARD_REGISTER);
		rootPanel.add(buildMainPanel(), CARD_MAIN);
		rootPanel.add(buildIncidentCreationPanel(), CARD_INCIDENT);

		// start on the login screen
		cardLayout.show(rootPanel, CARD_LOGIN);
	}

	public void setUserController(UserController userController) {
		this.userController = userController;
	}

	// ---------------------------------------------------------------
	// IncidentReportingView interface
	// ---------------------------------------------------------------

	@Override
	public void showError(String message) {
		// both labels updated — only the visible panel's label will be seen
		loginErrorLabel.setText(message);
		registerErrorLabel.setText(message);
	}

	@Override
	public void userLoggedIn(User user) {
		welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");
		loginErrorLabel.setText(" ");
		cardLayout.show(rootPanel, CARD_MAIN);
	}

	@Override
	public void showAllIncidents(List<Incident> is) {
	    incidentListModel.clear();
	    for (Incident i : is) { // Using the parameter 'is' here
	        incidentListModel.addElement(i);
	    }
	}

	// ---------------------------------------------------------------
	// Panel builders
	// ---------------------------------------------------------------

	private JPanel buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL; // stretch components to fill the row width
		c.insets = new Insets(4, 4, 4, 4);      // spacing around each component
		c.weightx = 1.0; // each row takes the full horizontal space
		c.gridx = 0;     // single column layout

		welcomeLabel = new JLabel("Welcome!");
		welcomeLabel.setName("welcomeLabel");
		c.gridy = 0; // row 0
		panel.add(welcomeLabel, c);

		addIncidentButton = new JButton("+ Add Incident");
		addIncidentButton.setName("addIncidentButton");
		
		// 
		addIncidentButton.addActionListener(e -> {
			System.out.print("clicked here");
			// clear fields all that are in form maybe call reset
			cardLayout.show(rootPanel, CARD_INCIDENT);
		});
		
		c.gridy = 1; // row 1
		panel.add(addIncidentButton, c);

		incidentListModel = new DefaultListModel<>();
		incidentList = new JList<>(incidentListModel);
		incidentList.setName("incidentList");
		JScrollPane scrollPane = new JScrollPane(incidentList);
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH; // list stretches both horizontally and vertically
		c.weighty = 1.0; // this row takes all the leftover vertical space
		panel.add(scrollPane, c);

		mainErrorLabel = new JLabel(" ");
		mainErrorLabel.setName("mainErrorLabel");
		mainErrorLabel.setForeground(Color.RED);
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL; // back to horizontal only — error label is a fixed height
		c.weighty = 0; // no extra vertical space — sits tight below the list
		panel.add(mainErrorLabel, c);

		return panel;
	}

	private JPanel buildLoginPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4, 4, 4, 4);
		c.weightx = 1.0;

		// inner panel with a visible titled border so it looks like a form section
		JPanel loginSection = new JPanel(new GridBagLayout());
		loginSection.setBorder(new TitledBorder("Login"));

		GridBagConstraints lc = new GridBagConstraints();
		lc.fill = GridBagConstraints.HORIZONTAL;
		lc.insets = new Insets(3, 3, 3, 3);

		// label gets no extra width (weightx=0), field stretches to fill the rest (weightx=1)
		lc.gridx = 0; lc.gridy = 0; lc.weightx = 0;
		loginSection.add(new JLabel("Email"), lc);
		lc.gridx = 1; lc.weightx = 1.0;
		loginEmailTextBox = new JTextField(20); // 20 columns sets a sensible minimum width
		loginEmailTextBox.setName("loginEmailTextBox");
		loginEmailTextBox.setText("sohail1@example.com"); // prefilled for quick testing
		loginSection.add(loginEmailTextBox, lc);

		lc.gridx = 0; lc.gridy = 1; lc.weightx = 0;
		loginSection.add(new JLabel("Password"), lc);
		lc.gridx = 1; lc.weightx = 1.0;
		loginPasswordTextBox = new JPasswordField(20);
		loginPasswordTextBox.setName("loginPasswordTextBox");
		loginPasswordTextBox.setText("SecurePass123"); // prefilled for quick testing
		loginSection.add(loginPasswordTextBox, lc);

		lc.gridx = 0; lc.gridy = 2; lc.gridwidth = 2; // span both columns so button fills the row
		loginButton = new JButton("Login");
		loginButton.setName("loginButton");
		loginButton.setEnabled(false); // enabled by DocumentListener once both fields are filled
		loginButton.addActionListener(e -> {
			loginErrorLabel.setText(" "); // clear any previous error before trying again
			userController.login(
				loginEmailTextBox.getText().trim(),
				new String(loginPasswordTextBox.getPassword())
			);
		});
		loginSection.add(loginButton, lc);

		c.gridx = 0; c.gridy = 0;
		panel.add(loginSection, c);

		// shown in red when login fails (wrong password, user not found, etc.)
		loginErrorLabel = new JLabel(" "); // space so the label keeps its height even when empty
		loginErrorLabel.setName("loginErrorLabel");
		loginErrorLabel.setForeground(Color.RED);
		c.gridx = 0; c.gridy = 1;
		panel.add(loginErrorLabel, c);

		// looks like a link — switches to the register screen without opening a new window
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

		// one KeyAdapter shared across both fields — fires on keyReleased
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
		loginButton.setEnabled(true); // prefilled fields are already valid on startup
		return panel;
	}

	private JPanel buildRegisterPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4, 4, 4, 4);
		c.weightx = 1.0;

		JPanel registerSection = new JPanel(new GridBagLayout());
		registerSection.setBorder(new TitledBorder("Register"));

		GridBagConstraints rc = new GridBagConstraints();
		rc.fill = GridBagConstraints.HORIZONTAL;
		rc.insets = new Insets(3, 3, 3, 3);

		// label column has no extra width, field column takes the rest
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

		rc.gridx = 0; rc.gridy = 4; rc.gridwidth = 2; // span both columns
		registerButton = new JButton("Register");
		registerButton.setName("registerButton");
		registerButton.setEnabled(false); // enabled only when all four fields are filled
		registerButton.addActionListener(e -> {
			registerErrorLabel.setText(" "); // clear any previous error before trying again
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

		// shown in red when registration fails (email already taken, validation error, etc.)
		registerErrorLabel = new JLabel(" ");
		registerErrorLabel.setName("registerErrorLabel");
		registerErrorLabel.setForeground(Color.RED);
		c.gridx = 0; c.gridy = 1;
		panel.add(registerErrorLabel, c);

		// looks like a link — goes back to the login screen
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

		// one KeyAdapter shared across all four fields — fires on keyReleased
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

	private JPanel buildIncidentCreationPanel() {
		// panel for incident
		JPanel incidentPanel = new JPanel(new GridBagLayout());
		// add padding each side to 10
		incidentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// create a grid constraints
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4, 4, 4, 4);
		c.weightx = 1.0;
		
		// add title to label
		JLabel incidentMainLabel = new JLabel("Create New Incident");
		incidentPanel.add(incidentMainLabel);
		
		return incidentPanel;
	}
}
