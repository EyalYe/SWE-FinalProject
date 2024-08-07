package Server;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, RestaurantUser> restaurants = new HashMap<>();
    private String username;
    private String type;

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
            case "disconnect":
                return handleDisconnect(params);
            default:
                return createResponse("error", "Unknown command");
        }
    }

    private String handleDisconnect(String[] params) {
        return createResponse("success", "Disconnected");
    }

    private String handleUpdateMenu(String[] params) {
        if (!this.type.equals("restaurant")) {
            return createResponse("error", "You are not authorized to update the menu");
        }
        String menu = getParamValue(params, "menu");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("restaurants.txt", true))) {
            writer.write(this.username + "," + menu);
        } catch (IOException e) {
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
        String type = users.containsKey(username) ? users.get(username).getType() : "";

        if (users.containsKey(username) && users.get(username).checkPassword(hashedPassword)) {
            this.username = username;
            this.type = type;
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
}