package com.example.projecttcp;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

import com.example.projecttcp.protocol.Chat;
import com.example.projecttcp.protocol.ChatMessage;
import com.example.projecttcp.protocol.InitialMessage;
import com.example.projecttcp.protocol.Message;
import com.example.projecttcp.protocol.RegistrationMessage;

public class ChatService implements Runnable {
    ChatController chatController;
    String address;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    public ChatService(ChatController chatController, String address) {
        this.chatController = chatController;
        this.address = address;
    }

    @Override
    public void run() {
        try {
            Properties properties = new Properties();
            properties.load(new java.io.FileInputStream(".env"));
            int port = Integer.parseInt(properties.getProperty("PORT"));

            socket = new Socket(address, port);
            System.out.printf("Connected to server at %s:%s\n", address, port);

            System.out.println("Creating streams");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Created streams");

            out.writeObject(new RegistrationMessage(chatController.getUsername()));
            System.out.println("Sending Registration Message ");

            while (in != null) {
                try {

                    Message message = (Message) in.readObject();

                    if (message == null) {
                        break;
                    }

                    switch (message.getType()) {
                        case INITIAL:
                            var initialMessage = (InitialMessage) message;
                            chatController.initializeMessages(initialMessage.getChats());
                            break;

                        case CHAT:
                            var chatMessage = (ChatMessage) message;
                            var chat = new Chat(chatMessage.getUsername(), chatMessage.getMessage(),
                                    chatMessage.getImage());
                            chatController.addChatMessage(chat);
                            break;

                        default:
                            break;
                    }

                } catch (EOFException e) {
                    ;
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Server sent an unknown class");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find .env file");
        } catch (IOException e) {
            System.out.println("In IO Exception");
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void sendMessage(String message, byte[] image) throws IOException {
        out.writeObject(new ChatMessage(message, image));
    }

    public void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
        }

        try {
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
        }

    }

}
