package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import javax.xml.XMLConstants;
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
            
            // Security: Disable external entities and DTD processing
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new java.io.ByteArrayInputStream(input.getBytes("UTF-8")));

            // Set up the transformer for formatting
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
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
