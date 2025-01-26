package com.sbomgen.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SyftToolCycloneDXTest {

    private SyftTool syftTool;
    private ObjectMapper objectMapper;
    private Map<String, Object> sampleCycloneDXSBOM;

    @BeforeEach
    void setUp() throws Exception {
        syftTool = new SyftTool();
        objectMapper = new ObjectMapper();

        // Load the sample CycloneDX JSON from the test resources
        File jsonFile = new File("src/test/resources/sample-cyclonedx.json");
        sampleCycloneDXSBOM = objectMapper.readValue(jsonFile, new TypeReference<Map<String, Object>>() {});
    }

    @Test
    void testSearchForPackage_WithMatchingComponentName() throws Exception {
        // Define the search pattern
        String searchPattern = "library-name";
        String format = "CycloneDX";

        // Call the method to test
        Map<String, Object> result = syftTool.searchForPackage(sampleCycloneDXSBOM, searchPattern, format);

        // Validate the result
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.containsKey("components"), "The result should contain a 'components' key.");

        // Validate that the matched components contain the search pattern
        List<Map<String, Object>> matchedComponents = (List<Map<String, Object>>) result.get("components");
        assertNotNull(matchedComponents, "Matched components should not be null.");
        assertFalse(matchedComponents.isEmpty(), "There should be at least one matched component.");

        for (Map<String, Object> component : matchedComponents) {
            String componentName = (String) component.get("name");
            assertNotNull(componentName, "Component name should not be null.");
            assertTrue(componentName.contains(searchPattern),
                    "The component name should match the search pattern.");
        }
    }

    @Test
    void testSearchForPackage_WithNonMatchingComponentName() throws Exception {
        // Define the search pattern
        String searchPattern = "nonexistent-component";
        String format = "CycloneDX";

        // Call the method to test
        Map<String, Object> result = syftTool.searchForPackage(sampleCycloneDXSBOM, searchPattern, format);

        // Validate the result
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.containsKey("components"), "The result should contain a 'components' key.");

        // Validate that no components match the search pattern
        List<Map<String, Object>> matchedComponents = (List<Map<String, Object>>) result.get("components");
        assertNotNull(matchedComponents, "Matched components should not be null.");
        assertTrue(matchedComponents.isEmpty(), "There should be no matched components.");
    }
}
