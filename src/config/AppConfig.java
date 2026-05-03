package config;

import util.LogManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final Properties props = new Properties();
    private static final String CONFIG_FILE = "/config/app.properties";

    static {
        try (InputStream is = AppConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (is != null) props.load(is);
        } catch (IOException e) {
            LogManager.error("Errore caricamento configurazione", e);
        }
    }

    private AppConfig() {}

    public static String get(String key) {
        return props.getProperty(key);
    }
}