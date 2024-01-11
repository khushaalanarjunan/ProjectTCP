package com.example.projecttcp.backend;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.example.projecttcp.protocol.Chat;
import com.example.projecttcp.protocol.ChatMessage;

public class Server {
    public static final List<ClientHandler> clients = new ArrayList<>();
    public static List<Chat> chats = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            Properties properties = new Properties();
            properties.load(new java.io.FileInputStream(".env"));
            int port = Integer.parseInt(properties.getProperty("PORT"));

            serverSocket = new ServerSocket(port);
            InetAddress serverAddress = InetAddress.getLocalHost();
            System.out.println("Server is running on address " + serverAddress.getHostAddress() + " and port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    public static void addChat(Chat newChat) {
        chats.add(newChat);
        var message = new ChatMessage(newChat);
        ArrayList<ClientHandler> pendingRemovalClients = new ArrayList<>();
        for (ClientHandler client : clients) {
            if (client.getUsername() != null && client.getUsername().equals(newChat.getUsername())) {
                continue;
            }

            try {
                client.sendMessage(message);
            } catch (Exception e) {
                System.err.printf("Failed to send message to %s\n", client.getUsername());
                pendingRemovalClients.add(client);
            }
        }

        clients.removeAll(pendingRemovalClients);
    }
}
