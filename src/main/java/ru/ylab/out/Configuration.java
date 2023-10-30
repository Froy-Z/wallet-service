package ru.ylab.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

@Getter
@AllArgsConstructor
public class Configuration {
    private String driver;
    private String url;
    private String username;
    private String password;
    private String changelog;

    public static Configuration loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new Configuration(
                properties.getProperty("database.driver"),
                properties.getProperty("database.url"),
                properties.getProperty("database.username"),
                properties.getProperty("database.password"),
                properties.getProperty("database.changelog")
        );
    }
}
