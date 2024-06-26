package message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import user.User;
import user.UserService;

public class MessageService {


  private static final int MAILBOX_SIZE = 2;
  private static final int MESSAGE_SIZE = 255;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final UserService userService = new UserService();



  public String sendMessage(String payload) {
    try {
      Message message = objectMapper.readValue(payload, Message.class);
      String receiver = message.getReceiver();
      List<Message> messageList = userService.getUser(receiver).getMessages();
      if (messageList.size() >= MAILBOX_SIZE) {
        return "{\"error\": \"Receiver mailbox is full, cannot send message\"}";
      }
      if(message.getContent().length() >= MESSAGE_SIZE){
        return "{\"error\": \"Message is too long\"}";
      }
      messageList.add(message);
      return "{\"message\": \"Message sent\"}";
    } catch (JsonProcessingException e) {
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
      if (messages.size() == 0) {
        return "{\"error\": \"Mailbox is empty\"}";
      }
      return objectMapper.writeValueAsString(messages);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "{\"error\": \"Unable to process messages\"}";
    }
  }
}
