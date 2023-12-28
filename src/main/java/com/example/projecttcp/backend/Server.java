package com.example.projecttcp.backend;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.example.projecttcp.protocol.Chat;
import com.example.projecttcp.protocol.ChatMessage;

public class Server {
    private static final int PORT = 3030;
    public static final List<ClientHandler> clients = new ArrayList<>();
    public static List<Chat> chats = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            InetAddress serverAddress = InetAddress.getLocalHost();
            System.out.println("Server is running on address " + serverAddress.getHostAddress() + " and port " + PORT);

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
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(newChat.getUsername())) {
                continue;
            }

            var message = new ChatMessage(newChat);
            
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                System.err.printf("Failed to send message to %s\n", client.getUsername());
            }
        }
    }
}
