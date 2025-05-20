package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class Validator {

    /**
     * Detects the type of input (JSON, XML, or Unknown).
     * @param input Input data as String.
     * @return Data type ("JSON", "XML" or "Unknown").
     */
    public static String detectType(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Unknown";
        }
        
        if (isValidJson(input)) {
            return "JSON";
        } else if (isValidXml(input)) {
            return "XML";
        } else {
            return "Unknown";
        }
    }

    /**
     * Checks if the input is valid JSON.
     * @param jsonString Input JSON as String.
     * @return `true` if JSON is valid; otherwise `false`.
     */
    public static boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }
        
        // Remove comments if present (not standard JSON but often used)
        String normalized = jsonString.replaceAll("(?s)/\\*.*?\\*/", "")
                                     .replaceAll("//.*?\\n", "\n")
                                     .trim();
        
        try {
            // Try to parse as JSON object
            new JSONObject(normalized);
            return true;
        } catch (Exception e) {
            try {
                // If not an object, try to parse as array
                new JSONArray(normalized);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * Checks if the input is valid XML.
     * @param xmlString Input XML as String.
     * @return `true` if XML is valid; otherwise `false`.
     */
    public static boolean isValidXml(String xmlString) {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            return false;
        }
        
        try {
            // If XML can be converted to JSON, it is valid
            XML.toJSONObject(xmlString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns an error message if the input is not valid JSON or XML.
     * @param input Input data as String.
     * @return Error description if the input is invalid; otherwise `null`.
     */
    public static String getValidationError(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Input is empty or null.";
        }
        
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
