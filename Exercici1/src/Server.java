import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static boolean exit = false;
    public static void main(String[] args) {
        int puertoDestino = 2222;
        try {
            ServerSocket serverSocket = new ServerSocket(puertoDestino);
            Socket server = serverSocket.accept();
            System.out.println("Conexion recibida!");
            while (!exit) {
                // Esperar la peticio del client
                InputStream is = server.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bf = new BufferedReader(isr);
                String userRequest = bf.readLine();
                OutputStream os = server.getOutputStream();
                PrintWriter pw = new PrintWriter(os);

                // Seleccionar l'acció a realitzar
                String dbAction = DataBaseActionSelection(userRequest);
                System.out.println("El servidor ha seleccionat: " + dbAction);
                // Si l'acció es exit, sortir del bucle
                if (dbAction == "exit") {
                    pw.write("Server Exit - OK");
                    pw.flush();
                    exit = true;
                }

                // Enviar l'acció al client
                pw.write(dbAction + "\n");
                pw.flush();
            }
        } catch (IOException e) {
            System.out.println("Error Server");
        }
    }

    private static String DataBaseActionSelection(String userRequest) {
        switch (userRequest) {
            case "select":
                return "select";
            case "insert":
                return "insert";
            case "delete":
                return "delete";
            case "exit":
                return "exit";
            default:
                String response = DataBaseAction(userRequest);
                if (response != "Error") {
                    return response;
                } else {
                    return "Error: No s'ha trobat l'acció";
                }
        }
    }
    private static void writeJSON(String userRequest) {
        JSONObject json = new JSONObject(userRequest);

        try (PrintWriter writer = new PrintWriter(new FileWriter("bbdd.txt", true))) {
            writer.println(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Accions de la base de dades
    private static String DataBaseAction(String userRequest) {
        System.out.println("El cliente ha solicitado: " + userRequest);

        // Insert
        if (userRequest.charAt(0) == '{' && userRequest.charAt(userRequest.length() - 1) == '}') {
            writeJSON(userRequest);

            return "Insert OK";
        } else {
            // Select
            if (userRequest.charAt(0) == 'S' && userRequest.charAt(1) == ',') {
                String idUsuario = userRequest.substring(2);
                String selectResponse = mostrarLinea(idUsuario, new File("bbdd.txt"));

                return selectResponse;
            }

            // Delete
            if (userRequest.charAt(0) == 'D' && userRequest.charAt(1) == ',') {
                String idUsuario = userRequest.substring(2);
                eliminarLinea(idUsuario, new File("bbdd.txt"));
                return "Delete OK";
            }
        }

        return "Error";
    }

    private static String mostrarLinea(String idUsuario, File archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String idToSelect= "\"ID\":\"" + idUsuario + "\"";

                if (linea.contains(idToSelect)) {
                    System.out.println(linea);
                    return linea;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al mostrar la línea del archivo.");
        }
        return null;
    }

    private static void eliminarLinea(String idUsuario, File archivoOriginal) {
        File archivoTemporal = new File("temporal.txt");
        System.out.println("ID: " + idUsuario);
        try (BufferedReader br = new BufferedReader(new FileReader(archivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTemporal))) {

            String linea;

            String idToDelete = "\"ID\":\"" + idUsuario + "\"";
            System.out.println("ID to delete: " + idToDelete);

            while ((linea = br.readLine()) != null) {
                if (!linea.contains(idToDelete)) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al eliminar la línea del archivo.");
        }

        // Renombrar el archivo temporal al original
        if (archivoOriginal.delete() && archivoTemporal.renameTo(archivoOriginal)) {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));
            pw.write("Información eliminada con éxito." + "\n");
            pw.flush();
        } else {
            System.out.println("Error al eliminar la información.");
        }
    }
}
