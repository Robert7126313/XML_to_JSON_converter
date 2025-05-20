package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class WebUtils {

    /**
     * Fetches content from URL using HTTP GET request.
     * Handles compressed content (gzip) and various encodings.
     * @param urlString URL address as String.
     * @return Page content as String.
     * @throws Exception If an error occurs during loading.
     */
    public static String fetchContentFromUrl(String urlString) throws Exception {
        if (!urlString.toLowerCase().startsWith("http://") && 
            !urlString.toLowerCase().startsWith("https://")) {
            urlString = "https://" + urlString;
        }
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000); // 15 second timeout
        connection.setReadTimeout(15000); // 15 second read timeout
        
        // Set common headers
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("User-Agent", "XMLJSONConverter/1.0");
        connection.setRequestProperty("Accept", "application/xml, application/json, text/plain, */*");
        
        try {
            int responseCode = connection.getResponseCode();

            // Check HTTP response
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Failed to fetch content. HTTP response code: " + responseCode);
            }

            // Get content encoding from header
            String contentType = connection.getContentType();
            String charset = "UTF-8"; // Default encoding
            if (contentType != null && contentType.contains("charset=")) {
                charset = contentType.split("charset=")[1].split(";")[0];
            }

            // Process content (including compression)
            InputStream inputStream = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            if ("gzip".equalsIgnoreCase(encoding)) {
                inputStream = new GZIPInputStream(inputStream);
            }

            // Read page content
            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, charset))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line).append("\n");
                }
                return content.toString().trim();
            }
        } catch (java.net.SocketTimeoutException e) {
            throw new Exception("Connection timed out. Please check your internet connection and try again.", e);
        } catch (java.net.UnknownHostException e) {
            throw new Exception("Cannot reach the server. Please check the URL and your internet connection.", e);
        } catch (Exception e) {
            throw new Exception("Error fetching content: " + e.getMessage(), e);
        } finally {
            connection.disconnect();
        }
    }
}
