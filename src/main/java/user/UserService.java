package user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = LogManager.getLogger(UserService.class);
  private final Database database = new Database();

  public User getUser(String username) throws IOException {
    return database.load().get(username);
  }

  public String getUsers() throws IOException {
    Map<String, User> users = database.load()
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> new User(entry.getValue().getUsername(), entry.getValue().getRole())));
    if (users.isEmpty()) {
      return "{\"message\": \"No users in the list\"}";
    }
    try {
      return objectMapper.writeValueAsString(users);
    } catch (JsonProcessingException e) {
      return "{\"error\": \"Unable to process users list\"}";
    }
  }

  public String addUser(String userJson) {
    try {
      User user = objectMapper.readValue(userJson, User.class);
      Map<String, User> users = database.load();
      users.put(user.getUsername(), user);
      database.save(users);
      return "{\"message\": \"User added successfully\"}";
    } catch (JsonProcessingException e) {
      return "{\"error\": \"Unable to add user\"}";
    } catch (IOException e) {
      e.printStackTrace();
      return "{\"error\": \"Unable to add user\"}";
    }
  }

  public String removeUser(String userName) {

    try {
      Map<String, User> users = database.load();
      users.remove(userName);
      database.save(users);
      return "{\"message\": \"User removed successfully\"}";
    } catch (IOException e) {
      e.printStackTrace();
      return "{\"error\": \"User not found\"}";
    }
  }

  public String loginUser(String userJson) throws IOException {
    try {
      User user = objectMapper.readValue(userJson, User.class);
      logger.info("Login user: " + user);
      User user1 = database.load().get(user.getUsername());
      if (user1 == null) {
        return "{\"error\": \"User not found\"}";
      } else {
        if (user1.getPassword().equals(user.getPassword())) {
          String roleResponse = String.format(
              "{\"message\": \"User logged in successfully\", \"role\": \"%s\"}", user1.getRole());
          return roleResponse;
        } else {
          return "{\"error\": \"Invalid credentials\"}";
        }
      }
    } catch (JsonProcessingException e) {
      return "{\"error\": \"Unable to process login request\"}";
    }
  }
}
