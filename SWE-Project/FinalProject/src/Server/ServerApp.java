package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ServerApp {
    private int port;
    private boolean running;
    private ServerSocket serverSocket;

    public ServerApp(int port) {
        this.port = port;
        this.running = false;
        createDatabase();
        ClientHandler.loadUsersFromDatabase();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            running = false;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDatabase() {
        String url = "jdbc:sqlite:your_database.db";

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {

            // Create tables if they do not exist
            String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "phoneNumber TEXT," +
                    "address TEXT," +
                    "cardNumber TEXT," +
                    "cardExpiration TEXT," +
                    "cardCVV TEXT" +
                    ")";
            statement.executeUpdate(createCustomersTable);

            String createRestaurantsTable = "CREATE TABLE IF NOT EXISTS restaurants (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "phoneNumber TEXT," +
                    "address TEXT," +
                    "restaurantName TEXT," +
                    "restaurantPhone TEXT," +
                    "restaurantHours TEXT," +
                    "restaurantCuisine TEXT," +
                    "restaurantMenu TEXT" +
                    ")";
            statement.executeUpdate(createRestaurantsTable);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "userType TEXT NOT NULL" +
                    ")";
            statement.executeUpdate(createUsersTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}