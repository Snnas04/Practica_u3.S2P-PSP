import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import Utils.FileUtils;
import org.json.JSONObject;

public class ClientThread implements Runnable {
    private Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    private static void saveJsonDataInFile(JSONObject json, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String message;
            while ((message = reader.readLine()) != null) {
                // Create a JSON object directly from the message string
                JSONObject json = new JSONObject(message);

                // Extreure les dades del JSON
                String sender = json.getString("dniSender");
                String target = json.getString("dniTarget");
                String msg = json.getString("message");
                String jsonData = json.toString();

                // Create a sorted list of sender and target
                List<String> names = Arrays.asList(sender, target);
                names.sort(String::compareTo);

                // Create the file name from the sorted list
                String fileName = names.get(0) + "_" + names.get(1) + ".txt";

                // Después de procesar el mensaje recibido
                saveJsonDataInFile(json, fileName);

                // Leer el contenido del archivo
                String fileContent = FileUtils.fileReader(fileName);

                // Enviar el mensaje de vuelta al cliente con el historial
                writer.println("Has enviat \"" + msg + "\" a l'usuari " + target);
                writer.println("Historial de missatges amb " + target + ": " + fileContent);
                writer.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}