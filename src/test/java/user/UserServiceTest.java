package user;

import static db.Tables.MESSAGES;
import static db.Tables.USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import database.Database;
import java.io.IOException;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

  private UserService userService;
  private Database database;

  @BeforeEach
  void setUp() {
    userService = new UserService();
    database = new Database();
    clearDatabase();
  }

  private void clearDatabase() {
    DSLContext create = database.getDSLContext();

    create.deleteFrom(MESSAGES).execute();
    create.deleteFrom(USERS).execute();
  }

  private String addUser(String username, String password, String role) {
    String userJson = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}", username, password, role);
    return userService.addUser(userJson);
  }

  @Test
  @DisplayName("Should correctly add user with role user to database and return success message")
  void shouldAddUserWithRoleUserToDatabase() {
    // given
    // when
    String result = addUser("john", "pass", "USER");

    // then
    assertEquals("{\"message\": \"User added successfully\"}", result);
  }

  @Test
  @DisplayName("Should correctly add user with role user to database and return success message")
  void shouldAddUserWithRoleAdminToDatabase() {
    // given
    // when
    String result = addUser("john", "pass", "ADMIN");

    // then
    assertEquals("{\"message\": \"User added successfully\"}", result);
  }

  @Test
  @DisplayName("Should not correctly add user with unknown role to database and return error message")
  void shouldNotAddUserWithUnknownRoleToDatabase() {
    // given
    // when
    String result = addUser("john", "pass", "DRIVER");

    // then
    assertEquals("{\"error\": \"Unable to add user\"}", result);
  }

  @Test
  @DisplayName("Should not correctly add user if password is too short and return error message")
  void shouldNotAddUserToDatabaseIfPasswordTooShort() {
    // given
    // when
    String result = addUser("john", "p", "ADMIN");

    // then
    assertEquals("{\"error\": \"Password length must be at least 4 characters\"}", result);
  }

  @Test
  @DisplayName("Should not correctly add user with unknown format to database and return error message")
  void shouldNotAddUserWithUnknownFormatToDatabase() {
    // given
    String userJson = "{\"name\":\"john\",\"pw\":\"pass\",\"role\":\"ADMIN\"}";

    // when
    String result = userService.addUser(userJson);

    // then
    assertEquals("{\"error\": \"Unable to add user\"}", result);
  }

  @Test
  @DisplayName("Should correctly log in user with correct credentials and return success message")
  void shouldSuccessfullyLoginToAppWithCorrectCredentials() {
    // given
    addUser("john", "pass", "ADMIN");

    // when
    String loginResult = userService.loginUser("{\"username\":\"john\",\"password\":\"pass\"}");

    // then
    assertEquals("{\"message\": \"User logged in successfully\", \"role\": \"ADMIN\"}", loginResult);
  }

  @Test
  @DisplayName("Should not log in user with incorrect credentials and return error message")
  void shouldUnsuccessfullyLoginToAppWithIncorrectCredentials() {
    // given
    addUser("john", "pass", "USER");

    // when
    String result = userService.loginUser("{\"username\":\"john\",\"password\":\"wrong\"}");

    // then
    assertEquals("{\"error\": \"Invalid credentials\"}", result);
  }

  @Test
  @DisplayName("Should not add two users with the same names and return error message")
  void shouldNotAddTwoUsersWithSameNames() {
    // given
    // when
    addUser("john", "pass", "USER");
    String resultOfRepeatedUser = addUser("john", "pass", "USER");

    // then
    assertEquals("{\"error\": \"User already exists\"}", resultOfRepeatedUser);
  }

  @Test
  @DisplayName("Should return empty list")
  void shouldCorrectlyReturnEmptyList() {
    // given
    // when
    String result = userService.getUsers();

    // then
    assertEquals("{\"message\": \"No users in the list\"}", result);
  }

  @Test
  @DisplayName("Should return list with five users")
  void shouldCorrectlyReturnListWithFiveUsers() {
    // given
    String userJsonOne = "{\"username\":\"john\",\"password\":\"pass\",\"role\":\"ADMIN\"}";
    String userJsonTwo = "{\"username\":\"colin\",\"password\":\"pass\",\"role\":\"ADMIN\"}";
    String userJsonThree = "{\"username\":\"Calum\",\"password\":\"pass\",\"role\":\"ADMIN\"}";
    String userJsonFour = "{\"username\":\"Declan\",\"password\":\"pass\",\"role\":\"ADMIN\"}";
    String userJsonFive = "{\"username\":\"Mike\",\"password\":\"pass\",\"role\":\"ADMIN\"}";

    // when
    userService.addUser(userJsonOne);
    userService.addUser(userJsonTwo);
    userService.addUser(userJsonThree);
    userService.addUser(userJsonFour);
    userService.addUser(userJsonFive);
    String result = userService.getUsers();

    // then
    assertEquals(
        "{\"Declan\":{\"username\":\"Declan\",\"password\":null,\"role\":\"ADMIN\",\"messages\":null},\"colin\":{\"username\":\"colin\",\"password\":null,\"role\":\"ADMIN\",\"messages\":null},\"Mike\":{\"username\":\"Mike\",\"password\":null,\"role\":\"ADMIN\",\"messages\":null},\"john\":{\"username\":\"john\",\"password\":null,\"role\":\"ADMIN\",\"messages\":null},\"Calum\":{\"username\":\"Calum\",\"password\":null,\"role\":\"ADMIN\",\"messages\":null}}",
        result);
  }

  @Test
  @DisplayName("Should correctly return list with one user after delete")
  void shouldCorrectlyRemoveUser() {
    // given
    String userJsonOne = "{\"username\":\"john\",\"password\":\"pass\",\"role\":\"ADMIN\"}";
    String userJsonTwo = "{\"username\":\"colin\",\"password\":\"pass\",\"role\":\"ADMIN\"}";

    // when
    userService.addUser(userJsonOne);
    userService.addUser(userJsonTwo);
    String resultBeforeRemove = userService.getUsers();
    String confirmRemove = userService.removeUser("john");
    String resultAfterRemove = userService.getUsers();

    // then
    assertEquals(
        "{\"colin\":{\"username\":\"colin\",\"password\":null,\"role\":\"ADMIN\",\"messages\":null},\"john\":{\"username\":\"john\",\"password\":null,\"role\":\"ADMIN\",\"messages\":null}}",
        resultBeforeRemove);
    assertEquals(
        "{\"colin\":{\"username\":\"colin\",\"password\":null,\"role\":\"ADMIN\",\"messages\":null}}",
        resultAfterRemove);
    assertEquals("{\"message\": \"User removed successfully\"}", confirmRemove);
  }


}


