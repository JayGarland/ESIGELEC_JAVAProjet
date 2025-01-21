package authentication;

import utils.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterUI extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneNumberField;
    private JComboBox<UserRole> roleComboBox;
    private JTextField truckRegNumberField;
    private JTextField truckCapacityField;
    private JButton registerButton;
    private AuthService authService;
    private AuthUI parent;

    public RegisterUI(AuthUI parent) {
        this.authService = new AuthService();
        this.parent = parent;
        setTitle("Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GuiUtil.setFrameSize(this, 500, 400);
        GuiUtil.centerFrame(this);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(emailField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(passwordField, gbc);

        // Phone number field
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(phoneNumberLabel, gbc);

        phoneNumberField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(phoneNumberField, gbc);

        // Role combobox
        JLabel roleLabel = new JLabel("Role:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(roleLabel, gbc);

        roleComboBox = new JComboBox<>(UserRole.values());
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(roleComboBox, gbc);

        // Truck Registration Number Field
        JLabel truckRegNumberLabel = new JLabel("Truck Reg Number (for drivers):");
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(truckRegNumberLabel, gbc);

        truckRegNumberField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(truckRegNumberField, gbc);

        // Truck Capacity Field
        JLabel truckCapacityLabel = new JLabel("Truck Capacity (Kg):");
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(truckCapacityLabel, gbc);

        truckCapacityField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(truckCapacityField, gbc);

        // Register button
        registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        mainPanel.add(registerButton, gbc);

        add(mainPanel);

        // Disable truck fields by default
        truckRegNumberField.setEnabled(false);
        truckCapacityField.setEnabled(false);
        // add visual to remind user these two fields are disabled, like the color of
        // the fields become grey
        truckRegNumberField.setBackground(Color.LIGHT_GRAY);
        truckCapacityField.setBackground(Color.LIGHT_GRAY);
        JLabel truckFieldsLabel = new JLabel(
                "<html>Please note that the fields above are only enabled for Driver role.<br>Other roles will ignore these fields.</html>");
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 7;
        gbc2.gridwidth = 2;
        gbc2.anchor = GridBagConstraints.CENTER;
        mainPanel.add(truckFieldsLabel, gbc2);

        // Enable/disable truck fields based on the selected role
        roleComboBox.addActionListener(e -> {
            UserRole selectedRole = (UserRole) roleComboBox.getSelectedItem();
            boolean isDriver = selectedRole == UserRole.DRIVER;
            truckRegNumberField.setEnabled(isDriver);
            truckCapacityField.setEnabled(isDriver);
            // the fields become white
            truckRegNumberField.setBackground(isDriver ? Color.WHITE : Color.LIGHT_GRAY);
            truckCapacityField.setBackground(isDriver ? Color.WHITE : Color.LIGHT_GRAY);
        });
    }

    private void registerUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String phoneNumber = phoneNumberField.getText();
        UserRole role = (UserRole) roleComboBox.getSelectedItem();
        User newUser = null;
        try {
            if (role == UserRole.DRIVER) {
                String truckRegNumber = truckRegNumberField.getText();
                int truckCapacity = Integer.parseInt(truckCapacityField.getText());
                newUser = new Driver(email, password, phoneNumber, truckRegNumber, truckCapacity);
            } else if (role == UserRole.CUSTOMER) {
                newUser = new Customer(email, password, phoneNumber);
            } else if (role == UserRole.SCHEDULER) {
                newUser = new Scheduler(email, password, phoneNumber);
            }
        } catch (NumberFormatException e) {
            GuiUtil.showErrorMessage(this, "Invalid truck capacity, please insert a valid number");
            return;
        }

        if (newUser != null && authService.register(newUser)) {
            GuiUtil.showInfoMessage(this, "User Registered Successfully");
            dispose();
            parent.setVisible(true);
        } else {
            GuiUtil.showErrorMessage(this, "Error Registering User");
        }
    }
}