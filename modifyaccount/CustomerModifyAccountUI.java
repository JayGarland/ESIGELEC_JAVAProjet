package modifyaccount;

import authentication.Customer;
import service.AuthService;
import utils.GuiUtil;

import javax.swing.*;
import java.awt.*;

public class CustomerModifyAccountUI extends ModifyAccountUI {
    private JTextField addresseeField;
    private JTextField deliveryPointField;
    private JTextField streetNameField;
    private JTextField addressLine2Field;
    private JTextField postalCodeCityField;
    private JTextField countryField;

    public CustomerModifyAccountUI(Customer customer) {
        super(customer);
    }

    @Override
    protected void initComponents() {
        // Address Panel
        JPanel addressPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addresseeField = new JTextField(20);
        deliveryPointField = new JTextField(20);
        streetNameField = new JTextField(20);
        addressLine2Field = new JTextField(20);
        postalCodeCityField = new JTextField(20);
        countryField = new JTextField(20);
        countryField.setText("FRA"); // Set the default country code

        // Add components to the address panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        addressPanel.add(new JLabel("Addressee*:"), gbc);
        gbc.gridx = 1;
        addressPanel.add(addresseeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addressPanel.add(new JLabel("Delivery Point Info:"), gbc);
        gbc.gridx = 1;
        addressPanel.add(deliveryPointField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addressPanel.add(new JLabel("Street Name*:"), gbc);
        gbc.gridx = 1;
        addressPanel.add(streetNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addressPanel.add(new JLabel("Address Line 2:"), gbc);
        gbc.gridx = 1;
        addressPanel.add(addressLine2Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addressPanel.add(new JLabel("Postal Code and City*:"), gbc);
        gbc.gridx = 1;
        addressPanel.add(postalCodeCityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        addressPanel.add(new JLabel("Country*(France Only):"), gbc);
        gbc.gridx = 1;
        addressPanel.add(countryField, gbc);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel accountDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.anchor = GridBagConstraints.WEST;
        initCommonComponents(accountDetailsPanel, gbc2);
        mainPanel.add(new JLabel("Modify info", SwingConstants.CENTER), BorderLayout.NORTH);
        mainPanel.add(addressPanel, BorderLayout.WEST);
        mainPanel.add(accountDetailsPanel, BorderLayout.EAST);

        // Save Button
        JButton saveButton = new JButton("Save Changes");

        addCommonButton(mainPanel, saveButton);

        add(mainPanel);
    }

    @Override
    protected boolean saveSpecificChanges() {
        String addressee = addresseeField.getText();
        String deliveryPoint = deliveryPointField.getText();
        String streetName = streetNameField.getText();
        String addressLine2 = addressLine2Field.getText();
        String postalCodeCity = postalCodeCityField.getText();
        String country = countryField.getText();

        // Validate address format
        if (!validateAddressFormat(addressee, deliveryPoint, streetName, addressLine2, postalCodeCity, country)) {
            GuiUtil.showErrorMessage(this, "Invalid address format. Please follow the instructions.");
            return false;
        }
        if (user instanceof Customer) {
            String fullAddress = addressee + ", " + deliveryPoint + ", " + streetName + ", " + addressLine2 + ", "
                    + postalCodeCity + ", " + country;
            ((Customer) user).setAddress(fullAddress);
        }
        return true;
    }

    private boolean validateAddressFormat(String addressee, String deliveryPoint, String streetName,
            String addressLine2, String postalCodeCity, String country) {
        // Implement validation logic based on the provided format
        if (addressee.isEmpty() || streetName.isEmpty() || postalCodeCity.isEmpty() || country.isEmpty()) {
            return false;
        }
        // Check country code
        if (!country.equals("FRA")) {
            return false;
        }
        return true; // If all validations pass
    }
}