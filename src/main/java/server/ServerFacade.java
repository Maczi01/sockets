package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import message.MessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.UserService;

public class ServerFacade {

  private static final Logger logger = LogManager.getLogger(ServerFacade.class);
  private final Instant creationTime;
  private final Storage storage;
  private final UserService userService;
  private final MessageService messageService = new MessageService();
  private final Response response = new Response();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ServerFacade(Storage storage, UserService userService, Instant creationTime) {
    this.storage = storage;
    this.userService = userService;
    this.creationTime = creationTime;
  }

  public String handleRequest(String request, String payload) throws IOException {
    ServerCommand command;
    try {
      command = ServerCommand.valueOf(request.toUpperCase());
    } catch (IllegalArgumentException e) {
      return "{\"error\": \"Incorrect command, try again\"}";
    }

    return switch (command) {
      case UPTIME -> response.calculateServerTime(creationTime);
      case HELP -> response.getCommands(storage.getCommands());
      case INFO -> response.getInformation(storage.getInformation());
      case USERS -> userService.getUsers();
      case ADD_USER -> userService.addUser(payload);
      case REMOVE_USER -> userService.removeUser(payload);
      case LOGIN -> userService.loginUser(payload);
      case STOP -> stopServer();
      case READ_MESSAGES -> messageService.readMessages(payload);
      case SEND_MESSAGE -> messageService.sendMessage(payload);
      default -> "{\"error\": \"Incorrect command, try again\"}";
    };
  }

  private String stopServer() {
    try {
      logger.info("Connection closed");
      Map<String, String> result = Map.of("message", "Connection closed");
      return objectMapper.writeValueAsString(result);
    } catch (JsonProcessingException e) {
      logger.error("Error closing server: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
