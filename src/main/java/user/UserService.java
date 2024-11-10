package user;

import static db.tables.Users.USERS;
import static org.jooq.impl.DSL.selectFrom;
import static user.RoleMapper.toRole;
import static user.RoleMapper.toUsersRole;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = LogManager.getLogger(UserService.class);
  private final Database database = new Database();
  private final DSLContext create = database.getDSLContext();
  private final int MIN_PASSWORD_LENGTH = 4;

  public User getUser(String username) {
    return create.selectFrom(USERS)
        .where(USERS.USERNAME.eq(username))
        .fetchOne(record -> new User(
            record.getValue(USERS.USERNAME),
            toRole(record.getValue(USERS.ROLE))
        ));
  }

  public String getUsers() {
    Map<String, User> users = create.selectFrom(USERS)
        .fetchStream()
        .collect(Collectors.toMap(
            record -> record.getValue(USERS.USERNAME),
            record -> new User(
                record.getValue(USERS.USERNAME),
                toRole(record.getValue(USERS.ROLE))
            )
        ));
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

      if (user.getPassword().length() < MIN_PASSWORD_LENGTH) {
        return "{\"error\": \"Password length must be at least 4 characters\"}";
      }

      String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

      return create.transactionResult(configuration -> {
        DSLContext ctx = DSL.using(configuration);

        if (ctx.fetchExists(
            ctx.selectFrom(USERS).where(USERS.USERNAME.eq(user.getUsername()))
        )) {
          return "{\"error\": \"User already exists\"}";
        }

        // Insert the new user
        ctx.insertInto(USERS)
            .set(USERS.USERNAME, user.getUsername())
            .set(USERS.PASSWORD, hashedPassword)
            .set(USERS.ROLE, toUsersRole(user.getRole())) // Convert Role to jOOQ UsersRole
            .execute();

        return "{\"message\": \"User added successfully\"}";
      });

    } catch (JsonProcessingException e) {
      return "{\"error\": \"Unable to add user\"}";
    }
  }

  public String removeUser(String username) {
    int deletedRows = create.deleteFrom(USERS)
        .where(USERS.USERNAME.eq(username))
        .execute();

    if (deletedRows > 0) {
      return "{\"message\": \"User removed successfully\"}";
    } else {
      return "{\"error\": \"User not found\"}";
    }
  }

  public String loginUser(String userJson) {
    System.out.println("XXXXXXXXXXx");
    try {
      User user = objectMapper.readValue(userJson, User.class);
      logger.info("Login user: " + user);

      User foundUser = create.selectFrom(USERS)
          .where(USERS.USERNAME.eq(user.getUsername()))
          .fetchOne(record -> new User(
              record.getValue(USERS.USERNAME),
              toRole(record.getValue(USERS.ROLE)),
              record.getValue(USERS.PASSWORD)
          ));

      if (foundUser == null) {
        return "{\"error\": \"User not found\"}";
      }

      if (BCrypt.checkpw(user.getPassword(), foundUser.getPassword())) {
        return String.format(
            "{\"message\": \"User logged in successfully\", \"role\": \"%s\"}",
            foundUser.getRole()
        );
      } else {
        return "{\"error\": \"Invalid credentials\"}";
      }
    } catch (JsonProcessingException e) {
      return "{\"error\": \"Unable to process login request\"}";
    }
  }
}