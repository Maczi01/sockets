package server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {

  private final Map<String, String> information = new ConcurrentHashMap<>();
  private final Map<String, String> commands = new ConcurrentHashMap<>();

  public Storage() {
    commands.put("uptime", "Show server uptime");
    commands.put("info", "Show server information");
    commands.put("help", "Show list of available commands");
    commands.put("stop", "Stop server");
    information.put("version", "0.1.0");
    information.put("creation date", "06.06.2024");
  }

  public Map<String, String> getCommands() {
    return commands;
  }

  public Map<String, String> getInformation() {
    return information;
  }
}
