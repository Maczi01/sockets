package server;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import user.UserService;

public class Server {

  private static final Logger logger = LogManager.getLogger(Server.class);
  private final ServerSocket server;
  private static final int PORT = 5000;
  private final ServerFacade serverFacade;

  public Server() {
    try {
      server = new ServerSocket(PORT);
    } catch (IOException e) {
      logger.error("Error creating server: " + e.getMessage());
      throw new RuntimeException(e);
    }
    Storage storage = new Storage();
    UserService userService = new UserService();
    Instant creationTime = Instant.now();
    serverFacade = new ServerFacade(storage, userService, creationTime);
    logger.info("Server successfully started on port " + PORT);
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
      while (!Objects.isNull(nextLine = input.readLine())) {
        logger.info("Incoming request: " + nextLine);
        try {
          String[] commandParts = nextLine.trim().split(" ", 2);
          String command = commandParts[0].toUpperCase();
          String payload = commandParts.length > 1 ? commandParts[1] : null;

          if (command == null) {
            output.println("{\"error\": \"Incorrect command, try again\"}");
          } else {
            String response = serverFacade.handleRequest(command, payload);
            output.println(response);
          }
        } catch (JsonProcessingException e) {
          logger.error("Error processing request: " + e.getMessage());
          output.println("{\"error\": \"Internal server error\"}");
        }
      }
    } catch (IOException e) {
      logger.error("Cannot connect client: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
