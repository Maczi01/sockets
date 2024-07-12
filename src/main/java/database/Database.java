package database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.User;

public class Database {

  private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final String FILE_PATH = "./db.json";
  private static final Logger logger = LogManager.getLogger(Database.class);

  public Map<String, User> load() throws IOException {
    File file = new File(FILE_PATH);
    if (file.exists() && file.length() != 0) {
      return OBJECT_MAPPER.readValue(file, new TypeReference<Map<String, User>>() {
      });
    } else {
      return new HashMap<>();
    }
  }

  public void save(Map<String, User> users)  {
    File file = new File(FILE_PATH);
    OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    try {
      OBJECT_MAPPER.writerFor(new TypeReference<Map<String, User>>() {
      }).writeValue(file, users);
    } catch (IOException e) {
      logger.error("Unable to save user: " + e.getMessage());
    }
  }

  public void clear() {
    File file = new File(FILE_PATH);
    try {
      OBJECT_MAPPER.writerFor(new TypeReference<Map<String, User>>() {
      }).writeValue(file, new HashMap<>());
    } catch (IOException e) {
      logger.error("Unable to clear database: " + e.getMessage());
    }
  }
}