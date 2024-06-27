package client;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Client {

  private static final Logger logger = LogManager.getLogger(Client.class);
  private static final ClientFacade clientFacade = new ClientFacade();

  public static void main(String[] args) {
    try {
      clientFacade.startConnection();
      clientFacade.handleUserInput();
    } catch (IOException e) {
      logger.error("Connection error " + e.getMessage());
    } finally {
      try {
        clientFacade.stopConnection();
      } catch (IOException e) {
        logger.error("Error closing connection: " + e.getMessage());
      }
    }
  }
}