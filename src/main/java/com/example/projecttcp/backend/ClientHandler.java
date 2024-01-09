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
    }

    @Override
    public void run() {
        try {
            System.out.println("Creating input stream");
            in = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Creating output stream");
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Created streams");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (in != null && out != null) {
                try {
                    Message message = (Message) in.readObject();

                    if (message == null) {
                        break;
                    }

                    switch (message.getType()) {
                        case REGISTRATION:
                            var registrationMessage = (RegistrationMessage) message;
                            username = registrationMessage.getUsername();
                            System.out.println(username + " has joined the chat.");

                            var initialMessage = new InitialMessage(Server.chats);
                            System.out.printf("Sending all previous messages to %s\n", username);
                            out.writeObject(initialMessage);
                            break;

                        case CHAT:
                            var chatMessage = (ChatMessage) message;
                            Server.addChat(new Chat(username, chatMessage.getMessage(), chatMessage.getImage()));
                            System.out.printf("Received message '%s' from %s\n", chatMessage.getMessage(), username);
                            break;

                        default:
                            break;
                    }
                } catch (EOFException e) {
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
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