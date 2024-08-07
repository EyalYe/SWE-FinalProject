package Server;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static Map<String, User> users = new HashMap<>();
    private static ArrayList<String> restaurants = new ArrayList<>();
    private Connection connection;
    private String username;
    private String type;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:sqlite:your_database.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Received: " + request);
                String response = handleRequest(request);
                out.println(response);

                if (request.contains("type=disconnect")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadUsersFromDatabase() {
        String url = "jdbc:sqlite:your_database.db";
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phoneNumber");
                String address = resultSet.getString("address");
                String userType = resultSet.getString("userType");

                if (userType.equals("customer")) {
                    String cardNumber = resultSet.getString("cardNumber");
                    String cardExpiration = resultSet.getString("cardExpiration");
                    String cardCVV = resultSet.getString("cardCVV");
                    users.put(username, new CustomerUser(username, password, email, phoneNumber, address, cardNumber, cardExpiration, cardCVV));
                } else if (userType.equals("restaurant")) {
                    String restaurantName = resultSet.getString("restaurantName");
                    String restaurantPhone = resultSet.getString("restaurantPhone");
                    String restaurantHours = resultSet.getString("restaurantHours");
                    String restaurantCuisine = resultSet.getString("restaurantCuisine");
                    String restaurantMenu = resultSet.getString("restaurantMenu");
                    users.put(username, new RestaurantUser(username, password, email, phoneNumber, address, restaurantName, restaurantPhone, restaurantHours, restaurantCuisine, restaurantMenu));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String request) {
        String[] params = request.split("&");
        String type = getParamValue(params, "type");

        switch (type) {
            case "login":
                return handleLogin(params);
            case "signupCustomer":
                return handleSignupCustomer(params);
            case "signupRestaurant":
                return handleSignupRestaurant(params);
            case "getRestaurants":
                return handleGetRestaurants(params);
            case "getMenu":
                return handleGetMenu(params);
            case "placeOrder":
                return handlePlaceOrder(params);
            case "updateMenu":
                return handleUpdateMenu(params);
            case "disconnect":
                return handleDisconnect(params);
            default:
                return createResponse("error", "Unknown command");
        }
    }

    private String handleDisconnect(String[] params) {
        if(this.type.equals("restaurant")){
            try {
                restaurants.remove(this.username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return createResponse("success", "Disconnected");
    }

    private String handleUpdateMenu(String[] params) {
        if (!this.type.equals("restaurant")) {
            return createResponse("error", "You are not authorized to update the menu");
        }
        String menu = getParamValue(params, "menu");
        try {
            String query = "UPDATE restaurants SET restaurantMenu = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, menu);
            statement.setString(2, this.username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return createResponse("error", "Failed to update menu");
        }
        return createResponse("success", "Menu updated successfully");
    }

    private String handlePlaceOrder(String[] params) {
        // Implement place order logic
        return createResponse("success", "Order placed successfully");
    }

    private String handleGetMenu(String[] params) {
        // Implement get menu logic
        return createResponse("success", "Menu retrieved");
    }

    private String handleGetRestaurants(String[] params) {
        // Implement get restaurant list logic
        return createResponse("success", "Restaurant list retrieved");
    }

    private String handleLogin(String[] params) {
        String username = getParamValue(params, "username");
        String password = getParamValue(params, "password");
        String hashedPassword = hashPassword(password);

        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String userType = resultSet.getString("userType");
                return createResponse("success", "Login successful", userType);
            } else {
                return createResponse("error", "Login failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return createResponse("error", "Login failed");
        }
    }

    private String handleSignupCustomer(String[] params) {
        String username = getParamValue(params, "username");
        String password = getParamValue(params, "password");
        String hashedPassword = hashPassword(password);
        String email = getParamValue(params, "email");
        String phoneNumber = getParamValue(params, "phoneNumber");
        String address = getParamValue(params, "address");
        String cardNumber = getParamValue(params, "cardNumber");
        String cardExpiration = getParamValue(params, "cardExpiration");
        String cardCVV = getParamValue(params, "cardCVV");

        CustomerUser customer = new CustomerUser(username, hashedPassword, email, phoneNumber, address, cardNumber, cardExpiration, cardCVV);
        users.put(username, customer);

        // Save to database
        try {
            String query = "INSERT INTO customers (username, password, email, phoneNumber, address, cardNumber, cardExpiration, cardCVV) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setString(3, email);
            statement.setString(4, phoneNumber);
            statement.setString(5, address);
            statement.setString(6, cardNumber);
            statement.setString(7, cardExpiration);
            statement.setString(8, cardCVV);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return createResponse("error", "Customer signup failed");
        }

        return createResponse("success", "Customer signup successful");
    }

    private String handleSignupRestaurant(String[] params) {
        String username = getParamValue(params, "username");
        String password = getParamValue(params, "password");
        String hashedPassword = hashPassword(password);
        String email = getParamValue(params, "email");
        String phoneNumber = getParamValue(params, "phoneNumber");
        String address = getParamValue(params, "address");
        String restaurantName = getParamValue(params, "restaurantName");
        String restaurantPhone = getParamValue(params, "restaurantPhone");
        String restaurantHours = getParamValue(params, "restaurantHours");
        String restaurantCuisine = getParamValue(params, "restaurantCuisine");
        String restaurantMenu = getParamValue(params, "restaurantMenu");

        RestaurantUser restaurant = new RestaurantUser(username, hashedPassword, email, phoneNumber, address, restaurantName, restaurantPhone, restaurantHours, restaurantCuisine, restaurantMenu);
        users.put(username, restaurant);

        // Save to database
        try {
            String query = "INSERT INTO restaurants (username, password, email, phoneNumber, address, restaurantName, restaurantPhone, restaurantHours, restaurantCuisine, restaurantMenu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setString(3, email);
            statement.setString(4, phoneNumber);
            statement.setString(5, address);
            statement.setString(6, restaurantName);
            statement.setString(7, restaurantPhone);
            statement.setString(8, restaurantHours);
            statement.setString(9, restaurantCuisine);
            statement.setString(10, restaurantMenu);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return createResponse("error", "Restaurant signup failed");
        }

        return createResponse("success", "Restaurant signup successful");
    }

    private String createResponse(String status, String message) {
        return "status=" + status + "&message=" + message;
    }

    private String createResponse(String status, String message, String userType) {
        return "status=" + status + "&message=" + message + "&userType=" + userType;
    }

    private String getParamValue(String[] params, String key) {
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue[0].equals(key) && keyValue.length > 1) {
                return keyValue[1];
            }
        }
        return "";
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}