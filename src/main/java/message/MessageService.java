package message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import user.User;
import user.UserService;

public class MessageService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final UserService userService = new UserService();
  private final Database database = new Database();
  private static final int MAILBOX_SIZE = 2;
  private static final int MESSAGE_SIZE = 255;

  public String sendMessage(String payload) {
    try {
      Message message = objectMapper.readValue(payload, Message.class);
      String receiver = message.getReceiver();
      Map<String, User> users = database.load();
      if (!users.containsKey(receiver)) {
        return "{\"error\": \"Receiver not found\"}";
      }
      List<Message> messages = users.get(receiver).getMessages();
      if (messages.size() >= MAILBOX_SIZE) {
        return "{\"error\": \"Receiver mailbox is full, cannot send message\"}";
      }
      if(message.getContent().length() >= MESSAGE_SIZE){
        return "{\"error\": \"Message is too long\"}";
      }
      messages.add(message);
      database.save(users);
      return "{\"message\": \"Message sent\"}";
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "{\"error\": \"Unable to sent message\"}";
    } catch (IOException e) {
      e.printStackTrace();
      return "{\"error\": \"Unable to sent message\"}";
    }
  }

  public String readMessages(String payload) {
    try {
      String userName = objectMapper.readValue(payload, User.class).getUsername();
      User user = userService.getUser(userName);
      if (user == null) {
        return "{\"error\": \"User not found\"}";
      }
      List<Message> messages = user.getMessages().stream().toList();
      if (messages.isEmpty()) {
        return "{\"message\": \"Mailbox is empty\"}";
      }
      return objectMapper.writeValueAsString(messages);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "{\"error\": \"Unable to process messages\"}";
    } catch (IOException e) {
      e.printStackTrace();
      return "{\"error\": \"Unable to process messages\"}";
    }
  }
}
