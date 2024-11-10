package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.Config;
import config.ConfigReader;
import exceptions.InvalidInputException;
import exceptions.ServerException;
import exceptions.UserNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.Role;
import user.UserSession;

class ClientFacade {

  private static final Logger logger = LogManager.getLogger(ClientFacade.class);
  private static final Scanner scanner = new Scanner(System.in);
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final UserSession session = new UserSession();
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;

  void startConnection() throws IOException {
    Config config = ConfigReader.loadConfig();
    int port = config.getPort();
    String clientIp = config.getClientIp();
    clientSocket = new Socket(clientIp, port);
    out = new PrintWriter(clientSocket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    logger.info("Client successfully connected");
  }

  void stopConnection() throws IOException {
    if (clientSocket != null) {
      clientSocket.close();
      logger.info("Client disconnected");
    }
  }

  void handleUserInput() throws IOException {
    String input;
    while (!clientSocket.isClosed()) {
      displayMainMenu();
      input = scanner.nextLine();
      try {
        if (processUserCommand(input)) {
          return;
        }
      } catch (NumberFormatException e) {
        logger.error("Invalid input: " + input);
        logger.info("Invalid input, please enter a number.");
      }
    }
  }

  private boolean processUserCommand(String input) throws IOException {
    int commandNumber = Integer.parseInt(input.trim());
    switch (commandNumber) {
      case 1 -> manageUsers();
      case 2 -> loginToApplication();
      case 3 -> readMessages();
      case 4 -> sendMessage();
      case 5 -> logout();
      case 9 -> manageApplicationInfo();
      case 0 -> {
        logger.info("Exiting application");
        stopConnection();
        return true;
      }
      default -> logger.info("Unknown request. Please enter a valid option.");
    }
    return false;
  }

  private void displayMainMenu() {
    if (session.isLoggedIn()) {
      if (session.getRole() == Role.ADMIN) {
        displayAdminMenu();
      } else {
        displayUserMenu();
      }
    } else {
      displayGuestMenu();
    }
  }

  private void loginToApplication() {
    if (session.isLoggedIn()) {
      throw new InvalidInputException("You are already logged in");
    }
    logger.info("Login to application");
    logger.info("Enter username: ");
    String username = scanner.nextLine().toLowerCase();
    logger.info("Enter password: ");
    String password = scanner.nextLine();
    String userDetails = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
    String response = messageServer("LOGIN " + userDetails);

    if (response.contains("successfully")) {
      try {
        JsonNode rootNode = mapper.readTree(response);
        String roleString = rootNode.path("role").asText();
        Role role = Role.valueOf(roleString);
        session.login(username, role);
        logger.info("Login successful. Welcome, " + username + "!");
      } catch (IOException e) {
        throw new ServerException("Error processing login response", e);
      }
    } else {
      throw new UserNotFoundException("Login failed: User not found");
    }
  }

  private void readMessages() {
    if (session.isLoggedIn()) {
      logger.info("Read messages");
      String userDetails = String.format("{\"username\":\"%s\"}", session.getUsername());
      messageServer(ClientCommand.READ_MESSAGES + " " + userDetails);
    } else {
      logger.info("You must be logged in to read messages.");
    }
  }

  private void sendMessage() {
    if (session.isLoggedIn()) {
      logger.info("Send message");
      logger.info("Enter message: ");
      String content = scanner.nextLine();
      logger.info("Enter receiver: ");
      String receiver = scanner.nextLine();
      String userDetails = String.format("{\"content\":\"%s\", \"sender\":\"%s\", \"receiver\":\"%s\"}", content, session.getUsername(), receiver);
      messageServer(ClientCommand.SEND_MESSAGE + " " + userDetails);
    } else {
      logger.info("You must be logged in to send messages.");
    }
  }

  private void manageUsers() {
    if (session.isLoggedIn() && session.getRole() == Role.ADMIN) {
      String userInput = "";
      while (!"0".equals(userInput)) {
        displayManageUsersMenu();
        userInput = scanner.nextLine();
        try {
          int userCommand = Integer.parseInt(userInput.trim());
          switch (userCommand) {
            case 1 -> {
              logger.info("Show user list");
              messageServer(ClientCommand.USERS.toString());
            }
            case 2 -> {
              logger.info("Add user selected");
              addUser();
            }
            case 3 -> {
              logger.info("Remove user selected");
              removeUser();
            }
            case 0 -> {
              logger.info("Returning to main menu");
              return;
            }
            default -> logger.info("Unknown request. Please enter 1, 2, 3, or 0.");
          }
        } catch (NumberFormatException e) {
          logger.error("Invalid input: " + userInput);
          logger.info("Invalid input, please enter a number (1, 2, 3, or 0).");
        }
      }
    } else {
      logger.info("You must be logged in as an admin to manage users.");
    }
  }

  private void removeUser() {
    logger.info("Enter username: ");
    String username = scanner.nextLine();
    messageServer(ClientCommand.REMOVE_USER + " " + username);
  }

  private void addUser() {
    logger.info("Enter username: ");
    String username = scanner.nextLine();
    logger.info("Enter password: ");
    String password = scanner.nextLine();
    logger.info("Enter role (USER/ADMIN): ");
    String roleString = scanner.nextLine().toUpperCase();
    Role role = Role.valueOf(roleString);
    String userDetails = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"role\":\"%s\"}", username, password, role);
    messageServer(ClientCommand.ADD_USER + " " + userDetails);
  }

