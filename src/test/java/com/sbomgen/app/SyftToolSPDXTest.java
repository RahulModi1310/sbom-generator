package com.sbomgen.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SyftToolTest {

    private SyftTool syftTool;
    private ObjectMapper objectMapper;
    private Map<String, Object> sampleSBOM;

    @BeforeEach
    void setUp() throws Exception {
        syftTool = new SyftTool();
        objectMapper = new ObjectMapper();

        // Load the sample SPDX JSON from the test resources
        File jsonFile = new File("src/test/resources/sample-spdx.json");
        sampleSBOM = objectMapper.readValue(jsonFile, new TypeReference<Map<String, Object>>() {});
    }

    @Test
    void testSearchForPackage_WithMatchingPackageName() throws Exception {
        // Define the search pattern
        String searchPattern = "apt";
        String format = "SPDX";

        // Call the method to test
        Map<String, Object> result = syftTool.searchForPackage(sampleSBOM, searchPattern, format);

        // Validate the result
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.containsKey("packages"), "The result should contain a 'packages' key.");

        // Validate that the matched packages contain the search pattern
        Iterable<Map<String, Object>> matchedPackages = (Iterable<Map<String, Object>>) result.get("packages");
        assertNotNull(matchedPackages, "Matched packages should not be null.");
        assertTrue(matchedPackages.iterator().hasNext(), "There should be at least one matched package.");

        for (Map<String, Object> pkg : matchedPackages) {
            String packageName = (String) pkg.get("name");
            assertNotNull(packageName, "Package name should not be null.");
            assertTrue(packageName.contains(searchPattern),
                    "The package name should match the search pattern.");
        }
    }

    @Test
    void testSearchForPackage_WithNonMatchingPackageName() throws Exception {
        // Define the search pattern
        String searchPattern = "nonexistent-package";
        String format = "SPDX";

        // Call the method to test
        Map<String, Object> result = syftTool.searchForPackage(sampleSBOM, searchPattern, format);

        // Validate the result
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.containsKey("packages"), "The result should contain a 'packages' key.");

        // Validate that no packages match the search pattern
        Iterable<Map<String, Object>> matchedPackages = (Iterable<Map<String, Object>>) result.get("packages");
        assertNotNull(matchedPackages, "Matched packages should not be null.");
        assertFalse(matchedPackages.iterator().hasNext(), "There should be no matched packages.");
    }
}
