package ozaii.factory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class LicenseFactory {

    private static final String API_URL = "http://localhost:3000/validate";

    public String validateLicense(String ipAddress, String licenseKey) {
        String jsonBody = String.format("{\"ip\":\"%s\", \"key\":\"%s\"}", ipAddress, licenseKey);

        try {
            // URL'yi kontrol et
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP ayarlarını yap
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // JSON verisini gönder
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // HTTP yanıt kodunu kontrol et
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return "Sunucudan hata kodu döndü: 404 - API endpoint bulunamadı.";
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                return "Sunucudan hata kodu döndü: " + statusCode;
            }

            // Yanıtı oku
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }

        } catch (FileNotFoundException e) {
            return "API endpoint bulunamadı: " + e.getMessage();
        } catch (IOException e) {
            return "API'ye bağlanırken bir hata oluştu: " + e.getMessage();
        }
    }


}
