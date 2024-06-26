package message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Date;

public class Message {

  private String content;
  private String sender;
  private String receiver;
  private Date createdDate;

  public Message(
      @JsonProperty("content") String content,
      @JsonProperty("sender") String sender,
      @JsonProperty("receiver") String receiver) {
    this.content = content;
    this.sender = sender;
    this.receiver = receiver;
    this.createdDate = Date.from(Instant.now());
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }
}
