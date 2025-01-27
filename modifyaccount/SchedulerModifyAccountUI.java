package modifyaccount;

import service.AuthService;
import utils.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SchedulerModifyAccountUI extends ModifyAccountUI {

    public SchedulerModifyAccountUI(authentication.Scheduler scheduler) {
        super(scheduler);
        initComponents();
    }

    @Override
    protected void initComponents() {
        JPanel accountDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.anchor = GridBagConstraints.WEST;
        initCommonComponents(accountDetailsPanel, gbc2);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("Modify Account Details", SwingConstants.CENTER), BorderLayout.NORTH);
        mainPanel.add(accountDetailsPanel, BorderLayout.CENTER);
        // Save Button
        JButton saveButton = new JButton("Save Changes");
        addCommonButton(mainPanel, saveButton);
        add(mainPanel);
    }

    @Override
    protected boolean saveSpecificChanges() {
        return true;
    }
}