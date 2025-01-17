package ozaii.utils.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class HttpUtil {

    // HTTP yöntemlerini tanımlayan enum
    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    /**
     * Genel bir HTTP isteği gönderir. Asenkron olarak çalışacak şekilde düzenlenmiştir.
     *
     * @param urlString İstek yapılacak URL
     * @param method HTTP yöntemi (GET, POST, PUT, DELETE)
     * @param payload Gönderilecek JSON verisi (sadece POST ve PUT için, diğer yöntemlerde null olabilir)
     * @return Yanıt JSON verisi (String olarak) CompletableFuture ile döner
     */
    public static CompletableFuture<String> sendHttpRequest(String urlString, HttpMethod method, String payload) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            DataOutputStream outputStream = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(method.name());
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (method == HttpMethod.POST || method == HttpMethod.PUT) {
                    connection.setDoOutput(true);
                    outputStream = new DataOutputStream(connection.getOutputStream());
                    if (payload != null) {
                        outputStream.writeBytes(payload);
                    }
                    outputStream.flush();
                }

                int responseCode = connection.getResponseCode();
                if (responseCode < 200 || responseCode >= 300) {
                    throw new IOException("HTTP isteği başarısız oldu. Yanıt kodu: " + responseCode);
                }

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } catch (IOException e) {
                throw new RuntimeException("HTTP isteği sırasında hata oluştu", e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    /**
     * Verilen URL'e GET isteği gönderir. Asenkron olarak çalışacak şekilde düzenlenmiştir.
     *
     * @param urlString İstek yapılacak URL
     * @return Yanıt JSON verisi (String olarak) CompletableFuture ile döner
     */
    public static CompletableFuture<String> sendGetRequest(String urlString) {
        return sendHttpRequest(urlString, HttpMethod.GET, null);
    }

    /**
     * Verilen URL'e POST isteği gönderir. Asenkron olarak çalışacak şekilde düzenlenmiştir.
     *
     * @param urlString İstek yapılacak URL
     * @param jsonPayload Gönderilecek JSON verisi
     * @return Yanıt JSON verisi (String olarak) CompletableFuture ile döner
     */
    public static CompletableFuture<String> sendPostRequest(String urlString, String jsonPayload) {
        return sendHttpRequest(urlString, HttpMethod.POST, jsonPayload);
    }

}
