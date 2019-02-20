package com.moventisusa.carpoolmatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

@Configuration
public class ApplicationConfig {

    private static String mapApiKey;

    private static Properties properties;

    private static final String CONFIG_FILE = "config.properties";
    private static final String MAPS_API_KEY = "mapApiKey";

    public ApplicationConfig() {
        properties = loadProperties();
        mapApiKey = properties.getProperty(MAPS_API_KEY);
    }

    public String getMapApiKey() {
        return mapApiKey;
    }

    private Properties loadProperties() {

        Properties props = new Properties();

        try {
            File resource = new ClassPathResource(CONFIG_FILE).getFile();
            FileReader in = new FileReader(resource);
            props.load(in);
        } catch (Exception e) {
            System.out.println("Error: Could not read api key properties file");
        }
        return props;
    }
}
