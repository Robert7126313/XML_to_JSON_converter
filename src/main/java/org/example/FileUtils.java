package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    /**
     * Načte obsah ze souboru.
     * @param filePath Cesta k souboru jako String.
     * @return Obsah souboru jako String.
     * @throws Exception Pokud dojde k chybě při čtení souboru.
     */
    public static String readFile(String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Zapíše obsah do souboru.
     * @param filePath Cesta k souboru jako String.
     * @param content Obsah, který má být zapsán.
     * @throws Exception Pokud dojde k chybě při zápisu souboru.
     */
    public static void writeFile(String filePath, String content) throws Exception {
        Files.write(Paths.get(filePath), content.getBytes());
    }
}
