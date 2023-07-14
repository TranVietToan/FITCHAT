package vn.tika.fitchat.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String timeSend;
    private boolean statusMessage;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String timeSend, boolean statusMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timeSend = timeSend;
        this.statusMessage = statusMessage;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(String timeSend) {
        this.timeSend = timeSend;
    }

    public boolean isStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(boolean statusMessage) {
        this.statusMessage = statusMessage;
    }
}