  private void manageApplicationInfo() {
    displayApplicationInfoMenu();
    String userInput = scanner.nextLine();
    try {
      int userCommand = Integer.parseInt(userInput.trim());
      switch (userCommand) {
        case 1 -> messageServer(ClientCommand.UPTIME.toString());
        case 2 -> messageServer(ClientCommand.INFO.toString());
        case 3 -> messageServer(ClientCommand.HELP.toString());
        case 0 -> logger.info("Returning to main menu");
        default -> logger.info("Unknown request. Please enter 1, 2, 3 or 0.");
      }
    } catch (NumberFormatException e) {
      logger.error("Invalid input: " + userInput);
      logger.info("Invalid input, please enter a number (1, 2, 3 or 0).");
    }
  }

  private String messageServer(String command) {
    out.println(command);
    try {
      String resp = in.readLine();
      handleResponse(resp);
      return resp;
    } catch (IOException e) {
      logger.error("Error reading message from server: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private void handleResponse(String jsonResp) {
    try {
      JsonNode rootNode = mapper.readTree(jsonResp);
      String prettyString = rootNode.toPrettyString();
      logger.info(prettyString);
    } catch (IOException e) {
      logger.error("Error processing JSON response: " + e.getMessage());
    }
  }

  private void logout() {
    if (session.isLoggedIn()) {
      session.logout();
      logger.info("Successfully logged out");
    } else {
      logger.info("You are not logged in");
    }
  }

  private void displayApplicationInfoMenu() {
    logger.info("Application Info Menu: "
        + "\n (1) - Show server uptime"
        + "\n (2) - Show server information"
        + "\n (3) - Show list of available commands"
        + "\n (0) - Return to main menu"
        + "\n");
  }

  private void displayManageUsersMenu() {
    logger.info("Manage Users Menu: "
        + "\n (1) - Show user list"
        + "\n (2) - Add user"
        + "\n (3) - Remove user"
        + "\n (0) - Return to main menu"
        + "\n");
  }


  private void displayAdminMenu() {
    logger.info("Type one of the options: "
        + "\n (1) - manage users"
        + "\n (2) - login to application"
        + "\n (3) - read messages"
        + "\n (4) - send message"
        + "\n (5) - logout"
        + "\n (0) - exit"
        + "\n");
  }

  private void displayUserMenu() {
    logger.info("Type one of the options: "
        + "\n (3) - read messages"
        + "\n (4) - send message"
        + "\n (5) - logout"
        + "\n (0) - exit"
        + "\n");
  }

  private void displayGuestMenu() {
    logger.info("Type one of the options: "
        + "\n (1) - manage users"
        + "\n (2) - login to application"
        + "\n (9) - application info"
        + "\n (0) - exit"
        + "\n");
  }
}
