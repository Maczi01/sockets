package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class Client {

  private static final Logger logger = LogManager.getLogger(Client.class);
  private static final Scanner scanner = new Scanner(System.in);
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String CLIENT_IP = "127.0.0.1";
  private static final int PORT = 5000;
  private UserSession session;

  public Client() {
    this.session = new UserSession();
  }

  public static void main(String[] args) {
    Client client = new Client();
    try {
      client.startConnection();
    } catch (RuntimeException | IOException e) {
      logger.error("Connection error " + e.getMessage());
    }
  }

  public void startConnection() throws IOException {
    try (
        Socket clientSocket = new Socket(CLIENT_IP, PORT);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
    ) {
      logger.info("Client successfully connected");
      String input;
      while (!clientSocket.isClosed()) {
        displayMenu();
        input = scanner.nextLine();
        try {
          int commandNumber = Integer.parseInt(input.trim());
          switch (commandNumber) {
            case 1 -> manageUsers(out, in);
            case 2 -> loginToApplication(out, in);
            case 3 -> readMessages(out, in);
            case 4 -> sendMessage(out, in);
            case 5 -> logout();
            case 9 -> manageApplicationInfo(out, in);
            case 0 -> {
              logger.info("Exiting application");
              clientSocket.close();
              return;
            }
            default -> logger.info("Unknown request. Please enter a valid option.");
          }
        } catch (NumberFormatException e) {
          logger.error("Invalid input: " + input);
          logger.info("Invalid input, please enter a number.");
        }
      }
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

  private void displayMenu() {
    if (session.isLoggedIn()) {
      if (session.getRole() == Role.ADMIN) {
        logger.info("Type one of the options: "
            + "\n (1) - manage users"
            + "\n (2) - login to application"
            + "\n (3) - read messages"
            + "\n (4) - send message"
            + "\n (5) - logout"
            + "\n (0) - exit"
            + "\n");
      } else {
        logger.info("Type one of the options: "
            + "\n (3) - read messages"
            + "\n (4) - send message"
            + "\n (5) - logout"
            + "\n (0) - exit"
            + "\n");
      }
    } else {
      logger.info("Type one of the options: "
          + "\n (1) - manage users"
          + "\n (2) - login to application"
          + "\n (9) - application info"
          + "\n (0) - exit"
          + "\n");
    }
  }

  private void loginToApplication(PrintWriter out, BufferedReader in) {
    if (session.isLoggedIn()) {
      logger.info("You are already logged in");
      return;
    }
    logger.info("Login to application");
    logger.info("Enter username: ");
    String username = scanner.nextLine();
    logger.info("Enter password: ");
    String password = scanner.nextLine();
    String userDetails = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
    String response = messageServer(out, in, "LOGIN " + userDetails);
    if (response.contains("successfully")) {
      try {
        JsonNode rootNode = mapper.readTree(response);
        String roleString = rootNode.path("role").asText();
        Role role = Role.valueOf(roleString);
        session.login(username, role);
        logger.info("Login successful. Welcome, " + username + "!");
      } catch (IOException e) {
        logger.error("Error processing login response: " + e.getMessage());
      }
    } else {
      logger.error("Login failed: " + response);
    }
  }

  private void readMessages(PrintWriter out, BufferedReader in) {
    if (session.isLoggedIn()) {
      logger.info("Read messages");
      String userDetails = String.format("{\"username\":\"%s\"}", session.getUsername());
      messageServer(out, in, "READ_MESSAGES " + userDetails);
    } else {
      logger.info("You must be logged in to read messages.");
    }
  }

  private void sendMessage(PrintWriter out, BufferedReader in) {
    if (session.isLoggedIn()) {
      logger.info("Send message");
      logger.info("Enter message: ");
      String content = scanner.nextLine();
      logger.info("Enter receiver: ");
      String receiver = scanner.nextLine();
      String userDetails = String.format("{\"content\":\"%s\", \"sender\":\"%s\", \"receiver\":\"%s\"}", content, session.getUsername(), receiver);
      messageServer(out, in, "SEND_MESSAGE " + userDetails);
    } else {
      logger.info("You must be logged in to send messages.");
    }
  }

  private void manageUsers(PrintWriter out, BufferedReader in) {
    if (session.isLoggedIn() && session.getRole() == Role.ADMIN) {
      String userInput = "";
      while (!"0".equals(userInput)) {
        logger.info("Manage Users Menu: "
            + "\n (1) - Show user list"
            + "\n (2) - Add user"
            + "\n (3) - Remove user"
            + "\n (0) - Return to main menu"
            + "\n");
        userInput = scanner.nextLine();
        try {
          int userCommand = Integer.parseInt(userInput.trim());
          switch (userCommand) {
            case 1 -> {
              logger.info("Show user list");
              messageServer(out, in, "USERS");
            }
            case 2 -> {
              logger.info("Add user selected");
              addUser(out, in);
            }
            case 3 -> {
              logger.info("Remove user selected");
              removeUser(out, in);
            }
            case 0 -> {
              logger.info("Returning to main menu");
              return;
            }
            default -> logger.info("Unknown request. Please enter 1, 2, 3, 4, or 0.");
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

  private void removeUser(PrintWriter out, BufferedReader in) {
    logger.info("Enter username: ");
    String username = scanner.nextLine();
    messageServer(out, in, "REMOVE_USER " + username);
  }

  private void addUser(PrintWriter out, BufferedReader in) {
    logger.info("Enter username: ");
    String username = scanner.nextLine();
    logger.info("Enter password: ");
    String password = scanner.nextLine();

    logger.info("Enter role (USER/ADMIN): ");
    String roleString = scanner.nextLine().toUpperCase();
    Role role = Role.valueOf(roleString);

    String userDetails = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"role\":\"%s\"}", username, password, role);
    messageServer(out, in, "ADD_USER " + userDetails);
  }

  private String messageServer(PrintWriter out, BufferedReader in, String command) {
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

  public void handleResponse(String jsonResp) {
    try {
      JsonNode rootNode = mapper.readTree(jsonResp);
      String prettyString = rootNode.toPrettyString();
      logger.info(prettyString);
    } catch (IOException e) {
      logger.error("Error processing JSON response: " + e.getMessage());
    }
  }

  private void manageApplicationInfo(PrintWriter out, BufferedReader in) {
    logger.info("Application Info Menu: "
        + "\n (1) - Show server uptime"
        + "\n (2) - Show server information"
        + "\n (3) - Show list of available commands"
        + "\n (0) - Return to main menu"
        + "\n");
    String userInput = scanner.nextLine();
    try {
      int userCommand = Integer.parseInt(userInput.trim());
      switch (userCommand) {
        case 1 -> messageServer(out, in, "UPTIME");
        case 2 -> messageServer(out, in, "INFO");
        case 3 -> messageServer(out, in, "HELP");
        case 0 -> logger.info("Returning to main menu");
        default -> logger.info("Unknown request. Please enter 1, 2, 3 or 0.");
      }
    } catch (NumberFormatException e) {
      logger.error("Invalid input: " + userInput);
      logger.info("Invalid input, please enter a number (1, 2, 3 or 0).");
    }
  }
}
