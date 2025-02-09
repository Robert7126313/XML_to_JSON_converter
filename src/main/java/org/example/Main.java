package org.example;

import org.example.Converter;
import org.example.FileUtils;
import org.example.Validator;
import org.example.WebUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("XML/JSON Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        setupUI(panel, frame);

        frame.setSize(800, 600); // Default size
        frame.setMinimumSize(new Dimension(600, 400)); // Minimum size
        frame.setVisible(true);
    }

    private static void setupUI(JPanel panel, JFrame frame) {
        panel.setLayout(new BorderLayout());

        // Top part: URL and Fetch button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel urlLabel = new JLabel("URL:");
        JTextField urlField = new JTextField();
        JButton fetchButton = new JButton("Fetch");
        topPanel.add(urlLabel, BorderLayout.WEST);
        topPanel.add(urlField, BorderLayout.CENTER);
        topPanel.add(fetchButton, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Middle part: Input and output fields
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        JTextArea inputArea = new JTextArea();
        inputArea.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        JScrollPane scrollInput = new JScrollPane(inputArea);

        JTextArea outputArea = new JTextArea();
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        JScrollPane scrollOutput = new JScrollPane(outputArea);

        centerPanel.add(scrollInput);
        centerPanel.add(scrollOutput);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Set up drag-and-drop for inputArea
        inputArea.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                try {
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        File file = files.get(0);
                        String content = new String(Files.readAllBytes(file.toPath()));
                        inputArea.setText(content);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        // Bottom part: Buttons and input format
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Buttons in the center
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton jsonToXmlButton = new JButton("JSON to XML");
        JButton xmlToJsonButton = new JButton("XML to JSON");
        JButton loadSampleButton = new JButton("Load Sample");
        JButton loadFileButton = new JButton("Load File");
        JButton exportFileButton = new JButton("Export File");
        buttonPanel.add(jsonToXmlButton);
        buttonPanel.add(xmlToJsonButton);
        buttonPanel.add(loadSampleButton);
        buttonPanel.add(loadFileButton);
        buttonPanel.add(exportFileButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        // Format label on the bottom left
        JLabel formatLabel = new JLabel("Format: Unknown");
        formatLabel.setHorizontalAlignment(SwingConstants.LEFT);
        formatLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        bottomPanel.add(formatLabel, BorderLayout.WEST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Dynamic resizing of components
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panel.revalidate();
                panel.repaint();
            }
        });

        // Automatic check after text change
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkAndUpdateStyle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkAndUpdateStyle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkAndUpdateStyle();
            }

            private void checkAndUpdateStyle() {
                String input = inputArea.getText().trim();
                String detectedType = Validator.detectType(input);

                switch (detectedType) {
                    case "JSON":
                        jsonToXmlButton.setEnabled(true);
                        xmlToJsonButton.setEnabled(false);
                        formatLabel.setText("Format: JSON");
                        break;
                    case "XML":
                        jsonToXmlButton.setEnabled(false);
                        xmlToJsonButton.setEnabled(true);
                        formatLabel.setText("Format: XML");
                        break;
                    default:
                        jsonToXmlButton.setEnabled(false);
                        xmlToJsonButton.setEnabled(false);
                        formatLabel.setText("Format: Unknown");
                        break;
                }
            }
        });

        // Fetch button
        fetchButton.addActionListener(e -> {
            String url = urlField.getText();
            try {
                String content = WebUtils.fetchContentFromUrl(url);
                inputArea.setText(content);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error fetching content: " + ex.getMessage());
            }
        });

        // JSON to XML button
        jsonToXmlButton.addActionListener(e -> {
            String input = inputArea.getText();
            try {
                String result = Converter.jsonToXml(input);
                outputArea.setText(result);
            } catch (Exception ex) {
                outputArea.setText("Error: Invalid JSON input.");
            }
        });

        // XML to JSON button
        xmlToJsonButton.addActionListener(e -> {
            String input = inputArea.getText();
            try {
                String result = Converter.xmlToJson(input);
                outputArea.setText(result);
            } catch (Exception ex) {
                outputArea.setText("Error: Invalid XML input.");
            }
        });

        // Load Sample button
        loadSampleButton.addActionListener(e -> {
            Object[] options = {"Load Sample JSON", "Load Sample XML", "Cancel"};
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Select a sample to load:",
                    "Load Sample",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == JOptionPane.YES_OPTION) {
                inputArea.setText("{\n" +
                        "    \"library\": {\n" +
                        "        \"name\": \"City Library\",\n" +
                        "        \"location\": \"Downtown\",\n" +
                        "        \"books\": [\n" +
                        "            { \"id\": 1, \"title\": \"The Great Gatsby\", \"author\": \"F. Scott Fitzgerald\", \"year\": 1925, \"available\": true }\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}");
            } else if (choice == JOptionPane.NO_OPTION) {
                inputArea.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<library>\n" +
                        "    <name>City Library</name>\n" +
                        "    <location>Downtown</location>\n" +
                        "    <books>\n" +
                        "        <book>\n" +
                        "            <id>1</id>\n" +
                        "            <title>The Great Gatsby</title>\n" +
                        "            <author>F. Scott Fitzgerald</author>\n" +
                        "            <year>1925</year>\n" +
                        "            <available>true</available>\n" +
                        "        </book>\n" +
                        "    </books>\n" +
                        "</library>");
            }
        });

        // Load File button
        loadFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    String content = FileUtils.readFile(selectedFile.getAbsolutePath());
                    inputArea.setText(content);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error loading file: " + ex.getMessage());
                }
            }
        });

        // Export File button
        exportFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }
                try {
                    FileUtils.writeFile(filePath, inputArea.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error saving file: " + ex.getMessage());
                }
            }
        });
    }
}