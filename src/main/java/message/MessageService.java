package message;


import static db.tables.Messages.MESSAGES;
import static db.tables.Users.USERS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import user.User;

public class MessageService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Database database = new Database();
  private final DSLContext create = database.getDSLContext();

  private static final int MESSAGE_SIZE = 255;
  private static final int MAILBOX_SIZE = 5;

  public String sendMessage(String payload) {
    try {
      Message message = objectMapper.readValue(payload, Message.class);

      return create.transactionResult(configuration -> {
        DSLContext ctx = DSL.using(configuration);

        Integer receiverId = ctx.select(USERS.ID)
            .from(USERS)
            .where(USERS.USERNAME.eq(message.getReceiver()))
            .fetchOne(USERS.ID);

        if (receiverId == null) {
          return "{\"error\": \"Receiver not found\"}";
        }

        Integer senderId = ctx.select(USERS.ID)
            .from(USERS)
            .where(USERS.USERNAME.eq(message.getSender()))
            .fetchOne(USERS.ID);

        if (message.getContent().length() >= MESSAGE_SIZE) {
          return "{\"error\": \"Message is too long\"}";
        }

        long numberOfMessages = ctx.select(MESSAGES.ID)
            .from(MESSAGES)
            .where(MESSAGES.RECEIVER_ID.eq(receiverId))
            .stream()
            .count();

        if (numberOfMessages >= MAILBOX_SIZE) {
          return "{\"error\": \"Receiver mailbox is full, cannot send message\"}";
        }

        ctx.insertInto(MESSAGES)
            .set(MESSAGES.CONTENT, message.getContent())
            .set(MESSAGES.SENDER_ID, senderId)
            .set(MESSAGES.RECEIVER_ID, receiverId)
            .execute();

        return "{\"message\": \"Message sent\"}";
      });

    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "{\"error\": \"Unable to send message\"}";
    }
  }

  public String readMessages(String payload) {
    try {
      String userName = objectMapper.readValue(payload, User.class).getUsername();

      Integer userId = create.select(USERS.ID)
          .from(USERS)
          .where(USERS.USERNAME.eq(userName))
          .fetchOne(USERS.ID);

      if (userId == null) {
        return "{\"error\": \"User not found\"}";
      }

      var USERS_SENDER = USERS.as("USERS_SENDER");
      var USERS_RECEIVER = USERS.as("USERS_RECEIVER");

      List<Message> messages = create
          .select(MESSAGES.CONTENT,
              USERS_SENDER.USERNAME.as("sender"),
              USERS_RECEIVER.USERNAME.as("receiver"),
              MESSAGES.CREATED_DATE)
          .from(MESSAGES)
          .join(USERS_SENDER).on(MESSAGES.SENDER_ID.eq(USERS_SENDER.ID))
          .join(USERS_RECEIVER).on(MESSAGES.RECEIVER_ID.eq(USERS_RECEIVER.ID))
          .fetch()
          .stream()
          .map(record -> new Message(
              record.get(MESSAGES.CONTENT),
              record.get("sender", String.class),
              record.get("receiver", String.class),
              record.get(MESSAGES.CREATED_DATE).toString()
          ))
          .collect(Collectors.toList());

      if (messages.isEmpty()) {
        return "{\"message\": \"Mailbox is empty\"}";
      }

      return objectMapper.writeValueAsString(messages);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "{\"error\": \"Unable to process messages\"}";
    }
  }
}