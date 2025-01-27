package modifyaccount;

import authentication.User;
import service.AuthService;
import utils.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ModifyAccountUI extends JPanel {
    protected User user;
    protected AuthService authService;

    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneNumberField;
    private JTextField emailField;

    public ModifyAccountUI(User user) {
        this.user = user;
        this.authService = new AuthService();
        setLayout(new BorderLayout());
        initComponents();
    }

    protected void initCommonComponents(JPanel panel, GridBagConstraints gbc) {

        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        phoneNumberField = new JTextField(20);
        phoneNumberField.setText(user.getPhoneNumber());
        emailField = new JTextField(20);
        emailField.setText(user.getEmail());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Email Adress:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
    }

    protected void addCommonButton(JPanel panel, JButton saveButton) {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(saveButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
    }

    private void saveChanges() {

        String password = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String phoneNumber = phoneNumberField.getText();

        // validate password
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            GuiUtil.showErrorMessage(this, "Passwords do not match.");
            return;
        }
        if (!password.isEmpty()) {
            user.setPassword(password);
        }
        if (!phoneNumber.equals(user.getPhoneNumber())) {
            user.setPhoneNumber(phoneNumber);
        }
        if (saveSpecificChanges() && authService.updateUser(user)) {
            GuiUtil.showInfoMessage(this, "Account updated Successfully");
        } else {
            GuiUtil.showErrorMessage(this, "Error updating account, please try again");
        }
    }

    protected abstract boolean saveSpecificChanges();

    protected abstract void initComponents();
}