package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

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

    public String getRestaurants(double distance, String Cuisines) throws Exception {
        String request = "type=getRestaurants&distance=" + distance + "&Cuisines=" + Cuisines;
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

            // Restaurant names and addresses
            String[] restaurantNames = {
                    "Aroma Cafe", "Sabich Palace", "Hummus Heaven", "Jerusalem Grill", "Tel Aviv Sushi",
                    "Haifa Bites", "Burger Brothers", "Pita Paradise", "Shawarma City", "Falafel Fiesta",
                    "The Kebab House", "Pizza King", "The Vegan Corner", "Grill Master", "Pasta Perfection",
                    "Meat and Eat", "Fish Delight", "Sweet Treats", "Bagel Bakery", "Saba's Schnitzel",
                    "Spicy Thai", "Indian Aroma", "Shakshuka Shack", "Rustic Ramen", "Italian Bistro",
                    "Israeli Breakfast", "Salad Station", "Street Food Spot", "Sandwich Sensation", "Noodle Nest",
                    "Chocolate Dream", "Cupcake Heaven", "Patisserie Paris", "Doughnut Delight", "Ice Cream Island",
                    "Cheese Feast", "Burekas Boulevard", "Juice Junction", "Breakfast Club", "The Deli Spot",
                    "Soup Symphony", "Comfort Cafe", "Sushi Story", "Grill Garden", "Tasty Taboon",
                    "The Meatball Place", "Caffeinate Me", "Waffle Wonderland", "Chicken Corner", "Bakery Bliss"
            };

            String[] addresses = {
                    "Dizengoff St 100, Tel Aviv", "Jaffa St 50, Jerusalem", "Herzl St 200, Rishon LeZion",
                    "Ben Yehuda St 10, Haifa", "Allenby St 80, Tel Aviv", "King George St 30, Jerusalem",
                    "HaYarkon St 120, Tel Aviv", "Balfour St 45, Bat Yam", "Ibn Gabirol St 60, Tel Aviv",
                    "Yafo St 40, Haifa", "Derech Beit Lechem 22, Jerusalem", "Bograshov St 5, Tel Aviv",
                    "Emek Refaim 15, Jerusalem", "Rothschild Blvd 60, Tel Aviv", "Dizengoff St 200, Tel Aviv",
                    "Azza St 20, Jerusalem", "Allenby St 150, Tel Aviv", "HaPalmach St 35, Jerusalem",
                    "HaCarmel St 25, Tel Aviv", "Agrippas St 10, Jerusalem", "HaArba'a St 70, Tel Aviv",
                    "Rabbi Akiva St 50, Bnei Brak", "Ibn Gabirol St 100, Tel Aviv", "Azrieli Towers, Tel Aviv",
                    "Ehad Ha'am St 80, Tel Aviv", "Shderot Yerushalayim 50, Tel Aviv", "Rothschild Blvd 90, Tel Aviv",
                    "King George St 40, Tel Aviv", "Ibn Gabirol St 120, Tel Aviv", "Dizengoff St 50, Tel Aviv",
                    "Arlozorov St 60, Tel Aviv", "Shaul HaMelech Blvd 10, Tel Aviv", "Kaplan St 20, Tel Aviv",
                    "Yehuda Halevi St 15, Tel Aviv", "Ben Gurion Blvd 60, Haifa", "Shderot Rothschild 120, Tel Aviv",
                    "Yehuda Maccabi St 70, Tel Aviv", "Allenby St 100, Tel Aviv", "King David St 30, Jerusalem",
                    "Yafo St 100, Haifa", "Ben Yehuda St 120, Tel Aviv", "HaYarkon St 140, Tel Aviv",
                    "HaNeviim St 50, Jerusalem", "Jaffa St 20, Jerusalem", "HaNassi St 30, Haifa",
                    "Rothschild Blvd 30, Tel Aviv", "HaShalom Rd 50, Tel Aviv", "Ibn Gabirol St 80, Tel Aviv",
                    "Herzl St 150, Tel Aviv", "Shderot Ben Gurion 10, Haifa"
            };

            String[] cuisines = {
                    "Middle Eastern", "Italian", "Japanese", "American", "Indian",
                    "Israeli", "Mexican", "Thai", "Greek", "French"
            };

            Random rand = new Random();

            for (int i = 0; i < 50; i++) {
                String username = "restaurantUser" + i;
                String password = "pass" + i;
                String email = "restaurant" + i + "@example.com";
                String phoneNumber = "050" + (rand.nextInt(9000000) + 1000000);
                String restaurantPhone = "03" + (rand.nextInt(9000000) + 1000000);
                String restaurantHours = "08:00-22:00";
                String restaurantCuisine = cuisines[rand.nextInt(cuisines.length)];
                String restaurantMenu = generateRandomMenu(rand);

                String signupResponse = client.signupRestaurant(username, password, email, phoneNumber, addresses[i], restaurantNames[i], restaurantPhone, restaurantHours, restaurantCuisine, restaurantMenu);
                System.out.println("Signup Response for " + restaurantNames[i] + ": " + signupResponse);
            }

            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomMenu(Random rand) {
        String[] items = {"Pizza", "Burger", "Falafel", "Shawarma", "Sushi", "Pasta", "Salad", "Steak", "Ice Cream", "Soup"};
        StringBuilder menu = new StringBuilder();
        int itemCount = rand.nextInt(5) + 3; // Generate between 3 to 7 menu items

        for (int i = 0; i < itemCount; i++) {
            String item = items[rand.nextInt(items.length)];
            int price = rand.nextInt(50) + 20; // Generate random prices between 20 and 70
            menu.append(item).append(":").append(price);
            if (i < itemCount - 1) {
                menu.append(",");
            }
        }

        return menu.toString();
    }


}

