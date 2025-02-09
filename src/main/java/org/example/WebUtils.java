package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class WebUtils {

    /**
     * Načte obsah z URL pomocí HTTP GET požadavku.
     * Zvládá komprimovaný obsah (gzip) a různé kódování.
     * @param urlString URL adresa jako String.
     * @return Obsah stránky jako String.
     * @throws Exception Pokud dojde k chybě během načítání.
     */
    public static String fetchContentFromUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000); // Timeout 10 sekund
        connection.setReadTimeout(10000); // Timeout čtení 10 sekund
        connection.setRequestProperty("Accept-Encoding", "gzip"); // Podpora gzip komprese

        int responseCode = connection.getResponseCode();

        // Kontrola HTTP odpovědi
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Failed to fetch content. HTTP response code: " + responseCode);
        }

        // Získání kódování obsahu z hlavičky
        String contentType = connection.getContentType();
        String charset = "UTF-8"; // Výchozí kódování
        if (contentType != null && contentType.contains("charset=")) {
            charset = contentType.split("charset=")[1].split(";")[0];
        }

        // Zpracování obsahu (včetně komprese)
        InputStream inputStream = connection.getInputStream();
        String encoding = connection.getContentEncoding();
        if ("gzip".equalsIgnoreCase(encoding)) {
            inputStream = new GZIPInputStream(inputStream);
        }

        // Načtení obsahu stránky
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString().trim();
        } catch (Exception e) {
            throw new Exception("Error reading content from URL: " + e.getMessage(), e);
        }
    }
}
