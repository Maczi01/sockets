package server;

public enum Command {
  UPTIME("uptime"),
  INFO("info"),
  HELP("help"),
  STOP("stop");

  private final String command;

  Command(String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }
}