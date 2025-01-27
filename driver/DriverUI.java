package driver;

import authentication.AuthService;
import authentication.Driver;
import data.Mission;
import service.DriverService;
import utils.GuiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DriverUI extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable assignedMissionsTable;
    private JTable completedMissionsTable;
    private DefaultTableModel assignedMissionsModel;
    private DefaultTableModel completedMissionsModel;
    private Driver driver;
    private DriverService driverService;

    public DriverUI(Driver driver) {
        this.driver = driver;
        this.driverService = new DriverService();
        setTitle("Driver Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GuiUtil.setFrameSize(this, 800, 600);
        GuiUtil.centerFrame(this); // Center the frame
        initComponents();
        loadMissions();
        setVisible(true);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Initialize tables
        assignedMissionsModel = new DefaultTableModel(new Object[] { "ID", "Route", "Status" }, 0);
        assignedMissionsTable = new JTable(assignedMissionsModel);
        completedMissionsModel = new DefaultTableModel(new Object[] { "ID", "Route", "Status" }, 0);
        completedMissionsTable = new JTable(completedMissionsModel);

        // Create Mark as Completed button
        JButton markAsCompleteButton = new JButton("Mark as Completed");
        markAsCompleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markMissionAsComplete();
            }
        });

        // Create Route Details button
        JButton assignedRouteDetailsButton = new JButton("Route Details");
        assignedRouteDetailsButton.addActionListener(e -> showRouteDetails(assignedMissionsTable));

        JButton completedRouteDetailsButton = new JButton("Route Details");
        completedRouteDetailsButton.addActionListener(e -> showRouteDetails(completedMissionsTable));

        // Create panels for each tab
        JPanel assignedMissionsPanel = new JPanel(new BorderLayout());
        assignedMissionsPanel.add(new JScrollPane(assignedMissionsTable), BorderLayout.CENTER);
        JPanel assignedActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        assignedActionsPanel.add(markAsCompleteButton);
        assignedActionsPanel.add(assignedRouteDetailsButton); // Add route details button
        assignedMissionsPanel.add(assignedActionsPanel, BorderLayout.SOUTH);

        JPanel completedMissionsPanel = new JPanel(new BorderLayout());
        completedMissionsPanel.add(new JScrollPane(completedMissionsTable), BorderLayout.CENTER);
        JPanel completedActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        completedActionsPanel.add(completedRouteDetailsButton); // Add route details button
        completedMissionsPanel.add(completedActionsPanel, BorderLayout.SOUTH);

        // Add tabs to the tabbed pane
        tabbedPane.addTab("Assigned Missions", assignedMissionsPanel);
        tabbedPane.addTab("Completed Missions", completedMissionsPanel);

        add(tabbedPane);
    }

    private void showRouteDetails(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int missionId = (int) table.getValueAt(selectedRow, 0);
            Mission mission = driverService.getMissionById(driver.getId(), missionId);
            if (mission != null) {
                new RouteDetailsUI(mission, driver).setVisible(true);
            } else {
                GuiUtil.showErrorMessage(this, "Error fetching mission details.");
            }
        } else {
            GuiUtil.showErrorMessage(this, "Please select a mission to view details.");
        }
    }

    private void loadMissions() {
        loadAssignedMissions();
        loadCompletedMissions();
    }

    private void loadAssignedMissions() {
        assignedMissionsModel.setRowCount(0); // Clear the table
        List<Mission> assignedMissions = driverService.getMissionsByDriverId(driver.getId(), "Assigned");
        if (assignedMissions != null) {
            for (Mission mission : assignedMissions) {
                assignedMissionsModel.addRow(
                        new Object[] { mission.getId(), String.join(" -> ", mission.getRoute()), mission.getStatus() });
            }
        }

    }

    private void loadCompletedMissions() {
        completedMissionsModel.setRowCount(0); // Clear the table
        List<Mission> completedMissions = driverService.getMissionsByDriverId(driver.getId(), "Completed");
        if (completedMissions != null) {
            for (Mission mission : completedMissions) {
                completedMissionsModel.addRow(
                        new Object[] { mission.getId(), String.join(" -> ", mission.getRoute()), mission.getStatus() });
            }
        }
    }

    private void markMissionAsComplete() {
        int selectedRow = assignedMissionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int missionId = (int) assignedMissionsTable.getValueAt(selectedRow, 0);
            String status = (String) assignedMissionsTable.getValueAt(selectedRow, 2);
            if (status.equals("Assigned")) {
                boolean updated = driverService.markMissionAsCompleted(driver.getId(), missionId);
                if (updated) {
                    GuiUtil.showInfoMessage(this, "Mission updated successfully");
                    loadMissions();
                } else {
                    GuiUtil.showErrorMessage(this, "Error updating mission");
                }
            } else {
                GuiUtil.showErrorMessage(this, "Mission already completed");
            }

        } else {
            GuiUtil.showErrorMessage(this, "Please select a mission to mark as completed");
        }
    }

    public static void main(String[] args) {
        // Create a dummy Driver object for testing
        Driver dummyDriver = new Driver("driver@example.com", "password789", "111-222-3333", "ABC-123", 5000);
        AuthService authService = new AuthService();
        if (!authService.register(dummyDriver)) {
            Driver loggedInDriver = (Driver) authService.login(dummyDriver.getEmail(), dummyDriver.getPassword());
            // Launch the DriverUI with the persisted driver
            java.awt.EventQueue.invokeLater(() -> {
                new DriverUI(loggedInDriver);
            });
        } else {
            System.err.println("Error registering driver");
        }
    }
}