import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public static void main(String[] args) {
        int PortNumber = 5555;

        try {
            ServerSocket serverSocket = new ServerSocket(PortNumber);
            while (true) {
                ClientThread clientThread;
                System.out.println("Waitting connection");
                clientThread = new ClientThread(serverSocket.accept());
                System.out.println("Client connected");
                Thread t = new Thread(clientThread);
                t.start();
                System.out.println("New Thread working");
            }
        } catch (IOException e) {
            System.err.println("Error on Echo Server v2");
        }
    }
}