package message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.UserService;

class MessageServiceTest {

  private UserService userService;
  private MessageService messageService;
  private Database database;
  private ObjectMapper objectMapper;


  @BeforeEach
  void setUp() {
    userService = new UserService();
    database = new Database();
    messageService = new MessageService();
    objectMapper = new ObjectMapper();
    clearDatabase();
  }

  private void clearDatabase() {
    database.clear();
  }

  private String addUser(String username, String password, String role) {
    String userJson = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}", username, password, role);
    return userService.addUser(userJson);
  }

  @Test
  @DisplayName("Should correctly send message from user to admin")
  void shouldSendMessageFromUserToAdmin() {
    // given
    addUser("john", "pass", "USER");
    addUser("admin", "admin", "ADMIN");

    String messageJson = "{\"sender\":\"john\",\"receiver\":\"admin\",\"content\":\"Hello admin\"}";

    // when
    String result = messageService.sendMessage(messageJson);

    // then
    assertEquals("{\"message\": \"Message sent\"}", result);
  }

  @Test
  @DisplayName("Should correctly send message from user to admin and read it")
  void shouldSendMessageFromUserToAdminAndReadIt() throws Exception {
    // given
    addUser("john", "pass", "USER");
    addUser("admin", "admin", "ADMIN");

    String messageJson = "{\"sender\":\"john\",\"receiver\":\"admin\",\"content\":\"Hello admin\"}";
    messageService.sendMessage(messageJson);

    // when
    String result = messageService.readMessages("{\"username\":\"admin\"}");

    JsonNode resultNode = objectMapper.readTree(result);
    long createdDate = resultNode.get(0).get("createdDate").asLong();

    // then
    String expectedMessage = String.format(
        "[{\"content\":\"Hello admin\",\"sender\":\"john\",\"receiver\":\"admin\",\"createdDate\":%d}]",
        createdDate
    );
    assertEquals(expectedMessage, result);
  }

  @Test
  @DisplayName("Should not send message from user to unknown user")
  void shouldNotSendMessageFromUserToUnknownUser() {
    // given
    addUser("john", "pass", "USER");
    String messageJson = "{\"sender\":\"john\",\"receiver\":\"gall\",\"content\":\"Hello gall\"}";

    // when
    String result = messageService.sendMessage(messageJson);

    // then
    assertEquals("{\"error\": \"Receiver not found\"}", result);
  }

  @Test
  @DisplayName("Should not send message if text too long")
  void shouldNotSendMessageIfTextTooLong() {
    // given
    addUser("john", "pass", "USER");
    addUser("admin", "admin", "ADMIN");
    String messageJson = "{\"sender\":\"john\",\"receiver\":\"admin\",\"content\":\"Hello admin, I hope you are doing well. I am writing to you to ask for a favor. I need you to help me with a project that I am working on. I need you to provide me with some information that I am missing. I hope you can help me with this. Thank you very much for your help. I really appreciate it.\"}";

    // when
    String result = messageService.sendMessage(messageJson);

    // then
    assertEquals("{\"error\": \"Message is too long\"}", result);
  }

  @Test
  @DisplayName("Should not send message if mailbox is full")
  void shouldNotSendMessageIfMailboxIsFull() {
    // given
    addUser("john", "pass", "USER");
    addUser("admin", "admin", "ADMIN");
    String messageJson = "{\"sender\":\"john\",\"receiver\":\"admin\",\"content\":\"Hello admin\"}";
    int MAILBOX_CAPACITY = 5;
    for (int i = 0; i < MAILBOX_CAPACITY; i++) {
      messageService.sendMessage(messageJson);
    }

    // when
    String result = messageService.sendMessage(messageJson);

    // then
    assertEquals("{\"error\": \"Receiver mailbox is full, cannot send message\"}", result);
  }

  @Test
  @DisplayName("Should not send message if sender not found")
  void shouldNotSendMessageIfSenderNotFound() {
    // given
    addUser("john", "pass", "USER");
    String messageJson = "{\"sender\":\"gall\",\"receiver\":\"mike\",\"content\":\"Hello john\"}";

    // when
    String result = messageService.sendMessage(messageJson);

    // then
    assertEquals("{\"error\": \"Receiver not found\"}", result);
  }

  @Test
  @DisplayName("Should return message about empty mailbox if mailbox is empty")
  void shouldReturnMessageAboutEmptyMailboxIfMailboxIsEmpty() {
    // given
    addUser("john", "pass", "USER");

    // when
    String result = messageService.readMessages("{\"username\":\"john\"}");

    // then
    assertEquals("{\"message\": \"Mailbox is empty\"}", result);
  }
}
