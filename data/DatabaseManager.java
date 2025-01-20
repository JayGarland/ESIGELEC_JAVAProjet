package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import authentication.Customer;
import authentication.Scheduler;
import authentication.Driver;
import authentication.User;
import authentication.UserRole;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/goodsdelivery";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("MySQL JDBC driver not found");
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving user by ID");
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving user by email");
        }
        return null;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user;
        int id = rs.getInt("id");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String phoneNumber = rs.getString("phone_number");
        String role = rs.getString("role");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        switch (UserRole.valueOf(role.toUpperCase())) {
            case CUSTOMER:
                user = new Customer(email, password, phoneNumber);
                break;
            case SCHEDULER:
                user = new Scheduler(email, password, phoneNumber);
                break;
            case DRIVER:
                String truckRegNumber = rs.getString("truck_reg_number");
                int truckCapacityKg = rs.getInt("truck_capacity_kg");
                user = new Driver(email, password, phoneNumber, truckRegNumber, truckCapacityKg);
                break;
            default:
                throw new IllegalArgumentException("Invalid user role: " + role);
        }
        user.setId(id);
        user.setCreateAt(createdAt);
        user.setUpdateAt(updatedAt);
        return user;
    }

    // Method to add a new User to the database
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (email, password, phone_number, role, truck_reg_number, truck_capacity_kg) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.setString(4, user.getRole().toString());
            if (user instanceof Driver) {
                pstmt.setString(5, ((Driver) user).getTruckRegNumber());
                pstmt.setInt(6, ((Driver) user).getTruckCapacityKg());
            } else {
                pstmt.setString(5, null);
                pstmt.setString(6, null);
            }
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding user");
        }
        return false;
    }

    // Method to update an existing User in the database
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, password = ?, phone_number = ? , truck_reg_number = ?, truck_capacity_kg = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getPhoneNumber());
            if (user instanceof Driver) {
                pstmt.setString(4, ((Driver) user).getTruckRegNumber());
                pstmt.setInt(5, ((Driver) user).getTruckCapacityKg());
            } else {
                pstmt.setString(4, null);
                pstmt.setString(5, null);
            }
            pstmt.setInt(6, user.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating user");
        }
        return false;
    }

    public boolean addMission(Mission mission) {
        String sql = "INSERT INTO missions (driver_id, route, status, time_completed) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, mission.getDriverId());
            pstmt.setString(2, mission.getRoute());
            pstmt.setString(3, mission.getStatus());
            pstmt.setTimestamp(4, mission.getTimeCompleted());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    mission.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding mission");
        }
        return false;
    }

    // Method to get Missions for a specific driver and status
    public List<Mission> getMissionsByDriverId(int driverId, String status) {
        List<Mission> missions = new ArrayList<>();
        String sql = "SELECT * FROM missions WHERE driver_id = ? AND status = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Mission mission = createMissionFromResultSet(rs);
                missions.add(mission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting mission by driver and status");
        }
        return missions;
    }

    public boolean updateMission(Mission mission) {
        String sql = "UPDATE missions SET status = ?, time_completed = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mission.getStatus());
            pstmt.setTimestamp(2, mission.getTimeCompleted());
            pstmt.setInt(3, mission.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating mission");
        }
        return false;
    }

    // Method to add a new Product to the database
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, weight_kg, category_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getWeightKg());
            pstmt.setInt(4, product.getCategoryId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding product");
        }
        return false;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving all products");
        }
        return products;
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving product by ID");
        }
        return null;
    }

    // Method to add a new Delivery to the database
    public boolean addDelivery(Delivery delivery, List<ProductItem> items) {
        String sql = "INSERT INTO deliveries (customer_id, delivery_date, delivery_address, driver_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, delivery.getCustomerId());
            pstmt.setDate(2, new java.sql.Date(delivery.getDeliveryDate().getTime()));
            pstmt.setString(3, delivery.getDeliveryAddress());
            pstmt.setInt(4, delivery.getDriverId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int deliveryId = generatedKeys.getInt(1);
                    delivery.setId(deliveryId);
                    if (!addDeliveryItems(deliveryId, items)) {
                        throw new SQLException("Error adding delivery items for delivery: " + deliveryId);
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding delivery");
        }
        return false;
    }

    // Method to add delivery items to the database
    private boolean addDeliveryItems(int deliveryId, List<ProductItem> items) {
        String sql = "INSERT INTO delivery_items (delivery_id, product_id, quantity_kg) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (ProductItem item : items) {
                pstmt.setInt(1, deliveryId);
                pstmt.setInt(2, item.getProduct().getId());
                pstmt.setDouble(3, item.getQuantity());
                pstmt.addBatch();
            }
            int[] affectedRows = pstmt.executeBatch();
            for (int rows : affectedRows) {
                if (rows <= 0) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding delivery items");
        }
        return false;
    }

    // Method to get all items from a delivery
    public List<ProductItem> getDeliveryItems(int deliveryId) {
        List<ProductItem> items = new ArrayList<>();
        String sql = "SELECT product_id, quantity_kg from delivery_items WHERE delivery_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deliveryId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                double quantity = rs.getDouble("quantity_kg");
                Product product = getProductById(productId);
                items.add(new ProductItem(product, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting delivery items");
        }
        return items;
    }

    // Method to check if delivery item exists in the database
    public boolean deliveryItemExists(int deliveryId, int productId) {
        String sql = "SELECT COUNT(*) FROM delivery_items WHERE delivery_id = ? AND product_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deliveryId);
            pstmt.setInt(2, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking if delivery item exists");
        }
        return false;
    }

    // Method to update an existing item in the delivery_items table
    public boolean updateDeliveryItem(int deliveryId, int productId, double quantityKg) {
        String sql = "UPDATE delivery_items SET quantity_kg = ? WHERE delivery_id = ? AND product_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, quantityKg);
            pstmt.setInt(2, deliveryId);
            pstmt.setInt(3, productId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating delivery item");
        }
        return false;
    }

    public List<Delivery> getAllDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM deliveries";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Delivery delivery = createDeliveryFromResultSet(rs);
                deliveries.add(delivery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving all deliveries");
        }
        return deliveries;
    }

    public Delivery getDeliveryById(int id) {
        String sql = "SELECT * FROM deliveries WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createDeliveryFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving delivery by ID");
        }
        return null;
    }

    // Method to add a new Category to the database
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, category.getName());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding category");
        }
        return false;
    }

    // Method to get a Category from the database by id
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createCategoryFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting category by id");
        }
        return null;
    }

    // Method to get Stock by productId
    public Stock getStockByProductId(int productId) {
        String sql = "SELECT * FROM stock WHERE product_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createStockFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting stock by product id");
        }
        return null;
    }

    // Method to add a new Stock to the database
    public boolean addStock(Stock stock) {
        String sql = "INSERT INTO stock (product_id, quantity_kg) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stock.getProductId());
            pstmt.setDouble(2, stock.getQuantityKg());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding stock");
        }
        return false;
    }

    // Method to update an existing Stock in the database
    public boolean updateStock(Stock stock) {
        String sql = "UPDATE stock SET quantity_kg = ? WHERE product_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, stock.getQuantityKg());
            pstmt.setInt(2, stock.getProductId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating stock");
        }
        return false;
    }

    // Helper method to create a Stock object from a ResultSet
    private Stock createStockFromResultSet(ResultSet rs) throws SQLException {
        int productId = rs.getInt("product_id");
        double quantityKg = rs.getDouble("quantity_kg");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        Stock stock = new Stock(productId, quantityKg);
        stock.setCreatedAt(createdAt);
        stock.setUpdatedAt(updatedAt);
        return stock;
    }

    // Helper method to create a Delivery object from a ResultSet
    private Delivery createDeliveryFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int customerId = rs.getInt("customer_id");
        Date deliveryDate = rs.getDate("delivery_date");
        String deliveryAddress = rs.getString("delivery_address");
        int driverId = rs.getInt("driver_id");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        Delivery delivery = new Delivery(customerId, deliveryDate, deliveryAddress, driverId);
        delivery.setId(id);
        delivery.setCreatedAt(createdAt);
        delivery.setUpdatedAt(updatedAt);
        return delivery;
    }

    // Helper method to create a Category object from a ResultSet
    private Category createCategoryFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        Category category = new Category(name);
        category.setId(id);
        category.setCreatedAt(createdAt);
        category.setUpdatedAt(updatedAt);
        return category;
    }

    // Helper method to create a Product object from a ResultSet
    private Product createProductFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        double weightKg = rs.getDouble("weight_kg");
        int categoryId = rs.getInt("category_id");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        Product product = new Product(name, weightKg, categoryId);
        product.setId(id);
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(updatedAt);
        return product;
    }

    // Helper method to create a Mission object from a ResultSet
    private Mission createMissionFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int driverId = rs.getInt("driver_id");
        String route = rs.getString("route");
        String status = rs.getString("status");
        Timestamp timeCompleted = rs.getTimestamp("time_completed");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        Mission mission = new Mission();
        mission.setId(id);
        mission.setDriverId(driverId);
        mission.setRoute(route);
        mission.setStatus(status);
        mission.setTimeCompleted(timeCompleted);
        mission.setCreatedAt(createdAt);
        mission.setUpdatedAt(updatedAt);
        return mission;
    }
}