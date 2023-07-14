package vn.tika.fitchat.Model;

public class Group {
    private String groupID;
    private String groupName;
    private String numberUser;
    private String groupAvatar;
    private String createdBy;
    private String timeCreateGroup;
    private Object userGroup;

    public Group(){

    }

    public Group(String groupName, String groupAvatar) {
        this.groupName = groupName;
        this.groupAvatar = groupAvatar;
    }

    public Group(String groupID, String groupName, String numberUser, String groupAvatar, String createdBy, String timeCreateGroup, Object userGroup) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.numberUser = numberUser;
        this.groupAvatar = groupAvatar;
        this.createdBy = createdBy;
        this.timeCreateGroup = timeCreateGroup;
        this.userGroup = userGroup;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNumberUser() {
        return numberUser;
    }

    public void setNumberUser(String numberUser) {
        this.numberUser = numberUser;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getTimeCreateGroup() {
        return timeCreateGroup;
    }

    public void setTimeCreateGroup(String timeCreateGroup) {
        this.timeCreateGroup = timeCreateGroup;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(Object userGroup) {
        this.userGroup = userGroup;
    }
}
