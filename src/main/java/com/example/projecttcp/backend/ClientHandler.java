package com.example.projecttcp.backend;

import java.io.*;
import java.net.*;

import com.example.projecttcp.protocol.Chat;
import com.example.projecttcp.protocol.ChatMessage;
import com.example.projecttcp.protocol.InitialMessage;
import com.example.projecttcp.protocol.Message;
import com.example.projecttcp.protocol.RegistrationMessage;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    public String getUsername() {
        return username;
    }

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            while (true) {
                try {
                    Message message = (Message) in.readObject();

                    switch (message.getType()) {
                        case REGISTRATION:
                            var registrationMessage = (RegistrationMessage) message;
                            username = registrationMessage.getUsername();
                            System.out.println(username + " has joined the chat.");

                            var initialMessage = new InitialMessage(Server.chats);
                            out.writeObject(initialMessage);

                        case CHAT:
                            var chatMessage = (ChatMessage) message;
                            Server.addChat(new Chat(username, chatMessage.getMessage()));
                            break;

                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                clientSocket.close();
                Server.clients.remove(this);
                System.out.println(username + " has left the chat.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }
}