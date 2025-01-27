package driver;

import authentication.Driver;
import data.Delivery;
import data.Mission;
import data.ProductItem;
import service.DriverService;
import utils.GuiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RouteDetailsUI extends JFrame {

    private JTable routeDetailsTable;
    private DefaultTableModel routeDetailsModel;
    private DriverService driverService;
    private Mission mission;
    private Driver driver;

    public RouteDetailsUI(Mission mission, Driver driver) {
        this.mission = mission;
        this.driver = driver;
        this.driverService = new DriverService();
        setTitle("Route Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GuiUtil.setFrameSize(this, 800, 600);
        GuiUtil.centerFrame(this);
        initComponents();
        loadRouteDetails();
        setVisible(true);
    }

    private void initComponents() {
        routeDetailsModel = new DefaultTableModel(new Object[] { "Address", "Delivery Date", "Items", "Customer ID" },
                0); // Added Customer ID column
        routeDetailsTable = new JTable(routeDetailsModel);
        JScrollPane scrollPane = new JScrollPane(routeDetailsTable);

        add(scrollPane);
    }

    private void loadRouteDetails() {
        routeDetailsModel.setRowCount(0); // Clear the table

        List<String> routeAddresses = mission.getRoute();

        for (String address : routeAddresses) {
            if (!address.equals("Warehouse")) {
                Delivery delivery = driverService.getDeliveryByAddress(address);
                if (delivery != null) {
                    List<ProductItem> items = driverService.getDeliveryItems(delivery.getId());
                    StringBuilder itemsString = new StringBuilder();
                    for (ProductItem item : items) {
                        itemsString.append(item.getProduct().getName()).append(" (").append(item.getWeight())
                                .append("kg), ");
                    }
                    if (itemsString.length() > 0) {
                        itemsString.delete(itemsString.length() - 2, itemsString.length());
                    }
                    routeDetailsModel.addRow(new Object[] { address, delivery.getDeliveryDate(), itemsString.toString(),
                            delivery.getCustomerId() }); // Added Customer ID
                }
            } else {
                routeDetailsModel.addRow(new Object[] { address, "-", "-", "-" }); // Added placeholder for Customer ID
            }
        }
    }
}
