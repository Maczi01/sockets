package client;

enum ClientCommand {
  UPTIME("UPTIME"),
  INFO("INFO"),
  HELP("HELP"),
  REMOVE_USER("REMOVE_USER"),
  USERS("USERS"),
  SEND_MESSAGE("SEND_MESSAGE"),
  LOGIN("LOGIN"),
  READ_MESSAGES("READ_MESSAGES"),
  ADD_USER("ADD_USER");

  ClientCommand(String info) {}
}
