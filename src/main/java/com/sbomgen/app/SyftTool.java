package com.sbomgen.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class SyftTool {
    /**
     * Executes the Syft CLI command to generate SBOM for the specified source.
     *
     * @param source the container image source (e.g., nginx)
     * @param format the SBOM format (SPDX or CycloneDX)
     */
    public Map<String, Object> generateSBOM(String source, String format) throws IOException {
        List<String> command = Arrays.asList("syft", "scan", source, "-o", getSyftFormatType(format));

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        // Convert JSON string to Map<String, Object>
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(output.toString(), new TypeReference<Map<String, Object>>() {
        });
    }

    public Map<String, Object> searchForPackage(Map<String, Object> syftSBOM, String searchPattern, String format)
            throws Exception {

        if (format.equals("CYCLONEDX"))
            return filterCyclonedxSBOM(syftSBOM, searchPattern);
        else
            return filterSpdxSBOM(syftSBOM, searchPattern);
    }

    private Map<String, Object> filterCyclonedxSBOM(Map<String, Object> sbom, String searchPattern) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dependenciesList = (List<Map<String, Object>>) sbom.get("dependencies");
        result.put("dependencies", RegexSearch.searchWithRegex(dependenciesList, searchPattern, "ref"));

        List<Map<String, Object>> componentsList = (List<Map<String, Object>>) sbom.get("components");
        result.put("components", RegexSearch.searchWithRegex(componentsList, searchPattern, "bom-ref"));

        return result;
    }

    private Map<String, Object> filterSpdxSBOM(Map<String, Object> sbom, String searchPattern) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> packageList = (List<Map<String, Object>>) sbom.get("packages");
        result.put("packages", RegexSearch.searchWithRegex(packageList, searchPattern, "name"));

        List<Map<String, Object>> filesList = (List<Map<String, Object>>) sbom.get("files");
        result.put("files", RegexSearch.searchWithRegex(filesList, searchPattern, "fileName"));

        return result;
    }

    public static String getSyftFormatType(String format) {
        switch (format) {
            case "CYCLONEDX":
                return "cyclonedx-json";
            default:
                return "spdx-json";
        }
    }
}
