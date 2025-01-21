package scheduler;

import authentication.Driver;
import data.Delivery;
import service.SchedulerService;
import utils.GuiUtil;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;

public class SchedulerUI extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable pendingDeliveriesTable;
    private JTable scheduledDeliveriesTable;
    private DefaultTableModel pendingDeliveriesModel;
    private DefaultTableModel scheduledDeliveriesModel;
    private JComboBox<Driver> driverComboBox;
    private JButton assignDriverButton;
    private JFormattedTextField reportDateField;
    private JButton generateReportButton;
    private SchedulerService schedulerService;

    public SchedulerUI() {
        this.schedulerService = new SchedulerService();
        setTitle("Scheduler Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GuiUtil.setFrameSize(this, 1000, 700);
        GuiUtil.centerFrame(this);
        initComponents();
        loadDeliveries();
        setVisible(true);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Initialize tables
        pendingDeliveriesModel = new DefaultTableModel(new Object[] { "ID", "Customer ID", "Delivery Date", "Address" },
                0);
        pendingDeliveriesTable = new JTable(pendingDeliveriesModel);
        scheduledDeliveriesModel = new DefaultTableModel(
                new Object[] { "ID", "Customer ID", "Delivery Date", "Address", "Driver ID" }, 0);
        scheduledDeliveriesTable = new JTable(scheduledDeliveriesModel);

        // Delivery Assignment components
        JPanel assignDriverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        driverComboBox = new JComboBox<>();
        loadDrivers(); // Load the drivers in the dropdown
        assignDriverButton = new JButton("Assign Driver");
        assignDriverButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        assignDriverToDelivery();
                    }
                });
        assignDriverPanel.add(new JLabel("Select Driver:"));
        assignDriverPanel.add(driverComboBox);
        assignDriverPanel.add(assignDriverButton);

        // Report Generation components
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel reportDateLabel = new JLabel("Report Date (yyyy-MM-dd):");
        reportDateField = new JFormattedTextField(createDateFormatter());
        reportDateField.setValue(new Date());

        generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        reportPanel.add(reportDateLabel);
        reportPanel.add(reportDateField);
        reportPanel.add(generateReportButton);

        // Create panels for each tab
        JPanel pendingDeliveriesPanel = new JPanel(new BorderLayout());
        pendingDeliveriesPanel.add(new JScrollPane(pendingDeliveriesTable), BorderLayout.CENTER);
        pendingDeliveriesPanel.add(assignDriverPanel, BorderLayout.SOUTH);

        JPanel scheduledDeliveriesPanel = new JPanel(new BorderLayout());
        scheduledDeliveriesPanel.add(new JScrollPane(scheduledDeliveriesTable), BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(reportPanel, BorderLayout.NORTH);
        // Add tabs to the tabbed pane
        tabbedPane.addTab("Pending Deliveries", pendingDeliveriesPanel);
        tabbedPane.addTab("Scheduled Deliveries", scheduledDeliveriesPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private DateFormatter createDateFormatter() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        return dateFormatter;
    }

    private void loadDeliveries() {
        loadPendingDeliveries();
        loadScheduledDeliveries();
    }

    private void loadDrivers() {
        List<Driver> drivers = schedulerService.getAllDrivers();
        if (drivers != null) {
            for (Driver driver : drivers) {
                driverComboBox.addItem(driver);
            }
        }
    }

    private void loadPendingDeliveries() {
        pendingDeliveriesModel.setRowCount(0); // Clear the table
        List<Delivery> pendingDeliveries = schedulerService.getPendingDeliveries();
        if (pendingDeliveries != null) {
            for (Delivery delivery : pendingDeliveries) {
                pendingDeliveriesModel.addRow(
                        new Object[] {
                                delivery.getId(),
                                delivery.getCustomerId(),
                                delivery.getDeliveryDate(),
                                delivery.getDeliveryAddress()
                        });
            }
        }
    }

    private void loadScheduledDeliveries() {
        scheduledDeliveriesModel.setRowCount(0); // Clear the table
        List<Delivery> scheduledDeliveries = schedulerService.getScheduledDeliveries();
        if (scheduledDeliveries != null) {
            for (Delivery delivery : scheduledDeliveries) {
                scheduledDeliveriesModel.addRow(
                        new Object[] {
                                delivery.getId(),
                                delivery.getCustomerId(),
                                delivery.getDeliveryDate(),
                                delivery.getDeliveryAddress(),
                                delivery.getDriverId()
                        });
            }
        }
    }

    private void assignDriverToDelivery() {
        int selectedRow = pendingDeliveriesTable.getSelectedRow();
        Driver selectedDriver = (Driver) driverComboBox.getSelectedItem();
        if (selectedRow >= 0 && selectedDriver != null) {
            int deliveryId = (int) pendingDeliveriesTable.getValueAt(selectedRow, 0);

            boolean updated = schedulerService.assignDriverToDelivery(deliveryId, selectedDriver.getId());
            if (updated) {
                GuiUtil.showInfoMessage(this, "Driver assigned successfully");
                loadDeliveries(); // Refresh tables
            } else {
                GuiUtil.showErrorMessage(this, "Error assigning driver");
            }
        } else {
            GuiUtil.showErrorMessage(this, "Please select a delivery and a driver");
        }
    }

    private void generateReport() {
        Date reportDate = (Date) reportDateField.getValue();
        if (reportDate == null) {
            GuiUtil.showErrorMessage(this, "Please select a date to generate the report");
            return;
        }

        boolean generated = schedulerService.generateReport(reportDate);

        if (generated) {
            GuiUtil.showInfoMessage(this, "Report generated successfully");
        } else {
            GuiUtil.showErrorMessage(this, "Error generating report");
        }
    }

    public static void main(String[] args) {
        // Launch the SchedulerUI
        java.awt.EventQueue.invokeLater(() -> {
            new SchedulerUI();
        });
    }
}