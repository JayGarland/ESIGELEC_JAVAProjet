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
    private static final String DB_URL = "jdbc:MySQL://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            Class.forName("come.mysql.cj.jdbc.Driver");
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

    public List<Mission> getMissionsByDriverId(int driverId) {
        List<Mission> missions = new ArrayList<>();
        String sql = "SELECT * FROM missions WHERE driver_id = ? AND status = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            pstmt.setString(2, "pending");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Mission mission = createMissionFromResultSet(rs);
                missions.add(mission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving missions by driver ID");
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
    public boolean addDelivery(Delivery delivery) {
        String sql = "INSERT INTO deliveries (customer_id, delivery_date, delivery_address) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, delivery.getCustomerId());
            pstmt.setDate(2, new java.sql.Date(delivery.getDeliveryDate().getTime()));
            pstmt.setString(3, delivery.getDeliveryAddress());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    delivery.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding delivery");
        }
        return false;
    }

    // Helper method to create a Product object from a ResultSet
    private Product createProductFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        double weightKg = rs.getDouble("weight_kg");
        double pricePerKg = rs.getDouble("price_per_kg");
        int categoryId = rs.getInt("category_id");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        Product product = new Product(name, weightKg, pricePerKg, categoryId);
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
