package user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import message.Message;

public class User {

  private String username;
  private String password;
  private Role role;
  private List<Message> messages;

  public User(
      @JsonProperty("username") String username,
      @JsonProperty("password") String password,
      @JsonProperty("role") Role role) {
    this.username = username;
    this.password = password;
    this.role = role != null ? role : Role.USER;
    this.messages = new ArrayList<>();
  }

  public User(String username, Role role) {
    this.username = username;
    this.role = role;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public List<Message> getMessages() {
    return messages;
  }

  @Override
  public String toString() {
    return "User{" +
        "username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", role=" + role +
        '}';
  }
}