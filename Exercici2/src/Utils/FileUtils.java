package Utils;

import org.json.JSONObject;
import java.io.*;

public class FileUtils {
    public static String fileReader(String fileName) {
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line as a JSON object
                JSONObject json = new JSONObject(line);

                // Extract and print the "message" field
                String message = json.getString("message");
                fileContent.append(" - ");
                fileContent.append(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent.toString();
    }
}