package modifyaccount;

import authentication.Driver;
import service.AuthService;
import utils.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DriverModifyAccountUI extends ModifyAccountUI {

    private JTextField truckRegNumberField;
    private JTextField truckCapacityField;

    public DriverModifyAccountUI(Driver driver) {
        super(driver);
        initComponents();
    }

    @Override
    protected void initComponents() {

        // Truck Details Panel
        JPanel truckDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        truckRegNumberField = new JTextField(20);
        truckCapacityField = new JTextField(20);
        if (user instanceof Driver) {
            truckRegNumberField.setText(((Driver) user).getTruckRegNumber());
            truckCapacityField.setText(String.valueOf(((Driver) user).getTruckCapacityKg()));
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        truckDetailsPanel.add(new JLabel("Truck Registration Number:"), gbc);
        gbc.gridx = 1;
        truckDetailsPanel.add(truckRegNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        truckDetailsPanel.add(new JLabel("Truck Capacity:"), gbc);
        gbc.gridx = 1;
        truckDetailsPanel.add(truckCapacityField, gbc);

        // Account Details Panel
        JPanel accountDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.anchor = GridBagConstraints.WEST;

        initCommonComponents(accountDetailsPanel, gbc2);

        // Save Button
        JButton saveButton = new JButton("Save Changes");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("Modify info", SwingConstants.CENTER), BorderLayout.NORTH);
        mainPanel.add(truckDetailsPanel, BorderLayout.WEST);
        mainPanel.add(accountDetailsPanel, BorderLayout.EAST);
        addCommonButton(mainPanel, saveButton);
        add(mainPanel);
    }

    @Override
    protected boolean saveSpecificChanges() {
        String truckRegNumber = truckRegNumberField.getText();
        String truckCapacity = truckCapacityField.getText();

        if (user instanceof Driver) {
            ((Driver) user).setTruckRegNumber(truckRegNumber);
            try {
                ((Driver) user).setTruckCapacityKg(Integer.parseInt(truckCapacity));
            } catch (NumberFormatException e) {
                GuiUtil.showErrorMessage(this, "Invalid capacity format.");
                return false;
            }
        }
        return true;
    }
}