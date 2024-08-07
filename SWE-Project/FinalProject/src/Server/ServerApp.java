package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    private int port;
    private boolean running;
    private ServerSocket serverSocket;

    public ServerApp(int port) {
        this.port = port;
        this.running = false;
        createFiles();
        ClientHandler.loadUsersFromFile();
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

    private void createFiles() {
        try {
            new File("customers.txt").createNewFile();
            new File("restaurants.txt").createNewFile();
            new File("users.txt").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerApp server = new ServerApp(12345);
        server.start();
    }
}