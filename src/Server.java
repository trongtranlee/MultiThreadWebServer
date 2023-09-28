import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Server class
public class Server {
    final static int ServerPort = 1234;
    static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Server is listening on port 1234
        ServerSocket ss = new ServerSocket(ServerPort);

        System.out.println("Server started...");

        // Start a separate thread to handle server-to-client messages
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String serverMessage = scanner.nextLine();
                    // Send the server message to all clients
                    for (ClientHandler client : clients) {
                        client.sendMessage("Server: " + serverMessage);
                    }
                }
            }
        });
        serverThread.start();

        // Running infinite loop for getting client requests
        while (true) {
            Socket s = null;

            try {
                // Accept the incoming request
                s = ss.accept();

                System.out.println("New client connected: " + s);

                // Obtain input and output streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                // Create a new thread to handle the client
                ClientHandler clientThread = new ClientHandler(s, dis, dos);
                clients.add(clientThread);
                clientThread.start();
            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    // ClientHandler class
    private static class ClientHandler extends Thread {
        final Socket socket;
        final DataInputStream dis;
        final DataOutputStream dos;

        public ClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos) {
            this.socket = socket;
            this.dis = dis;
            this.dos = dos;
        }

        @Override
        public void run() {
            String nickname;
            try {
                // Read the nickname from the client
                nickname = dis.readUTF();
                System.out.println(nickname + " has joined the Chat Room !");

                while (true) {
                    // Read the message sent by the client
                    String message = dis.readUTF();

                    // Print the received message on the server
                    System.out.println(nickname + ": " + message);

                    // Send the message to all clients except the sender
                    for (ClientHandler client : clients) {
                        if (client != this) {
                            client.sendMessage(nickname + ": " + message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Close the connection
                    socket.close();
                    dis.close();
                    dos.close();
                    clients.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Send a message to the client
        public void sendMessage(String message) {
            try {
                dos.writeUTF(message);
                dos.flush(); // Flush the output stream to ensure the message is sent immediately
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}