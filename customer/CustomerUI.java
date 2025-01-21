package customer;

import authentication.AuthService;
import authentication.Customer;
import data.Category;
import data.Product;
import data.ProductItem;
import service.CustomerService;
import utils.GuiUtil;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;

public class CustomerUI extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable productsTable;
    private DefaultTableModel productsModel;
    private JTextField deliveryAddressField;
    private JFormattedTextField deliveryDateField;
    private JSpinner quantitySpinner;
    private Customer customer;
    private CustomerService customerService;
    private JLabel totalWeightLabel;
    private double totalWeight;
    private List<ProductItem> cart;

    public CustomerUI(Customer customer) {
        this.customer = customer;
        this.customerService = new CustomerService();
        this.cart = new ArrayList<>();
        setTitle("Customer Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GuiUtil.setFrameSize(this, 1000, 700);
        GuiUtil.centerFrame(this);
        initComponents();
        loadProducts();
        setVisible(true);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        productsModel = new DefaultTableModel(new Object[] { "ID", "Name", "Weight (Kg)", "Category", "Stock" }, 0);
        productsTable = new JTable(productsModel);
        JScrollPane productsScrollPane = new JScrollPane(productsTable);
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.add(productsScrollPane, BorderLayout.CENTER);

        // Delivery Details Panel
        JPanel deliveryDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel deliveryAddressLabel = new JLabel("Delivery Address:");
        deliveryAddressField = new JTextField(20);
        JLabel deliveryDateLabel = new JLabel("Delivery Date (yyyy-MM-dd):");
        deliveryDateField = new JFormattedTextField(createDateFormatter());

        deliveryDateField.setValue(new Date());

        JLabel quantityLabel = new JLabel("Quantity (Kg):");
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000.0, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(quantitySpinner, "#0.0");
        quantitySpinner.setEditor(editor);

        // Buttons Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addToCartButton = new JButton("Add to Cart");
        JButton createOrderButton = new JButton("Create Order");
        JButton viewCartButton = new JButton("View Cart");
        totalWeightLabel = new JLabel("Total Weight: 0.0 Kg");
        actionsPanel.add(addToCartButton);
        actionsPanel.add(viewCartButton);
        actionsPanel.add(createOrderButton);
        actionsPanel.add(totalWeightLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        deliveryDetailsPanel.add(deliveryAddressLabel, gbc);
        gbc.gridx = 1;
        deliveryDetailsPanel.add(deliveryAddressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        deliveryDetailsPanel.add(deliveryDateLabel, gbc);
        gbc.gridx = 1;
        deliveryDetailsPanel.add(deliveryDateField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        deliveryDetailsPanel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        deliveryDetailsPanel.add(quantitySpinner, gbc);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(productsPanel, BorderLayout.CENTER);
        mainPanel.add(deliveryDetailsPanel, BorderLayout.NORTH);
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Products", mainPanel);
        add(tabbedPane);

        // Action Listener
        addToCartButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addToCart();
                    }
                });
        viewCartButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showCart();
                    }
                });
        createOrderButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        createDeliveryOrder();
                    }
                });
    }

    private DateFormatter createDateFormatter() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        return dateFormatter;
    }

    private void loadProducts() {
        productsModel.setRowCount(0);
        List<Product> products = customerService.getAllProducts();
        if (products != null) {
            for (Product product : products) {
                Category category = customerService.getCategoryById(product.getCategoryId());
                String categoryName = category != null ? category.getName() : "Unknown";
                productsModel.addRow(
                        new Object[] { product.getId(), product.getName(), product.getWeightKg(), categoryName,
                                product.getStock() });
            }
        }
    }

    private void addToCart() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product selectedProduct = customerService.getProductById((int) productsTable.getValueAt(selectedRow, 0));
            double quantity = (double) quantitySpinner.getValue();

            if (selectedProduct != null && quantity > 0) {
                // Check stock first
                if (!customerService.checkStock(selectedProduct.getId(), quantity)) {
                    GuiUtil.showErrorMessage(this, "Not enough stock for product: " + selectedProduct.getName());
                    return;
                }
                ProductItem item = new ProductItem(selectedProduct, quantity * selectedProduct.getWeightKg());
                cart.add(item);
                totalWeight += (selectedProduct.getWeightKg() * quantity);
                totalWeightLabel.setText("Total Weight: " + String.format("%.2f", totalWeight) + " Kg");
                GuiUtil.showInfoMessage(this, "Product added to cart");
            }
        } else {
            GuiUtil.showErrorMessage(this, "Please select a product to add to the cart.");
        }
    }

    private void showCart() {
        if (cart.isEmpty()) {
            GuiUtil.showErrorMessage(this, "Your shopping cart is empty.");
            return;
        }

        StringBuilder cartItems = new StringBuilder("Shopping Cart:\n");
        for (ProductItem item : cart) {
            cartItems.append("- Product: ").append(item.getProduct().getName())
                    .append(", Quantity: ").append(item.getWeight()).append(" Kg\n");
        }

        GuiUtil.showInfoMessage(this, cartItems.toString());
    }

    private void createDeliveryOrder() {
        String deliveryAddress = deliveryAddressField.getText();
        Date deliveryDate = (Date) deliveryDateField.getValue();

        if (deliveryAddress.isEmpty() || deliveryDate == null || cart.isEmpty()) {
            GuiUtil.showErrorMessage(this, "Please fill all the fields and add products to the cart.");
            return;
        }

        try {
            customerService.createDelivery(
                    customer, cart, deliveryDate, deliveryAddress, 3); // TODO: Remove hardcoded driver ID
            GuiUtil.showInfoMessage(this, "Delivery order created successfully!");
            cart.clear();
            totalWeight = 0.0;
            totalWeightLabel.setText("Total Weight: 0.0 Kg");
        } catch (Exception e) {
            GuiUtil.showErrorMessage(this, "Error creating delivery order: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Create a dummy Customer object for testing
        Customer dummyCustomer = new Customer("testcustomer@example.com", "password", "123-456-7890");
        AuthService authService = new AuthService();
        if (!authService.register(dummyCustomer)) {
            Customer loggedInCustomer = (Customer) authService.login(dummyCustomer.getEmail(),
                    dummyCustomer.getPassword());
            // Launch the CustomerUI with the persisted customer
            java.awt.EventQueue.invokeLater(() -> {
                new CustomerUI(loggedInCustomer);
            });
        } else {
            System.err.println("Error registering customer");
        }

    }
}