package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import Client.GeocodingService;

public class ClientApp {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientApp(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() throws Exception {
        socket = new Socket(serverAddress, serverPort);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connected to the server.");
    }

    public void disconnect() throws Exception {
        sendRequest("type=disconnect");
        socket.close();
        System.out.println("Disconnected from the server.");
    }

    public String sendRequest(String request) throws Exception {
        out.println(request);
        String response = in.readLine();
        System.out.println("Response: " + response);
        return response;
    }

    public String login(String username, String password) throws Exception {
        String request = "type=login&username=" + username + "&password=" + password;
        return sendRequest(request);
    }

    public String signupCustomer(String username, String password, String email, String phoneNumber, String address, String cardNumber, String cardExpiration, String cardCVV) throws Exception {
        try{
            double[] coordinates = GeocodingService.getCoordinates(address);
            if (coordinates[0] == 0 && coordinates[1] == 0){
                System.out.println("Invalid address");
                return "Invalid address";
            }
            String coordinatesString = coordinates[0] + "#" + coordinates[1];
            String request = "type=signupCustomer&username=" + username + "&password=" + password +
                    "&email=" + email + "&phoneNumber=" + phoneNumber + "&address=" + coordinatesString +
                    "&cardNumber=" + cardNumber + "&cardExpiration=" + cardExpiration + "&cardCVV=" + cardCVV;
            return sendRequest(request);
        }
        catch (Exception e){
            System.out.println("Invalid address");
            return "Invalid address";
        }

    }

    public String signupRestaurant(String username, String password, String email, String phoneNumber, String address, String restaurantName, String restaurantPhone, String restaurantHours, String restaurantCuisine, String restaurantMenu) throws Exception {
        try {
            double[] coordinates = GeocodingService.getCoordinates(address);
            if (coordinates[0] == 0 && coordinates[1] == 0) {
                System.out.println("Invalid address");
                return "Invalid address";
            }
            String coordinatesString = coordinates[0] + "#" + coordinates[1];
            String request = "type=signupRestaurant&username=" + username + "&password=" + password +
                    "&email=" + email + "&phoneNumber=" + phoneNumber + "&address=" + coordinatesString +
                    "&restaurantName=" + restaurantName + "&restaurantPhone=" + restaurantPhone + "&restaurantHours=" + restaurantHours +
                    "&restaurantCuisine=" + restaurantCuisine + "&restaurantMenu=" + restaurantMenu;
            return sendRequest(request);
        } catch (Exception e) {
            System.out.println("Invalid address");
            return "Invalid address";
        }
    }

    public String getRestaurants() throws Exception {
        String request = "type=getRestaurants";
        return sendRequest(request);
    }

    public String getMenu(String restaurantName) throws Exception {
        String request = "type=getMenu&restaurantName=" + restaurantName;
        return sendRequest(request);
    }

    public String placeOrder(String username, String restaurantName, String orderDetails) throws Exception {
        String request = "type=placeOrder&username=" + username + "&restaurantName=" + restaurantName + "&orderDetails=" + orderDetails;
        return sendRequest(request);
    }

    public String updateMenu(String restaurantName, String menuDetails) throws Exception {
        String request = "type=updateMenu&restaurantName=" + restaurantName + "&menuDetails=" + menuDetails;
        return sendRequest(request);
    }

    public static void main(String[] args) {
        try {
            ClientApp client = new ClientApp("localhost", 12345); // Use the correct server address and port
            client.connect();

            // Example usage
            String loginResponse = client.login("testUser", "testPass");
            String signupCustomerResponse = client.signupCustomer("testUser", "testPass", "testEmail", "testPhone", "testAddress", "testCardNumber", "testCardExpiration", "testCardCVV");
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

