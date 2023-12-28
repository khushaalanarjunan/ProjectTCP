package com.example.projecttcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

public class client {
    private static final int PORT = 3030;

    public static void main(String[] args) {
        try {
            //System.out.print("Enter the server address: ");
            //String serverAddress = new Scanner(System.in).nextLine();

            // Load configuration from the .env file
            Properties properties = new Properties();
            properties.load(new java.io.FileInputStream(".env"));
            String serverAddress = properties.getProperty("SERVER_ADDRESS");

            System.out.print("Enter your name: ");
            String name = new Scanner(System.in).nextLine();

            Socket socket = new Socket(serverAddress, PORT);
            System.out.println("Connected to server at " + serverAddress);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            out.println(name);


            Thread serverListener = new Thread(() -> {
                try {
                    while (true) {
                        String serverMessage = in.readLine();
                        if (serverMessage == null) {
                            break;
                        }
                        String[] parts = serverMessage.split(":", 2);
                        String client_name = parts[0];
                        String message = parts[1].trim();

                        if (client_name.equals("["+name+"]")) {
                            System.out.println("[You]: " + message);
                        } else {
                            System.out.println(client_name + ": " + message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListener.start();


            while (true) {
                String message = userInput.readLine();
                out.println(message);

                if ("/exit".equalsIgnoreCase(message)) {
                    break;
                }
            }

            socket.close();
            serverListener.join();
            System.out.println("Connection closed");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
