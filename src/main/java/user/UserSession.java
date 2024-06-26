package user;

public class UserSession {
  private boolean loggedIn;
  private String username;
  private Role role;

  public UserSession() {
    this.loggedIn = false;
    this.username = null;
    this.role = Role.USER;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public void login(String username, Role role) {
    this.loggedIn = true;
    this.username = username;
    this.role = role;
  }

  public void logout() {
    this.loggedIn = false;
    this.username = null;
    this.role = Role.USER;
  }

  public String getUsername() {
    return username;
  }

  public Role getRole() {
    return role;
  }
}
