import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Response {

  private final ObjectMapper mapper = new ObjectMapper();

  public String calculateServerTime(Instant startTime) throws JsonProcessingException {
    Duration serverUptime = Duration.between(startTime, Instant.now());
    int hours = serverUptime.toHoursPart();
    int minutes = serverUptime.toMinutesPart();
    int seconds = serverUptime.toSecondsPart();

    String uptimeMessage = String.format("Server was started %d hours, %d minutes, %d seconds ago",
        hours, minutes, seconds);
    Map<String, String> uptimeResponse = new ConcurrentHashMap<>();
    uptimeResponse.put("info", uptimeMessage);

    return mapper.writeValueAsString(uptimeResponse);
  }

  public String getCommands(Map<String, String> commandsInfo)
      throws JsonProcessingException {
    return mapper.writeValueAsString(commandsInfo);
  }

  public String getInformation(Map<String, String> information) throws JsonProcessingException {
    return mapper.writeValueAsString(information);
  }
}
