package com.example.mindjavafx.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();
    private static boolean loaded = false;

    private static void load() {
        if (loaded) return;
        
        String[] possiblePaths = {"config.properties", "src/main/resources/config.properties", "../config.properties"};
        
        for (String path : possiblePaths) {
            java.io.File configFile = new java.io.File(path);
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    properties.load(fis);
                    System.out.println("[ConfigLoader] Properties loaded from: " + configFile.getAbsolutePath());
                    loaded = true;
                    return;
                } catch (IOException e) {
                    System.err.println("[ConfigLoader] Error loading " + path + ": " + e.getMessage());
                }
            }
        }

        // Try ClassLoader
        try (InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                properties.load(is);
                System.out.println("[ConfigLoader] Properties loaded via ClassLoader.");
                loaded = true;
            }
        } catch (Exception e) {
            System.err.println("[ConfigLoader] Error via ClassLoader: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        load();
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        load();
        return properties.getProperty(key, defaultValue);
    }
}
