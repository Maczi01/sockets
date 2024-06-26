package user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserService {

  private List<User> users;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = LogManager.getLogger(UserService.class);
  private final Database database = new Database();

  public UserService() {
    this.users = new ArrayList<>();
    users.add(new User("admin", "admin", Role.ADMIN));
    users.add(new User("user", "user", Role.USER));

    Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getUsername, user -> user));
    database.save(userMap);
  }

  public User getUser(String username) {
    Optional<User> first = users.stream().filter(user -> user.getUsername().equals(username))
        .findFirst();
    return first.orElse(null);
  }

  public String getUsers() {
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
      users.add(user);
      return "{\"message\": \"User added successfully\"}";
    } catch (JsonProcessingException e) {
      return "{\"error\": \"Unable to add user\"}";
    }
  }

  public String removeUser(String userName) {
    Optional<User> first = users.stream().filter(user -> user.getUsername().equals(userName))
        .findFirst();
    if (first.isPresent()) {
      users.remove(first.get());
      return "{\"message\": \"User removed successfully\"}";
    }
    return "{\"error\": \"User not found\"}";
  }

  public String loginUser(String userJson) {
    try {
      User user = objectMapper.readValue(userJson, User.class);
      logger.info("Login user: " + user);
      Optional<User> first = users.stream().filter(u -> u.getUsername()
          .equals(user.getUsername().toLowerCase())).findFirst();
      if (first.isEmpty()) {
        return "{\"error\": \"User not found\"}";
      } else {
        if (first.get().getPassword().equals(user.getPassword())){
          String roleResponse = String.format("{\"message\": \"User logged in successfully\", \"role\": \"%s\"}", first.get().getRole());
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
