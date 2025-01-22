package scheduler;

import authentication.Driver;
import data.Delivery;
import data.Mission;
import service.SchedulerService;
import utils.GuiUtil;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ReorderDeliveriesUI extends JFrame {

    private JTable deliveriesTable;
    private DefaultTableModel deliveriesModel;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JButton generateMissionButton;
    private List<Delivery> deliveries;
    private Driver driver;
    private SchedulerService schedulerService;

    public ReorderDeliveriesUI(List<Delivery> deliveries, Driver driver, SchedulerService schedulerService) {
        this.deliveries = deliveries;
        this.driver = driver;
        this.schedulerService = schedulerService;
        setTitle("Reorder Deliveries");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GuiUtil.setFrameSize(this, 800, 600);
        GuiUtil.centerFrame(this);
        initComponents();
        loadDeliveries();
    }

    private void initComponents() {
        deliveriesModel = new DefaultTableModel(new Object[] { "ID", "Customer ID", "Delivery Date", "Address" }, 0);
        deliveriesTable = new JTable(deliveriesModel);
        JScrollPane scrollPane = new JScrollPane(deliveriesTable);

        moveUpButton = new JButton("Move Up");
        moveDownButton = new JButton("Move Down");
        generateMissionButton = new JButton("Generate Mission");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(moveUpButton);
        buttonsPanel.add(moveDownButton);
        buttonsPanel.add(generateMissionButton);

        moveUpButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveDeliveryUp();
                    }
                });
        moveDownButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveDeliveryDown();
                    }
                });
        generateMissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMission();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadDeliveries() {
        deliveriesModel.setRowCount(0);
        if (deliveries != null) {
            for (Delivery delivery : deliveries) {
                deliveriesModel.addRow(
                        new Object[] {
                                delivery.getId(),
                                delivery.getCustomerId(),
                                delivery.getDeliveryDate(),
                                delivery.getDeliveryAddress()
                        });
            }
        }
    }

    private void moveDeliveryUp() {
        int selectedRow = deliveriesTable.getSelectedRow();
        if (selectedRow > 0) {
            Collections.swap(deliveries, selectedRow, selectedRow - 1);
            loadDeliveries();
            deliveriesTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        }
    }

    private void moveDeliveryDown() {
        int selectedRow = deliveriesTable.getSelectedRow();
        if (selectedRow < deliveries.size() - 1 && selectedRow >= 0) {
            Collections.swap(deliveries, selectedRow, selectedRow + 1);
            loadDeliveries();
            deliveriesTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
        }
    }

    private void generateMission() {
        Mission mission = schedulerService.createMission(deliveries, driver);

        if (mission != null) {
            GuiUtil.showInfoMessage(this, "Mission generated successfully");
            dispose();
        } else {
            GuiUtil.showErrorMessage(this, "Error generating the mission");
        }
    }

}