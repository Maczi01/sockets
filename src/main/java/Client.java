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

public class Client {

  private static final Logger logger = LogManager.getLogger(Client.class);
  private static final Scanner scanner = new Scanner(System.in);
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String CLIENT_IP = "127.0.0.1";
  private static final int PORT = 5000;

  public static void main(String[] args) {

    Client client = new Client();
    try {
      client.startConnection();
    } catch (RuntimeException | IOException e) {
      logger.error("connection error" + e.getMessage());
    }
  }

  public void startConnection() throws IOException {
    try (
        Socket clientSocket = new Socket(CLIENT_IP, PORT);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()))
    ) {
      logger.info("Client successfully connected");
      String input;
      while (!clientSocket.isClosed()) {
        logger.info("Type one of the options: \n uptime \n info \n help \n stop \n");
        input = scanner.nextLine();
        try {
          Command command = Command.valueOf(input.trim().toUpperCase());
          switch (command) {
            case UPTIME, INFO, HELP -> messageServer(out, in, command);
            case STOP -> {
              messageServer(out, in, command);
              logger.info("connection stopped");
              return;
            }
            default -> logger.info("request unknown");
          }
        } catch (IllegalArgumentException e) {
          logger.error("Invalid command: " + input);
          logger.info("Incorrect command, try again.");
        }
      }
    }
  }

  private void messageServer(PrintWriter out, BufferedReader in, Command command) {
    out.println(command);
    try {
      String resp = in.readLine();
      handleResponse(resp);
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
}