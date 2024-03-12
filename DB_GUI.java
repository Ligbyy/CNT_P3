
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;

public class DB_GUI extends JFrame{



    // GUI Components
    private JComboBox<String> dbUrlPropertiesSelector = new JComboBox<>(new String[]{"project3.properties"});
    private JComboBox<String> userPropertiesSelector = new JComboBox<>(new String[]{"root.properties", "client1.properties", "client2.properties", "theaccountant.properties"});
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JButton connectButton = new JButton("Connect to Database");
    private JButton disconnectButton = new JButton("Disconnect From Database");
    private JTextField commandField = new JTextField(30);
    private JButton executeButton = new JButton("Execute SQL Command");
    private JButton clearSqlCommandButton = new JButton("Clear SQL Command");
    private JTextArea resultArea = new JTextArea(10, 30);
    private JButton clearResultButton = new JButton("Clear Result Window");
    private JLabel connectionStatusLabel = new JLabel("NO CONNECTION ESTABLISHED");

    // JDBC Components
    private Connection connection = null;
    private Statement statement = null;

    public DB_GUI() {
        super("SQL Client Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        // Initialize GUI and JDBC components
        initializeGUI();
        // Initialize JDBC setup is deferred to the action of the Connect button
    }

    private void initializeGUI() {
        setLayout(new BorderLayout(5, 5));

        // Connection Details Panel
JPanel connectionDetailsPanel = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();
gbc.fill = GridBagConstraints.HORIZONTAL;
gbc.insets = new Insets(5, 5, 5, 5); // Padding between grid cells

// DB URL Properties label and combo box
gbc.gridx = 0;
gbc.gridy = 0;
connectionDetailsPanel.add(new JLabel("DB URL Properties"), gbc);

gbc.gridx = 1;
gbc.gridy = 0;
gbc.weightx = 1.0; // Give extra horizontal space to the combo box
connectionDetailsPanel.add(dbUrlPropertiesSelector, gbc);

// User Properties label and combo box
gbc.gridx = 0;
gbc.gridy = 1;
connectionDetailsPanel.add(new JLabel("User Properties"), gbc);

gbc.gridx = 1;
gbc.gridy = 1;
connectionDetailsPanel.add(userPropertiesSelector, gbc);

// Username label and text field
gbc.gridx = 0;
gbc.gridy = 2;
connectionDetailsPanel.add(new JLabel("Username"), gbc);

gbc.gridx = 1;
gbc.gridy = 2;
connectionDetailsPanel.add(usernameField, gbc);

// Password label and text field
gbc.gridx = 0;
gbc.gridy = 3;
connectionDetailsPanel.add(new JLabel("Password"), gbc);

gbc.gridx = 1;
gbc.gridy = 3;
gbc.weightx = 0; // Reset to default
connectionDetailsPanel.add(passwordField, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();


        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.PAGE_AXIS));
        buttonsPanel.add(connectButton);
        buttonsPanel.add(disconnectButton);
        buttonsPanel.add(clearSqlCommandButton);
        buttonsPanel.add(executeButton);
        buttonsPanel.add(clearResultButton);
        
        ActionListener buttonClickListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (e.getActionCommand()) {
                    case "connect":
                        connectToDatabase("e");//CHANGE THE STRING HERE
                        System.out.println("CONNECT BUTTON WORKS");
                        break;
                    case "disconnect":
                        disconnectFromDatabase();
                        System.out.println("DISCONNECT BUTTON WORKS");
                        break;
                    case "clearSQL":
                        commandField.setText("");
                        System.out.println("CLEARSQL BUTTON WORKS");
                        break;
                    case "execute":
                        executeSQL(commandField.getText());
                        System.out.println("EXECUTE SQL BUTTON WORKS");
                        break;
                    case "clearResult":
                        resultArea.setText("");
                        System.out.println("CLEAR BUTTON WORKS");
                        break;
                    default:
                        
                }
            }
        };

        connectButton.setActionCommand("connect");
        disconnectButton.setActionCommand("disconnect");
        clearSqlCommandButton.setActionCommand("clearSQL");
        executeButton.setActionCommand("execute");
        clearResultButton.setActionCommand("clearResult");

        connectButton.addActionListener(buttonClickListener);
        disconnectButton.addActionListener(buttonClickListener);
        clearSqlCommandButton.addActionListener(buttonClickListener);
        executeButton.addActionListener(buttonClickListener);
        clearResultButton.addActionListener(buttonClickListener);

        

        // SQL Command Panel
        JPanel commandPanel = new JPanel(new BorderLayout());
        commandPanel.setBorder(BorderFactory.createTitledBorder("Enter An SQL Command"));
        commandPanel.add(commandField, BorderLayout.CENTER);

        // SQL Execution Result Panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("SQL Execution Result Window"));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        resultPanel.add(connectionStatusLabel, BorderLayout.SOUTH);

        // Add sub-panels to the main frame
        add(connectionDetailsPanel, BorderLayout.NORTH);
        add(commandPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.EAST);
        add(resultPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        
    }

    private void connectToDatabase(String propertiesFileName) {
        String dbProperties = (String) dbUrlPropertiesSelector.getSelectedItem();
    String userProperties = (String) userPropertiesSelector.getSelectedItem();
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());

    try {
        // Load database properties
        Properties dbProps = new Properties();
        dbProps.load(DB_GUI.class.getResourceAsStream(dbProperties));

        Properties userProps = new Properties();
        userProps.load(DB_GUI.class.getResourceAsStream(userProperties));

        // Extract database properties
        String url = dbProps.getProperty("db.url");

        // Override user properties if provided through GUI
        if (!username.isEmpty() && !password.isEmpty()) {
            userProps.setProperty("db.user", username);
            userProps.setProperty("db.password", password);
        }

        // Establish connection
        connection = DriverManager.getConnection(url, userProps.getProperty("db.user"), userProps.getProperty("db.password"));
        statement = connection.createStatement();
        connectionStatusLabel.setText("Connected to database");
    } catch (Exception e) {
        e.printStackTrace();
        connectionStatusLabel.setText("Error connecting to database: " + e.getMessage());
    }
    }
    private void disconnectFromDatabase() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            JOptionPane.showMessageDialog(this, "Disconnected from database");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error disconnecting from database: " + e.getMessage());
        }
    }

    private void executeSQL(String sql) {
        try {
            if (sql.trim().toUpperCase().startsWith("SELECT")) {
                ResultSet rs = statement.executeQuery(sql);
                // You'll need to handle ResultSet and display it in resultArea
                // This can get complex based on your result set structure
                resultArea.setText("Query executed. Handle ResultSet display.");
            } else {
                int count = statement.executeUpdate(sql);
                resultArea.setText("Command executed. " + count + " rows affected.");
            }
        } catch (SQLException e) {
            resultArea.setText("Error executing command: " + e.getMessage());
        }
    }
    
}
