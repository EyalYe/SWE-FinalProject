package Client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

public class GeocodingService {

    public static String getApiKey() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/Client/.env")) {
            properties.load(input);
            return properties.getProperty("APIKEY");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String API_KEY = getApiKey();

    public GeocodingService() throws FileNotFoundException {
    }

    public static double[] getCoordinates(String address) {
        try {
            // Prepare the URL with the address and API key
            String encodedAddress = URLEncoder.encode(address, "UTF-8");
            String urlString = "https://api.opencagedata.com/geocode/v1/json?q=" + encodedAddress + "&key=" + API_KEY;

            // Create a URL and open a connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            double latitude = 0;
            double longitude = 0;
            // Parse the JSON response
            String[] jsonString = response.toString().split(",");
            for (String s : jsonString) {
                if (s.contains("lat")) {
                    try{
                        latitude = Double.parseDouble(s.split(":")[2].split(":")[0]);
                        break;
                    }
                    catch (NumberFormatException e){
                        latitude = 0;
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        latitude = 0;
                    }
                }
            }
            for (String s : jsonString) {
                if (s.contains("lng")) {
                    try{
                        longitude = Double.parseDouble(s.split(":")[1].split("}")[0]);
                        return new double[]{latitude, longitude};
                    }
                    catch (NumberFormatException e){
                        longitude = 0;
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        longitude = 0;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static void main(String[] args) {
        String address = "1600 Amphitheatre Parkway, Mountain View, CA";
        double[] coordinates = GeocodingService.getCoordinates(address);
        if (coordinates != null) {
            System.out.println("Latitude: " + coordinates[0] + ", Longitude: " + coordinates[1]);
        } else {
            System.out.println("Failed to get coordinates.");
        }
    }
}