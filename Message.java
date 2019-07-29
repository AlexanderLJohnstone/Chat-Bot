public class Message {
    private String sender;
    private long timeStamp;
    private String content;

    //constructor
    public Message(String sender, long timeStamp, String content) {
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.content = content;
    }

    //getters

    public String getSender() {
        return sender;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getContent() {
        return content;
    }

    //setters

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
