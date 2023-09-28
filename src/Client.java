import java.io.*;
import java.net.*;

// Client class
public class Client {
    final static int ServerPort = 1234;

    public static void main(String[] args) throws UnknownHostException, IOException {
        // Establish a connection with the server
        Socket s = new Socket("localhost", ServerPort);

        // Obtain input and output streams
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // Create a separate thread to receive messages from the server
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // Read the message from the server
                        String message = dis.readUTF();
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        receiveThread.start();

        // Read the client nickname from the console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter your nickname: ");
        String nickname = reader.readLine();

        // Send the nickname to the server
        dos.writeUTF(nickname);
        System.out.println("Type your message : ");
        // Start sending messages to the server
        while (true) {

            String message = reader.readLine();
            dos.writeUTF(message);
            dos.flush(); // Flush the output stream to ensure the message is sent immediately
        }
    }
}