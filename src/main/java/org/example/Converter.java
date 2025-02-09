package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringWriter;

public class Converter {
    /**
     * Converts JSON to XML. Supports both JSON objects and arrays.
     * @param jsonString Input JSON as String.
     * @return Formatted XML as String.
     */
    public static String jsonToXml(String jsonString) {
        try {
            // Attempt to parse as a JSON object
            JSONObject json = new JSONObject(jsonString);
            String xml = XML.toString(json);
            return prettyFormatXml(xml);
        } catch (Exception e) {
            // If it fails, attempt to parse as a JSON array
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                JSONObject wrapper = new JSONObject();
                wrapper.put("root", jsonArray); // Wrap array into <root> element
                String xml = XML.toString(wrapper);
                return prettyFormatXml(xml);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Invalid JSON input: " + ex.getMessage());
            }
        }
    }

    /**
     * Converts XML to JSON.
     * @param xmlString Input XML as String.
     * @return Pretty formatted JSON as String.
     */
    public static String xmlToJson(String xmlString) {
        try {
            JSONObject json = XML.toJSONObject(xmlString);
            return json.toString(4); // Pretty format JSON with 4 spaces
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid XML input: " + e.getMessage());
        }
    }

    /**
     * Formats raw XML into a pretty-printed format with indents and newlines.
     * @param input Raw XML as String.
     * @return Pretty formatted XML as String.
     */
    private static String prettyFormatXml(String input) throws Exception {
        try {
            // Parse the raw XML string into a DOM document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new java.io.ByteArrayInputStream(input.getBytes()));

            // Set up the transformer for formatting
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Transform the DOM document into a formatted string
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new Exception("Error formatting XML: " + e.getMessage(), e);
        }
    }
}
