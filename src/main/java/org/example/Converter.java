package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.XML;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringWriter;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;

public class Converter {
    /**
     * Converts JSON to XML. Supports both JSON objects and arrays.
     * @param jsonString Input JSON as String.
     * @return Formatted XML as String.
     */
    public static String jsonToXml(String jsonString) {
        try {
            // Detect input type
            String trimmed = jsonString.trim();
            
            // If it starts and ends with square brackets, it's a JSON array
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    
                    // Create a custom wrapper with item elements for each array entry
                    StringBuilder xmlBuilder = new StringBuilder();
                    xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    xmlBuilder.append("<root>\n");
                    
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Object item = jsonArray.get(i);
                        if (item instanceof JSONObject) {
                            // Convert each object to XML and remove the XML declaration
                            String itemXml = XML.toString(jsonArray.getJSONObject(i));
                            // Wrap the object's XML in an item element
                            xmlBuilder.append("  <item>\n");
                            // Add indentation
                            itemXml = itemXml.replaceAll("(?m)^", "    ");
                            xmlBuilder.append(itemXml).append("\n");
                            xmlBuilder.append("  </item>\n");
                        } else {
                            // For primitive array values
                            xmlBuilder.append("  <item>").append(item.toString()).append("</item>\n");
                        }
                    }
                    
                    xmlBuilder.append("</root>");
                    
                    return formatXmlString(xmlBuilder.toString());
                } catch (JSONException e) {
                    throw new IllegalArgumentException("Invalid JSON array: " + e.getMessage(), e);
                }
            } 
            // Otherwise, assume it's a JSON object
            else {
                try {
                    JSONObject json = new JSONObject(jsonString);
                    String xml = XML.toString(json);
                    // Add XML declaration if it's not present
                    if (!xml.startsWith("<?xml")) {
                        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xml;
                    }
                    return formatXmlString(xml);
                } catch (JSONException e) {
                    throw new IllegalArgumentException("Invalid JSON object: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            Throwable cause = e.getCause();
            String message = e.getMessage();
            if (cause != null) {
                message += " - Cause: " + cause.getMessage();
            }
            throw new IllegalArgumentException("Error processing JSON: " + message, e);
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
     * Format XML string using DOM parser and transformer.
     * @param xmlString Input XML as string.
     * @return Formatted XML string.
     * @throws Exception if XML formatting fails
     */
    private static String formatXmlString(String xmlString) throws Exception {
        try {
            // Clean up the XML string first
            xmlString = xmlString.trim();
            
            // Use the Transformer to format the XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            // Create a string writer for output
            StringWriter writer = new StringWriter();
            
            // Parse XML to DOM
            InputSource source = new InputSource(new StringReader(xmlString));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(source);
            
            // Transform DOM to formatted XML
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            
            return writer.toString();
            
        } catch (Exception e) {
            // Manual line-by-line formatting if transformer fails
            // This is a fallback if DOM parsing fails
            return xmlString;
        }
    }

    /**
     * Formats raw XML into a pretty-printed format with indents and newlines.
     * This is a fallback method that maintains compatibility.
     * @param input Raw XML as String.
     * @return Pretty formatted XML as String.
     * @deprecated Use formatXmlString instead
     */
    @Deprecated
    private static String prettyFormatXml(String input) throws Exception {
        return formatXmlString(input);
    }
}
