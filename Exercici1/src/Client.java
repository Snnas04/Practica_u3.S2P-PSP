import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String destino = "localhost";
        int puertoDestino = 2222;
        Socket socket = new Socket();
        InetSocketAddress direccion = new InetSocketAddress(destino, puertoDestino);
        try {
            socket.connect(direccion);
            while (true) {
                // demanara quian opcio del menu vol realitzar
                String userRequest = Menu();
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                // enviar la opcio al servidor
                userRequest = userRequest.toLowerCase();
                pw.print(userRequest + "\n");
                pw.flush();

                // esperar la resposta del servidor i enviar les dades necessaries per realitzar l'acció
                switch (userRequest) {
                    case "insert" -> {
                        String insertRequest = InsertRequest();
                        pw.print(insertRequest + "\n");
                        pw.flush();
                    }
                    case "select" -> {
                        String selectDeleteRequest = SelectDeleteRequest();
                        pw.print("S," + selectDeleteRequest + "\n");
                        pw.flush();
                    }
                    case "delete" -> {
                        String selectDeleteRequest = SelectDeleteRequest();
                        pw.print("D," + selectDeleteRequest + "\n");
                        pw.flush();
                    }
                    case "exit" -> {
                        System.out.println("Gracies per venir ;)");
                    }
                    default -> System.out.println("Error: No s'ha trobat l'acció");
                }

                // esperar la resposta del servidor
                BufferedReader bfr = Client.getFlujo(socket.getInputStream());
                if (bfr.readLine().equals("Server Exit - OK")) {
                    socket.close();
                    break;
                } else {
                    System.out.println(bfr.readLine());
                }

                //socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error Client");
        }
    }

    public static BufferedReader getFlujo(InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bfr = new BufferedReader(isr);
        return bfr;
    }

    private static String Menu() throws IOException {
        System.out.println("Selecciona una opcio:\n    - INSERT\n    - SELECT\n    - DELETE\n    - EXIT\n");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String txt = reader.readLine();
        return txt;
    }

    private static String InsertRequest() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Introdueix les dades per crear un usuari:");
        System.out.println("ID: ");
        String idUser = reader.readLine();
        System.out.println("Nom: ");
        String userName = reader.readLine();
        System.out.println("Cognoms: ");
        String userSurnames = reader.readLine();

        JSONObject json = new JSONObject();
        json.put("ID", idUser);
        json.put("Nom", userName);
        json.put("Cognoms", userSurnames);

        return json.toString();
    }

    private static String SelectDeleteRequest() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Introdeuix l'ID de l'usuari:");
        System.out.println("ID: ");
        String idUser = reader.readLine();

        return idUser;
    }
}
