package message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

  private String content;
  private String sender;
  private String receiver;
  private String createdDate;

  public Message(
      @JsonProperty("content") String content,
      @JsonProperty("sender") String sender,
      @JsonProperty("receiver") String receiver,
      @JsonProperty("createdDate") String date) {
    this.content = content;
    this.sender = sender;
    this.receiver = receiver;
    this.createdDate = date;
  }

  public Message() {
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

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }
}
