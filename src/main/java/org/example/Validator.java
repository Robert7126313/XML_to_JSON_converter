package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class Validator {

    /**
     * Detekuje typ vstupu (JSON, XML, nebo Unknown).
     * @param input Vstupní data jako String.
     * @return Typ dat ("JSON", "XML" nebo "Unknown").
     */
    public static String detectType(String input) {
        if (isValidJson(input)) {
            return "JSON";
        } else if (isValidXml(input)) {
            return "XML";
        } else {
            return "Unknown";
        }
    }

    /**
     * Kontroluje, zda je vstup validní JSON.
     * @param jsonString Vstupní JSON jako String.
     * @return `true`, pokud je JSON validní; jinak `false`.
     */
    public static boolean isValidJson(String jsonString) {
        try {
            // Pokus o parsování JSON objektu
            new JSONObject(jsonString);
            return true;
        } catch (Exception e) {
            try {
                // Pokud to není objekt, pokus o parsování jako pole
                new JSONArray(jsonString);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * Kontroluje, zda je vstup validní XML.
     * @param xmlString Vstupní XML jako String.
     * @return `true`, pokud je XML validní; jinak `false`.
     */
    public static boolean isValidXml(String xmlString) {
        try {
            // Pokud se XML dá převést na JSON, je validní
            XML.toJSONObject(xmlString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vrací chybovou zprávu, pokud vstup není validní JSON nebo XML.
     * @param input Vstupní data jako String.
     * @return Popis chyby, pokud je vstup nevalidní; jinak `null`.
     */
    public static String getValidationError(String input) {
        if (!isValidJson(input) && !isValidXml(input)) {
            return "Input is neither valid JSON nor XML.";
        } else if (!isValidJson(input)) {
            return "Invalid JSON format.";
        } else if (!isValidXml(input)) {
            return "Invalid XML format.";
        }
        return null;
    }
}
