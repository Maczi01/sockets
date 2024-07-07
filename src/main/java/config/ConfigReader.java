package config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class ConfigReader {

  public static Config loadConfig() {
    ObjectMapper objectMapper = new ObjectMapper();
    Config config = new Config();
    try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("config.json")) {
      if (inputStream == null) {
        throw new IOException("Sorry, unable to find config.json");
      }
      JsonNode rootNode = objectMapper.readTree(inputStream);

      config.setClientIp(rootNode.path("clientIp").asText());
      config.setPort(rootNode.path("port").asInt());

      return config;
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
