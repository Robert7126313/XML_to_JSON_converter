package org.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtils {

    /**
     * Reads content from a file.
     * @param filePath Path to the file as String.
     * @return File content as String.
     * @throws Exception If an error occurs while reading the file.
     */
    public static String readFile(String filePath) throws Exception {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (java.nio.file.NoSuchFileException e) {
            throw new Exception("File not found: " + filePath, e);
        } catch (java.nio.file.AccessDeniedException e) {
            throw new Exception("Cannot access file (permission denied): " + filePath, e);
        } catch (Exception e) {
            throw new Exception("Error reading file: " + e.getMessage(), e);
        }
    }

    /**
     * Writes content to a file.
     * @param filePath Path to the file as String.
     * @param content Content to be written.
     * @throws Exception If an error occurs while writing the file.
     */
    public static void writeFile(String filePath, String content) throws Exception {
        try {
            Files.write(
                Paths.get(filePath), 
                content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (java.nio.file.AccessDeniedException e) {
            throw new Exception("Cannot write to file (permission denied): " + filePath, e);
        } catch (Exception e) {
            throw new Exception("Error writing file: " + e.getMessage(), e);
        }
    }
}
