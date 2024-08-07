package Client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeocodingService {

    public static String getApiKey() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("SWE-Project/FinalProject/.env")) {
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
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
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

            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray results = jsonObject.getJSONArray("results");
            if (results != null && results.length() > 0) {
                JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                double lat = geometry.getDouble("lat");
                double lng = geometry.getDouble("lng");
                return new double[]{lat, lng};
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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