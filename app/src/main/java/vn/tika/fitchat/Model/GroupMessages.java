package vn.tika.fitchat.Model;

public class GroupMessages {
    private String sender;
    private String groupID;
    private String groupMessage;
    private String timeSend;

    public GroupMessages(){

    }
    public GroupMessages(String sender, String groupID, String groupMessage, String timeSend) {
        this.sender = sender;
        this.groupID = groupID;
        this.groupMessage = groupMessage;
        this.timeSend = timeSend;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupMessage() {
        return groupMessage;
    }

    public void setGroupMessage(String groupMessage) {
        this.groupMessage = groupMessage;
    }

    public String getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(String timeSend) {
        this.timeSend = timeSend;
    }
}
