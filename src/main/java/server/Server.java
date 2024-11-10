package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.Role;
import user.User;
import user.UserService;

public class Server {

  private static final Logger logger = LogManager.getLogger(Server.class);
  private final ServerSocket server;
  private static final int PORT = 5000;
  private final ServerFacade serverFacade;
  private final UserService userService;

  public Server() {
    try {
      server = new ServerSocket(PORT);
    } catch (IOException e) {
      logger.error("Error creating server: " + e.getMessage());
      throw new RuntimeException(e);
    }
    userService = new UserService();
    Instant creationTime = Instant.now();
    serverFacade = new ServerFacade(new Storage(), userService, creationTime);

    // Add two user records to the database
    initializeUsers();

    logger.info("Server successfully started on port " + PORT);
  }

  // Method to initialize users in the database
  private void initializeUsers() {
    try {
      User user1 = new User("john", "password", Role.USER);
      User user2 = new User("admin", "admin", Role.ADMIN);

      // Convert to JSON and add users using UserService
      userService.addUser(userToJson(user1));
      userService.addUser(userToJson(user2));

      logger.info("Added default users to the database.");
    } catch (Exception e) {
      logger.error("Error initializing default users: " + e.getMessage(), e);
    }
  }

  // Helper method to convert a User object to JSON format
  private String userToJson(User user) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(user);
  }

  public static void main(String[] args) {
    Server server = new Server();
    try {
      server.run();
    } catch (RuntimeException e) {
      logger.error("Error starting connection: " + e.getMessage());
    }
  }

  public void run() {
    try (
        Socket client = server.accept();
        PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()))
    ) {
      logger.info("Client connected");
      String nextLine;
      while ((nextLine = input.readLine()) != null) {
        logger.info("Incoming request: " + nextLine);
        try {
          String[] commandParts = nextLine.trim().split(" ", 2);
          String command = commandParts[0].toUpperCase();
          String payload = commandParts.length > 1 ? commandParts[1] : null;

          if (command == null) {
            throw new InvalidInputException("Incorrect command, try again");
          }

          String response = serverFacade.handleRequest(command, payload);
          output.println(response);

        } catch (UserNotFoundException | InvalidInputException e) {
          logger.error("Error: " + e.getMessage());
          output.println("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (JsonProcessingException e) {
          logger.error("Error processing request: " + e.getMessage());
          output.println("{\"error\": \"Internal server error\"}");
        } catch (Exception e) {
          logger.error("Unexpected error: " + e.getMessage(), e);
          output.println("{\"error\": \"Unexpected server error\"}");
        }
      }
    } catch (IOException e) {
      logger.error("Cannot connect client: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}