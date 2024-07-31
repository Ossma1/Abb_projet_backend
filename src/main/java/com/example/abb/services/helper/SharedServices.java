package com.example.abb.services.helper;

import com.example.abb.exception.MissingHeaderException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Service
public class SharedServices {
    public Properties loadHeadersForEtat(String etat) throws IOException {
        Properties properties = new Properties();
        String propertiesFileName = "static/stat_" + etat + ".properties";
        try (InputStream input = new ClassPathResource(propertiesFileName).getInputStream()) {
            properties.load(input);
        }
        return properties;
    }
    public void verifyHeaders(Properties expectedHeaders, Map<String, Integer> actualHeaders) throws MissingHeaderException {
        for (String key : expectedHeaders.stringPropertyNames()) {
            if (!actualHeaders.containsKey(expectedHeaders.getProperty(key))) {
                throw new MissingHeaderException("Missing expected header: " + expectedHeaders.getProperty(key));
            }
        }
    }
}
