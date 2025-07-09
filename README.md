# XML/JSON Converter

This project provides a desktop application to convert data between XML and JSON formats. It uses Java with the Swing toolkit for the user interface and the `org.json` library for parsing and formatting.

## Features

- Convert **JSON** to **XML** and vice versa
- Detect input type automatically and enable the correct conversion button
- Load data from a local file or fetch content directly from a URL
- Drag and drop a file onto the input area
- Load example JSON or XML snippets for quick testing
- Export the converted output to a file
- Adjust the font size of the text areas

## Project Structure

- `Main` – Sets up the Swing UI and wires all user actions
- `Converter` – Contains the logic for translating between JSON and XML
- `Validator` – Provides simple validation and input type detection
- `FileUtils` – Utility methods for reading and writing files
- `WebUtils` – Fetches remote content using HTTP

The Maven `pom.xml` declares all dependencies, including JUnit for potential unit testing.
