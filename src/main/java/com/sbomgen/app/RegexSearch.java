package com.sbomgen.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSearch {
    /**
     * Filters the list of strings based on the given regex pattern.
     *
     * @param inputList the list of strings to search within
     * @param regex     the regex pattern to use for matching
     * @return a list of strings that match the regex
     */
    public static List<Map<String, Object>> searchWithRegex(List<Map<String, Object>> inputList, String regex,
                                                            String packageNameKey) {
        if (inputList == null || regex == null) {
            throw new IllegalArgumentException("Input list and regex pattern cannot be null");
        }

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        List<Map<String, Object>> matchedStrings = new ArrayList<>();
        // Filter the list using regex and collect the matching items
        // Loop through each string in the list
        for (Map<String, Object> packageObj : inputList) {
            Matcher matcher = pattern.matcher((String) packageObj.get(packageNameKey));
            if (matcher.find()) {
                matchedStrings.add(packageObj);
            }
        }

        return matchedStrings;
    }
}