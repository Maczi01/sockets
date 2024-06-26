package database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import user.User;

public class Database {

  private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final String FILE_PATH = "./db.json";

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
      e.printStackTrace();
    }
  }
}