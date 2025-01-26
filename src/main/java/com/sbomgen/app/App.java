package com.sbomgen.app;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    public static void main(String[] args) {
        SyftTool syftTool = new SyftTool();

        Map<String, String> arguments = parseArguments(args);
        if (!valdateArguments(arguments)) {
            System.out.println("Error: --source and --output are required");
            System.exit(1);
        }

        try {
            // // Step 1: Generate SBOM
            Map<String, Object> syftSBOM = syftTool.generateSBOM(arguments.get("source"), arguments.get("format"));

            // Step 2: Search and Convert to JSON
            if (arguments.get("search") == null) {
                writeToJson(syftSBOM, arguments.get("o"));
            } else {
                Map<String, Object> results = syftTool.searchForPackage(syftSBOM, arguments.get("search"),
                        arguments.get("format"));
                writeToJson(results, arguments.get("o"));
            }

            System.out.printf("Search results successfully written to %s%n", arguments.get("o"));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parses command-line arguments into a map of key-value pairs.
     *
     * @param args the command-line arguments
     * @return a map of argument keys and values
     */
    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                String value = (i + 1 < args.length && !args[i + 1].startsWith("--")) ? args[++i] : null;
                arguments.put(key, value);
            } else if (args[i].startsWith("-")) {
                String key = args[i].substring(1);
                String value = (i + 1 < args.length && !args[i + 1].startsWith("-")) ? args[++i] : null;
                arguments.put(key, value);
            }
        }
        return arguments;
    }

    private static boolean valdateArguments(Map<String, String> arguments) {
        // Validate mandatory arguments
        if (!arguments.containsKey("source") || !arguments.containsKey("o")) {
            return false;
        }

        return true;
    }

    /**
     * Converts a list of search results into JSON and writes it to a file.
     *
     * @param results    the list of search results
     * @param outputFile the output JSON file path
     * @throws IOException if the file cannot be written
     */
    private static void writeToJson(Map<String, Object> results, String outputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), results);
    }

}