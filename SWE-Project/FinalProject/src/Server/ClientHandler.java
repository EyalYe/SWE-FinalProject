package Server;

import java.io.*;
import java.net.Socket;
import java.lang.Math;
import java.lang.Double;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, RestaurantUser> restaurants = new HashMap<>();
    private String username;
    private String type;
    private static int orderId = 0;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
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
        }
    }

    public static void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                String password = parts[1];
                String userType = parts[2];
                if (userType.equals("customer")) {
                    users.put(username, new CustomerUser(username, password, parts[3], parts[4], parts[5], parts[6], parts[7], parts[8]));
                } else if (userType.equals("restaurant")) {
                    restaurants.put(username, new RestaurantUser(username, password, parts[3], parts[4], parts[5], parts[6], parts[7], parts[8], parts[9], parts[10]));
                }
            }
        } catch (IOException e) {
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
            case "updateCreditCard":
                return handleUpdateCreditCard(params);
            case "disconnect":
                return handleDisconnect(params);
            default:
                return createResponse("error", "Unknown command");
        }
    }

    private String handleDisconnect(String[] params) {
        return createResponse("success", "Disconnected");
    }

    private String handleUpdateCreditCard(String[] params) {
        if (!this.type.equals("customer")) {
            return createResponse("error", "You are not authorized to update the credit card");
        }
        String cardNumber = getParamValue(params, "cardNumber");
        String cardExpiration = getParamValue(params, "cardExpiration");
        String cardCVV = getParamValue(params, "cardCVV");
        CustomerUser customer = (CustomerUser) users.get(this.username);
        customer.setCardNumber(cardNumber);
        customer.setCardExpiration(cardExpiration);
        customer.setCardCVV(cardCVV);

        // Update file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(this.username + "," + customer.getPassword() + ",customer," + customer.getEmail() + "," + customer.getPhoneNumber() + "," + customer.getAddress() + "," + cardNumber + "," + cardExpiration + "," + cardCVV);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            return createResponse("error", "Failed to update credit card");
        }

        return createResponse("success", "Credit card updated successfully");
    }

    private String handleUpdateMenu(String[] params) {
        if (!this.type.equals("restaurant")) {
            return createResponse("error", "You are not authorized to update the menu");
        }
        String menu = getParamValue(params, "menuDetails");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("restaurants.txt", true))) {
            writer.write(this.username + "," + menu);
        } catch (IOException e) {
            e.printStackTrace();
            return createResponse("error", "Failed to update menu");
        }
        return createResponse("success", "Menu updated successfully");
    }

    private String handlePlaceOrder(String[] params) {
        if (!this.type.equals("customer")) {
            return createResponse("error", "You are not authorized to place an order");
        }
        String restaurantName = getParamValue(params, "restaurantName");
        if (!restaurants.containsKey(restaurantName)) {
            return createResponse("error", "Restaurant not found");
        }
        Order order = new Order(orderId++, this.username, restaurantName, getParamValue(params, "orderDetails"));
        RestaurantUser restaurant = restaurants.get(restaurantName);
        restaurant.addOrder(order);

        return createResponse("success", "Order placed successfully");
    }

    private String handleGetMenu(String[] params) {
        String restaurantName = getParamValue(params, "restaurantName");
        if (!restaurants.containsKey(restaurantName)) {
            return createResponse("error", "Restaurant not found");
        }
        RestaurantUser restaurant = restaurants.get(restaurantName);
        String menu = restaurant.getRestaurantMenu();
        return createResponse("success", menu);
    }

    private String handleGetRestaurants(String[] params) {
        StringBuilder restaurantString = new StringBuilder();
        String restaurantType = getParamValue(params, "restaurantType");
        String address = getParamValue(params, "address");
        double distance = Double.parseDouble(getParamValue(params, "distance"));
        for (Map.Entry<String, RestaurantUser> entry : restaurants.entrySet()) {
            RestaurantUser restaurant = entry.getValue();
            if (checkDistance(address, restaurant.getAddress(), distance) && (restaurant.getRestaurantCuisine().equals(restaurantType) || restaurantType.equals(""))) {
                System.out.println(restaurant.getRestaurantName());
                restaurantString.append(restaurant.getRestaurantName()).append(",");
            }
        }
        return createResponse("success", restaurantString.toString());
    }

    private String handleLogin(String[] params) {
        String username = getParamValue(params, "username");
        String password = getParamValue(params, "password");
        String hashedPassword = hashPassword(password);

        if (users.containsKey(username) && users.get(username).checkPassword(hashedPassword)) {
            this.username = username;
            this.type = users.get(username).getType();
            if ( this.type.equals("restaurant")) {
                restaurants.put(username, (RestaurantUser) users.get(username));
            }
            return createResponse("success", "Login successful", this.type);
        } else {
            return createResponse("error", "Login failed");
        }
    }

    private String handleSignupCustomer(String[] params) {
        String username = getParamValue(params, "username");
        if (users.containsKey(username)) {
            return createResponse("error", "User already exists");
        }

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

        // Save to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(username + "," + hashedPassword + ",customer," + email + "," + phoneNumber + "," + address + "," + cardNumber + "," + cardExpiration + "," + cardCVV);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            return createResponse("error", "Customer signup failed");
        }

        return createResponse("success", "Customer signup successful");
    }

    private String handleSignupRestaurant(String[] params) {
        String username = getParamValue(params, "username");
        if (users.containsKey(username)) {
            return createResponse("error", "User already exists");
        }

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

        // Save to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(username + "," + hashedPassword + ",restaurant," + email + "," + phoneNumber + "," + address + "," + restaurantName + "," + restaurantPhone + "," + restaurantHours + "," + restaurantCuisine + "," + restaurantMenu);
            writer.newLine();
        } catch (IOException e) {
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

    private boolean checkDistance(String address1, String address2, double distance){
        double lat1 = Double.parseDouble(address1.split("#")[0]);
        double lon1 = Double.parseDouble(address1.split("#")[1]);
        double lat2 = Double.parseDouble(address2.split("#")[0]);
        double lon2 = Double.parseDouble(address2.split("#")[1]);

        double calculatedDistance = calculateDistance(lat1, lon1, lat2, lon2);
        return calculatedDistance < distance;
        }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371000; // radius in meters

        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}