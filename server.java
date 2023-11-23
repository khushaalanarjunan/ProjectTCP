import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class server {
    private static final int PORT = 3030;
    public static final List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on address " + serverAddress.getHostAddress() + " and port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            clientName = in.readLine();
            System.out.println(clientName + " has joined the chat.");

            while (true) {
                String message = in.readLine();
                if (message == null || message.equalsIgnoreCase("/exit")) {
                    break;
                }
                System.out.println("Received from " + clientName + ": " + message);
                server.broadcastMessage("[" + clientName + "]: " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                server.clients.remove(this);
                System.out.println(clientName + " has left the chat.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
