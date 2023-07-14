package vn.tika.fitchat.Model;

public class User {
    private String userID;
    private String userName;
    private String nickName;
    private String avatar;
    private String activeStatus;
    private String keySearch;
    private String studentCode;
    private String className;
    private String address;
    private String specialized;

    public User() {
    }

    public User(String userID) {
        this.userID = userID;
    }

    public User(String userName, String avatar) {
        this.userName = userName;
        this.avatar = avatar;
    }

    public User(String userID, String userName, String avatar) {
        this.userID = userID;
        this.userName = userName;
        this.avatar = avatar;
    }

    public User(String userID, String userName, String nickName, String avatar, String activeStatus, String keySearch, String studentCode, String className, String address, String specialized) {
        this.userID = userID;
        this.userName = userName;
        this.nickName = nickName;
        this.avatar = avatar;
        this.activeStatus = activeStatus;
        this.keySearch = keySearch;
        this.studentCode = studentCode;
        this.className = className;
        this.address = address;
        this.specialized = specialized;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getKeySearch() {
        return keySearch;
    }

    public void setKeySearch(String keySearch) {
        this.keySearch = keySearch;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialized() {
        return specialized;
    }

    public void setSpecialized(String specialized) {
        this.specialized = specialized;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
