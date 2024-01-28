import Utils.FileUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client {
    public static BufferedReader getFlujo(InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bfr = new BufferedReader(isr);
        return bfr;
    }

    public static void main(String[] args) {
        String local = "localhost";
        int port = 5555;
        String DNI = "a";
        Socket socket = new Socket();
        InetSocketAddress direccion = new InetSocketAddress(local, port);
        try {
            socket.connect(direccion);
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Escriu el DNI del client:");
                String dniTarget = reader.readLine();
                System.out.println("Escriu el missatge:");
                String message = reader.readLine();

                Map<String, String> data = new HashMap<>();
                data.put("dniSender", DNI);
                data.put("dniTarget", dniTarget);
                data.put("message", message);

                // Convert Map to JSON string
                JSONObject json = new JSONObject(data);
                String jsonString = json.toString();

                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.print(jsonString + "\n");
                pw.flush();

                BufferedReader magServer = Client.getFlujo(socket.getInputStream());
                System.out.println(magServer.readLine()); // imrpimeix el missatge de confirmacio que noltros hem enviat
                System.out.println(magServer.readLine()); // imrpimeix la conversa amb l'altre client
            }
        } catch (IOException e) {
            System.out.println("Error Client");
        }
    }
}