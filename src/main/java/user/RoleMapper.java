package user;

import db.enums.UsersRole;

public class RoleMapper {

  public static Role toRole(UsersRole usersRole) {
    if (usersRole == null) {
      return null;
    }
    switch (usersRole) {
      case USER:
        return Role.USER;
      case ADMIN:
        return Role.ADMIN;
      default:
        throw new IllegalArgumentException("Unknown role: " + usersRole);
    }
  }

  // Convert from Role (your enum) to UsersRole (jOOQ)
  public static UsersRole toUsersRole(Role role) {
    if (role == null) {
      return null;
    }
    switch (role) {
      case USER:
        return UsersRole.USER;
      case ADMIN:
        return UsersRole.ADMIN;
      default:
        throw new IllegalArgumentException("Unknown role: " + role);
    }
  }
}

